package com.github.scribeWizTeam.scribewiz.fragments

import com.github.scribeWizTeam.scribewiz.util.Export
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
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.activities.NotesDisplayedActivity
import com.github.scribeWizTeam.scribewiz.NotesStorageManager
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.activities.ParticipateInChallengeActivity
import com.github.scribeWizTeam.scribewiz.models.MusicNoteModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import kotlin.contracts.ExperimentalContracts


class NotesListFragment(contentLayoutId: Int = 0) : Fragment(contentLayoutId) {

    private lateinit var notesStorageManager: NotesStorageManager
    val dialogName = "Rename Note"
    private val contentDescriptionDialog = "New Name"


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
                    val showRenameDialog = remember { mutableStateOf(false) }
                    val renamingNoteName = remember { mutableStateOf("") }

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
                        horizontalAlignment = CenterHorizontally
                    ) {
                        if (showShareMenu.value) {
                            ShareMenu(sharedNoteName.value, showShareMenu)
                        }

                        val text = if (notesNames.isEmpty()) {
                            "You have no note yet"
                        } else {
                            "All notes:"
                        }

                        Text(
                            text = text,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(15.dp)
                                .height(25.dp)
                        )

                        if (notesNames.isEmpty()) {
                            return@ScribeWizTheme
                        }

                        LazyColumn(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .testTag("columnList"),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = CenterHorizontally
                        ) {

                            items(notesNames, key = { note -> note }) { name ->
                                val state = rememberDismissState(
                                    confirmStateChange = {
                                        if (it == DismissValue.DismissedToStart) {
                                            notesStorageManager.deleteNote(name)
                                            notesNames.remove(name)
                                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        true
                                    }
                                )
                                Row(verticalAlignment = CenterVertically) {
                                    SwipeToDismissNote(
                                        state,
                                        name,
                                        showRenameDialog = showRenameDialog,
                                        renamingNoteName = renamingNoteName,
                                        showShareMenu = showShareMenu,
                                        sharedNoteName = sharedNoteName,
                                        noteNames = notesNames
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
        state: DismissState,
        name: String,
        showRenameDialog: MutableState<Boolean>,
        renamingNoteName: MutableState<String>,
        showShareMenu: MutableState<Boolean>,
        sharedNoteName: MutableState<String>,
        noteNames: SnapshotStateList<String>

    ) {
        SwipeToDismiss(
            state = state,
            background = {
                Surface(
                    color = Color.Red, modifier = getTileModifier(Color.Red, Color.Red)
                ) {}
            },
            dismissContent = {
                NoteTile(
                    name = name,
                    showRenameDialog,
                    renamingNoteName,
                    sharedNoteName,
                    showShareMenu,
                    noteNames
                )
            },
            directions = setOf(DismissDirection.EndToStart)
        )


    }

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun getTileModifier(
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
        sharedNoteName: MutableState<String>,
        showShareMenu: MutableState<Boolean>,
        noteNames: SnapshotStateList<String>
    ) {
        val showMenu = remember { mutableStateOf(false) }

        var buttonName by remember { mutableStateOf("Export") }
        Surface(modifier = getTileModifier()
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
                        .align(CenterVertically),
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
                    DropdownMenuItem(onClick = {
                        showShareMenu.value = true
                        sharedNoteName.value = name
                    }) {
                        Text("Share to friend")
                    }

                    // Add more DropdownMenuItem here for more options
                    // Added "com.github.scribeWizTeam.scribewiz.util.Export" option
                    DropdownMenuItem(onClick = {
                        if(export(name)){
                            buttonName = "Export"
                        }
                    }) {
                        Text(buttonName)
                    }
                    DropdownMenuItem(modifier = Modifier.background(Color.Red), onClick = {
                        notesStorageManager.deleteNote(name)
                        noteNames.remove(name)
                        Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Delete")
                    }
                }

            }
        }
    }

    // Added export helper function which calls com.github.scribeWizTeam.scribewiz.util.Export.exportMusicXMLFile to export the file
    private fun export(name: String): Boolean {
        // Get the file
        val noteFile = notesStorageManager.getNoteFile(name)
        // com.github.scribeWizTeam.scribewiz.util.Export the file
        val success = noteFile?.let { Export.exportMusicXMLFile(it, requireContext()) }
        return if (success == true) {
            Toast.makeText(context, "Exported $name", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(context, "Failed to export $name", Toast.LENGTH_SHORT).show()
            false
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
        Dialog(
            onDismissRequest = { showShareMenu.value = false },
        ) {

            val ret = UserModel.currentUser(requireContext())

            if (ret.isFailure) {
                Toast.makeText(context, "You're not logged in", Toast.LENGTH_LONG).show()
                return@Dialog
            }

            val user = ret.getOrThrow()
            val mSelectedName = remember { mutableStateOf("") }
            val mSelectedID = remember { mutableStateOf("") }

            Surface(
                modifier = Modifier.size(400.dp),
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = CenterHorizontally) {

                    // Create an Outlined Text Field
                    // with icon and not expanded
                    Text(text = mSelectedName.value)


                    val userFriendsId = user.friends.orEmpty()

                    Button(
                        onClick = {
                        showShareMenu.value = false
                        shareNoteToOtherUser(noteName, mSelectedID.value)
                    }) {
                        Text("share")
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .testTag("columnList"),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = CenterHorizontally
                    ) {
                        items(userFriendsId, key = { user -> user }) { id ->
                            UserModel.user(id).onSuccess { friend ->
                                friend.userName?.let {
                                    Row(
                                        Modifier.clickable {
                                            mSelectedName.value = it
                                            mSelectedID.value = friend.id
                                        },
                                        verticalAlignment = CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.no_user),
                                            contentDescription = "User profile picture",
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(text = it)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun shareNoteToOtherUser(noteName: String, userId: String) {

        val musicNoteModel = MusicNoteModel(name = noteName)
        musicNoteModel.updateInDB()
        notesStorageManager.uploadFileToDatabase(musicNoteModel)

        UserModel.currentUser(requireContext()).onFailure {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_LONG).show()
        }.onSuccess { curUser ->
            if (!curUser.musicNotes?.contains(musicNoteModel.id)!!) {
                curUser.musicNotes!!.add(musicNoteModel.id)
                curUser.updateInDB()
            }

            UserModel.user(userId).onSuccess { toUser ->
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
        val nameDisplayed = remember { mutableStateOf(renamingNoteName.value) }
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
