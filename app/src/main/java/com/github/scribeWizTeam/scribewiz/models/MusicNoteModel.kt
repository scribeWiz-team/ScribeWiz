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

        fun musicNote(id: String) : Result<MusicNoteModel> {
            var musicNote : MusicNoteModel? = null

            runBlocking {
                val job = launch {
                    musicNote = Firebase.firestore
                        .collection(COLLECTION)
                        .document(id)
                        .get()
                        .await()
                        .toObject()
                }
                job.join()
            }

            return if (musicNote == null) {
                Result.failure(Exception("No note with id $id"))
            } else {
                Result.success(musicNote!!)
            }
        }

        fun getAllNotesFromUser(user: UserModel) : Set<MusicNoteModel> {
            val db = Firebase.firestore

            val notes : MutableSet<MusicNoteModel> = mutableSetOf()

            runBlocking {
                val job = launch {
                    for (id in user.musicNotes!!) {
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

    override fun collectionName(): String {
        return COLLECTION
    }
}