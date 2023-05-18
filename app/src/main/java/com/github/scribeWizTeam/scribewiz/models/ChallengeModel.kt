package com.github.scribeWizTeam.scribewiz.models

import java.time.LocalDateTime
import java.util.Date

data class ChallengeModel(
    override val id: String = "",
    val name: String = "",
    val dateBeginning: LocalDateTime?,
    val dateEnd: LocalDateTime?,
    val description: String? = "",
    val badge: String? = ""
) : Model {

    companion object Controller {
        const val COLLECTION = "Challenges"
        const val SUBMISSION_COLLECTION = "Submissions"


        val challengeTest1: ChallengeModel =
            ChallengeModel(
                "1", "test1",
                LocalDateTime.of(2023, 5, 15, 12, 0),
                LocalDateTime.of(2023, 5, 15, 12, 0),
                "This is a description",
                "This is a badge"
            )

        val challengeTest2: ChallengeModel =
            ChallengeModel(
                "2", "test2",
                LocalDateTime.of(2023, 5, 15, 12, 0),
                LocalDateTime.of(2023, 5, 15, 12, 0),
                "This is a description",
                "This is a badge"
            )

        fun challengesAvailableTest(): List<ChallengeModel> {
            return listOf(challengeTest1, challengeTest2)
        }

        fun challenge(challengeId: String): ChallengeModel {
            TODO("Not yet implemented")
        }

        fun latestChallenge(): ChallengeModel {
            TODO("Not yet implemented")
        }

        fun challengesAvailable(): List<ChallengeModel> {
            return listOf(challengeTest1, challengeTest2)
        }


        fun allSubmissions(): List<ChallengeSubmissionModel> {
            TODO("Not yet implemented")
        }

        fun winningSubmission(): ChallengeSubmissionModel {
            TODO("Not yet implemented")
        }


    }

    fun addSubmission(recordId: String, userId: String) {
        //do something
    }

    override fun getMapping(): HashMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun getCollectionName(): String {
        return COLLECTION
    }

}