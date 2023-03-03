package com.github.scribeWizTeam.scribewiz

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "database-name"
        ).build()
        val boredActDao = db.boredActivityDao()

        val okButton = findViewById<Button>(R.id.okButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val boredApi = retrofit.create(BoredApi::class.java)

        okButton.setOnClickListener {
            boredApi.getActivity().enqueue(object : Callback<BoredActivity> {
                override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {

                    val text = when(val body = response.body()) {
                        null -> "empty activity"
                        else -> body.activity
                    }

                    findViewById<TextView>(R.id.activityText).text = text
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                    findViewById<TextView>(R.id.activityText).text = "empty activity"
                }
            })
        }
    }
}