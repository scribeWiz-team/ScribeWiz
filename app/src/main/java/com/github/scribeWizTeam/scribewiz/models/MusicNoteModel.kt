package com.github.scribeWizTeam.scribewiz.models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

data class MusicNoteModel (
    override var id: String,
    var name: String = "new note",
) : Model {
    companion object Controller {
        const val COLLECTION = "MusicNotes"

        private const val NOTE_ID = "noteId"
        private const val NOTE_NAME = "noteName"

        fun getAllNotesFromUser(user: UserModel) : Set<MusicNoteModel> {
            val db = Firebase.firestore

            val notes : MutableSet<MusicNoteModel> = mutableSetOf()

            for (id in user.musicNoteList) {
                 db.collection(COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val ret = documentSnapshot.toObject<MusicNoteModel>()
                        if (ret != null) notes.add(ret)
                    }
            }
            return notes
        }
    }

    override fun getMapping(): HashMap<String, Any?> {
        return hashMapOf(
            NOTE_ID to id,
            NOTE_NAME to name
        )
    }

    override fun getCollectionName(): String {
        return COLLECTION
    }
}