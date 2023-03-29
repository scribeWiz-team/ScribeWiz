package com.github.scribeWizTeam.scribewiz

import android.Manifest
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

class RecFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    private lateinit var recordButton: Button //button to start and stop recording
    private lateinit var mediaRecorder: MediaRecorder //media recorder to record audio
    private lateinit var recordingTimeText: TextView //text to show recording time
    private lateinit var timer: CountDownTimer //timer to show recording time

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec, container, false) //inflate the layout
        recordButton = view.findViewById(R.id.record_button) //get the button
        recordingTimeText = view.findViewById(R.id.time_recording) //get the text

        //check if the app has permission to record audio
        checkPermission()
        //set the event for the button
        setEvent()
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
                startRecording()
            }
        }
    }

    fun getOutputFilePath(): String {
        return requireContext().externalCacheDir?.absolutePath + "/recording.3gp"
    }

    private fun startRecording() {
        //initialize the media recorder
        mediaRecorder = MediaRecorder()
        //set the audio source
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        //set the output format
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        //set the output file path
        mediaRecorder.setOutputFile(getOutputFilePath())
        //set the encoder
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            //prepare the media recorder
            mediaRecorder.prepare()
            //start recording
            mediaRecorder.start()
            //set recording in progress
            isRecording = true
            //change the button text
            recordButton.text = "Stop Recording"

            //set the timer
            timer = object : CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    recordingTimeText.text = ((MILLISINFUTURE - millisUntilFinished) / COUNT_DOWN_INTERVAL).toString()
                }
                override fun onFinish() {
                //do nothing
                }
            }
            //start the timer
            timer.start()

        //if there is any error in recording
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Failed to start the recording", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    //method to stop recording
    private fun stopRecording() {
        //stop recording
        mediaRecorder.stop()
        //release the media recorder
        mediaRecorder.release()
        //set recording not in progress
        isRecording = false
        //change the button text
        recordButton.text = "Start Recording"
        //stop the timer
        timer.cancel()
        //set the recording time to 0
        recordingTimeText.text = "Recording saved!"
    }
}