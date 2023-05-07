package com.github.scribeWizTeam.scribewiz.models

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

data class ChallengeSubmissionModel (
    override val id: String = "",
    val date: Date? = Date(),
    val recordId : String? = "",
    val challengeId : String? = "",
    val userId : String? = "",
    val upVote : Int? = 0,
) : Model {

    override fun updateInDB() {
        if (challengeId != null) {
            Firebase.firestore
                .collection(getCollectionName())
                .document(challengeId)
                .collection(ChallengeModel.SUBMISSION_COLLECTION)
                .document(id)
                .set(getMapping(), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("SETTINGUPDB", "data added")
                }
                .addOnFailureListener { e ->
                    Log.w("SETTINGUPDB", "Error adding data", e)
                }
        }
    }

    override fun getMapping(): HashMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun getCollectionName(): String {
        TODO("Not yet implemented")
    }

}