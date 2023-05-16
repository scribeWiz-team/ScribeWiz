package com.github.scribeWizTeam.scribewiz.models

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*

data class ChallengeSubmissionModel (
    override val id: String = "",
    val date: Date? = Date(),
    val recordId : String? = "",
    val challengeId : String? = "",
    val userId : String? = "",
    val upVote : Long? = 0,
) : Model {

    companion object Controller {
        fun getAll(challengeId: String): List<ChallengeSubmissionModel> {
            val submissionsList : MutableList<ChallengeSubmissionModel> = mutableListOf()

            runBlocking {
                val job = launch {
                    Firebase.firestore
                        .collection(ChallengeModel.COLLECTION)
                        .document(challengeId)
                        .collection(ChallengeModel.SUBMISSION_COLLECTION)
                        .get()
                        .addOnSuccessListener {
                            for (submission in it) {
                                submissionsList.add(submission.toObject())
                            }
                        }
                        .await()
                }
                job.join()
            }

            return submissionsList
        }
    }

    fun submission(challengeId: String, submissionId : String) : Result<ChallengeSubmissionModel> {
        var submission : ChallengeSubmissionModel? = null

        runBlocking {
            val job = launch {
                submission = Firebase.firestore
                    .collection(ChallengeModel.COLLECTION)
                    .document(challengeId)
                    .collection(ChallengeModel.SUBMISSION_COLLECTION)
                    .document(submissionId)
                    .get()
                    .await()
                    .toObject()
            }
            job.join()
        }

        return if (submission == null) {
            Result.failure(Exception("No challenge with id $challengeId"))
        } else {
            Result.success(submission!!)
        }
    }


    override fun updateInDB() {
        if (challengeId != null) {
            Firebase.firestore
                .collection(ChallengeModel.COLLECTION)
                .document(challengeId)
                .collection(collectionName())
                .document(id)
                .set(this)
                .addOnSuccessListener {
                    Log.d("SETTINGUPDB", "data added with id $id")
                }
                .addOnFailureListener { e ->
                    Log.w("SETTINGUPDB", "Error adding data", e)
                }
        }
    }

    override fun collectionName(): String {
        return ChallengeModel.SUBMISSION_COLLECTION
    }
}
