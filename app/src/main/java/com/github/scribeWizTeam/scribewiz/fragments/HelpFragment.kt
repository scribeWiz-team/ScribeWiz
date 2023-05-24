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

    private val faqs = mapOf(
        "How to log in with Google?" to "Answer for how to log in with Google.",
        "Can I use the app offline?" to "Answer for can I use the app offline.",
        "What are musicxml files?" to "Answer for what are musicxml files.",
        "How do I record?" to "Answer for how do I record.",
        "How do I share my masterpieces with friends on the app?" to "Answer for how do I share my masterpieces with friends on the app.",
        "How do I delete a transcription?" to "Answer for how do I delete a transcription.",
        //TODO: add more FAQS here and edit existing ones.

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
                        items(faqs.toList()) { (faq, answer) ->
                            ExpandableCard(
                                title = faq,
                                answer = answer
                            )
                        }
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