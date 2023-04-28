package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Fragments.HomeFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeFragment_DisplayHomeTitle() {
        val dummyLayoutId = 0
        val fragmentScenario = launchFragmentInContainer {
            HomeFragment(dummyLayoutId)
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
