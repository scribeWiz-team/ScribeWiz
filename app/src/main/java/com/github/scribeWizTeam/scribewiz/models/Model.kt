package com.github.scribeWizTeam.scribewiz.models

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface Model {
    val id: String

    fun updateInDB(onResultListener: ResultListener = object: ResultListener {
        override fun onSuccess() {
            Log.d("SETTINGUPDB", "data added")
        }
        override fun onError(error: Throwable) {
            Log.w("SETTINGUPDB", "Error adding data", error)
        }
    }): Task<Void> {
        return Firebase.firestore
            .collection(collectionName()).document(id).set(this)
            .addOnSuccessListener {
                onResultListener.onSuccess()
            }
            .addOnFailureListener { e ->
                onResultListener.onError(e)
            }

    }

    fun delete() : Task<Void> {
        return Firebase.firestore.collection(collectionName()).document(id).delete()
    }

    fun collectionName(): String
}