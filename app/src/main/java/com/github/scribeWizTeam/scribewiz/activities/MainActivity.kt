package com.github.scribeWizTeam.scribewiz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import coil.compose.AsyncImage
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.fragments.NotesListFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Get the current user
    private val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.width(10.dp))
                    HelpButton()
                    Spacer(Modifier.weight(1f))
                    ProfileButton()
                    Spacer(Modifier.width(10.dp))
                }
                Spacer(Modifier.height(50.dp))
                Image(
                    painter = painterResource(id = R.mipmap.scribewiz_logo),
                    contentDescription = "App logo",
                    modifier = Modifier.size(180.dp)
                )
                Spacer(Modifier.height(50.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        ChallengesButton()
                        RecordButton()
                    }
                    NotesListFragmentComponent(supportFragmentManager)
                }
            }
        }
    }

    @Composable
    fun HelpButton() {
        val modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable {
                launchNavActivity("helpPage")
            }
        Image(
            painter = painterResource(id = R.mipmap.help),
            contentDescription = "Help page",
            modifier = modifier
        )
    }

    @Composable
    fun ChallengesButton() {
        Button(
            modifier = Modifier
                .height(50.dp)
                .width(190.dp)
                .padding(5.dp),
            onClick = {
                launchNavActivity("challengesPage")
            }
        ) {
            Text("Challenges")
        }
    }

    @Composable
    fun RecordButton() {
        Button(
            modifier = Modifier
                .height(50.dp)
                .width(190.dp)
                .padding(5.dp),
            onClick = {
                launchNavActivity("recordPage")
            }
        ) {
            Text("Record")
        }
    }

    @Composable
    fun ProfileButton() {
        val modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
        if (user != null) {
            // Use user profile picture
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "User profile picture",
                modifier = modifier.clickable {
                    launchNavActivity("profilePage")
                }
            )
        } else {
            // Default profile picture
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "User profile picture",
                modifier = modifier.clickable {
                    val login = Intent(this, FirebaseUIActivity::class.java)
                    this.startActivity(login)
                }
            )
        }
    }

    @Composable
    fun NotesListFragmentComponent(
        fragmentManager: FragmentManager,
        modifier: Modifier = Modifier,
        tag: String = "noteListFragmentTag"
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                FrameLayout(context).apply {
                    id = ViewCompat.generateViewId()
                }
            },
            update = {
                val fragmentAlreadyAdded = fragmentManager.findFragmentByTag(tag) != null

                if (!fragmentAlreadyAdded) {
                    fragmentManager.commit {
                        add(it.id, NotesListFragment(), tag)
                    }
                }
            }
        )
    }

    private fun launchNavActivity(fragment: String) {
        val nav = Intent(this, NavigationActivity::class.java)
        nav.putExtra("fragment", fragment)
        startActivity(nav)
    }
}
