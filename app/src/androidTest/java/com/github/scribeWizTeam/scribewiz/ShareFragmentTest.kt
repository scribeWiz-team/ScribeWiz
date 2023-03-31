package com.github.scribeWizTeam.scribewiz

import androidx.core.content.FileProvider
import androidx.test.core.app.launchActivity
import com.github.scribeWizTeam.scribewiz.Fragments.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.Activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.Fragments.ShareFragment
import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)

class ShareFragmentTest  {

    private val midiFilePath = "path/to/midi/file"

    //check if the file exists, if not, should return false
    @Test
    fun shareMidiFile_fileDoesNotExist() {
        // Arrange
        val scenario = launchActivity<NavigationActivity>()
        scenario.onActivity { activity ->
            val fragment = ShareFragment(R.layout.fragment_share)
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            // Act
            val result = fragment.shareMidiFile(midiFilePath, activity)

            // Assert
            assertFalse(result)
        }
    }

    //check if the file exists, if so, should return true
    @Test
    fun shareMidiFile_fileExists() {
        // Arrange
        val scenario = launchActivity<NavigationActivity>()
        scenario.onActivity { activity ->
            val fragment = ShareFragment(R.layout.fragment_share)
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            val shareFragment = ShareFragment(0)
            val midiFile = File.createTempFile("test_midi", ".midi")
            midiFile.deleteOnExit()

            // Act
            val result = shareFragment.shareMidiFile(midiFile.absolutePath, activity)

            // Assert
            assertTrue(result)
        }
    }
    //check if the file URI is correct
    @Test
    fun shareMidiFile_fileUriIsCorrect() {
        // Arrange
        val scenario = launchActivity<NavigationActivity>()
        scenario.onActivity { activity ->
            val fragment = ShareFragment(R.layout.fragment_share)
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            val midiFile = File.createTempFile("test_midi", ".midi")
            midiFile.deleteOnExit()

            val staticTempFile = File(midiFile.parent, "test_midi.midi")
            midiFile.renameTo(staticTempFile)

            // Act
            val uri = FileProvider.getUriForFile(activity, activity.applicationContext.packageName + ".fileprovider", staticTempFile)

            // Assert
            assertEquals("content://${activity.packageName}.fileprovider/cache/test_midi.midi", uri.toString())
        }
    }

}