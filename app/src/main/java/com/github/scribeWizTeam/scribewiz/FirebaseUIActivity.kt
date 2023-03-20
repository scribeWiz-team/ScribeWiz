package com.github.scribeWizTeam.scribewiz


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth


class FirebaseUIActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            LoginPage()
        }
    }

    private val user = FirebaseAuth.getInstance().currentUser

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
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_ScribeWiz)
            .setLogo(R.mipmap.ic_launcher_round)
            .build()
        signInLauncher.launch(signInIntent)
    }

    @SuppressLint("SetTextI18n")
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        reloadPage()
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            if (user != null) {

            }

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
                reloadPage()
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

    private fun loginMessage(): String{
        return if(user != null){
            "Hello, " + user.displayName + "!"
        }else{
            "Not signed in"
        }
    }

    private fun reloadPage(){
        this@FirebaseUIActivity.recreate()
    }


    @Composable
    fun LoginPage() {

        Column(
            modifier = Modifier.fillMaxSize().padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ScribeWiz \uD83C\uDFBC",
                style = MaterialTheme.typography.h4,
                fontSize = 50.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = loginMessage(),
                style = MaterialTheme.typography.h4,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    createSignInIntent()
                    reloadPage()},
            ) {
                Text("Login")
            }

            Button(
                onClick = {
                    signOut()
                    reloadPage()
                    },
            ) {
                Text("Sign out")
            }

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    val navigate = Intent(this@FirebaseUIActivity, MainActivity::class.java)
                    startActivity(navigate)
                          },
            ) {
                Text("Home")
            }

        }
    }
}