package com.github.scribeWizTeam.scribewiz

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import alphaTab.Settings
import alphaTab.core.ecmaScript.Uint8Array
import alphaTab.importer.ScoreLoader
import alphaTab.model.Score
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream
import kotlin.contracts.ExperimentalContracts


class NotesDisplayedActivity : AppCompatActivity() {

    @ExperimentalContracts //Required by the implementation of the library to work
    private fun loadInMemoryMusicXML(uri: Uri, settings: Settings?) : Score? {
        var inMemoryObject : Score? = null
        try {
            val fileData = readFileData(uri)
            inMemoryObject = ScoreLoader.loadScoreFromBytes(fileData, settings)
            Log.i("AlphaTab", "File loaded: ${inMemoryObject.title}")
        } catch (e: Exception) {
            Log.e("AlphaTab", "Failed to load file: $e, ${e.stackTraceToString()}")
            Toast.makeText(this, "Open File Failed", Toast.LENGTH_LONG).show() //simple feedback in a small popup
        }

        return inMemoryObject
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

}