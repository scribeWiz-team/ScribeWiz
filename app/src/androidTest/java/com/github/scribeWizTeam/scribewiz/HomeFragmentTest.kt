package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.fragments.HomeFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavigationActivity>()
    @Test
    fun canLaunchRecordFragment() {
        FragmentScenario.launchInContainer(HomeFragment::class.java)
        Intents.init()
        composeTestRule.onNodeWithText("Record").performClick()
        intended(hasComponent(NavigationActivity::class.java.name))
        intended(hasExtra("fragment", "recordPage"))
        Intents.release()
    }

    @Test
    fun canLaunchChallengesFragment() {
        FragmentScenario.launchInContainer(HomeFragment::class.java)
        Intents.init()
        composeTestRule.onNodeWithContentDescription("Challenges page").performClick()
        intended(hasComponent(NavigationActivity::class.java.name))
        intended(hasExtra("fragment", "challengesPage"))
        Intents.release()
    }

}
