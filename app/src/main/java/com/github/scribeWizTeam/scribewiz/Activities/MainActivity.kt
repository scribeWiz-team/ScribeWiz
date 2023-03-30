package com.github.scribeWizTeam.scribewiz.Activities
import android.content.Intent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.scribeWizTeam.scribewiz.FirebaseUIActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this@MainActivity, FirebaseUIActivity::class.java))
    }

}


