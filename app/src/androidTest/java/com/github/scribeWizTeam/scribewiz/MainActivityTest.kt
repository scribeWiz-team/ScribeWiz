package com.github.scribeWizTeam.scribewiz

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = activityScenarioRule<MainActivity>()

    @Test
    // This test will fail because the mainGoButton is not clickable
    fun testExample() {
        Intents.init()

        val mainNameInteraction = onView(ViewMatchers.withId(R.id.mainName))
        mainNameInteraction.perform(ViewActions.replaceText("John Doe"))

        val mainGoButtonInteraction = onView(ViewMatchers.withId(R.id.mainGoButton))
        mainGoButtonInteraction.perform(ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(GreetingActivity::class.java.name))

        Intents.release()
    }

}