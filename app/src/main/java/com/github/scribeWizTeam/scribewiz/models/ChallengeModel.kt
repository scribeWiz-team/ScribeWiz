package com.github.scribeWizTeam.scribewiz.models

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.Date

data class ChallengeModel(
    override val id: String = "",
    val name: String? = "",
    val startDate: Date? = Date(),
    val endDate: Date? = Date(),
    val description: String? = "",
    val badge: String? = ""
) : Model {

    companion object Controller {
        const val COLLECTION = "Challenges"
        const val SUBMISSION_COLLECTION = "Submissions"

        fun challenge(challengeId : String) : Result<ChallengeModel> {
            var challenge : ChallengeModel? = null

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

        fun challengesAvailable() : List<ChallengeModel> {
            val challengesList : MutableList<ChallengeModel> = mutableListOf()

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

        fun latestChallenge() : Result<ChallengeModel> {
            var challenge : ChallengeModel? = null

            runBlocking {
                val job = launch {
                    challenge = Firebase.firestore.collection(COLLECTION)
                        .orderBy("date", Query.Direction.DESCENDING)
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

    fun addSubmission(recordId: String, userId: String) {
        val id = Firebase.firestore
            .collection(COLLECTION)
            .document(id)
            .collection(
            SUBMISSION_COLLECTION).id

        ChallengeSubmissionModel(id, Date(), recordId, id, userId).updateInDB()
    }

    fun allSubmissions() : List<ChallengeSubmissionModel> {
        return ChallengeSubmissionModel.getAll(id)
    }

    fun winningSubmission() : Result<ChallengeSubmissionModel> {
        val submissions = ChallengeSubmissionModel.getAll(id)
        return if (submissions.isEmpty()) {
            Result.failure(Exception("No submission yet"))
        } else {
            Result.success(submissions.minByOrNull { it.upVote?:0 }!!)
        }
    }

    override fun collectionName(): String {
        return COLLECTION
    }
}