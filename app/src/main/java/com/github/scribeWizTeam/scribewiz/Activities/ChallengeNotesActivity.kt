package com.github.scribeWizTeam.scribewiz.Activities;

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.scribeWizTeam.scribewiz.models.ChallengeSubmissionModel

class ChallengeNotesActivity : AppCompatActivity() {

    private lateinit var challengeId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract the challenge ID and user ID from the Intent that started this activity
        challengeId = intent.getStringExtra("challengeId")!!
        userId = intent.getStringExtra("userId")!!

        setContent {
            // Define a state variable for holding the list of submissions.
            val submissions = remember { mutableStateListOf<ChallengeSubmissionModel>() }

            // Launch a coroutine which fetches all submissions for the challenge and adds them to the list
            LaunchedEffect(challengeId) {
                submissions.addAll(ChallengeSubmissionModel.getAll(challengeId))
            }

            // Define the UI of the activity using Compose
            Column(modifier = Modifier.padding(16.dp)) {
                // For each submission, create a text and three buttons
                for (submission in submissions) {
                    // Display the user ID of the submission
                    Text(text = "Submission by ${submission.userId}")
                    // A button for playing the submission
                    Button(onClick = {
                        //TODO play submission
                    }) {
                        Text("Play")
                    }
                    // A button for upvoting the submission
                    Button(onClick = {
                        //TODO upvote submission
                    }) {
                        Text("Upvote")
                    }
                    // A button for downvoting the submission
                    Button(onClick = {
                        //TODO downvote submission
                    }) {
                        Text("Downvote")
                    }
                }
            }
        }
    }
}
