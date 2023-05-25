package com.github.scribeWizTeam.scribewiz.activities


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
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.models.UserModel
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.google.firebase.auth.FirebaseAuth


class FirebaseUIActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already signed in
        if (user != null) {
            val goHome = Intent(this@FirebaseUIActivity, NavigationActivity::class.java)
            startActivity(goHome)
        }
        setContent {
            ScribeWizTheme() {
                LoginPage()
            }

        }
    }

    private val user = FirebaseAuth.getInstance().currentUser

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    private fun addCurrentUserToDB() {
        // User needs to be refreshed for the code to detect the change
        val curUser = FirebaseAuth.getInstance().currentUser
        if (curUser != null) {
            UserModel.user(curUser.uid).onSuccess {
                it.registerAsCurrentUser(this)
            }.onFailure {
                val newUser = UserModel(
                    curUser.uid,
                    curUser.displayName ?: "new user"
                )
                newUser.registerAsCurrentUser(this)
                newUser.updateInDB()
            }
        }
    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
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
        //val response = result.idpResponse
        reloadPage()
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in

            // Add the user to the database, now they can add friends :)
            addCurrentUserToDB()


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
        UserModel.currentUser(this).onSuccess { it.unregisterAsCurrentUser(this) }

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

    private fun loginMessage(): String {
        return if (user != null) {
            "Hello, " + user.displayName + "!"
        } else {
            "Not signed in"
        }
    }

    private fun reloadPage() {
        this@FirebaseUIActivity.recreate()
    }


    @Composable
    fun LoginPage() {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp),
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

            if (user == null) {
                Button(
                    onClick = {
                        createSignInIntent()
                        reloadPage()
                    },
                ) {
                    Text("Login")
                }
            }

            if (user != null) {
                Button(
                    onClick = {
                        signOut()
                        reloadPage()
                    },
                ) {
                    Text("Sign out")
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}