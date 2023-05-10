package com.github.scribeWizTeam.scribewiz.models

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface Model {
    val id: String?

//    fun getMapping(): HashMap<String, Any?>

    fun updateInDB() {
        id?.let {
            Firebase.firestore
                .collection(collectionName()).document(it).set(this)
                .addOnSuccessListener {
                    Log.d("SETTINGUPDB", "data added")
                }
                .addOnFailureListener { e ->
                    Log.w("SETTINGUPDB", "Error adding data", e)
                }
        }
    }

    fun collectionName(): String
}