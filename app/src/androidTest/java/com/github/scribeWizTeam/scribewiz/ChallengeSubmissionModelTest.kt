package com.github.scribeWizTeam.scribewiz

import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

class ChallengeSubmissionModelTest {
    @Test
    fun putAndRetrieveChallengeSubmissionInDBSucceed(){
        val id = "test-id-challenge"
        val userId = "test-user-id"
        val recordId = "test-record-id"
        val challenge = ChallengeModel(id)
        challenge.updateInDB()

        challenge.addSubmission(recordId, userId)

        val submissions = challenge.allSubmissions()

        assertTrue(submissions.size == 1)
        assertEquals(userId, submissions.first().userId)
        assertEquals(recordId, submissions.first().recordId)

        challenge.delete()
    }
}