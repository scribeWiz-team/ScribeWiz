package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.github.scribeWizTeam.scribewiz.activities.FirebaseUIActivity
import com.github.scribeWizTeam.scribewiz.activities.MainActivity
import com.github.scribeWizTeam.scribewiz.fragments.ProfilePageFragment
import com.github.scribeWizTeam.scribewiz.models.UserModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfilePageFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun goToNonOrganizerFragment() {
        val testUserModel = UserModel(
            "testUserID",
            "testUser",
            42,
            friends = mutableListOf("testFriendUserID")
        )
        val testFriendModel = UserModel(
            "testFriendUserID",
            "testFriendName",
            10,
        )
        testUserModel.registerAsCurrentUser(InstrumentationRegistry.getInstrumentation().targetContext)
        testUserModel.updateInDB()
        testFriendModel.updateInDB()
        FragmentScenario.launchInContainer(ProfilePageFragment::class.java)
    }

    @Test
    fun shouldDisplayProfilePage() {
        composeTestRule.onNodeWithText("My ScribeWiz Profile").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayProfileImage() {
        composeTestRule.onNodeWithContentDescription("User profile picture").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayUserName(){
       composeTestRule.onNodeWithText("testUser").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayUserNumRecordings(){
        composeTestRule.onNodeWithText("My recordings : 42").assertIsDisplayed()
    }

    @Test
    fun signOutWorks() {
        composeTestRule.onNodeWithText("Sign out").performClick()
        composeTestRule.onNodeWithText("Guest").assertIsDisplayed()
    }

    @Test
    fun signInWorks() {
        Intents.init()
        composeTestRule.onNodeWithText("Sign out").performClick()
        composeTestRule.onNodeWithText("Sign in").performClick()
        Intents.intended(IntentMatchers.hasComponent(FirebaseUIActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun friendsListIsDisplayed() {
        composeTestRule.onNodeWithText("testFriendName").assertIsDisplayed()
    }

    @Test
    fun addFriendWorks(){
        val testFriendModel = UserModel(
            "testAddFriendUserID",
            "testFriendName2",
        )
        testFriendModel.updateInDB()

        composeTestRule.onNodeWithTag("SearchFriendField").performTextInput("testFriendName2")
        composeTestRule.onNodeWithText("Add friend").performClick()
        composeTestRule.onNodeWithText("testFriendName2").assertIsDisplayed()
    }


}