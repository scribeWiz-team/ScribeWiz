package com.github.scribeWizTeam.scribewiz.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {


    private val audioSource = MediaRecorder.AudioSource.MIC
    private val sampleRateInHz = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

    private lateinit var audioRecorder: AudioRecord //media recorder to record audio

    private lateinit var timer: CountDownTimer //timer to show recording time

    private lateinit var outputFile: File
    private lateinit var outputFilePath: String
    var isRecording = false //boolean to check if recording is in progress
    val REQUEST_RECORD_AUDIO_PERMISSION = 200 //request code for permission
    val MILLISINFUTURE = 9999999L //number of milliseconds maximum record time
    val COUNT_DOWN_INTERVAL = 1000L //number of milliseconds between each tick

    constructor() : this(0) {
        // Default constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //check if the app has permission to record audio
        checkPermission()

        return ComposeView(requireContext()).apply {
            setContent {

                var counterText by remember { mutableStateOf("00:00") }
                var recordButtonText by remember { mutableStateOf("Start recording") }


                Column(
                    modifier = Modifier.fillMaxSize().padding(all = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = counterText, fontSize = 24.sp)
                    Button(modifier = Modifier
                        .height(40.dp)
                        .width(180.dp),
                        onClick = {
                            if (!isRecording) {
                                // Set the timer
                                timer = object : CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        counterText = ((MILLISINFUTURE - millisUntilFinished) / COUNT_DOWN_INTERVAL).toString()
                                    }

                                    override fun onFinish() {
                                        // Do nothing
                                    }
                                }

                                //start recording
                                recordButtonText = "Stop recording"
                                startRecording()
                            } else {
                                //stop recording
                                // Set the recording time to 0
                                counterText = "Recording saved!"
                                recordButtonText = "Start recording"
                                stopRecording()
                            }
                    }) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(recordButtonText)
                    }
                }
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //if not, request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //set the result of the permission request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //check if the request code is same as the one we sent
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            //check if the permission is granted
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //if not, show a toast "Permission Denied"
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            else{
                //else when permission granted, start recording

            }
        }
    }

    fun getOutputFilePath(): String {
        return requireContext().externalCacheDir?.absolutePath + "/recording.3gp"
    }

    @SuppressLint("Permissions are checked before calling this method", "MissingPermission")
    private fun startRecording() {
        // Initialize the AudioRecord
        audioRecorder = AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize)

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

        // Start the timer
        timer.start()
    }

    // Method to stop recording
    private fun stopRecording() {
        // Stop recording
        audioRecorder.stop()
        // Release the AudioRecord
        audioRecorder.release()
        // Set recording not in progress
        isRecording = false
        // Stop the timer
        timer.cancel()
    }
}