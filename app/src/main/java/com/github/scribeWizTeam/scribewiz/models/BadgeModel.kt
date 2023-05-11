package com.github.scribeWizTeam.scribewiz.models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class BadgeModel(override var id: String? = "",
                      var challengeID: String? = "",
                      var dateObtained: String? = "",
                      var photoID: String? = ""
                      )
: Model{

    companion object Controller {
        const val COLLECTION = "Badges"

        fun getAllBadgesFromUser(user: UserModel) : Set<BadgeModel> {
            val db = Firebase.firestore

            val badges : MutableSet<BadgeModel> = mutableSetOf()

            runBlocking {
                val job = launch {
                    for (id in user.badges!!) {
                        db.collection(COLLECTION)
                            .document(id)
                            .get()
                            .await()
                            .toObject<BadgeModel>()?.let {
                                badges.add(it)
                            }
                    }
                }
                job.join()
            }
            return badges
        }
    }


    override fun collectionName(): String {
        return COLLECTION
    }


}
