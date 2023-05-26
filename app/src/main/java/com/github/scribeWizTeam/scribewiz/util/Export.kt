package com.github.scribeWizTeam.scribewiz.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class Export {
    companion object {
        /*
        * This function is called when the export option is selected in the library fragment,
        * it takes the MusicXML file and the context of the activity,
        * will share the file with the user's preferred app
        * returns true on success, false on failure
        */
        fun exportMusicXMLFile(file: File, context: Context): Boolean {
            // Log a debug message
            Log.d("com.github.scribeWizTeam.scribewiz.util.Export", "exportMusicXMLFile called with file: ${file.path}")

            // Check if the file exists
            if (!file.exists()) {
                // Log an error message
                Log.e("com.github.scribeWizTeam.scribewiz.util.Export", "File does not exist: ${file.path}")
                return false
            }

            try {
                // create a new intent with the ACTION_SEND action
                val shareIntent = Intent(Intent.ACTION_SEND)

                // set the MIME type of the file to share, in our case, xml
                shareIntent.type = "application/xml"

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
                context.startActivity(Intent.createChooser(shareIntent, "com.github.scribeWizTeam.scribewiz.util.Export MusicXML file"))

            } catch (
                e: SecurityException
            ) {
                Log.e("ExportMusicXMLFile", "Error message: " + e.message)
                return false
            }

            return true
        }
    }
}
