package com.github.scribeWizTeam.scribewiz.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.data.model.User
import com.github.scribeWizTeam.scribewiz.Fragments.ChallengesFragment
import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class ParticipateInChallengeActivity : AppCompatActivity() {

    companion object {
        var isTest: Boolean = false //Test mode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var musicName: String = ""

        if (!isTest) {
            val extras = intent.extras
                ?: throw Exception("Exception : No parameters passed to the activity by the intent")
            musicName = extras.getString("musicName")
                ?: throw Exception("Exception : No musicName parameter passed to the activity")
        } else {
            musicName = "Test"
        }

        setContent {
            val challenges = remember { ChallengeModel.challengesAvailable().toMutableStateList() }

            if (isTest) {
                ChallengeListParticipateIn(
                    challenges = ChallengeModel.challengesAvailableTest(),
                    context = this,
                    musicName = musicName
                )
            } else {
                ChallengeListParticipateIn(
                    challenges = challenges,
                    context = this,
                    musicName = musicName
                )
            }


        }
    }

    /**
     * Composable function to create a specific challenge button.
     *
     * @param challenge  The challenge model.
     * @param context    The context.
     * @param musicName  The name of the music.
     */
    @Composable
    fun specificChallengeButton(challenge: ChallengeModel, context: Context, musicName: String) {


        Button(
            onClick = {
                val currentUser = UserModel.currentUser(context = context)
                if (currentUser.isSuccess) {
                    val musicFileId: String = addLocalMusicFileMetaDataToDB(musicName)
                    challenge.addSubmission(
                        recordId = musicFileId,
                        userId = currentUser.getOrNull()!!.id,
                    )
                } else {
                    Toast.makeText(
                        context,
                        "You must be logged in to participate in a challenge",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxWidth()
            ) {
                Text(text = challenge.name ?: "No name specified")
                Text(text = niceDurationDateFormatting(challenge.startDate, challenge.endDate))
            }

        }
    }

    /**
     * Composable function to create a list of challenges for participation.
     *
     * @param challenges      The list of challenges.
     * @param challengesTest  The optional list of challenges for testing.
     * @param context         The context.
     * @param musicName       The name of the music.
     */
    @Composable
    fun ChallengeListParticipateIn(
        challenges: List<ChallengeModel>,
        challengesTest: List<ChallengeModel>? = null,
        context: Context,
        musicName: String
    ) {

        Column {
            Text(
                text = "Participate in a Challenge",
                style = MaterialTheme.typography.h4, // adjust text style as needed
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            challenges.forEach { challenge ->
                specificChallengeButton(challenge = challenge, context, musicName)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    private fun niceDurationDateFormatting(
        startingDate: Date?,
        endDate: Date?
    ): String {

        if (startingDate == null && endDate == null) {
            return "No date specified"
        }
        if (startingDate == null) {
            return "To ${dateFormatting(endDate!!)}"
        }
        if (endDate == null) {
            return "From ${dateFormatting(startingDate)}"
        }

        return "From ${dateFormatting(startingDate)} to ${dateFormatting(endDate)}"
    }

    private fun dateFormatting(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy, HH:mm")
        return formatter.format(date) ?: throw Exception("There was a problem with your date")
    }

    //It is needed here as long as I don't have the part from Stefan that upload local music files in a nice way
    private fun addLocalMusicFileMetaDataToDB(nameMusic: String): String {
        val musicModel = MusicNoteModel("dummy value", "name") /* the "dummy value" is here because with the next merge,
        it won't be needed to pass an id, it will be automatically generated. */
        musicModel.updateInDB()
        return musicModel.id
    }

}