package com.github.scribeWizTeam.scribewiz.models

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class UserModel(
    override var id: String = Firebase.firestore.collection(COLLECTION).document().id,
    var userName: String? = "Guest",
    var userNumRecordings: Long? = 0,
    var friends: MutableList<String>? = mutableListOf(),
    var musicNotes: MutableList<String>? = mutableListOf(),
    var badges: MutableList<String>? = mutableListOf()

) : Model {
    companion object Controller {
        const val COLLECTION = "Users"
        private const val LOGGED_USER = "LOGGED_USER"

        private const val USER_ID = "id"

        /**
         * Retrieves the current user from the shared preferences.
         *
         * @param context The context.
         * @return The current user.
         */
        fun currentUser(context: Context): Result<UserModel> {
            val reader = context.getSharedPreferences(LOGGED_USER, Context.MODE_PRIVATE)

            val userId = reader.getString(USER_ID, "")
            if (userId == null || userId.isEmpty()) return Result.failure(Exception("Invalid user registered"))

            return user(userId)
        }

        /**
         * Retrieves a user with the specified user ID from Firestore.
         *
         * @param userId The ID of the user.
         * @return The user with the specified ID.
         */
        fun user(userId: String): Result<UserModel> {
            var user: UserModel? = null

            runBlocking {
                val job = launch {
                    user = Firebase.firestore
                        .collection(COLLECTION)
                        .document(userId)
                        .get()
                        .await()
                        .toObject()
                }
                job.join()
            }

            return if (user == null) {
                Result.failure(Exception("No user with id $userId"))
            } else {
                Result.success(user!!)
            }
        }
    }

    /**
     * Registers the current user in the shared preferences of the specified context.
     *
     * @param context The context in which to register the current user.
     */
    fun registerAsCurrentUser(context: Context) {
        val editor = context.getSharedPreferences(
            LOGGED_USER, Context.MODE_PRIVATE
        ).edit()
        editor.putString(USER_ID, id)
        editor.apply()
    }

    /**
     * Unregisters the current user from the shared preferences of the specified context.
     *
     * @param context The context from which to unregister the current user.
     */
    fun unregisterAsCurrentUser(context: Context) {
        val editor = context.getSharedPreferences(
            LOGGED_USER, Context.MODE_PRIVATE
        ).edit()
        editor.remove(USER_ID)
        editor.apply()
    }

    /**
     * Returns the name of the collection in the Firebase Firestore database.
     *
     * @return The name of the collection.
     */
    override fun collectionName(): String {
        return COLLECTION
    }
}