package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.FirebaseUIActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
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

    //TODO: Figure out how to actually log in a user using the Google API
    /*@Test
    fun testSignOutButton(){
        composeTestRule.onNodeWithText("Sign out").performClick()
        composeTestRule.onNodeWithText("Not signed in").assertIsDisplayed()
    }*/

}