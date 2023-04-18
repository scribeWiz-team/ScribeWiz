package com.github.scribeWizTeam.scribewiz

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import com.github.scribeWizTeam.scribewiz.Fragments.NotesListFragment
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


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

    @Test
    fun dismissNoteDeleteCorrectly() {
        for (title in expectedFiles) {
            composeTestRule.onNodeWithText(title.toString()).performTouchInput {
                this.down(Offset(200F, 0F))
                this.moveTo(Offset(0F, 0F))
                this.up()
            }
        }
        for (title in expectedFiles) {
            composeTestRule.onNodeWithText(title.toString()).assertDoesNotExist()
        }
    }

    @Test
    fun onlyMusicXMLFiles() {
        composeTestRule.onNodeWithText(invalidFileName).assertDoesNotExist()
    }

    @After
    fun removeTestFiles(){
        notesDir.deleteRecursively()
    }
}