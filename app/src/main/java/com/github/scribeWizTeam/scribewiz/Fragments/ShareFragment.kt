package com.github.scribeWizTeam.scribewiz.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.R
import java.io.File

class ShareFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_share, container, false)

    }
    /*
    * This function is called when the share button is clicked,
    * it takes the path of the midi file and the context of the activity,
    * will share the file with the user's preferred app
    * returns true on success, false on failure
     */
    fun shareMidiFile(midiFilePath: String, context: Context): Boolean {
        val file = File(midiFilePath)

        // Check if the file exists
        if (!file.exists()) {
            // Log an error message
            Log.e("ShareMidiFile", "File does not exist: $midiFilePath")
            return false
        }
        try {
            // create a new intent with the ACTION_SEND action
            val shareIntent = Intent(Intent.ACTION_SEND)

            // set the MIME type of the file to share, in our case, 3gp
            shareIntent.type = "audio/three-gpp"

            // create a file URI for the file to share
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                file
            )

            // set the URI of the file to share as the intent data
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

            // grant temporary access to the receiving app
            context.grantUriPermission(
                context.packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // add the FLAG_ACTIVITY_NEW_TASK flag to start the activity from a non-activity context
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // start the intent to share the file
            context.startActivity(Intent.createChooser(shareIntent, "Share MIDI file"))

        } catch (
            e: SecurityException
        ) {
            Log.d("TAG", "Error message: " + e.message)
        }
        return true
    }
}