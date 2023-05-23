package com.github.scribeWizTeam.scribewiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*

class HelpFragment : Fragment() {

    private val faqs = listOf(
        "How to log in with Google?",
        "Can I use the app offline?",
        "What are musicxml files?",
        //TODO: add more FAQS here and adjust
    )

    private val helpTopics = listOf(
        "Getting Started",
        "Recording",
        "Transcriptions",
        "Library",
        "Weekly Challenges",
        //TODO: add more help topics here and adjust
    )

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Help",
                        style = MaterialTheme.typography.h4,
                        fontSize = 50.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "FAQs",
                        style = MaterialTheme.typography.h5,
                        fontSize = 30.sp
                    )

                    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                        items(faqs) { faq ->
                            ExpandableCard(
                                title = faq,
                                answer = faq //TODO: replace this with the actual answer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Help Topics",
                        style = MaterialTheme.typography.h5,
                        fontSize = 30.sp
                    )

                    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                        items(helpTopics) { topic ->
                            ExpandableCard(
                                title = topic,
                                answer = topic //TODO: replace this with the actual answer
                            )
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ExpandableCard(title: String, answer: String) {
        var expanded by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6
                )

                // Use AnimatedVisibility for the expand/shrink animation
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(animationSpec = tween(300)),
                    exit = shrinkVertically(animationSpec = tween(300))
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = answer,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }

}