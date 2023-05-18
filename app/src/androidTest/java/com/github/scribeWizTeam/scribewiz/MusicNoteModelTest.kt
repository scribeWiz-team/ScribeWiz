package com.github.scribeWizTeam.scribewiz

import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MusicNoteModelTest {

    @Test
    fun putAndRetrieveMusicNoteInDBSucceed(){
        val id = "test-music-note-id"
        val musicNote = MusicNoteModel(id)

        musicNote.updateInDB()
        val ret = MusicNoteModel.musicNote(id)
        Assert.assertTrue(ret.isSuccess)

        ret.onSuccess {
            assertEquals(id, it.id)
        }

        musicNote.delete()
    }

    @Test
    fun getAllNotesFromUserReturnsAllNoteModels(){
        val noteId1 = "test-music-note-id1"
        val noteId2 = "test-music-note-id2"
        val musicNote1 = MusicNoteModel(noteId1)
        val musicNote2 = MusicNoteModel(noteId2)

        val userId = "test-user-id"
        val user = UserModel(
            userId,
            "test user",
            musicNotes = mutableListOf(noteId1, noteId2)
        )

        runBlocking {
            musicNote1.updateInDB().await()
            musicNote2.updateInDB().await()
        }

        val ret = MusicNoteModel.getAllNotesFromUser(user).map { it.id }
        assertContains(ret, noteId1, noteId2)

        runBlocking {
            musicNote1.delete().await()
            musicNote2.delete().await()
        }
    }

    @Test
    fun tryToRetrieveInvalidMusicNoteFromDBFails(){
        val id = "INVALID-test-id"

        val ret = MusicNoteModel.musicNote(id)
        Assert.assertTrue(ret.isFailure)
    }
}