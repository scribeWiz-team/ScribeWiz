package com.github.scribeWizTeam.scribewiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
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
     */
    fun shareMidiFile(midiFilePath: String, context: Context) {
        val file = File(midiFilePath)

        // create a new intent with the ACTION_SEND action
        val shareIntent = Intent(Intent.ACTION_SEND)

        // set the MIME type of the file to share
        shareIntent.type = "audio/midi"

        // create a file URI for the file to share
        val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", file)

        // set the URI of the file to share as the intent data
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

        // grant permission to read the file to any app that receives the intent
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // start the intent to share the file
        context.startActivity(Intent.createChooser(shareIntent, "Share MIDI file"))
    }


}