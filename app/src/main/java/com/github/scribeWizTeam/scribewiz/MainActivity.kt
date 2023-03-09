package com.github.scribeWizTeam.scribewiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gotButton = findViewById<Button>(R.id.mainGoButton)
        val loginButton = findViewById<Button>(R.id.LoginButton)
        val nameText = findViewById<EditText>(R.id.mainName)

        gotButton.setOnClickListener {
            //set the event you want to perform when button is clicked
            //you can go to another activity in your app by creating Intent
            val intent = Intent(this, GreetingActivity::class.java)
            intent.putExtra("name", nameText.text.toString())
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            //set the event you want to perform when button is clicked
            //you can go to another activity in your app by creating Intent
            val intent = Intent(this, FirebaseUIActivity::class.java)
            startActivity(intent)
        }

    }
}