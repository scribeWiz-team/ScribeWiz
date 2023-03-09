package com.example.firebasebootcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun set(view: View) {
        val email : String = findViewById<TextView>(R.id.eMail).text.toString()
        val phoneNumber : String = findViewById<TextView>(R.id.phoneNumber).text.toString()

        val db: DatabaseReference = Firebase.database.reference
        db.child(phoneNumber).setValue(email)

    }
    fun get(view: View) {
        val emailView = findViewById<TextView>(R.id.eMail)
        val phoneNumber = findViewById<TextView>(R.id.phoneNumber).text

        val db: DatabaseReference = Firebase.database.reference
        val future = CompletableFuture<String>()
        db.child(phoneNumber.toString()).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        future.thenAccept {
            emailView.text = it
        }

    }
}