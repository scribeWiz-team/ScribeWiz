package com.github.scribeWizTeam.scribewiz.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.scribeWizTeam.scribewiz.NotesStorageManager
import com.github.scribeWizTeam.scribewiz.models.ChallengeSubmissionModel
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.github.scribeWizTeam.scribewiz.models.UserModel


class ChallengeNotesActivity : AppCompatActivity() {

    private lateinit var challengeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract the challenge ID and user ID from the Intent that started this activity
        challengeId = intent.getStringExtra("challengeId")!!
        val notesStorageManager = NotesStorageManager(context = this)

        setContent {
            // Define a state variable for holding the list of submissions.
            val submissions = remember { mutableStateListOf<ChallengeSubmissionModel>() }

            ScribeWizTheme() {

                LaunchedEffect(challengeId) {
                    submissions.addAll(ChallengeSubmissionModel.getAll(challengeId))
                }
            // Get local user
            var userProfile = UserModel()
            UserModel.currentUser(this).onSuccess {
                userProfile = it
            }
            val isGuest = userProfile.userName == "Guest"

            // Launch a coroutine which fetches all submissions for the challenge and adds them to the list
            LaunchedEffect(challengeId) {
                submissions.addAll(ChallengeSubmissionModel.getAll(challengeId))
            }

            // Define the UI of the activity using Compose
            Column (modifier = Modifier.padding(16.dp)
                .verticalScroll(rememberScrollState())
            ) {
                // For each submission, create a text and three buttons
                for (submission in submissions) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        // Get the username from userId
                        var userName = "Account deleted"
                        UserModel.user(submission.userId).onSuccess {
                            userName = it.userName!!
                        }

                        // Display the submission name
                        val fileNameText = notesStorageManager.getFileName(submission.recordId)
                        Text(text = fileNameText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        // Display the creator's username
                        Text(text = "Submission by $userName")


                        // A button for playing the submission
                        Button(onClick = {
                            Log.w("DOWNLOADING:", submission.id)
                            notesStorageManager.downloadFileFromDatabase(submission.recordId)
                        }) {
                            Text("Download to my library")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom=30.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            if(!isGuest) {
                                // A button for upvoting the submission
                                Button(onClick = {
                                    // Call the upVote method of the submission on upVote click
                                    if(submission.upVote(userProfile.id))
                                        refreshPage()
                                }) {
                                    Text("Upvote")
                                }
                            }
                                // Current submission score
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Score")
                                    Text(submission.upVote!!.toString())
                                }

                            if(!isGuest) {
                                // A button for downvoting the submission
                                Button(onClick = {
                                    // Call the downVote method of the submission on downVote click
                                    if(submission.downVote(userProfile.id))
                                        refreshPage()
                                }) {
                                    Text("Downvote")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun refreshPage(){
        finish()
        startActivity(intent)
    }
}
