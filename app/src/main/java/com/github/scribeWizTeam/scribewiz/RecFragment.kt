package com.github.scribeWizTeam.scribewiz

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.IOException
import java.util.Timer

class RecFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {


    private lateinit var recordButton: Button //button to start and stop recording
    private lateinit var mediaRecorder: MediaRecorder //media recorder to record audio
    private lateinit var recordingTimeText: TextView //text to show recording time
    private lateinit var timer: CountDownTimer //timer to show recording time

    private var isRecording = false //boolean to check if recording is in progress
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200 //request code for permission

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec, container, false) //inflate the layout
        recordButton = view.findViewById(R.id.record_button) //get the button
        recordingTimeText = view.findViewById(R.id.time_recording) //get the text

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

        //check if the app has permission to record audio
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }

        return view
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            else{
                //permission granted
                startRecording()
            }
        }
    }

    private fun getOutputFilePath(): String {
        return requireContext().externalCacheDir?.absolutePath + "/recording.3gp"
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder() //initialize the media recorder
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC) //set the audio source
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) //set the output format
        mediaRecorder.setOutputFile(getOutputFilePath()) //set the output file path
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //set the encoder

        try {
            mediaRecorder.prepare() //prepare the media recorder
            mediaRecorder.start() //start recording
            isRecording = true //set recording in progress
            recordButton.text = "Stop Recording" //change the button text

            //set the timer
            timer = object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    recordingTimeText.text = "Recording Time: " + millisUntilFinished / 1000
                }

                override fun onFinish() {
                    stopRecording()
                }
            }
            //start the timer
            timer.start()


        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Failed to start the recording", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    private fun stopRecording() {
        mediaRecorder.stop() //stop recording
        mediaRecorder.release() //release the media recorder
        isRecording = false //set recording not in progress
        recordButton.text = "Start Recording" //change the button text
    }
}