package com.github.scribeWizTeam.scribewiz

import alphaTab.AlphaTabView
import alphaTab.core.ecmaScript.Uint8Array
import alphaTab.importer.ScoreLoader
import alphaTab.model.Score
import alphaTab.synth.PlayerState
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.scribeWizTeam.scribewiz.util.Editor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.contracts.ExperimentalContracts

/**
 * This activity displays dynamically the notes of a passed MusicXML file.
 * To use this activity, you have to use an Intent with the Uri of the MusicXML file converted to string, passed as additional data to the intent with the key fileKey.
 * For a concrete use case, don't hesitate to check the NotesDisplayedActivityTest.kt file.
 */
@ExperimentalContracts
@ExperimentalUnsignedTypes
class NotesDisplayedActivity : AppCompatActivity() {

    private lateinit var _alphaTabView: AlphaTabView
    private lateinit var _viewModel: ViewScoreViewModel
    private val fileKey : String = "FILE"
    var exceptionCaught: Boolean = false //Used in the unit tests to make sure the exception was handled
    private lateinit var noteSpinner: Spinner
    private lateinit var replaceNoteButton: Button
    private lateinit var fileName : String

    /**
     * Initializes the activity and sets up the view.
     *
     * @param savedInstanceState The saved instance state.
     */
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

        replaceNoteButton = findViewById(R.id.replace_note_button)
        noteSpinner = findViewById(R.id.note_spinner)

        // Initialize the Spinner with an ArrayAdapter using an array of note choices.
        val notesArray = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, notesArray)
        noteSpinner.adapter = spinnerAdapter

        // Make the spinner visible from the start
        noteSpinner.visibility = View.VISIBLE

        // Handle replaceNoteButton clicks
        replaceNoteButton.setOnClickListener {
            //print the current tick position
            val selectedNote = noteSpinner.selectedItem.toString()
            if (filePassed != null) {
                val edited_file = editNote(filePassed,selectedNote)

            }
        }
    }

    //Required by the implementation of the library to work
    private fun openFile(uri: Uri) {
        var inMemoryObject = Score()
        fileName = inMemoryObject.title
        try {
            val fileData = readFileData(uri)
            inMemoryObject = ScoreLoader.loadScoreFromBytes(fileData, _alphaTabView.settings)
            Log.i("AlphaTab", "File loaded: $fileName")
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

    /**
     * Edits the note at the current tick position in the input MusicXML file.
     *
     * @param filePassed The URI string pointing to the input MusicXML file.
     * @param newNote    The note to replace the existing note with.
     * @return The output file with the modified note.
     */
    private fun editNote(filePassed: String, newNote: String): File {
        val inputFileUri = Uri.parse(filePassed)
        val inputFile = createTempFileFromUri(this, inputFileUri)
        val outputFile = File.createTempFile("temp_musicxml_modified", ".xml", cacheDir)

        val tickPosition = _viewModel.currentTickPosition.value
        val noteLocation = Editor.getNoteCountWithinQuarterNotes(inputFile, tickPosition!!)

        Editor.editNoteInMusicXML(outputFile, inputFile, noteLocation, newNote)

        // Get the original file name and create a new file name for the edited file
        val originalFileName = inputFileUri.lastPathSegment
        val originalFileNameWithoutExtension = originalFileName?.substringBeforeLast('.')
        val editedName = "edited_$originalFileNameWithoutExtension"

        // You have to specify where you want to save your edited file. Here, it's saved in the same directory as the original file
        val editedFile = File(inputFile.parentFile, editedName)

        // Copy the content of the outputFile to the editedFile
        outputFile.copyTo(editedFile, overwrite = true)

        val noteStorageManager = NotesStorageManager(this)

        openFile(Uri.fromFile(outputFile))

        noteStorageManager.writeNoteFile(editedName, outputFile.readText())

        inputFile.delete()

        return outputFile
    }

    companion object {
        /**
         * Creates a temporary file from a content URI.
         *
         * @param notesDisplayedActivity The activity.
         * @param uri The content URI.
         * @return The temporary file.
         */
        fun createTempFileFromUri(notesDisplayedActivity: NotesDisplayedActivity, uri: Uri): File {
            // Create a temporary file with a prefix "temp_musicxml" and a suffix ".xml" in the cache directory of the activity
            val tempFile = File.createTempFile("temp_musicxml", ".xml", notesDisplayedActivity.cacheDir)

            // Open an input stream for the content URI
            notesDisplayedActivity.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Copy the input stream to the temporary file using a FileOutputStream
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Return the temporary file
            return tempFile
        }
    }
}

