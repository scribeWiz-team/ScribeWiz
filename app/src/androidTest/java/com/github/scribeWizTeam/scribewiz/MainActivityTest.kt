package com.github.scribeWizTeam.scribewiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.containsString
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

    @Test
    fun testNavigationDrawerIsDisplayed() {
        // Check if the navigation drawer is displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testGreetingActivityIsStarted() {
        // Perform a click on the "Go" button with a name typed in the EditText
        onView(withId(R.id.mainName)).perform(typeText("John"))
        onView(withId(R.id.mainGoButton)).perform(click())

        // Check if the GreetingActivity is started with the correct name passed as an extra
        onView(withId(R.id.greetingMessage)).check(matches(withText(containsString("John"))))
    }

}
