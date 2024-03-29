package com.github.scribeWizTeam.scribewiz.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.google.firebase.auth.FirebaseAuth

class HomeFragment(contentLayoutId: Int = 0) : Fragment(contentLayoutId) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val fragmentAdded = mutableStateOf(false)
        return ComposeView(requireContext()).apply {
            setContent {
                ScribeWizTheme {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(50.dp))
                        Image(
                            painter = painterResource(id = R.drawable.scribewiz_logo),
                            contentDescription = "App logo",
                            modifier = Modifier.size(180.dp)
                        )
                        Spacer(Modifier.height(50.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.height(100.dp)) {
                                Box(
                                    Modifier
                                        .weight(0.5f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    ChallengesButton()
                                }
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    RecordButton()
                                }
                                Box(Modifier.weight(0.5f)) {}
                            }
                            NotesListFragmentComponent(parentFragmentManager, fragmentAdded)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ChallengesButton() {
        val modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable {
                launchNavActivity("challengesPage")
            }
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.challenge),
                contentDescription = "Challenges page",
                modifier = Modifier.size(40.dp)
            )
        }
    }

    @Composable
    fun RecordButton() {
        Button(
            modifier = Modifier
                .height(100.dp)
                .width(190.dp)
                .padding(5.dp),
            shape = RoundedCornerShape(100),
            onClick = {
                launchNavActivity("recordPage")
            }
        ) {
            Text("Record", fontSize = 25.sp)
        }
    }

    @Composable
    fun NotesListFragmentComponent(
        fragmentManager: FragmentManager,
        fragmentAdded: MutableState<Boolean>,
        modifier: Modifier = Modifier,
        tag: String = "noteListFragmentTag"
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                FrameLayout(context).apply {
                    id = ViewCompat.generateViewId()
                }
            }
        ) {
            if (!fragmentAdded.value) {
                fragmentAdded.value = true
                fragmentManager.commit {
                    add(it.id, NotesListFragment(), tag)
                }
            }
        }
    }

    private fun launchNavActivity(fragment: String) {
        val nav = Intent(requireContext(), NavigationActivity::class.java)
        nav.putExtra("fragment", fragment)
        startActivity(nav)
    }
}
