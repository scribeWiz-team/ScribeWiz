package com.github.scribeWizTeam.scribewiz


import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth


class FirebaseUIActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gotButton = findViewById<ImageButton>(R.id.GoogleLoginButton)


        gotButton.setOnClickListener {
            //set the event you want to perform when button is clicked
            //you can go to another activity in your app by creating Intent
            createSignInIntent()
        }

    }


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    // Choose authentication providers
    // AuthUI.IdpConfig.EmailBuilder().build()
    // AuthUI.IdpConfig.PhoneBuilder().build()
    // AuthUI.IdpConfig.FacebookBuilder().build()
    // AuthUI.IdpConfig.TwitterBuilder().build()

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_signout]
    }

    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_delete]
    }



}