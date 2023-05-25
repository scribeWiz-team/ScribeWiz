package com.github.scribeWizTeam.scribewiz.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.github.scribeWizTeam.scribewiz.util.RecordingParameters


class RecParameterFragment(contentLayoutId: Int = 0) : Fragment(contentLayoutId) {

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

    private var recordingParameters = RecordingParameters()

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
                ScribeWizTheme {

                    val scoreName = remember { mutableStateOf(recordingParameters.scoreName) }
                    val beats = remember { mutableStateOf(recordingParameters.beats.toString()) }
                    val tempo = remember { mutableStateOf(recordingParameters.tempo.toString()) }

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
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = "Tonality: ",
                                    modifier = Modifier.padding(5.dp),
                                )
                                Spinner(
                                    modifier = Modifier.wrapContentSize(),
                                    displayItems = TONALITIES_NAMES,
                                    dataItems = TONALITIES_FIFTHS,
                                    onItemSelected = { recordingParameters.fifths = it },
                                    startIndex = 2 // start at C major
                                )
                            }
                            // key
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = "Staff key: ",
                                    modifier = Modifier.padding(5.dp),
                                )
                                Spinner(
                                    modifier = Modifier.wrapContentSize(),
                                    displayItems = KEY_NAMES,
                                    dataItems = KEY_VALUES,
                                    onItemSelected = { recordingParameters.useGKeySignature = it },
                                    startIndex = 1 // start with F key
                                )
                            }
                            // time signature
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(text = "Time signature: ")
                                TextField(
                                    beats.value,
                                    { beats.value = it },
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(60.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                )
                                Text(
                                    text = "/",
                                    modifier = Modifier.padding(5.dp),
                                    style = TextStyle(fontSize = 30.sp)
                                )
                                Spinner(
                                    modifier = Modifier.wrapContentSize(),
                                    displayItems = BEAT_TYPES.map { it.toString() },
                                    dataItems = BEAT_TYPES,
                                    onItemSelected = { recordingParameters.beatType = it },
                                    startIndex = 2 // start on 4 beats
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
                                onClick = {
                                    launchRecordingFragment(
                                        scoreName.value,
                                        beats.value,
                                        tempo.value
                                    )
                                }
                            ) {
                                Text(text = "Next")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun launchRecordingFragment(
        rawScoreName: String,
        rawBeats: String,
        rawTempo: String
    ) {
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
        val scoreName =
            if (rawScoreName == "") RecordingParameters.FALLBACK_SCORE_NAME else rawScoreName
        recordingParameters.scoreName = scoreName
        recordingParameters.beats = beats
        recordingParameters.tempo = tempo
        // launch recording fragment with recording_parameters

        parentFragmentManager.commit {
            replace((requireView().parent as ViewGroup).id, RecFragment(0, recordingParameters))
            addToBackStack(null)
        }
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

        Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = { expanded = !expanded }
            ) {
                Text(currentText)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "drop down arrow"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = dropDownModifier
            ) {
                displayItems.zip(dataItems).forEach { (label, data) ->
                    DropdownMenuItem(onClick = {
                        Log.i("RecParam", "Selected: $label $data")
                        onItemSelected(data)
                        expanded = false
                        currentText = label
                    }) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}
