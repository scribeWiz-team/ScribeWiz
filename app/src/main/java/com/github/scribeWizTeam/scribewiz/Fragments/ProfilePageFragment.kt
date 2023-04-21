package com.github.scribeWizTeam.scribewiz.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.firebase.ui.auth.AuthUI
import com.github.scribeWizTeam.scribewiz.FirebaseUIActivity
import com.github.scribeWizTeam.scribewiz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

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

        // Retrieve the database
        val db = Firebase.firestore

        // Check if user is logged in, set default values otherwise
        var userName = "Guest"
        var numRecordings = "0"
        var friendsList = hashMapOf<Int, String>(Pair(0, "Chris"), Pair(1, "Louis"),
            Pair(2, "Baptiste"), Pair(3, "Noe"), Pair(4, "Louca"), Pair(5, "Zach"), Pair(6, "George")
            , Pair(7, "Alice"), Pair(8, "Bob"))
        if(user != null) {
             db.collection("Users").document(user.uid).get()
                .addOnSuccessListener { data ->
                    numRecordings = data.get("userNumRecordings").toString()
                    // TODO: Currently bugging
                    //friendsList = data.get("friends") as HashMap<Int, String>
                }


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

            if(user != null){
                // Use user profile picture
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }else {
                // Default profile picture
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "User profile picture",
                    modifier = Modifier.size(100.dp)
                )
            }



            Text(
                text = userName,
                style = MaterialTheme.typography.h4,
                fontSize = 30.sp
            )

            // LOGIN/LOG-OUT BUTTON
            Button(
                onClick = {
                    if(user != null){
                        AuthUI.getInstance().signOut(context)
                    }
                    val goHome = Intent(context, FirebaseUIActivity::class.java)
                    context.startActivity(goHome)
                },
                modifier = Modifier.height(60.dp).width(100.dp).padding(top = 10.dp, bottom = 10.dp)
            ) {
                if(user == null) {
                    Text("Sign in")
                }else{
                    Text("Sign out")
                }
            }
            Text(
                text = "My total recordings : $numRecordings",
                style = MaterialTheme.typography.h4,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(20.dp))

            // TODO: Add user null check once bug is fixed
            //if(user != null){
                Text(
                    text = "My friends",
                    style = MaterialTheme.typography.h4,
                    fontSize = 22.sp,
                )
                DrawFriendsGrid(friendsList)
            //}

        }
    }

    @Composable
    fun DrawFriendsGrid(friendsList : HashMap<Int, String>){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(friendsList.size) { item ->
                Card(
                    modifier = Modifier.padding(4.dp),
                    backgroundColor = Color(
                        red = Random.nextInt(0, 255),
                        green = Random.nextInt(0, 255),
                        blue = Random.nextInt(0, 255)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally){

                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "FriendPP",
                            modifier = Modifier.size(60.dp).padding(bottom = 0.dp)
                        )

                        Text(
                            text = friendsList[item]!!,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                        )
                    }
                    // Default profile picture

                }
            }
        }
    }

}