package com.github.scribeWizTeam.scribewiz.models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class MusicNoteModel (
    override var id: String = "",
    var name: String = "new note",
) : Model {

    companion object Controller {
        const val COLLECTION = "MusicNotes"

        private const val NOTE_ID = "id"
        private const val NOTE_NAME = "name"

        fun getAllNotesFromUser(user: UserModel) : Set<MusicNoteModel> {
            val db = Firebase.firestore

            val notes : MutableSet<MusicNoteModel> = mutableSetOf()

            runBlocking {
                val job = launch {
                    for (id in user.musicNoteList) {
                        db.collection(COLLECTION)
                            .document(id)
                            .get()
                            .await()
                            .toObject<MusicNoteModel>()?.let {
                                notes.add(it)
                            }
                    }
                }
                job.join()
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