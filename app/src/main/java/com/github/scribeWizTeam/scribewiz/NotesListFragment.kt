package com.github.scribeWizTeam.scribewiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme

class NotesListFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    private lateinit var notesStorageManager: NotesStorageManager

    constructor() : this(0) {
        // Default constructor
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notesStorageManager = NotesStorageManager(this.requireContext())
        return ComposeView(requireContext()).apply {
            setContent {
                ScribeWizTheme {
                    // A surface container using the 'background' color from the theme

                    val notesNames = remember {
                        notesStorageManager.getNotesNames().toMutableStateList()
                    }

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("All notes:",  fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier
                            .padding(15.dp)
                            .height(25.dp))
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

                                SwipeToDismissNote(state, name)
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SwipeToDismissNote(state: DismissState, name: String) {
        SwipeToDismiss(
            state = state,
            background = {
                Surface(color = Color.Red, modifier = Modifier.getTileModifier(Color.Red, Color.Red)
                ) {}
            },
            dismissContent = {
                NoteTile(name = name)
            },
            directions = setOf(DismissDirection.EndToStart)
        )
    }

    @Composable
    fun NoteTile(name: String) {
        Surface(modifier =  Modifier.getTileModifier()
            .clickable {
                val score = Intent(this.requireContext(), NotesDisplayedActivity::class.java)
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

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getTileModifier(color: Color = Color.White, borderColor: Color = Color.Black) : Modifier {
        return Modifier
            .padding(0.dp, 5.dp)
            .border(1.dp, borderColor, CircleShape)
            .background(color, CircleShape)
            .width(300.dp)
            .height(50.dp)
            .padding(10.dp, 5.dp)
    }
}