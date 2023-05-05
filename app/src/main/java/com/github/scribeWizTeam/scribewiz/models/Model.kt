package com.github.scribeWizTeam.scribewiz.models

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface Model {
    val id: String

    fun getMapping(): HashMap<String, Any?>

    fun updateInDB() {
        Firebase.firestore
            .collection(getCollectionName()).document(id).set(getMapping(), SetOptions.merge())
            .addOnSuccessListener {
                Log.d("SETTINGUPDB", "data added")
            }
            .addOnFailureListener { e ->
                Log.w("SETTINGUPDB", "Error adding data", e)
            }
    }

    fun getCollectionName(): String
}