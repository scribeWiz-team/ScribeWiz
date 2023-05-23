package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.MainActivity
import com.github.scribeWizTeam.scribewiz.fragments.HelpFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun goToNonOrganizerFragment() {
        FragmentScenario.launchInContainer(HelpFragment::class.java)
    }

    private val helpTopics = listOf(
        "Topic 1",
        "Topic 2",
        "Topic 3",
    )

    @Test
    fun helpFragment_DisplayHelpTitle() {
        composeTestRule.onNodeWithText("Help").assertIsDisplayed()
    }

    @Test
    fun helpFragment_DisplayAllHelpTopics() {
        helpTopics.forEach { topic ->
            composeTestRule.onNodeWithText(topic).assertExists()
        }
    }
}
