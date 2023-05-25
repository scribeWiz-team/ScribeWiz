package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.FirebaseUIActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FirebaseUIActivity>()
    @Test
    fun assertLoginTextExist() {
        FirebaseAuth.getInstance().signOut()
        composeTestRule.onNodeWithText("Login").assertExists()
    }

//    @Before
//    fun setUp() {
//        // Initialize the ActivityScenario for FirebaseUIActivity
//        ActivityScenario.launch(FirebaseUIActivity::class.java)
//        FirebaseAuth.getInstance().signOut()
//    }

    /*    @Test
        fun testLoginButton() {
            Intents.init()
            composeTestRule.onNodeWithText("Login").performClick()
            intended(hasComponent(KickoffActivity::class.java.name))

            Intents.release()
        }*/


}