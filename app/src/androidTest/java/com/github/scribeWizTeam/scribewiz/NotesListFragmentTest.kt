package com.github.scribeWizTeam.scribewiz

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Fragments.NotesListFragment
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.contracts.ExperimentalContracts


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NotesListFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var rRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(READ_EXTERNAL_STORAGE)

    @get:Rule
    var wRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(WRITE_EXTERNAL_STORAGE)



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

    @Test
    fun testNumberOfComponentMatchNumberOfFile() {
        for (title in expectedFiles) {
            composeTestRule.onNodeWithText(title.toString()).assertExists()
        }
    }

//    @Test
//    fun dismissNoteDeleteCorrectly() {
//        for (title in expectedFiles) {
//            composeTestRule.onNodeWithText(title.toString()).performTouchInput {
//                this.down(Offset(200F, 0F))
//                this.moveTo(Offset(0F, 0F))
//                this.up()
//            }
//        }
//        for (title in expectedFiles) {
//            composeTestRule.onNodeWithText(title.toString()).assertDoesNotExist()
//        }
//    }

    @Test
    fun onlyMusicXMLFiles() {
        composeTestRule.onNodeWithText(invalidFileName).assertDoesNotExist()
    }

    @OptIn(ExperimentalContracts::class)
    @Test
    fun displayNotesWhenClickOnPlay() {
        Intents.init()

        composeTestRule.onNode(hasText("a")).performClick()

        Intents.intended(IntentMatchers.hasComponent(NotesDisplayedActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun openDialogForChangingNameWhenLongClick() {
        composeTestRule.onNode(hasText("a")).performTouchInput {longClick()}
        composeTestRule.onNodeWithText(NotesListFragment().dialogName).assertIsDisplayed()
    }

    @Test
    fun fileIsModifiedToTheGoodValue() {

        composeTestRule.onNode(hasText("a")).performTouchInput {longClick()}
        composeTestRule.onNodeWithContentDescription("New Name").performTextClearance()
        composeTestRule.onNodeWithContentDescription("New Name").performTextInput("newNameTest")
        composeTestRule.onNode(hasText("Rename")).performClick()

        runBlocking {
            composeTestRule.waitUntil(timeoutMillis = 100000) {
                try {
                    composeTestRule.onNode(hasText("newNameTest")).fetchSemanticsNode()
                    true
                } catch (e: AssertionError) {
                    false
                }
            }
        }

        composeTestRule.onNode(hasText("newNameTest")).assertIsDisplayed()
    }

    @Test
    fun dialogGetCanceledCorrectly() {
        composeTestRule.onNode(hasText("a")).performTouchInput {longClick()}
        composeTestRule.onNodeWithContentDescription("New Name").performTextInput("anewNameTest")
        composeTestRule.onNode(hasText("Cancel")).performClick()

        composeTestRule.onNode(hasText("a")).assertIsDisplayed() //Since only one "a" file was loaded, make sure that it was not modified

    }

    @Test
    fun goToParticipateInChallenge() {
        composeTestRule.onNode(hasText("a")).performTouchInput { longClick() }
        composeTestRule.onNode(hasText("Challenges")).assertExists()
        composeTestRule.onNode(hasText("Challenges")).performClick()
        //assert that I'm on the activity ParticipateInChallengeActivity
        composeTestRule.onNode(hasText("Participate in a Challenge")).assertExists()
    }


    @After
    fun removeTestFiles(){
        notesDir.deleteRecursively()
    }
}