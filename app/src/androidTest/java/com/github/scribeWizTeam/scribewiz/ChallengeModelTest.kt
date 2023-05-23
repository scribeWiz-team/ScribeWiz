package com.github.scribeWizTeam.scribewiz;

import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import com.github.scribeWizTeam.scribewiz.models.ChallengeModel.Controller.challenge
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Test
import java.util.Date
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChallengeModelTest {

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

