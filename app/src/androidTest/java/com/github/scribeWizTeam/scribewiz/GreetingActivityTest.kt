package com.github.scribeWizTeam.scribewiz

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {
    @Test
    fun testGreeting() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            GreetingActivity::class.java
        )
        ActivityScenario.launch<MainActivity>(intent).use {
            Espresso.onView(ViewMatchers.withId(R.id.greetingMessage))
                .check(ViewAssertions.matches(ViewMatchers.withId(R.id.greetingMessage)))
        }
    }
}