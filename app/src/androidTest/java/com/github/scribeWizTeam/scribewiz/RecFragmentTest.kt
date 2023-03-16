package com.github.scribeWizTeam.scribewiz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RecFragmentTest {

    private val recordPermission = Manifest.permission.RECORD_AUDIO

    @Test
    fun startAndStopRecording() {

        val scenario = FragmentScenario.launchInContainer(RecFragment::class.java)
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            val recordButton = fragment.view!!.findViewById<Button>(R.id.record_button)
            val recordingTimeText = fragment.view!!.findViewById<TextView>(R.id.time_recording)

            // Start recording
            recordButton.performClick()
            assertTrue(fragment.isRecording)
            assertEquals("Stop Recording", recordButton.text)
            assertTrue(File(fragment.getOutputFilePath()).exists())

            // Wait for 3 seconds
            Thread.sleep(3000)

            // Stop recording
            recordButton.performClick()
            assertFalse(fragment.isRecording)
            assertEquals("Start Recording", recordButton.text)

            // Check that the recording time is displayed
            assertTrue(recordingTimeText.text.isNotEmpty())

            // Check that the recording file was saved and has non-zero size
            val recordingFile = File(fragment.getOutputFilePath())
            assertTrue(recordingFile.exists())
            assertTrue(recordingFile.length() > 0)
        }
    }

    @Test
    fun onRequestPermissionsResult() {
        val scenario = FragmentScenario.launchInContainer(RecFragment::class.java)
        scenario.onFragment { fragment ->
            val requestCode = fragment.REQUEST_RECORD_AUDIO_PERMISSION
            val permissions = arrayOf(recordPermission)
            val grantResults = intArrayOf(PackageManager.PERMISSION_GRANTED)

            // Request the permission
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)

            // Check that recording started after permission was granted
            assertTrue(fragment.isRecording)
            assertTrue(File(fragment.getOutputFilePath()).exists())

            // Check that the recording file was saved and has non-zero size
            val recordingFile = File(fragment.getOutputFilePath())
            assertTrue(recordingFile.exists())
            assertTrue(recordingFile.length() > 0)
        }
    }
}
