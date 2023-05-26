package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.scribeWizTeam.scribewiz.activities.ChallengeNotesActivity
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.fragments.ChallengesFragment
import com.github.scribeWizTeam.scribewiz.fragments.ProfilePageFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChallengesFragmentTest {


    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavigationActivity>()

    @Before
    fun setUp() {
        Intents.init()
        ChallengesFragment.isTest = true
        FragmentScenario.launchInContainer(ChallengesFragment::class.java)
    }

    @Test
    fun testChallengesFragment() {
        composeTestRule.onNodeWithText("test1").assertExists()
        composeTestRule.onNodeWithText("test2").assertExists()
    }

    @Test
    fun testChallengeButtonLaunchesActivity() {
        //wait for the button to appear
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
