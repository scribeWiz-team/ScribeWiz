package com.github.scribeWizTeam.scribewiz

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Activities.ParticipateInChallengeActivity
import com.github.scribeWizTeam.scribewiz.Fragments.NotesListFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

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