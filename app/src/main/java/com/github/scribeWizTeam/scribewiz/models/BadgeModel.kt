package com.github.scribeWizTeam.scribewiz.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.util.*

data class BadgeModel(override var id: String? = "",
                      var badgeName: String? = "",
                      var challengeID: String? = "",
                      var dateObtained: String? = "",
                      var photoID: String? = ""
                      )
: Model{

    companion object Controller {
        const val COLLECTION = "Badges"

        fun getAllBadgesFromUser(user: UserModel) : MutableSet<BadgeModel> {
            val db = Firebase.firestore

            val badges : MutableSet<BadgeModel> = mutableSetOf()

            runBlocking {
                val job = launch {
                    for (id in user.badges!!) {
                        db.collection("Users")
                            .document(user.id!!)
                            .collection(COLLECTION)
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

        fun addBadgeToUser(user: UserModel){
            val time = Calendar.getInstance().time
            val formatter = DateFormat.getDateTimeInstance()
            val badgeData = BadgeModel(
                "123",
                "Test Badge",
                "abc",
                formatter.format(time),
                ""
            )

            val userDoc : DocumentReference =
                Firebase.firestore
                .collection("Users")
                .document(user.id!!)

            // Add badge in badge collection
            userDoc.collection("Badges")
                .document(badgeData.id!!)
                .set(badgeData)

            // Add badge in badge list
            user.badges!!.add(badgeData.id.toString())

            // Update user and badge in DB
            user.updateInDB()
            badgeData.updateInDB()

        }
    }


    override fun collectionName(): String {
        return COLLECTION
    }


}
