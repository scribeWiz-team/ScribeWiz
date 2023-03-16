package com.github.scribeWizTeam.scribewiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleForm()
        }
    }
}

fun compute_presentation(name: String, raw_age: String): String {
    val current_year = 2023
    val age = raw_age.toIntOrNull()
    val birth_year = age?.let{current_year - it}
    if (age == null){
        return "Please provide a valid age"
    } else if (age <= 0){
        return "You are not even born ! How is that possible ?"
    } else {
        return "Hello $name, you were born in $birth_year !"
    }
}

@Composable
fun SimpleForm(){
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var presentation by remember { mutableStateOf("") }

    Column (modifier = Modifier.fillMaxSize().padding(all = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Simple hello world",
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") }
        )
        Button(
            onClick = { presentation = compute_presentation(name, age)},
        ) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = presentation,
            style = MaterialTheme.typography.body1
        )
    }
}
/*package com.github.scribeWizTeam.scribewiz

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
}*/