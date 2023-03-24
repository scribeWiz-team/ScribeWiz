package com.github.scribeWizTeam.scribewiz

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme

class NotesListActivity : ComponentActivity() {

    private lateinit var memoryManager: NotesStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        memoryManager = applicationContext
            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.let { NotesStorageManager(it) }!!

        val notesNames = memoryManager.notesNames()

        setContent {
            ScribeWizTheme {
                // A surface container using the 'background' color from the theme
                Column (modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 8.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("columnList"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Recent notes:",  fontSize = 20.sp, modifier = Modifier.padding(20.dp))
                    for (name in notesNames) {
                        NoteTile(name = name)
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
            .clickable {}) {
            Row {
                Image(painter = painterResource(R.drawable.music_note), modifier = Modifier
                    .height(20.dp)
                    .align(Alignment.CenterVertically), contentDescription = "music_file")
                Text(text = name, modifier = Modifier.padding(10.dp))
            }
        }
    }
}