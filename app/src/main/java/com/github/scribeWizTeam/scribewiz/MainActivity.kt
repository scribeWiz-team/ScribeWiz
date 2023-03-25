package com.github.scribeWizTeam.scribewiz
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column (modifier = Modifier.fillMaxSize().padding(all = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        val navigate = Intent(this@MainActivity, FirebaseUIActivity::class.java)
                        startActivity(navigate)
                    }
                ) {
                    Text("To login page")
                }
            }
        }
    }

}


