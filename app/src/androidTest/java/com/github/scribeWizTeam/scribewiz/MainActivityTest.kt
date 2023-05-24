package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.FirebaseUIActivity
import com.github.scribeWizTeam.scribewiz.activities.MainActivity
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun canLaunchHelpFragment() {
        Intents.init()
        composeTestRule.onNodeWithContentDescription("Help page").performClick()
        intended(hasComponent(NavigationActivity::class.java.name))
        intended(hasExtra("fragment", "helpPage"))
        Intents.release()
    }

    @Test
    fun canLaunchLoginFragment() {
        Intents.init()
        composeTestRule.onNodeWithContentDescription("User profile picture").performClick()
        FirebaseAuth.getInstance().signOut()
        intended(hasComponent(FirebaseUIActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun canLaunchRecordFragment() {
        Intents.init()
        composeTestRule.onNodeWithText("Record").performClick()
        intended(hasComponent(NavigationActivity::class.java.name))
        intended(hasExtra("fragment", "recordPage"))
        Intents.release()
    }

    @Test
    fun canLaunchChallengesFragment() {
        Intents.init()
        composeTestRule.onNodeWithText("Challenges").performClick()
        intended(hasComponent(NavigationActivity::class.java.name))
        intended(hasExtra("fragment", "challengesPage"))
        Intents.release()
    }
}
