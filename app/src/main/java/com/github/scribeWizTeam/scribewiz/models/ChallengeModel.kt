package com.github.scribeWizTeam.scribewiz.models

import java.util.Date

data class ChallengeModel(
    override val id: String = "",
    val name: String? = "",
    val date: Date? = Date(),
    val description: String? = "",
    val badge: String? = ""
) : Model {

    companion object Controller {
        const val COLLECTION = "Challenges"
        const val SUBMISSION_COLLECTION = "Submissions"
    }

    fun challenge(challengeId : String) : ChallengeModel {
        TODO("Not yet implemented")
    }

    fun latestChallenge() : ChallengeModel {
        TODO("Not yet implemented")
    }

    fun allSubmissions() : List<ChallengeSubmissionModel> {
        TODO("Not yet implemented")
    }

    fun winningSubmission() : ChallengeSubmissionModel {
        TODO("Not yet implemented")
    }

    fun addSubmission() {
        TODO("Not yet implemented")
    }

    override fun getMapping(): HashMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun getCollectionName(): String {
        return COLLECTION
    }
}