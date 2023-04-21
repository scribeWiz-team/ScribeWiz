package com.github.scribeWizTeam.scribewiz.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.PermissionsManager
import com.github.scribeWizTeam.scribewiz.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {


    private val audioSource = MediaRecorder.AudioSource.MIC
    private val sampleRateInHz = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

    private lateinit var recordButton: Button //button to start and stop recording
    private lateinit var audioRecorder: AudioRecord //media recorder to record audio
    private lateinit var recordingTimeText: TextView //text to show recording time
    private lateinit var timer: CountDownTimer //timer to show recording time

    private lateinit var outputFile: File
    private lateinit var outputFilePath: String
    var isRecording = false //boolean to check if recording is in progress
    val MILLISINFUTURE = 9999999L //number of milliseconds maximum record time
    val COUNT_DOWN_INTERVAL = 1000L //number of milliseconds between each tick

    constructor() : this(0) {
        // Default constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec, container, false) //inflate the layout
        recordButton = view.findViewById(R.id.record_button) //get the button
        recordingTimeText = view.findViewById(R.id.time_recording) //get the text

        //check if the app has permission to record audio
        PermissionsManager().checkPermissionThenExecute(this, requireContext(), Manifest.permission.RECORD_AUDIO) {
            //set the event for the button
            setEvent()
        }
        return view
    }
    //This function is called when the record button is clicked
    private fun setEvent() {
        recordButton.setOnClickListener {
            //set the event you want to perform when button is clicked
            //you can go to another activity in your app by creating Intent
            if (!isRecording) {
                //start recording
                startRecording()
            } else {
                //stop recording
                stopRecording()
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
        recordButton.text = "Stop Recording"

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

        // Set the timer
        timer = object : CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                recordingTimeText.text = ((MILLISINFUTURE - millisUntilFinished) / COUNT_DOWN_INTERVAL).toString()
            }

            override fun onFinish() {
                // Do nothing
            }
        }
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
        // Change the button text
        recordButton.text = "Start Recording"
        // Stop the timer
        timer.cancel()
        // Set the recording time to 0
        recordingTimeText.text = "Recording saved!"
    }
}