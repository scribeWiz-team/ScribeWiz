package com.github.scribeWizTeam.scribewiz

import android.Manifest
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.rule.GrantPermissionRule
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Fragments.NotesListFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class ParticipateInChallengeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var rRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)

    @get:Rule
    var wRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val expectedFiles = 'a'..'g'

    private val invalidFileName = "NOT_A_VALID_FILE"

    private var notesDir = File("test")

    @Before
    fun initialize() {
        notesDir = composeTestRule.activity.getExternalFilesDir(NOTES_FOLDER)?.absoluteFile!!

        for (name in expectedFiles) {
            File(notesDir, "$name.$MUSIC_XML_EXTENSION").createNewFile()
        }

        File(notesDir, invalidFileName).createNewFile()

        FragmentScenario.launchInContainer(NotesListFragment::class.java)

    }

    //The below test makes sure I'm going to the right activity when I click on "challenges" in the activity
    @Test
    fun goToParticipateInChallenge() {
        composeTestRule.onNode(hasText("a")).performTouchInput { longClick() }
        composeTestRule.onNode(hasText("challenges")).assertExists()
        composeTestRule.onNode(hasText("challenges")).performClick()
        //assert that I'm on the activity ParticipateInChallengeActivity
        composeTestRule.onNode(hasText("Participate in a Challenge")).assertExists()
    }
}