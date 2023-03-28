package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.firebase.ui.auth.KickoffActivity
import com.github.scribeWizTeam.scribewiz.Activities.FirebaseUIActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FirebaseUIActivity>()
    @Before
    fun setUp() {
        // Initialize the ActivityScenario for MainActivity
        ActivityScenario.launch(FirebaseUIActivity::class.java)
    }
    @Test
    fun testLoginButton(){
        Intents.init()
        composeTestRule.onNodeWithText("Login").performClick()
        intended(hasComponent(KickoffActivity::class.java.name))
        Intents.release()
    }

    //TODO: Figure out how to actually log in a user using the Google API
    @Test
    fun testSignOutButton(){
        composeTestRule.onNodeWithText("Sign out").performClick()
        composeTestRule.onNodeWithText("Not signed in").assertIsDisplayed()
    }


}