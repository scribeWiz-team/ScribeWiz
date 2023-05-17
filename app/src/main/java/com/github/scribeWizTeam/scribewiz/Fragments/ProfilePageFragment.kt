package com.github.scribeWizTeam.scribewiz.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.github.scribeWizTeam.scribewiz.Activities.BadgeDisplayActivity
import com.github.scribeWizTeam.scribewiz.Activities.FirebaseUIActivity
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.models.BadgeModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread
import kotlin.random.Random


class ProfilePageFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    // Get the current user
    private val user = FirebaseAuth.getInstance().currentUser

    // Retrieve the database
    private val db = Firebase.firestore

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
        var userProfile : UserModel = remember{ UserModel() }
        // Badge collection button/display
        UserModel.currentUser(context).onSuccess{
            userProfile = it
        }

        // Check if user is logged in, set default values otherwise
        var userName = "Guest"
        var numRecordings = "0"
        val friendsList = hashMapOf(Pair("exampleFriendID0", "Chris"),
            Pair("exampleFriendID1", "Louis"),
            Pair("exampleFriendID2", "Baptiste"),
            Pair("exampleFriendID3", "Noe"),
            Pair("exampleFriendID4", "Louca"),
            Pair("exampleFriendID5", "Zach"),
            Pair("exampleFriendID6", "George"),
            Pair("exampleFriendID7", "Alice"),
            Pair("exampleFriendID8", "Bob"))
        if(userProfile.id != "") {
            Log.w("READINGFRIENDSLIST", "USER EXISTS")
             db.collection("Users").document(userProfile.id!!)
                 .get().addOnSuccessListener { data ->
                     numRecordings = data.get("userNumRecordings").toString()
                     // TODO: Currently bugging
                     //friendsList = data.get("friendsList") as HashMap<String, String>
                 }

            userName = userProfile.userName!!
        }
        friendsList.forEach{Log.w(it.key, it.value)}
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
                        .size(60.dp)
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
                    UserModel.currentUser(context).onSuccess{
                        it.unregisterAsCurrentUser(context)
                    }

                    val goHome = Intent(context, FirebaseUIActivity::class.java)
                    context.startActivity(goHome)
                },
                modifier = Modifier.height(60.dp).width(100.dp).padding(top = 10.dp)
            ) {
                if(user == null) {
                    Text("Sign in")
                }else{
                    Text("Sign out")
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(PaddingValues(20.dp, 0.dp, 20.dp, 0.dp)),
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "My recordings : $numRecordings",
                    style = MaterialTheme.typography.h4,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )


                if(userProfile.id != ""){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Image(painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "My badges button",
                            modifier = Modifier.clickable {

                                BadgeModel.addBadgeToUser(userProfile, null)

                                val openBadges = Intent(context, BadgeDisplayActivity::class.java)
                                startActivity(openBadges)
                            }
                        )

                        Text("My badges", modifier=Modifier.offset(y= (-20).dp))
                    }
                }
            }

            if(user != null) {
                val text = remember { mutableStateOf("") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = text.value,
                        onValueChange = { text.value = it },
                        label = { Text("Search for user") },
                        modifier = Modifier.height(50.dp).width(250.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            sendFriendRequest(text.value)
                        },
                        modifier = Modifier.height(50.dp)
                            .width(100.dp)
                    )
                    {
                        Text("Add friend", textAlign = TextAlign.Center)
                    }
                }
            }
            // TODO: Add user null check once bug is fixed
            //if(user != null){
                Text(
                    text = "My friends",
                    style = MaterialTheme.typography.h4,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                DrawFriendsGrid(friendsList)


        }
    }

    @Composable
    fun DrawFriendsGrid(friendsList : MutableMap<String, String>){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(friendsList.size) {item ->
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
                            text = friendsList.values.elementAt(item),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp),

                        )
                    }
                    // Default profile picture

                }
            }
        }
    }

    private fun sendFriendRequest(friendName: String){

        val ret = UserModel.currentUser(requireContext())
        if (ret.isFailure) {
            return
        }

        val localUser = ret.getOrThrow()

        if(user != null) {
            // Cannot add oneself as friend
            if(friendName == user.displayName){
                return
            }
            thread {

                // Data abstraction leak, will fix in future
                val snapshot = await(db.collection(UserModel.COLLECTION).get())

                // Check if no users exist in the database
                if(snapshot.isEmpty) {
                    Log.w("ADDINGFRIEND", "EMPTYDB")
                }

                // Go through user list, check for a match
                snapshot.forEach { doc ->
                    Log.w("ADDINGFRIEND", "READINGUSER")
                    // Checking for a match in the database
                    if (doc.get("userName") == friendName) {
                        // Check that the user is not already in the local friend list
                        if(localUser.friends?.contains(doc.id) == true){
                            return@thread
                        }

                        UserModel.user(doc.get("id") as String).onSuccess { toUser ->
                            localUser.id?.let { toUser.friendRequests?.add(it) }
                            toUser.updateInDB()
                        }
                    }
                }
            }
        }
    }
    /*

    // Work in progress, to be finished next sprint
    fun RemoveFriend(friendName:String){
        if(user != null && friendName != null) {
            thread {
                db.collection("Users").document(user.uid)
                    .collection("friendsList")
            }
        }
    }
    */
}