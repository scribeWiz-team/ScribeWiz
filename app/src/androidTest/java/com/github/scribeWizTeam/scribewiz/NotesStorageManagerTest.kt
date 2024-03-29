package com.github.scribeWizTeam.scribewiz

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
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

    private val notesFolder: File = createTempDirectory("test").toFile()

    private val expectedFiles = ('a'..'z').map { a -> a.toString() }

    private val invalidFileName = "NOT_A_VALID_FILE"

    private val newNoteName = "new_note"
    private val expectedNewNoteFileName = "$newNoteName.$MUSIC_XML_EXTENSION"

    private var notesStorageManager = NotesStorageManager(notesFolder)

    @Before
    fun initialize() {

        val listFile = notesFolder.listFiles() ?: emptyArray<File>()

        for (file in listFile) {
            file.delete()
        }

        for (name in expectedFiles) {
            File(notesFolder, "$name.$MUSIC_XML_EXTENSION").createNewFile()
        }

        File(notesFolder, "$invalidFileName.$MUSIC_XML_EXTENSION")
    }

    @Test
    fun testManagerReturnAllNotesNames() {
        assertArrayEquals(
            expectedFiles.toTypedArray(),
            notesStorageManager.getNotesNames().toTypedArray()
        )
    }

    @Test
    fun testManagerNotesNamesReturnsEmptyListWhenNoFile() {
        notesStorageManager.clearFolder()
        assertArrayEquals(emptyArray(), notesStorageManager.getNotesNames().toTypedArray())
    }

    @Test
    fun deleteNoteDeleteTheFile() {
        notesStorageManager.deleteNote("a")
        assertArrayEquals(
            expectedFiles.filter { s -> s != "a" }.toTypedArray(),
            notesStorageManager.getNotesNames().toTypedArray()
        )
    }

    @Test
    fun onlyMusicXMLFiles() {
        assertFalse(notesStorageManager.getNotesNames().contains(invalidFileName))
    }

    @Test
    fun writeNewNote() {
        val content = "<dummy content>"
        val expectedFile = notesFolder.resolve(expectedNewNoteFileName)
        assertFalse(expectedFile.exists())
        notesStorageManager.writeNoteFile(newNoteName, content)
        assertTrue(expectedFile.exists())
    }

    @Test
    fun clearFolderDeleteTheNotesFolder() {
        notesStorageManager.clearFolder()
        assertFalse(notesFolder.exists())
    }

    @Test
    fun getAllFile() {
        assertEquals(expectedFiles.size, notesStorageManager.getAllNotesFiles()?.size ?: 0)
    }
    @Test
    fun getFileRetrieveCorrectFile() {

        for (name in expectedFiles) {
            assertEquals(
                "$name.$MUSIC_XML_EXTENSION",
                notesStorageManager.getNoteFile(name)?.name ?: ""
            )
        }
    }

    @Test
    fun testRenameFile() {
        val oldName = "a"
        val newName = "renamed"
        assertTrue(notesStorageManager.renameFile(oldName, newName))
        assertNotNull(notesStorageManager.getNoteFile(newName))
        assertNull(notesStorageManager.getNoteFile(oldName))
    }

    @Test(expected = Exception::class)
    fun testRenameNonExistentFile() {
        val oldName = "nonExistentFile"
        val newName = "renamed"
        notesStorageManager.renameFile(oldName, newName)
    }

    @Test
    fun testUploadAndDownloadFile() {
        val musicNoteModel = MusicNoteModel("id1", "a")
        notesStorageManager.uploadFileToDatabase(musicNoteModel)

        val downloadedFileName = "${musicNoteModel.name}_DOWNLOADED"
        notesStorageManager.downloadFileFromDatabase(musicNoteModel.id)

        val downloadedFile = notesStorageManager.getNoteFile(downloadedFileName)
        assertNotNull(downloadedFile)
    }

    @Test
    fun testGetFileName() {
        val musicNoteModel = MusicNoteModel("id1", "a")
        notesStorageManager.uploadFileToDatabase(musicNoteModel)
        val fileName = notesStorageManager.getFileName(musicNoteModel.id)
        assertEquals(musicNoteModel.name, fileName)
    }

    @After
    fun clearTempDir() {
        notesFolder.deleteRecursively()
    }
}
