package com.github.scribeWizTeam.scribewiz.models

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface Model {
    val id: String

    /**
     * Updates the challenge submission in the database.
     *
     * @param onResultListener The listener to handle the result of the update operation.
     * @return A Task that can be used to track the completion of the update operation.
     */
    fun updateInDB(
        onResultListener: ResultListener = object : ResultListener {
            override fun onSuccess() {
                Log.d("SETTINGUPDB", "data added")
            }

            override fun onError(error: Throwable) {
                Log.w("SETTINGUPDB", "Error adding data", error)
            }
        }
    ): Task<Void> {
        return Firebase.firestore
            .collection(collectionName()).document(id).set(this)
            .addOnSuccessListener {
                onResultListener.onSuccess()
            }
            .addOnFailureListener { e ->
                onResultListener.onError(e)
            }

    }

    /**
     * Deletes the challenge submission from the database.
     *
     * @return A Task that can be used to track the completion of the delete operation.
     */
    fun delete(): Task<Void> {
        return Firebase.firestore.collection(collectionName()).document(id).delete()
    }

    fun collectionName(): String
}