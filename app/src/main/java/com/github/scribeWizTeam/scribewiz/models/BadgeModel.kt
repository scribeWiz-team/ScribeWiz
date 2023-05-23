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

enum class BadgeRanks {
    GOLD,
    SILVER,
    BRONZE
}

data class BadgeModel(
    override var id: String = "",
    var badgeName: String? = "",
    var challengeID: String? = "",
    var dateObtained: String? = "",
    var photoID: String? = "",
    var rank: Int? = 0
) : Model {

    companion object Controller {
        const val COLLECTION = "Badges"

        /**
         * Returns a set containing the badges belonging to the user.
         * To get the user, LocalContext.current can be used
         */
        fun getAllBadgesFromUser(user: UserModel): MutableSet<BadgeModel> {
            val db = Firebase.firestore

            val badges: MutableSet<BadgeModel> = mutableSetOf()

            runBlocking {
                val job = launch {
                    for (id in user.badges!!) {
                        db.collection(UserModel.COLLECTION)
                            .document(user.id)
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

        /**
         * Adds the given badge to the provided user.
         * If no badge is provided, a default test badge will be added
         * To get the user, LocalContext.current can be used
         */
        fun addBadgeToUser(user: UserModel, badge: BadgeModel?) {
            val time = Calendar.getInstance().time
            val formatter = DateFormat.getDateInstance()

            var badgeData = BadgeModel(
                "789",
                "Test Badge",
                "abc",
                formatter.format(time),
                "",
                BadgeRanks.GOLD.ordinal
            )
            if (badge != null) {
                badgeData = badge
                badgeData.dateObtained = formatter.format(time)
            }

            val userDoc: DocumentReference =
                Firebase.firestore
                    .collection(UserModel.COLLECTION)
                    .document(user.id)

            // Add badge in badge collection
            userDoc.collection(COLLECTION)
                .document(badgeData.id)
                .set(badgeData)

            // Add badge in badge list
            if (!user.badges!!.contains(badgeData.id)) {
                user.badges!!.add(badgeData.id)
            }


            // Update user and badge in DB
            user.updateInDB()
            badgeData.updateInDB()

        }
    }

    override fun collectionName(): String {
        return COLLECTION
    }
}
