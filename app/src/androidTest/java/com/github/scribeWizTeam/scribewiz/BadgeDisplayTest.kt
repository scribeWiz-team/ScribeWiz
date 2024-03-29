package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.github.scribeWizTeam.scribewiz.activities.BadgeDisplayActivity
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.fragments.ProfilePageFragment
import com.github.scribeWizTeam.scribewiz.models.BadgeModel
import com.github.scribeWizTeam.scribewiz.models.BadgeRanks
import com.github.scribeWizTeam.scribewiz.models.UserModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BadgeDisplayTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavigationActivity>()


    @Before
    fun setUp() {

        val testUserModel = UserModel(
            "testUserID",
            "testUser",
            42,
        )

        testUserModel.registerAsCurrentUser(InstrumentationRegistry.getInstrumentation().targetContext)

        testUserModel.updateInDB()
        BadgeModel.addBadgeToUser(testUserModel,
            BadgeModel(
                "testBadgeID",
                "testBadge",
                "testChallenge",
                rank = BadgeRanks.GOLD.ordinal
            )
        )

        // Initialize the ActivityScenario for ProfilePageFragment
        FragmentScenario.launchInContainer(ProfilePageFragment::class.java)
    }


    @Test
    fun testMyBadgesButton(){
        Intents.init()
        composeTestRule.onNodeWithContentDescription("My badges button").performClick()
        //wait for the page to load
        Thread.sleep(1000)
        Intents.intended(IntentMatchers.hasComponent(BadgeDisplayActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun testMyBadgesPage(){
        Intents.init()
        composeTestRule.onNodeWithContentDescription("My badges button").performClick()
        //wait for the page to load
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("My badges").assertIsDisplayed()
        composeTestRule.onNodeWithText("testBadge").assertIsDisplayed()
        Intents.release()
    }

    @Test
    fun testBackButton(){
        Intents.init()
        composeTestRule.onNodeWithContentDescription("My badges button").performClick()
        //wait for the page to load
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("<-").performClick()
        //wait for the page to load
        Thread.sleep(1000)
        Intents.intended(IntentMatchers.hasComponent(NavigationActivity::class.java.name))
        Intents.release()
    }


}

