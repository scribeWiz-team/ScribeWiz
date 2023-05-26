package com.github.scribeWizTeam.scribewiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.scribeWizTeam.scribewiz.NotesStorageManager
import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import java.text.SimpleDateFormat
import java.util.*

class ParticipateInChallengeActivity : AppCompatActivity() {

    companion object {
        var isTest: Boolean = false //Test mode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val musicName = if (!isTest) {
            val extras = intent.extras
                ?: throw Exception("Exception : No parameters passed to the activity by the intent")
            extras.getString("musicName")
                ?: throw Exception("Exception : No musicName parameter passed to the activity")
        } else {
            "Test"
        }

        setContent {
            ScribeWizTheme {
                val challenges =
                    remember { ChallengeModel.challengesAvailable().toMutableStateList() }

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
    }


    @Composable
    fun SpecificChallengeButton(challenge: ChallengeModel, context: Context, musicName: String) {


        Button(
            onClick = {
                val currentUser = UserModel.currentUser(context = context)
                if (currentUser.isSuccess) {
                    val storageManager = NotesStorageManager(context = context)

                    val curDocID = Firebase.firestore.collection(MusicNoteModel.COLLECTION).document().id
                    val modelToUpload = MusicNoteModel(
                        curDocID,
                        name=musicName,
                    )
                    storageManager.uploadFileToDatabase(modelToUpload)
                    challenge.addSubmission(
                        recordId = curDocID,
                        userId = currentUser.getOrNull()!!.id,
                    )
                    val goHome = Intent(context, NavigationActivity::class.java)
                    startActivity(goHome)
                    Toast.makeText(
                        context,
                        "Submission successful!",
                        Toast.LENGTH_SHORT
                    ).show()
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

    @Composable
    fun ChallengeListParticipateIn(
        challenges: List<ChallengeModel>,
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
                SpecificChallengeButton(challenge = challenge, context, musicName)
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


}