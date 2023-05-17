package com.github.scribeWizTeam.scribewiz.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.PermissionsManager
import com.github.scribeWizTeam.scribewiz.NotesStorageManager
import com.github.scribeWizTeam.scribewiz.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

import kotlin.math.*


class RecordingParameters() {
    companion object {
        const val DEFAULT_SCORE_NAME = ""
        const val FALLBACK_SCORE_NAME = "new_score"
        const val DEFAULT_TONALITY = 0 // C major
        const val DEFAULT_BEATS = 4
        const val DEFAULT_BEAT_TYPE = 4
        const val DEFAULT_TEMPO = 120
        const val DEFAULT_KEY_SIGNATURE = false // use F key
    }

    var scoreName: String = DEFAULT_SCORE_NAME
    var fifths: Int = DEFAULT_TONALITY
    var beats: Int = DEFAULT_BEATS
    var beat_type: Int = DEFAULT_BEAT_TYPE
    var tempo: Int = DEFAULT_TEMPO
    var use_g_key_signature: Boolean = DEFAULT_KEY_SIGNATURE
}




class RecParameterFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    companion object {
        private val TONALITIES_NAMES = listOf(
            "A", "B", "C", "D", "E", "F", "G",
            "Ab", "Bb", "Db", "Eb", "F#",
            "a", "b", "c", "d", "e", "f", "g",
            "bb", "c#", "d#", "f#", "g#"
        )

        private val TONALITIES_FIFTHS = listOf(
            3, 5, 0, 2, 4, -1, 1,
            -4, -2, -5, -3, 6,
            0, 2, -3, -1, 1, -4, -2,
            -5, 4, 6, 3, 5
        )

        private val KEY_NAMES = listOf("G", "F")
        private val KEY_VALUES = listOf(true, false)

        private val BEAT_TYPES = listOf(1, 2, 4, 8, 16)

    }

    private var recording_parameters = RecordingParameters()

    constructor() : this(0) {
        // Default constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                val scoreName = remember { mutableStateOf(recording_parameters.scoreName) }
                val beats = remember { mutableStateOf(recording_parameters.beats.toString()) }
                val tempo = remember { mutableStateOf(recording_parameters.tempo.toString()) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        // score name
                        OutlinedTextField(scoreName.value,
                            { scoreName.value = it },
                            modifier = Modifier
                                .width(200.dp)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            label = { Text(text = "Score name") })
                        // tonality
                        Row(verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly){
                            Text(text = "Tonality: ",
                                 modifier = Modifier.padding(5.dp),
                            )
                            Spinner(
                                modifier = Modifier.wrapContentSize(),
                                displayItems = TONALITIES_NAMES,
                                dataItems = TONALITIES_FIFTHS,
                                onItemSelected = {recording_parameters.fifths = it},
                            )
                        }
                        // key
                        Row(verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly){
                            Text(text = "Staff key: ",
                                 modifier = Modifier.padding(5.dp),
                            )
                            Spinner(
                                modifier = Modifier.wrapContentSize(),
                                displayItems = KEY_NAMES,
                                dataItems = KEY_VALUES,
                                onItemSelected = {recording_parameters.use_g_key_signature = it},
                            )
                        }
                        // time signature
                        Row(verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly){
                            Text(text = "Time signature: ")
                            TextField(beats.value,
                                { beats.value = it },
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(60.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                            Text(text = "/",
                                 modifier = Modifier.padding(5.dp),
                                 style = TextStyle(fontSize = 30.sp)
                            )
                            Spinner(
                                modifier = Modifier.wrapContentSize(),
                                displayItems = BEAT_TYPES.map({it.toString()}),
                                dataItems = BEAT_TYPES,
                                onItemSelected = {recording_parameters.beat_type = it},
                                startIndex = 2
                            )
                        }
                        // tempo
                        OutlinedTextField(tempo.value,
                            { tempo.value = it },
                            modifier = Modifier
                                .width(120.dp)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = "Tempo") })
                        // record button
                        Button(
                            modifier = Modifier
                                .height(50.dp)
                                .width(190.dp)
                                .padding(5.dp),
                            onClick = { launch_recording_fragment(scoreName.value,
                                                                  beats.value,
                                                                  tempo.value) }
                        ){
                            Text(text = "Next")
                        }
                    }
                }
            }
        }
    }

    private fun launch_recording_fragment(rawScoreName: String, rawBeats: String, rawTempo: String){
        val tempo = try {
           rawTempo.toInt()
        } catch (_: java.lang.NumberFormatException) {
            RecordingParameters.DEFAULT_TEMPO
        }
        val beats = try {
           rawBeats.toInt()
        } catch (_: java.lang.NumberFormatException) {
            RecordingParameters.DEFAULT_BEATS
        }
        val scoreName = if (rawScoreName == "") RecordingParameters.FALLBACK_SCORE_NAME else rawScoreName
        recording_parameters.scoreName = scoreName
        recording_parameters.beats = beats
        recording_parameters.tempo = tempo
        // TODO: launch recording fragment and pass recording_parameters to it
        //
    }

    @Composable
    private fun <T> Spinner(
        // idea for the spinner inspired from
        // https://stackoverflow.com/questions/67842511/dropdown-button-wheel-picker-spinner-in-jetpack-compose
        // and https://gist.github.com/Pinaki93/163f293a9c6f7ba3ae5f20bc87d133da 
        modifier: Modifier = Modifier,
        dropDownModifier: Modifier = Modifier,
        displayItems: List<String>,
        dataItems: List<T>,
        onItemSelected: (T) -> Unit,
        startIndex: Int = 0
    ) {
        var expanded: Boolean by remember { mutableStateOf(false) }
        // set to first element by default
        var currentText: String by remember { mutableStateOf(displayItems[startIndex]) }
        onItemSelected(dataItems[startIndex])

        Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = { expanded = !expanded }
            ){
                Text(currentText)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "drop down arrow",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = dropDownModifier
            ) {
                displayItems.zip(dataItems).forEach { (label, data) ->
                    DropdownMenuItem(onClick = {
                        onItemSelected(data)
                        expanded = false
                        currentText = label
                    }) {
                        Text(text=label)
                    }
                }
            }
        }
    }
}
