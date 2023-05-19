package com.github.scribeWizTeam.scribewiz

import HomeFragment
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.scribeWizTeam.scribewiz.Activities.NavigationActivity
import org.junit.Before
import org.junit.Test
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.navigation.NavigationView
import org.hamcrest.Matcher
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.scribeWizTeam.scribewiz.Fragments.HelpFragment
import com.github.scribeWizTeam.scribewiz.Fragments.NotesListFragment
import com.github.scribeWizTeam.scribewiz.Fragments.ProfilePageFragment
import junit.framework.TestCase.assertTrue

class NavigationActivityTest {

    @Before
    fun setUp() {
        // Initialize the ActivityScenario for MainActivity
        ActivityScenario.launch(NavigationActivity::class.java)

        // Increase the default Espresso action timeout
        Espresso.onView(isRoot()).perform(waitFor(5000))
    }

    // Custom ViewAction to wait for a specified amount of time
    private fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for $millis milliseconds"
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
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

    @Test
    fun testNavigationDrawerItemClicks() {
        // Open the navigation drawer
        Espresso.onView(withId(R.id.drawer_layout))
            .perform(ViewActions.swipeRight())

        val itemsToTest = arrayOf(
            R.id.nav_home, R.id.nav_library, R.id.nav_profile,
            R.id.nav_help, R.id.nav_rec, R.id.nav_settings
        )

        itemsToTest.forEach { menuItemId ->
            // Get the navigation view and simulate item click
            Espresso.onView(withId(R.id.nav_view)).perform(object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(NavigationView::class.java)
                }

                override fun getDescription(): String {
                    return "Simulate item click on NavigationView"
                }

                override fun perform(uiController: UiController, view: View) {
                    val navigationView = view as NavigationView
                    navigationView.setNavigationItemSelectedListener {
                        it.itemId == menuItemId
                    }
                    navigationView.setCheckedItem(menuItemId)
                }
            })

            // Check if the corresponding fragment is displayed
            Espresso.onView(withId(R.id.fragment_container))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun testOnBackPressed() {
        // Get the UiDevice instance
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Open the navigation drawer
        Espresso.onView(withId(R.id.drawer_layout))
            .perform(ViewActions.swipeRight())

        // Close the navigation drawer
        Espresso.onView(withId(R.id.drawer_layout))
            .perform(ViewActions.swipeLeft())

        // Get the DrawerLayout instance
        var drawerLayout: DrawerLayout? = null
        Espresso.onView(withId(R.id.drawer_layout)).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(DrawerLayout::class.java)
            }

            override fun getDescription(): String {
                return "Get DrawerLayout instance"
            }

            override fun perform(uiController: UiController, view: View) {
                drawerLayout = view as DrawerLayout
            }
        })

        // Press the back button only if the navigation drawer is closed
        if (drawerLayout != null && !drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            uiDevice.pressBack() // Use UiDevice's pressBack() method instead of Espresso.pressBack()
        }
    }

}
