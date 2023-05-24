package com.github.scribeWizTeam.scribewiz

import android.util.Log
import androidx.core.content.FileProvider
import androidx.test.core.app.launchActivity
import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import java.io.File

@RunWith(AndroidJUnit4::class)
class ExportTest  {


    // Check if the function handles non-existing file correctly
    @Test
    fun exportMusicXMLFile_fileDoesNotExist() {
        // Arrange
        val scenario = launchActivity<NavigationActivity>()
        scenario.onActivity { activity ->


            // Create a temp file and immediately delete it to get a nonexistent file path
            val tempFile = File.createTempFile("non_existent", ".xml", activity.cacheDir)
            val nonExistentFilePath = tempFile.absolutePath
            tempFile.delete()

            // Act
            Log.d("ExportTest", "About to call exportMusicXMLFile")
            val result = Export.exportMusicXMLFile(File(nonExistentFilePath), activity)
            Log.d("ExportTest", "Called exportMusicXMLFile, result: $result")

            // Assert
            assertFalse(result)
        }
    }

    //check if the file exists, if so, should return true
    @Test
    fun exportMusicXMLFile_fileExists() {
        // Arrange
        val scenario = launchActivity<NavigationActivity>()
        scenario.onActivity { activity ->
            val musicXMLFile = File.createTempFile("test_musicxml", ".xml")
            musicXMLFile.deleteOnExit()

            // Act
            val result = Export.exportMusicXMLFile(musicXMLFile, activity)

            // Assert
            assertTrue(result)
        }
    }

    //check if the file URI is correct
    @Test
    fun exportMusicXMLFile_fileIsValid() {
        // Arrange
        val scenario = launchActivity<NavigationActivity>()
        scenario.onActivity { activity ->
            val musicXMLFile = File.createTempFile("test_musicxml", ".xml")
            musicXMLFile.deleteOnExit()

            val staticTempFile = File(musicXMLFile.parent, "test_musicxml.xml")
            musicXMLFile.renameTo(staticTempFile)

            // Act
            val uri = FileProvider.getUriForFile(activity, activity.applicationContext.packageName + ".fileprovider", staticTempFile)

            // Assert
            assertEquals("content://${activity.packageName}.fileprovider/cache/test_musicxml.xml", uri.toString())
        }
    }

}
