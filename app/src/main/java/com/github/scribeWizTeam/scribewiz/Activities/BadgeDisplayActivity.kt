package com.github.scribeWizTeam.scribewiz.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.scribeWizTeam.scribewiz.Fragments.ProfilePageFragment
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.models.BadgeModel
import com.github.scribeWizTeam.scribewiz.models.UserModel
import kotlin.random.Random

class BadgeDisplayActivity : ComponentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            badgeDisplayLayout()
        }
    }

    @Composable
    fun badgeDisplayLayout(){
        val context = LocalContext.current

        Column(
            modifier = Modifier.fillMaxSize().padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Title text
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = {
                        val backToProfile = Intent(context, ProfilePageFragment::class.java)
                        context.startActivity(backToProfile)
                    },
                    modifier = Modifier.height(50.dp).width(50.dp).align(Alignment.CenterVertically),

                ) {
                    Text("<-")
                }
                Text("My badges", fontSize = 20.sp, textAlign = TextAlign.Center)
            }
            displayBadges()
        }

    }



    // Displays the user's current badges in a grid
    @Composable
    fun displayBadges(){

        val user = UserModel.currentUser(this)
        val badges = BadgeModel.getAllBadgesFromUser(user.getOrThrow())

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp)
        ){
            items(badges.size) {i ->
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
                            contentDescription = "BadgeImage",
                            modifier = Modifier.size(60.dp).padding(bottom = 0.dp)
                        )

                        Text(
                            text = badges.elementAt(i).badgeName!!,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp),

                            )
                    }

                }
            }
        }
    }
}