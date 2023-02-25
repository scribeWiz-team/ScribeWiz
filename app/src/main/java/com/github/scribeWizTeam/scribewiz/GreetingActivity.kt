package com.github.scribeWizTeam.scribewiz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        val intent = intent

        val greetingText = findViewById<TextView>(R.id.greetingMessage)

        val content = "Hello " + intent.getStringExtra("name") + "!"
        greetingText.text = content
    }
}