package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.fragments.RecParameterFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecParameterFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavigationActivity>()

    @Test
    fun fragmentIsCreatedCorrectly() {

        launchFragmentInContainer<RecParameterFragment>()
        composeTestRule.onNodeWithText("Time signature: ").assertExists()
    }

    @Test
    fun launchRecordingFragmentWorks() {
        ActivityScenario.launch(NavigationActivity::class.java)
        launchFragmentInContainer<RecParameterFragment>()
        composeTestRule.onNodeWithText("Score name").performTextInput("test name")
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("Start recording").assertExists()
    }
}