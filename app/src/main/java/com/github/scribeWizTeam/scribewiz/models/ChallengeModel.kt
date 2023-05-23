package com.github.scribeWizTeam.scribewiz.models

import java.util.Date
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class ChallengeModel(
    override val id: String = Firebase.firestore.collection(COLLECTION).document().id,
    val name: String? = "",
    val startDate: Date? = Date(0),
    val endDate: Date? = Date(0),
    val description: String? = "",
    val badge: String? = ""
) : Model {

    companion object Controller {
        const val COLLECTION = "Challenges"
        const val SUBMISSION_COLLECTION = "Submissions"


        val challengeTest1: ChallengeModel =
            ChallengeModel(
                "1", "test1",
                Date(0),
                Date(0),
                "This is a description",
                "This is a badge"
            )

        val challengeTest2: ChallengeModel =
            ChallengeModel(
                "2", "test2",
                Date(0),
                Date(0),
                "This is a description",
                "This is a badge"
            )

        /**
         * Returns a list of available challenges.
         *
         * @return The list of available challenges.
         */
        fun challengesAvailableTest(): List<ChallengeModel> {
            return listOf(challengeTest1, challengeTest2)
        }

        /**
         * Retrieves a challenge by its ID.
         *
         * @param challengeId The ID of the challenge.
         * @return The result containing the challenge if it exists, or a failure with an exception.
         */
        fun challenge(challengeId: String): Result<ChallengeModel> {
            var challenge: ChallengeModel? = null

            runBlocking {
                val job = launch {
                    challenge = Firebase.firestore.collection(COLLECTION)
                        .document(challengeId)
                        .get()
                        .await()
                        .toObject()
                }
                job.join()
            }

            return if (challenge == null) {
                Result.failure(Exception("No challenge with id $challengeId"))
            } else {
                Result.success(challenge!!)
            }
        }

        /**
         * Retrieves a list of available challenges.
         *
         * @return The list of available challenges.
         */
        fun challengesAvailable(): List<ChallengeModel> {
            val challengesList: MutableList<ChallengeModel> = mutableListOf()

            runBlocking {
                val job = launch {
                    Firebase.firestore
                        .collection(COLLECTION)
                        .whereGreaterThan("endDate", Date())
                        .get()
                        .await()
                        .forEach {
                            challengesList.add(it.toObject())
                        }
                }
                job.join()
            }

            return challengesList
        }

        /**
         * Retrieves the latest challenge.
         *
         * @return The result containing the latest challenge if it exists, or a failure with an exception.
         */
        fun latestChallenge(): Result<ChallengeModel> {
            var challenge: ChallengeModel? = null

            runBlocking {
                val job = launch {
                    challenge = Firebase.firestore.collection(COLLECTION)
                        .orderBy("startDate", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .documents
                        .first()
                        .toObject()
                }
                job.join()
            }

            return if (challenge == null) {
                Result.failure(Exception("No challenge found"))
            } else {
                Result.success(challenge!!)
            }
        }
    }

    /**
     * Adds a submission to the challenge.
     *
     * @param recordId  The ID of the record.
     * @param userId    The ID of the user.
     * @return The task representing the submission update in the database.
     */
    fun addSubmission(recordId: String, userId: String): Task<Void> {
        return ChallengeSubmissionModel(
            recordId = recordId,
            userId = userId,
            challengeId = id
        ).updateInDB()
    }

    /**
     * Retrieves all submissions for the challenge.
     *
     * @return The list of challenge submissions.
     */
    fun allSubmissions(): List<ChallengeSubmissionModel> {
        return ChallengeSubmissionModel.getAll(id)
    }

    /**
     * Retrieves the winning submission for the challenge.
     *
     * @return The result containing the winning submission if it exists, or a failure with an exception.
     */
    fun winningSubmission(): Result<ChallengeSubmissionModel> {
        val submissions = ChallengeSubmissionModel.getAll(id)
        return if (submissions.isEmpty()) {
            Result.failure(Exception("No submission yet"))
        } else {
            Result.success(submissions.maxByOrNull { it.upVote ?: 0 }!!)
        }
    }

    /**
     * Returns the name of the collection.
     *
     * @return The name of the collection.
     */
    override fun collectionName(): String {
        return COLLECTION
    }

    /**
     * Deletes the challenge and its associated submissions.
     *
     * @return The task representing the deletion.
     */
    override fun delete(): Task<Void> {
        Firebase.firestore
            .collection(collectionName())
            .document(id)
            .collection(SUBMISSION_COLLECTION)
            .get()
            .addOnSuccessListener {
                for (sub in it) {
                    sub.reference.delete()
                }
            }
        return super.delete()
    }
}