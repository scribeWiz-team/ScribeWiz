package com.github.scribeWizTeam.scribewiz.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
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

class RecFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    companion object {
        private const val MILLIS_IN_FUTURE = 9999999L //number of milliseconds maximum record time
        private const val COUNT_DOWN_INTERVAL = 1000L //number of milliseconds between each tick
        private const val SAMPLE_RATE_IN_HZ = 44100
    }

    private val audioSource = MediaRecorder.AudioSource.MIC
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(Companion.SAMPLE_RATE_IN_HZ, channelConfig, audioFormat)

    private lateinit var audioRecorder: AudioRecord //media recorder to record audio
    private lateinit var mediaPlayer: MediaPlayer//media player to play sound

    private lateinit var timer: CountDownTimer //timer to show recording time

    private lateinit var outputFile: File
    private lateinit var outputFilePath: String
    var isRecording = false //boolean to check if recording is in progress

    private var metronomeIsPlaying = false

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
                            switchRecordState(counterText, recordButtonText)
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
            timer.cancel()
        } else {
            val tempo : Long = try {
               tempoValue.value.toLong()
            } catch (_: java.lang.NumberFormatException) {
                60L
            }

            timer = onTickTimer(1000 * 60 / tempo) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                }
                mediaPlayer.start()
            }
            timer.start()
            metronomeIsPlaying = true
            metronomeButtonText.value = "Stop metronome"
        }
    }

    private fun switchRecordState(counterText : MutableState<String>, recordButtonText : MutableState<String>) {
        if (!isRecording) {
            // Set the timer
            timer = onTickTimer(COUNT_DOWN_INTERVAL) { millisUntilFinished ->
                counterText.value = ((MILLIS_IN_FUTURE - millisUntilFinished) / Companion.COUNT_DOWN_INTERVAL).toString()
            }

            //start recording
            recordButtonText.value = "Stop recording"
            // Start the timer
            timer.start()
            startRecording()
        } else {
            //stop recording
            // Stop the timer
            timer.cancel()
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
    private fun startRecording() {
        // Initialize the AudioRecord
        audioRecorder = AudioRecord(audioSource,
            Companion.SAMPLE_RATE_IN_HZ, channelConfig, audioFormat, bufferSize)

        // Set the output file path
        outputFilePath = getOutputFilePath()
        outputFile = File(outputFilePath)

        // Start recording
        audioRecorder.startRecording()
        isRecording = true

        // Write the audio data to a file
        val buffer = ByteArray(bufferSize)
        val outputStream = FileOutputStream(outputFile)

        Thread(Runnable {
            while (isRecording) {
                val bytesRead = audioRecorder.read(buffer, 0, bufferSize)
                if (bytesRead > 0) {
                    try {
                        outputStream.write(buffer, 0, bytesRead)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            outputStream.close()
        }).start()
    }

    // Method to stop recording
    private fun stopRecording() {
        // Stop recording
        audioRecorder.stop()
        // Release the AudioRecord
        audioRecorder.release()
        // Set recording not in progress
        isRecording = false
    }


    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        super.onDestroy()
    }
}