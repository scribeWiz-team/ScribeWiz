package com.github.scribeWizTeam.scribewiz;

import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import com.github.scribeWizTeam.scribewiz.models.ChallengeModel.Controller.challenge
import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Test
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChallengeModelTest {


    @Test
    fun fillDB(){
        val challenge1 = ChallengeModel(
            id = "mock-challenge-id",
            name = "Halloween theme",
            startDate = Date(),
            endDate = Date(Date().time + 2629800000),
            description = "Compose your best horror movie musique theme !",
            badge = "horror composer"
        )

        val challenge2 = ChallengeModel(
            id = "mock-challenge-id2",
            name = "Piano composition",
            startDate = Date(),
            endDate = Date(Date().time + 2629800000),
            description = "Create a composition that could be played at piano",
            badge = "The Pianist"
        )

        val note1 = MusicNoteModel(
            id = "mock-note-id1",
            name = "Tom note 1"
        )
        val note2 = MusicNoteModel(
            id = "mock-note-id2",
            name = "Juliette note 1"
        )
        val note3 = MusicNoteModel(
            id = "mock-note-id3",
            name = "Sam note 1"
        )
        val note4 = MusicNoteModel(
            id = "mock-note-id4",
            name = "Sam note 1"
        )

        val user1 = UserModel(
            id = "mock-user-id1",
            userName = "Tom",
            musicNotes = mutableListOf(note1.id)
        )
        val user2 = UserModel(
            id = "mock-user-id2",
            userName = "Juliette",
            musicNotes = mutableListOf(note2.id),
            friends = mutableListOf(user1.id)
        )
        val user3 = UserModel(
            id = "mock-user-id3",
            userName = "Sam",
            musicNotes = mutableListOf(note3.id, note4.id),
            friends = mutableListOf(user1.id, user2.id)
        )

        runBlocking {
            user1.updateInDB().await()
            user2.updateInDB().await()
            user3.updateInDB().await()
            note1.updateInDB().await()
            note2.updateInDB().await()
            note3.updateInDB().await()
            note4.updateInDB().await()
            challenge1.updateInDB().await()
            challenge1.addSubmission(note1.id, user1.id).await()
            challenge1.addSubmission(note2.id, user2.id).await()
            challenge2.updateInDB().await()
            challenge2.addSubmission(note3.id, user3.id).await()
        }
    }

    @Test
    fun putAndRetrieveChallengeInDBSucceed(){
        val id = "test-id"
        val challenge = ChallengeModel(id)

        runBlocking {
            challenge.updateInDB().await()

            challenge(id).onSuccess {
                assertEquals(id, it.id)
                challenge.delete().await()
            }.onFailure {
                throw Exception("test challenge not recovered from db")
            }
        }
    }


    @Test
    fun latestChallengeRetrieveTheGoodOne() {

        val todayDate = Date()

        val challenge1 = ChallengeModel("noTheLatest", startDate = Date(0))
        val challenge2 = ChallengeModel("latest", startDate =  todayDate)

        runBlocking {
            challenge1.updateInDB().await()
            challenge2.updateInDB().await()
        }

        val ret = ChallengeModel.latestChallenge()
        assertTrue(ret.isSuccess)
        ret.onSuccess {
            assertEquals("latest", it.id)
            assertEquals(todayDate, it.startDate)
        }

        runBlocking {
            challenge1.delete()
            challenge2.delete()
        }
    }

    @Test
    fun challengesAvailableRetrieveTheGoodOnes() {

        val endDate = Date(Date().time + 10_000)

        val challenge1 = ChallengeModel("first-test", endDate = endDate)
        val challenge2 = ChallengeModel("second-test", endDate =  endDate)

        runBlocking {
            challenge1.updateInDB().await()
            challenge2.updateInDB().await()
        }

        val ret = ChallengeModel.challengesAvailable().map { it.id }
        assertContains(ret, "first-test")
        assertContains(ret, "second-test")

        runBlocking {
            challenge1.delete()
            challenge2.delete()
        }
    }
}

