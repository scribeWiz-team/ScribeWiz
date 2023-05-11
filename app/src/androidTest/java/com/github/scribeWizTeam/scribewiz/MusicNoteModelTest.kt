package com.github.scribeWizTeam.scribewiz

import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

class MusicNoteModelTest {

    @Test
    fun putAndRetrieveUserInDBSucceed(){
        val id = "test-id"
        val musicNote = MusicNoteModel(id)

        musicNote.updateInDB()
        val ret = MusicNoteModel.musicNote(id)
        Assert.assertTrue(ret.isSuccess)

        ret.onSuccess {
            assertEquals(id, it.id)
        }

        musicNote.delete()
    }
}