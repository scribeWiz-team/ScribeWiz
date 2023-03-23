package com.github.scribeWizTeam.scribewiz

import org.junit.Rule

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setUp() {
        // Initialize the ActivityScenario for MainActivity
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testToolbarIsDisplayed() {
        // Check if the toolbar is displayed
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @Test
    fun testLoginPageButtonClick() {
        val expectedText = "ScribeWiz \uD83C\uDFBC"
        composeTestRule.onNodeWithText("To login page").performClick()
        composeTestRule.onNodeWithText(expectedText).assertIsDisplayed()
    }

    @Test
    fun testNavigationDrawerIsDisplayed() {
        // Check if the navigation drawer is displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
    }

}
