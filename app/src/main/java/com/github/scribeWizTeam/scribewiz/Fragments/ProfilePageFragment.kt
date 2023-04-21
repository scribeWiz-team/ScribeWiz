package com.github.scribeWizTeam.scribewiz.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.firebase.ui.auth.AuthUI
import com.github.scribeWizTeam.scribewiz.FirebaseUIActivity
import com.google.firebase.auth.FirebaseAuth

class ProfilePageFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose the Composition when viewLifecycleOwner is destroyed
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                ProfilePage()
            }
        }
    }

    constructor() : this(0) {
        // Default constructor
    }

    @Composable
    fun ProfilePage(){
        val context = LocalContext.current

        // Get the current user
        val user = FirebaseAuth.getInstance().currentUser

        // Check if user is logged in, set default values otherwise
        var userName = "Guest"
        if(user != null) {
            userName = user.displayName!!
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My ScribeWiz Profile",
                style = MaterialTheme.typography.h4,
                fontSize = 30.sp
            )
            Spacer(Modifier.height(20.dp))

        /*
            // Default profile picture
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "User profile picture",
                modifier = Modifier.size(50.dp)
            )
        */

            AsyncImage(
                model = user?.photoUrl,
                contentDescription = "User profile picture",
                modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
            )

            Text(
                text = userName,
                style = MaterialTheme.typography.h4,
                fontSize = 30.sp
            )

            Spacer(Modifier.height(30.dp))

            Text(
                text = "My total recordings : 10",
                style = MaterialTheme.typography.h4,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(50.dp))

            if(user != null){
                Text(
                    text = "My friends",
                    style = MaterialTheme.typography.h4,
                    fontSize = 22.sp,
                )
            }

            Spacer(Modifier.height(110.dp))
            // LOGIN/LOG-OUT BUTTON
            Button(
                onClick = {
                    if(user != null){
                        AuthUI.getInstance().signOut(context)
                    }
                    val goHome = Intent(context, FirebaseUIActivity::class.java)
                    context.startActivity(goHome)
                },
            ) {
                if(user == null) {
                    Text("Sign in")
                }else{
                    Text("Sign out")
                }
            }
        }
    }

}