package com.github.scribeWizTeam.scribewiz

import android.os.Environment
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NotesListActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NotesListActivity>()

    private val expectedFiles = 'a'..'z'

    private val invalidFileName = "NOT_A_VALID_FILE"

    private lateinit var notesDir: File

    @Before
    fun initialize() {

        notesDir = File(composeTestRule.activity.applicationContext
            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?.absoluteFile,
            NOTES_FOLDER)

        notesDir.mkdir()

        for (filename in expectedFiles) {
            File(notesDir, "$filename.$MUSIC_XML_EXTENSION").createNewFile()
        }

        File(notesDir, invalidFileName).createNewFile()

        // refresh the activity
        composeTestRule.activity.finish()
        composeTestRule.activity.startActivity(composeTestRule.activity.intent)
    }

    @Test
    fun testNumberOfComponentMatchNumberOfFile() {

        for (file in notesDir.listFiles()!!) {
            println(file.name)
        }

        for (title in expectedFiles) {
            composeTestRule.onNodeWithText(title.toString()).assertExists()
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