package com.github.scribeWizTeam.scribewiz

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File
import kotlin.io.path.createTempDirectory


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NotesStorageManagerTest {

    private var rootFolder: File = createTempDirectory("test").toFile()

    private var notesFolder = File(rootFolder, NOTES_FOLDER)

    private val expectedFiles = ('a'..'z').map { a -> a.toString() }

    private val invalidFileName = "NOT_A_VALID_FILE"

    private lateinit var notesDir: File

    private var notesStorageManager = NotesStorageManager(rootFolder)

    @Before
    fun initialize() {

        notesFolder.mkdir()

        for (file in notesFolder.listFiles()!!) {
            file.delete()
        }

        for (name in expectedFiles) {
            File(notesFolder, "$name.$MUSIC_XML_EXTENSION").createNewFile()
        }

        File(notesFolder, "$invalidFileName.$MUSIC_XML_EXTENSION")
    }

    @Test
    fun testManagerReturnAllNotesNames() {
        assertArrayEquals(expectedFiles.toTypedArray(), notesStorageManager.notesNames().toTypedArray())

    }

    @Test
    fun deleteNoteDeleteTheFile() {
        notesStorageManager.deleteNote("a")
        assertArrayEquals(expectedFiles.filter { s -> s != "a" }.toTypedArray(), notesStorageManager.notesNames().toTypedArray())
    }

    @Test
    fun onlyMusicXMLFiles() {
        assertFalse(notesStorageManager.notesNames().contains(invalidFileName))
    }

    @Test
    fun clearFolderDeleteTheNotesFolder() {
        notesStorageManager.clearFolder()
        assertFalse(notesFolder.exists())
    }

    @After
    fun clearTempDir() {
        notesFolder.deleteRecursively()
    }
}