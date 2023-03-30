package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfilePageFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun goToNonOrganizerFragment() {
        FragmentScenario.launchInContainer(ProfilePageFragment::class.java)
    }

    @Test
    fun shouldDisplayProfilePage(){
        composeTestRule.onNodeWithText("My ScribeWiz Profile").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayProfileImage(){
        composeTestRule.onNodeWithContentDescription("User profile picture").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayGuestName(){
        composeTestRule.onNodeWithText("Guest").assertIsDisplayed()
    }

    @Test
    fun canAccessLoginPage(){
        Intents.init()
        composeTestRule.onNodeWithText("Sign in").performClick()
        Intents.intended(IntentMatchers.hasComponent(FirebaseUIActivity::class.java.name))
        Intents.release()
    }
}