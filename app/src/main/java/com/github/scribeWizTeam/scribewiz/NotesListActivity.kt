package com.github.scribeWizTeam.scribewiz

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme

class NotesListActivity : ComponentActivity() {


    private lateinit var notesStorageManager: NotesStorageManager

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        notesStorageManager = applicationContext
            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?.let { NotesStorageManager() }!!

        setContent {
            ScribeWizTheme {
                // A surface container using the 'background' color from the theme

                val notesNames = remember {
                    notesStorageManager.getNotesNames().toMutableStateList()
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Recent notes:",  fontSize = 20.sp, modifier = Modifier
                        .height(30.dp)
                        .padding(5.dp))
                    LazyColumn (modifier = Modifier
                        .padding(all = 8.dp)
                        .testTag("columnList"),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        items(notesNames, key={ name -> name }) { name ->

                            val state = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        notesStorageManager.deleteNote(name)
                                        notesNames.remove(name)
                                    }
                                    true
                                }
                            )

                            SwipeToDismiss(
                                state = state,
                                background = {},
                                dismissContent = {
                                    NoteTile(name = name)
                                },
                                directions = setOf(DismissDirection.EndToStart)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NoteTile(name: String) {
        Surface(modifier = Modifier
            .padding(0.dp, 5.dp)
            .border(1.dp, Color.Black, CircleShape)
            .width(300.dp)
            .padding(10.dp, 5.dp)
            .background(Color.Red)
            .clickable {
                val score = Intent(this@NotesListActivity, NotesDisplayedActivity::class.java)
                score.putExtra("note_name", name)
                startActivity(score)
            }) {
            Row {
                Image(painter = painterResource(R.drawable.music_note),
                    modifier = Modifier
                        .height(20.dp)
                        .align(Alignment.CenterVertically),
                    contentDescription = "music_file")
                Text(text = name, modifier = Modifier
                    .padding(10.dp)
                    .width(220.dp))
            }
        }
    }
}