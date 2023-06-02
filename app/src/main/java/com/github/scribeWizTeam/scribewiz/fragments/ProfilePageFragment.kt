package com.github.scribeWizTeam.scribewiz.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import coil.compose.AsyncImage
import com.firebase.ui.auth.AuthUI
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.activities.BadgeDisplayActivity
import com.github.scribeWizTeam.scribewiz.activities.FirebaseUIActivity
import com.github.scribeWizTeam.scribewiz.models.BadgeModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


class ProfilePageFragment(contentLayoutId: Int = 0) : Fragment(contentLayoutId) {

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
                ScribeWizTheme {
                    ProfilePage()
                }
            }
        }
    }

    constructor() : this(0) {
        // Default constructor
    }

    /**
     * Composable function to display the profile page.
     */
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ProfilePage() {
        var isGuest = true
        val context = LocalContext.current
        var userProfile: UserModel = remember { UserModel() }
        // Badge collection button/display
        UserModel.currentUser(context).onSuccess {
            userProfile = it
            isGuest = false
        }

        // Check if user is logged in, set default values otherwise
        var userName = userProfile.userName
        val numRecordings = userProfile.userNumRecordings
        val friendsList = remember { getUserNamesFromList(getFriendIDList(context)) }
        if (userProfile.id != "null") {
            userName = userProfile.userName!!
        }

        /*
        ***************************************************************
                                HEADER
        ***************************************************************
        */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My ScribeWiz Profile",
                style = MaterialTheme.typography.h4,
                fontSize = 30.sp
            )
            Spacer(Modifier.height(20.dp))

            /*
        ***************************************************************
                            LOCAL USER DETAILS
        ***************************************************************
        */
            if (user != null) {
                // Use user profile picture
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            } else {
                // Default profile picture
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "User profile picture",
                    modifier = Modifier.size(100.dp)
                )
            }
            Text(
                text = userName!!,
                style = MaterialTheme.typography.h4,
                fontSize = 30.sp
            )

            /*
            ***************************************************************
                                LOGIN/LOG-OUT BUTTON
            ***************************************************************
            */
            Button(
                onClick = {
                    if(user != null) {
                        AuthUI.getInstance().signOut(context)
                    }
                    if(!isGuest) {
                        UserModel.currentUser(context).onSuccess {
                            it.unregisterAsCurrentUser(context)
                        }
                        reloadFragment()
                    }else{
                        val goHome = Intent(context, FirebaseUIActivity::class.java)
                        startActivity(goHome)
                    }
                },
                modifier = Modifier
                    .height(60.dp)
                    .width(100.dp)
                    .padding(top = 10.dp)
            ) {
                if (isGuest) {
                    Text("Sign in")
                } else {
                    Text("Sign out")
                }
            }

            /*
            ***************************************************************
                              NUM RECORDINGS AND BADGES
            ***************************************************************
            */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(20.dp, 0.dp, 20.dp, 0.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My recordings : $numRecordings",
                    style = MaterialTheme.typography.h4,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                if (!isGuest) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "My badges button",
                            modifier = Modifier.clickable {

                                BadgeModel.addBadgeToUser(userProfile, BadgeModel("123", "Best Guitar Solo"))
                                val openBadges = Intent(context, BadgeDisplayActivity::class.java)
                                startActivity(openBadges)
                            }
                        )
                        Text("My badges", modifier = Modifier.offset(y = (-20).dp))
                    }
                }
            }

            /*
            ***************************************************************
                            ADD FRIENDS INPUT AND BUTTON
            ***************************************************************
            */
            val localKeyboard = LocalSoftwareKeyboardController.current!!
            if (!isGuest) {
                val text = remember { mutableStateOf("") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = text.value,
                        onValueChange = { text.value = it },
                        label = { Text("Search for user") },
                        modifier = Modifier
                            .height(50.dp)
                            .width(250.dp)
                            .testTag("SearchFriendField"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                localKeyboard.hide()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (!friendsList.contains(text.value))
                                addFriend(userProfile, text.value)

                            // Refresh the user profile
                            userProfile = UserModel.user(userProfile.id).getOrThrow()
                            reloadFragment()

                        },
                        modifier = Modifier
                            .height(50.dp)
                            .width(100.dp)
                    )
                    {
                        Text("Add friend", textAlign = TextAlign.Center)
                    }
                }
            }

            if (!isGuest) {
                Text(
                    text = "My friends",
                    style = MaterialTheme.typography.h4,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                DrawFriendsGrid(friendsList)

            }
        }
    }

    /**
     * Creates a grid with a card corresponding to each friend in the provided friends list.
     *
     * @param friendsList A list of names to display on each friend's card
     */
    @Composable
    private fun DrawFriendsGrid(friendsList: MutableList<String>){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(friendsList.size + 1) { item ->
                Card(
                    modifier = Modifier.padding(4.dp),
                    backgroundColor = Color(
                        red = Random.nextInt(0, 255),
                        green = Random.nextInt(0, 255),
                        blue = Random.nextInt(0, 255)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "FriendPP",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(bottom = 0.dp)
                        )

                        Text(
                            text = "Netzu",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }

    /**
     * Adds a user to the specified user's friend list. If the specified username
     * is not registered in the database, this function will not do anything.
     *
     * @param user The currently signed-in user
     * @param friendName The friend's userName, as displayed on their profile
     *
     */
    private fun addFriend(user : UserModel, friendName: String){

        // Cannot add oneself as friend
        if(friendName == user.userName){
            return
        }

        /* Search the database for a match on friendName
         * If successful, add the friend's ID to the user's friend ID list
         * It does NOT notify the friend that they have been added, nor
         * does it add the user to the friend's friend list
         */
        runBlocking {
            val job = launch{
                db.collection(UserModel.COLLECTION)
                    .get()
                    .await()
                    .toObjects(UserModel::class.java)
                    .forEach{
                        if(it.userName == friendName){
                            user.friends!!.add(it.id)
                            user.updateInDB()
                        }
                    }
            }
            job.join()
        }
    }

    /**
     * Gets the current user's friends list, returns an empty list on failure
     * @return The list of friend user IDs
     */
    private fun getFriendIDList(context : Context) : MutableList<String>{
        UserModel.currentUser(context).onSuccess {
            return it.friends!!
        }
        return mutableListOf()
    }

    /**
     * Gets the respective user names associated to each userID from the database.
     * Any invalid IDs will be skipped.
     * @param userIDList The list of user IDs to retrieve usernames from
     * @return The list of usernames associated with the provided IDs
     */
    private fun getUserNamesFromList(userIDList : MutableList<String>) : MutableList<String> {
        val userNameList = mutableListOf<String>()

        runBlocking {
            val job = launch {
                for(id in userIDList){
                    val userDoc = db.collection(UserModel.COLLECTION)
                        .document(id)
                        .get().await()

                    if(userDoc.exists()){
                        userDoc.toObject<UserModel>()?.let {user ->
                            userNameList.add(user.userName!!)
                        }
                    }
                }
            }
            job.join()
        }

        return userNameList
    }

    /**
     * Reloads the profile page fragment, usually done when data is
     * modified in the database
     */
    private fun reloadFragment(){
        // Reload current fragment
        val fragmentManager = requireActivity().supportFragmentManager
        val ft1: FragmentTransaction =
            fragmentManager.beginTransaction()
        ft1.detach(this@ProfilePageFragment)
        ft1.commit()

        val ft2: FragmentTransaction =
            fragmentManager.beginTransaction()
        ft2.attach(this@ProfilePageFragment)
        ft2.commit()
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