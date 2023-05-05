package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Fragments.ProfilePageFragment
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
    @Test
    fun friendsListIsDisplayed(){
        composeTestRule.onNodeWithText("Bob").assertIsDisplayed()
    }

    /* Due to the adaptable screen size, the CI does not work well with this test,
       will have to find a better solution
    @Test
    fun friendsProfilePicturesAreDisplayed(){
        composeTestRule.onAllNodesWithContentDescription("FriendPP").assertCountEquals(9)
    }

     */
}