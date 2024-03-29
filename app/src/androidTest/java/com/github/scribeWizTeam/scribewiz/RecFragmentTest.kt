package com.github.scribeWizTeam.scribewiz

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.fragments.RecFragment
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecFragmentTest {

    @get:Rule
    var recordPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavigationActivity>()

    @Test
    fun startAndStopRecording() {

        launchFragmentInContainer<RecFragment>()

        // Start recording
        composeTestRule.onNodeWithText("Start recording").performClick()
        composeTestRule.onNodeWithText("Stop recording").assertExists()

        // Stop recording
        composeTestRule.onNodeWithText("Stop recording").performClick()
        composeTestRule.onNodeWithText("Start recording").assertExists()


        // Check that the recording file was saved and has non-zero size
        // scenario.onFragment { fragment ->
        //      val recordingFile = File(fragment.getOutputFilePath())
        //      assertTrue(recordingFile.exists())
        //      assertTrue(recordingFile.length() > 0)
        // }
    }

    // @Test
    // fun startAndStopMetronome() {
    //     launchFragmentInContainer<RecFragment>()
    //     // Start metronome
    //     composeTestRule.onNodeWithText("Start metronome").performClick()
    //     composeTestRule.onNodeWithText("Stop metronome").assertExists()

    //     // Stop metronome
    //     composeTestRule.onNodeWithText("Stop metronome").performClick()
    //     composeTestRule.onNodeWithText("Start metronome").assertExists()
    // }

    // @Test
    // fun badFormatTempoReplacedByDefaultValue() {
    //     launchFragmentInContainer<RecFragment>()
    //     // write invalid input with a coma at the end
    //     composeTestRule.onNodeWithText("60").performTextInput(",")
    //     composeTestRule.onNodeWithText("Start metronome").performClick()
    //     composeTestRule.onNodeWithText("60").assertExists()
    // }
}
