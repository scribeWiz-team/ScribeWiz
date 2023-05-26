package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.scribeWizTeam.scribewiz.activities.ChallengeNotesActivity
import com.github.scribeWizTeam.scribewiz.fragments.ChallengesFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChallengesFragmentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        Intents.init()
        ChallengesFragment.isTest = true
        composeTestRule.setContent {
            ChallengesFragment()
        }
    }

    @Test
    fun testChallengesFragment() {
        composeTestRule.onNodeWithText("test1").assertExists()
        composeTestRule.onNodeWithText("test2").assertExists()
    }

    @Test
    fun testChallengeButtonLaunchesActivity() {
        // Click the button
        composeTestRule.onNodeWithText("test1").performClick()

        // Check if intent was launched
        Intents.intended(IntentMatchers.hasComponent(ChallengeNotesActivity::class.java.name))
    }

    @After
    fun tearDown() {
        ChallengesFragment.isTest = false
        Intents.release()
    }
}
