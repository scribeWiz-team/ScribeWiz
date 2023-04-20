
 

package com.github.scribeWizTeam.scribewiz

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import alphaTab.AlphaTabView
import alphaTab.Settings
import alphaTab.core.ecmaScript.Uint8Array
import alphaTab.importer.ScoreLoader
import alphaTab.model.Score
import alphaTab.synth.PlayerState
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import java.io.ByteArrayOutputStream
import kotlin.contracts.ExperimentalContracts
import kotlinx.coroutines.flow.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton


//This activity displays dynamically the notes of a passed MusicXML file
//To use this activity, you have to use an Intent with the Uri of the MusicXML file converted to string, passed as additional data to the intent with the key fileKey
//For a concrete use case, don't hesitate to check the NotesDisplayedActivityTest.kt file
@ExperimentalContracts
@ExperimentalUnsignedTypes
class NotesDisplayedActivity : AppCompatActivity() {

    private lateinit var _alphaTabView: AlphaTabView
    private lateinit var _viewModel: ViewScoreViewModel
    val fileKey : String = "FILE"
    var exceptionCaught: Boolean = false //Used in the unit tests to make sure the exception was handled

    //TODO: Think about a better way to pass the file


    fun isPlaying() : Boolean{
        //here value is 0 when the player is paused and 1 when it is running
       return _alphaTabView.api.playerState.value == 1
    }
    //The URI of the file has to be passed as a String with Key fileKey
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes_displayed_activity)
        _alphaTabView = findViewById(R.id.alphatab_view)
        _viewModel = ViewModelProvider(this)[ViewScoreViewModel::class.java]

        val playButton = findViewById<FloatingActionButton>(R.id.play_button)

        _alphaTabView.api.playerReady.on {
            playButton.isEnabled = true
        }

        _alphaTabView.api.playerStateChanged.on {
            if (it.state == PlayerState.Playing) {
                playButton.setImageResource(android.R.drawable.ic_media_pause)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                playButton.setImageResource(android.R.drawable.ic_media_play)
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        _alphaTabView.api.playerPositionChanged.on {
            _viewModel.currentTickPosition.value = it.currentTick.toInt()
        }

        observeViewModel()

        val widthDp = resources.displayMetrics.widthPixels /
                resources.displayMetrics.density
        _viewModel.updateLayout(widthDp)

        val filePassed: String? = intent.getStringExtra(fileKey)

        if (filePassed != null) {
            openFile(Uri.parse(filePassed))
        }
        playButton.setOnClickListener {
            _alphaTabView.api.playPause()
        }
    }

    //Required by the implementation of the library to work
    private fun openFile(uri: Uri) {
        var inMemoryObject: Score = Score()
        try {
            val fileData = readFileData(uri)
            inMemoryObject = ScoreLoader.loadScoreFromBytes(fileData, _alphaTabView.settings)
            Log.i("AlphaTab", "File loaded: ${inMemoryObject.title}")
        } catch (e: Exception) {
            exceptionCaught = true
            Log.e("AlphaTab", "Failed to load file: $e, ${e.stackTraceToString()}")
            Toast.makeText(this, "Open File Failed", Toast.LENGTH_LONG)
                .show() //simple feedback in a small popup
        }

        try {
            _viewModel.currentTickPosition.value = 0
            _viewModel.tracks.value = arrayListOf(inMemoryObject.tracks[0])
        } catch (e: Exception) {
            exceptionCaught = true
            Log.e("AlphaTab", "Failed to render file: $e, ${e.stackTraceToString()}")
            Toast.makeText(this, "Open File Failed", Toast.LENGTH_LONG).show()
        }


    }

    @ExperimentalContracts
    //Copied-paste from the usage example of alphaTab
    private fun readFileData(uri: Uri): Uint8Array {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream.use {
            ByteArrayOutputStream().use {
                inputStream!!.copyTo(it)
                return Uint8Array(it.toByteArray().asUByteArray())
            }
        }
    }

    private fun observeViewModel() {
        _viewModel.settings.observe(this) {
            _alphaTabView.settings = it
        }
        _viewModel.tracks.observe(this) {
            _alphaTabView.tracks = it
        }

        val initialPosition = _viewModel.currentTickPosition.value
        var shouldSetPosition = true
        _alphaTabView.api.playerReady.on {
            if (shouldSetPosition && _alphaTabView.tracks == _viewModel.tracks.value) {
                _viewModel.currentTickPosition.value = initialPosition
                _alphaTabView.api.tickPosition = initialPosition!!.toDouble()
            }
            shouldSetPosition = false
        }
    }




}