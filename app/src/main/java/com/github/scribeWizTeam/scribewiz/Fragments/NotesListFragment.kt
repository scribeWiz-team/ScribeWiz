package com.github.scribeWizTeam.scribewiz.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.github.scribeWizTeam.scribewiz.NotesDisplayedActivity
import com.github.scribeWizTeam.scribewiz.NotesStorageManager
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import java.lang.IllegalStateException
import kotlin.contracts.ExperimentalContracts


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

    @Composable
    fun NoteTile(name: String) {
        Surface(modifier =  Modifier.getTileModifier()
                .clickable {
                    //commented out this part since it wouldn't make the app compile
                    //Indeed I would have had to add @experimentalContracts everywhere to support notesDisplayedActivity
                    makeTheMusicBeDisplayed(name)
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
    @OptIn(ExperimentalContracts::class, ExperimentalUnsignedTypes::class)
    fun makeTheMusicBeDisplayed(name: String) {
        val newNotesDisplayedActivity = Intent(this.requireContext(), NotesDisplayedActivity::class.java)
        val file = notesStorageManager.getNoteFile(name)
        var stringUri = ""

        file?.let {
            stringUri = file.toURI().toString()
        } ?: run {
            Toast.makeText(context, "The file is empty !", Toast.LENGTH_LONG).show()
            throw IllegalStateException("The file is null")
        }

        newNotesDisplayedActivity.putExtra("FILE", stringUri)
        startActivity(newNotesDisplayedActivity)
    }
}
