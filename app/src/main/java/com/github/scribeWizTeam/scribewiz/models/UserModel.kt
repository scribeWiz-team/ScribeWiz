package com.github.scribeWizTeam.scribewiz.models

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class UserModel (
    override var id: String = "",
    var userName: String? = "null",
    var userNumRecordings: Int = 0,
    var friendsList: MutableSet<String> = mutableSetOf(),
    var musicNoteList: MutableSet<String> = mutableSetOf()
) : Model {
    companion object Controller {
        const val COLLECTION = "Users"

        private const val USER_ID = "userId"
        private const val USER_NAME = "userName"
        private const val USER_NUM_NOTES = "userNumRecordings"
        private const val FRIEND_LIST = "friendsList"
        private const val NOTES_LIST = "musicNoteList"

        fun getCurrentUser(context: Context) : UserModel {
            val reader = context.getSharedPreferences(
                "LOGGED_USER", Context.MODE_PRIVATE
            )

            val userId = reader.getString(USER_ID, "") ?: throw Exception("Invalid user registered")
            if (userId.isEmpty()) throw Exception("Invalid user registered")

            return UserModel(
                userId,
                reader.getString(USER_NAME, ""),
                reader.getInt(USER_NUM_NOTES, 0),
                reader.getStringSet(FRIEND_LIST, mutableSetOf()) ?: mutableSetOf(),
                reader.getStringSet(NOTES_LIST, mutableSetOf()) ?: mutableSetOf()
            )
        }

        fun getUser(userId: String) : UserModel {
            val db = Firebase.firestore

            var user = UserModel(userId)

            runBlocking {
                val job = launch {
                    user = db.collection(MusicNoteModel.COLLECTION)
                        .document(userId)
                        .get()
                        .await()
                        .toObject<UserModel>() ?: UserModel(userId)
                }
                job.join()
            }

            return user
        }
    }

    override fun getMapping(): HashMap<String, Any?> {
        return hashMapOf(
            USER_ID to id,
            USER_NAME to userName,
            USER_NUM_NOTES to userNumRecordings,
            FRIEND_LIST to friendsList.toString(),
            NOTES_LIST to musicNoteList.toString()
        )
    }

    fun registerAsCurrentUser(context: Context) {
        val editor = context.getSharedPreferences(
            "LOGGED_USER", Context.MODE_PRIVATE
        ).edit()

        editor.putString(USER_ID, id)
        editor.putString(USER_NAME, userName)
        editor.putInt(USER_NUM_NOTES, userNumRecordings)
        editor.putStringSet(FRIEND_LIST, friendsList)
        editor.putStringSet(NOTES_LIST, musicNoteList)
        editor.apply()
    }

    override fun getCollectionName(): String {
        return COLLECTION
    }
}