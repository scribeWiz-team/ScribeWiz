package com.github.scribeWizTeam.scribewiz

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import alphaTab.AlphaTabView
import alphaTab.core.ecmaScript.Uint8Array
import alphaTab.importer.ScoreLoader
import alphaTab.model.Score
import alphaTab.synth.PlayerState
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import java.io.ByteArrayOutputStream
import kotlin.contracts.ExperimentalContracts
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
    //TODO: Think about a better way to pass the file



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
            Log.e("AlphaTab", "Failed to load file: $e, ${e.stackTraceToString()}")
            Toast.makeText(this, "Open File Failed", Toast.LENGTH_LONG)
                .show() //simple feedback in a small popup
        }

        try {
            _viewModel.currentTickPosition.value = 0
            _viewModel.tracks.value = arrayListOf(inMemoryObject.tracks[0])
        } catch (e: Exception) {
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
