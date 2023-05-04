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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.PermissionsManager
import com.github.scribeWizTeam.scribewiz.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

import kotlin.math.*

import com.github.scribeWizTeam.scribewiz.transcription.*

class RecFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    companion object {
        private const val MILLIS_IN_FUTURE = 9999999L //number of milliseconds maximum record time
        private const val COUNT_DOWN_INTERVAL = 1000L //number of milliseconds between each tick
        private const val SAMPLE_RATE_IN_HZ = 44100
        private const val NOTE_SAMPLE_INTERVAL = 80L //number of milliseconds between two note guesses
        //number of a samples used for each note guess
        private const val NOTE_SAMPLE_WINDOW_SIZE = (SAMPLE_RATE_IN_HZ*NOTE_SAMPLE_INTERVAL/1000).toInt()

    }

    private val audioSource = MediaRecorder.AudioSource.MIC
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = 2*AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, channelConfig, audioFormat)

    private lateinit var audioRecorder: AudioRecord //media recorder to record audio
    private lateinit var mediaPlayer: MediaPlayer//media player to play sound

    private lateinit var transcriber: Transcriber

    private lateinit var outputFile: File
    private lateinit var outputFilePath: String
    private var isRecording = false //boolean to check if recording is in progress

    private var metronomeIsPlaying = false
    private var tempo: Long = 60L

    private var processSamplesTimer = onTickTimer(1) {}
    private var recordTimer = onTickTimer(1) {}
    private var metronomeTimer = onTickTimer(1) {}

    constructor() : this(0) {
        // Default constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //check if the app has permission to record audio
        PermissionsManager().checkPermissionThenExecute(this, requireContext(), Manifest.permission.RECORD_AUDIO) {}

        mediaPlayer = MediaPlayer.create(context, R.raw.tick)

        return ComposeView(requireContext()).apply {
            setContent {

                val counterText = remember { mutableStateOf("00:00") }
                val recordButtonText = remember { mutableStateOf("Start recording") }
                val metronomeButtonText = remember { mutableStateOf("Start metronome") }
                val tempoValue = remember { mutableStateOf("60") }
                val thresholdValue = remember { mutableStateOf("0.95") }
                val debugValueText = remember { mutableStateOf("") }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Text(
                            text = counterText.value,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                        PlayButton(recordButtonText) {
                            switchRecordState(counterText, recordButtonText,
                                              debugValueText, thresholdValue)
                        }
                        PlayButton(metronomeButtonText) {
                            switchMetronomeState(metronomeButtonText, tempoValue)
                        }
                        OutlinedTextField(tempoValue.value,
                            { tempoValue.value = it },
                            modifier = Modifier
                                .height(70.dp)
                                .width(190.dp)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = "Tempo") })
                        // this is to display information when debugging
                        // the signal processing
                        OutlinedTextField(thresholdValue.value,
                            { thresholdValue.value = it },
                            modifier = Modifier
                                .height(70.dp)
                                .width(190.dp)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = "Threshold") })
                        Text(
                            text = debugValueText.value,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun PlayButton(text: MutableState<String>,  onClick : () -> Unit) {
        Button(
            modifier = Modifier
                .height(50.dp)
                .width(190.dp)
                .padding(5.dp),
            onClick = onClick) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "play",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text.value)
        }
    }

    private fun onTickTimer(interval: Long, onTick: (Long) -> Unit) : CountDownTimer {
        return object : CountDownTimer(MILLIS_IN_FUTURE, interval) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                // Do nothing
            }
        }
    }

    private fun switchMetronomeState(metronomeButtonText: MutableState<String>, tempoValue: MutableState<String>) {
        if (metronomeIsPlaying) {
            metronomeButtonText.value = "Start metronome"
            metronomeIsPlaying = false
            metronomeTimer.cancel()
        } else {
            tempo = try {
               tempoValue.value.toLong()
            } catch (_: java.lang.NumberFormatException) {
                tempoValue.value = "60"
                60L
            }


            metronomeTimer = onTickTimer(1000 * 60 / tempo) {
                if (this::mediaPlayer.isInitialized) {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        mediaPlayer.seekTo(0)
                    }
                    mediaPlayer.start()
                }
            }

            metronomeTimer.start()
            metronomeIsPlaying = true
            metronomeButtonText.value = "Stop metronome"
        }
    }

    private fun switchRecordState(counterText : MutableState<String>,
                                  recordButtonText : MutableState<String>,
                                  debugValueText : MutableState<String>,
                                  thresholdValue : MutableState<String>) {
        if (!isRecording) {
            // Set the timer
            recordTimer = onTickTimer(COUNT_DOWN_INTERVAL) { millisUntilFinished ->
                counterText.value = ((MILLIS_IN_FUTURE - millisUntilFinished) / Companion.COUNT_DOWN_INTERVAL).toString()
            }

            processSamplesTimer = onTickTimer(NOTE_SAMPLE_INTERVAL){
                val raw_samples = ShortArray(NOTE_SAMPLE_WINDOW_SIZE)
                val bytesRead = audioRecorder.read(raw_samples, 0, NOTE_SAMPLE_WINDOW_SIZE)
                val samples = Signal(NOTE_SAMPLE_WINDOW_SIZE)
                for (i in 0 until NOTE_SAMPLE_WINDOW_SIZE){
                    samples[i] = raw_samples[i].toFloat() * (1.0f / 32768.0f)
                }
                val debugValue = transcriber.process_samples(samples)
                // Log.i("RecProcess", "pitch: ${debugValue}")
                debugValueText.value = debugValue.toString()
            }

            //start recording
            recordButtonText.value = "Stop recording"
            // Start the timer
            recordTimer.start()
            processSamplesTimer.start()
            startRecording(thresholdValue)
        } else {
            //stop recording
            // Stop the timer
            recordTimer.cancel()
            processSamplesTimer.cancel()
            // Set the recording time to 0
            counterText.value = "Recording saved!"
            recordButtonText.value = "Start recording"
            stopRecording()
        }
    }


    fun getOutputFilePath(): String {
        return requireContext().externalCacheDir?.absolutePath + "/recording.3gp"
    }

    @SuppressLint("Permissions are checked before calling this method", "MissingPermission")
    private fun startRecording(thresholdValue: MutableState<String>) {
        // Initialize the AudioRecord
        audioRecorder = AudioRecord(audioSource,
            Companion.SAMPLE_RATE_IN_HZ, channelConfig, audioFormat, bufferSize)

        val threshold = try {
           thresholdValue.value.toDouble()
        } catch (_: java.lang.NumberFormatException) {
            thresholdValue.value = "0.95"
            0.95
        }
        // Initialize the Transcriber
        val pitch_detector = PitchDetector(Companion.SAMPLE_RATE_IN_HZ.toDouble(),
                                           threshold)
        val note_guesser = NoteGuesser(Companion.NOTE_SAMPLE_INTERVAL / 1000.0)
        val score_name = "sample score"
        val signature = Signature(0, 4, 4, tempo=tempo.toInt())
        val renderer = MusicxmlBuilder(score_name, signature)
        transcriber = Transcriber(pitch_detector, note_guesser, renderer)

        // Set the output file path
        outputFilePath = getOutputFilePath()
        outputFile = File(outputFilePath)

        // Start recording
        audioRecorder.startRecording()
        isRecording = true

    }

    // Method to stop recording
    private fun stopRecording() {
        // Stop recording
        audioRecorder.stop()
        // Release the AudioRecord
        audioRecorder.release()
        // Set recording not in progress
        isRecording = false
        // end the transcription
        transcriber.end_transcription()
    }


    override fun onStop() {
        try {
            recordTimer.cancel()
            metronomeTimer.cancel()
            processSamplesTimer.cancel()
            if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        super.onStop()
    }
}
