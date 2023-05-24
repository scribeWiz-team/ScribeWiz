package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.FirebaseUIActivity
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FirebaseUIActivity>()

    @Before
    fun setUp() {
        // Initialize the ActivityScenario for FirebaseUIActivity
        ActivityScenario.launch(FirebaseUIActivity::class.java)
        FirebaseAuth.getInstance().signOut()
    }

    /*    @Test
        fun testLoginButton() {
            Intents.init()
            composeTestRule.onNodeWithText("Login").performClick()
            intended(hasComponent(KickoffActivity::class.java.name))

            Intents.release()
        }*/


    @Test
    fun testHomeButton() {
        Intents.init()
        composeTestRule.onNodeWithText("Home").performClick()
        intended(hasComponent(NavigationActivity::class.java.name))
        Intents.release()
    }


}