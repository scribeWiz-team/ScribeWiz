package com.github.scribeWizTeam.scribewiz.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.Activities.ParticipateInChallengeActivity
import com.github.scribeWizTeam.scribewiz.NotesDisplayedActivity
import com.github.scribeWizTeam.scribewiz.NotesStorageManager
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.IllegalStateException
import kotlin.contracts.ExperimentalContracts
import kotlin.math.log


class NotesListFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    private lateinit var notesStorageManager: NotesStorageManager
    val dialogName = "Rename Note"
    val contentDescriptionDialog = "New Name"

    constructor() : this(0) {
        // Default constructor
    }


    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

                    val showShareMenu = remember { mutableStateOf(false) }
                    val sharedNoteName = remember { mutableStateOf("") }

                    // Add this state variable to control the visibility of the rename dialog
                    var showRenameDialog = remember { mutableStateOf(false) }
                    var renamingNoteName = remember { mutableStateOf("") }

                    // Function to handle renaming
                    fun handleRename(newName: String) {
                        val hasSucceeded =
                            notesStorageManager.renameFile(renamingNoteName.value, newName)

                        if (hasSucceeded) {
                            val index = notesNames.indexOf(renamingNoteName.value)
                            notesNames[index] = newName
                            Log.i("tag", "The name was correctly changed")

                        } else Toast.makeText(
                            this.context,
                            "Couldn't rename the file",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (showShareMenu.value) {
                            ShareMenu(sharedNoteName.value, showShareMenu)
                        }

                        Text(
                            "All notes:",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(15.dp)
                                .height(25.dp)
                        )
                        LazyColumn(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .testTag("columnList"),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            items(notesNames, key = { note -> note }) { name ->

                                val state = rememberDismissState(
                                    confirmStateChange = {
                                        if (it == DismissValue.DismissedToStart) {
                                            notesStorageManager.deleteNote(name)
                                            notesNames.remove(name)
                                        }
                                        true
                                    }
                                )
                                Row(verticalAlignment = CenterVertically) {
                                    Button(
                                        onClick = {
                                            showShareMenu.value = true
                                            sharedNoteName.value = name
                                        },
                                        modifier = Modifier
                                            .width(85.dp)
                                            .height(45.dp)
                                            .background(Color.White, CircleShape)
                                            .padding(5.dp)
                                    ) {
                                        Text(text = "share")
                                    }
                                    SwipeToDismissNote(
                                        state,
                                        name,
                                        showRenameDialog = showRenameDialog,
                                        renamingNoteName = renamingNoteName,
                                        onDelete = ::handleRename
                                    )
                                }
                            }
                        }
                    }

                    if (showRenameDialog.value) {
                        RenameDialog(
                            renamingNoteName = renamingNoteName,
                            onRename = { newName -> handleRename(newName) },
                            onDismissRequest = { showRenameDialog.value = false }
                        )
                    }

                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SwipeToDismissNote(
        state: DismissState, name: String, showRenameDialog: MutableState<Boolean>,
        renamingNoteName: MutableState<String>, onDelete: (String) -> Unit
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                Surface(
                    color = Color.Red, modifier = Modifier.getTileModifier(Color.Red, Color.Red)
                ) {}
            },
            dismissContent = {
                NoteTile(name = name, showRenameDialog, renamingNoteName, onDelete)
            },
            directions = setOf(DismissDirection.EndToStart)
        )


    }

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getTileModifier(
        color: Color = Color.White,
        borderColor: Color = Color.Black
    ): Modifier {
        return Modifier
            .padding(0.dp, 5.dp)
            .border(1.dp, borderColor, CircleShape)
            .background(color, CircleShape)
            .width(300.dp)
            .height(50.dp)
            .padding(10.dp, 5.dp)
    }

    @Composable
    fun NoteTile(
        name: String,
        showRenameDialog: MutableState<Boolean>,
        renamingNoteName: MutableState<String>,
        onDelete: (String) -> Unit

    ) {
        var showMenu = remember { mutableStateOf(false) }
        Surface(modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showMenu.value = true
                    },
                    onTap = {
                        makeTheMusicBeDisplayed(name)
                    })
            }
        ) {

            Row {
                Image(
                    painter = painterResource(R.drawable.music_note),
                    modifier = Modifier
                        .height(20.dp)
                        .align(Alignment.CenterVertically),
                    contentDescription = "music_file"
                )
                Text(
                    text = name, modifier = Modifier
                        .padding(10.dp)
                        .width(220.dp)
                )

                DropdownMenu(
                    expanded = showMenu.value,
                    onDismissRequest = { showMenu.value = false }
                ) {
                    DropdownMenuItem(onClick = {
                        val noteToRename = name
                        renamingNoteName.value = name
                        showRenameDialog.value = true
                    }) {
                        Text("Rename")
                    }
                    DropdownMenuItem(onClick = {
                        val intent = Intent(context, ParticipateInChallengeActivity::class.java)
                        intent.putExtra("musicName", name)
                        startActivity(intent)
                    }) {
                        Text("Challenges")
                    }

                    // Add more DropdownMenuItem here for more options
                }

            }
        }
    }

    /**
     * Displays the sheet music by starting the NotesDisplayedActivity.
     *
     * @param name The name of the music.
     */
    @OptIn(ExperimentalContracts::class, ExperimentalUnsignedTypes::class)
    fun makeTheMusicBeDisplayed(name: String) {
        val newNotesDisplayedActivity =
            Intent(this.requireContext(), NotesDisplayedActivity::class.java)
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

    @Composable
    private fun ShareMenu(noteName: String, showShareMenu: MutableState<Boolean>) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { showShareMenu.value = false },
        ) {

            val ret = UserModel.currentUser(requireContext())

            if (ret.isFailure) {
                Toast.makeText(context, "You're not logged in", Toast.LENGTH_LONG).show()
                return@Popup
            }

            val user = ret.getOrThrow()

            val mExpanded = remember { mutableStateOf(true) }
            val mSelectedName = remember { mutableStateOf("") }
            val mSelectedID = remember { mutableStateOf("") }

            Column(Modifier.padding(20.dp)) {

                // Create an Outlined Text Field
                // with icon and not expanded
                Text(text = mSelectedName.value)
//                Button(onClick = { mExpanded.value = mExpanded.value.not() }) {
//
//                }

                DropdownMenu(
                    expanded = mExpanded.value,
                    onDismissRequest = { mExpanded.value = false },
                    modifier = Modifier.align(CenterHorizontally)
                ) {
                    user.friends?.forEach { id ->
                        UserModel.user(id).onSuccess { friend ->
                            DropdownMenuItem(onClick = {
                                mSelectedName.value = friend.userName.toString()
                                mSelectedID.value = id
                                mExpanded.value = false
                            }) {
                                friend.userName?.let { Text(text = it) }
                            }
                        }
                    }
                }
                Button(onClick = {
                    showShareMenu.value = false
                    shareNoteToOtherUser(noteName, mSelectedID.value)
                }) {
                    Text("share")
                }
            }
        }
    }

    private fun shareNoteToOtherUser(noteName: String, userId: String) {

        val docRef = Firebase.firestore.collection(MusicNoteModel.COLLECTION).document()
        val musicNoteModel = MusicNoteModel(docRef.id, noteName)
        musicNoteModel.updateInDB()

        UserModel.currentUser(requireContext()).onFailure {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_LONG).show()
        }.onSuccess { curUser ->
            if (!curUser.musicNotes?.contains(musicNoteModel.id)!!) {
                curUser.musicNotes!!.add(musicNoteModel.id)
                curUser.updateInDB()
            }

            UserModel.currentUser(requireContext()).onSuccess { toUser ->
                if (!toUser.musicNotes?.contains(musicNoteModel.id)!!) {
                    toUser.musicNotes!!.add(musicNoteModel.id)
                    toUser.updateInDB()
                }
            }
        }
    }

    @Composable
    fun RenameDialog(
        renamingNoteName: MutableState<String>,
        onRename: (String) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        var nameDisplayed = remember { mutableStateOf(renamingNoteName.value) }
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(dialogName) },
            text = {
                OutlinedTextField(
                    value = nameDisplayed.value,
                    onValueChange = { nameInput -> nameDisplayed.value = nameInput },
                    label = { Text("New Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = contentDescriptionDialog }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRename(nameDisplayed.value.trim())
                    onDismissRequest()
                }) {
                    Text("Rename")
                }
            },
            dismissButton = {
                Button(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        )
    }

}
