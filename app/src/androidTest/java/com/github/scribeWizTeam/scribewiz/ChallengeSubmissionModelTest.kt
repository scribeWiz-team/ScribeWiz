package com.github.scribeWizTeam.scribewiz

import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import com.github.scribeWizTeam.scribewiz.models.ChallengeSubmissionModel
import com.github.scribeWizTeam.scribewiz.models.ChallengeSubmissionModel.Controller.getSubmissionId
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains

class ChallengeSubmissionModelTest {

    private val challengeId = "test-id-challenge"
    private val challenge = ChallengeModel(challengeId)

    @Before
    fun createChallenge() {
        runBlocking {
            challenge.updateInDB().await()
        }
    }

    @After
    fun deleteChallenge() {
        runBlocking {
            challenge.delete().await()
        }
    }

    @Test
    fun putAndRetrieveChallengeSubmissionInDBSucceed(){
        val userId = "test-user-id"
        val recordId = "test-record-id"

        runBlocking {
            challenge.addSubmission(recordId, userId).await()
        }

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId, recordId))
            .onSuccess {
                assertEquals(userId, it.userId)
                assertEquals(recordId, it.recordId)
            }.onFailure {
                throw Exception("no matching submission found in db")
            }
    }

    @Test
    fun retrieveAllChallengeSubmissionsInDBSucceed(){
        val userId1 = "test-user-id1"
        val recordId1 = "test-record-id1"
        val userId2 = "test-user-id2"
        val recordId2 = "test-record-id2"

        runBlocking {
            challenge.addSubmission(recordId1, userId1).await()
            challenge.addSubmission(recordId2, userId2).await()
        }

        val submissions = challenge.allSubmissions()

        assertEquals(2, submissions.size)

        assertContains(submissions.map { it.recordId }, recordId1, recordId2)
        assertContains(submissions.map { it.userId }, userId1, userId2)
    }

    @Test
    fun upVoteSubmissionIncreaseUpVote() {
        val userId = "test-user-id"
        val recordId = "test-record-id"
        challenge.addSubmission(recordId, userId)

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId, recordId)).onSuccess {
            it.upVote(userId)
            assertEquals(1, it.upVote)
        }.onFailure {
            throw it
        }
    }

    @Test
    fun upVoteSubmissionIncreaseUpVoteOnlyOnce() {
        val userId = "test-user-id"
        val recordId = "test-record-id"
        challenge.addSubmission(recordId, userId)

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId, recordId)).onSuccess {
            it.upVote(userId)
            assertEquals(1, it.upVote)
            it.upVote(userId)
            assertEquals(1, it.upVote)
        }.onFailure {
            throw it
        }
    }

    @Test
    fun upVoteSubmissionIncreaseOnceForManyUser() {
        val userId1 = "test-user-id1"
        val recordId1 = "test-record-id1"
        val userId2 = "test-user-id2"

        challenge.addSubmission(recordId1, userId1)

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId1, recordId1)).onSuccess {
            it.upVote(userId1)
            assertEquals(1, it.upVote)
            it.upVote(userId2)
            assertEquals(2, it.upVote)
        }.onFailure {
            throw it
        }
    }

    @Test
    fun downVoteSubmissionNotUnderZeroUpVote() {
        val userId = "test-user-id"
        val recordId = "test-record-id"
        challenge.addSubmission(recordId, userId)

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId, recordId)).onSuccess {
            it.votersUser.add(userId)
            it.downVote(userId)
            assertEquals(0, it.upVote)
        }.onFailure {
            throw it
        }
    }

    @Test
    fun downVoteSubmissionDecreaseUpVoteCorrectly() {
        val userId = "test-user-id"
        val recordId = "test-record-id"
        challenge.addSubmission(recordId, userId)

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId, recordId)).onSuccess {
            it.upVote(userId)
            it.downVote(userId)
            assertEquals(0, it.upVote)
        }.onFailure {
            throw it
        }
    }

    @Test
    fun cannotDownVoteTwice() {
        val userId = "test-user-id"
        val recordId = "test-record-id"
        challenge.addSubmission(recordId, userId)

        ChallengeSubmissionModel.submission(challengeId, getSubmissionId(userId, recordId)).onSuccess {
            it.upVote = 1
            it.upVote(userId)
            it.downVote(userId)
            it.downVote(userId)
            assertEquals(1, it.upVote)
        }.onFailure {
            throw it
        }
    }
}
