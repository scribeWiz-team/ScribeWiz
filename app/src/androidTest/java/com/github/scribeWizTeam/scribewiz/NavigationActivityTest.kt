package com.github.scribeWizTeam.scribewiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.scribeWizTeam.scribewiz.Activities.NavigationActivity
import org.junit.Before
import org.junit.Test

class NavigationActivityTest {

    @Before
    fun setUp() {
        // Initialize the ActivityScenario for MainActivity
        ActivityScenario.launch(NavigationActivity::class.java)
    }

    @Test
    fun testToolbarIsDisplayed() {
        // Check if the toolbar is displayed
        Espresso.onView(ViewMatchers.withId(R.id.toolbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testNavigationDrawerIsDisplayed() {
        // Check if the navigation drawer is displayed
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}