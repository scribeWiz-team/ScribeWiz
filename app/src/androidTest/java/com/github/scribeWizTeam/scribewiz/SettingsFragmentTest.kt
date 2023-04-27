package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Fragments.SettingsFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun settingsFragment_DisplaySettingsTitle() {
        val dummyLayoutId = 0
        val fragmentScenario = launchFragmentInContainer {
            SettingsFragment(dummyLayoutId)
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }
}
