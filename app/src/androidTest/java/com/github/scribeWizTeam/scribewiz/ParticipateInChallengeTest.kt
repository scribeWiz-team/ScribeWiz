package com.github.scribeWizTeam.scribewiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.github.scribeWizTeam.scribewiz.activities.ParticipateInChallengeActivity
import org.junit.After
import org.junit.Rule
import org.junit.Test

class ParticipateInChallengeTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()


    @Test
    fun challengesAppear() = composeTestRule.launch<ParticipateInChallengeActivity>(
        onBefore = {
            ParticipateInChallengeActivity.isTest = true
        },
        onAfterLaunched = {
            composeTestRule.onNodeWithText("test1").assertExists()
            composeTestRule.onNodeWithText("test2").assertExists()
        }
    )


    @After
    fun cleanup() {
        ParticipateInChallengeActivity.isTest = false
    }

    /**
     * Uses a [ComposeTestRule] created via [createEmptyComposeRule] that allows setup before the activity
     * is launched via [onBefore]. Assertions on the view can be made in [onAfterLaunched].
     */
    inline fun <reified A : Activity> ComposeTestRule.launch(
        onBefore: () -> Unit = {},
        intentFactory: (Context) -> Intent = {
            Intent(
                ApplicationProvider.getApplicationContext(),
                A::class.java
            )
        },
        onAfterLaunched: ComposeTestRule.() -> Unit
    ) {
        onBefore()

        val context = ApplicationProvider.getApplicationContext<Context>()
        ActivityScenario.launch<A>(intentFactory(context))

        onAfterLaunched()
    }
}