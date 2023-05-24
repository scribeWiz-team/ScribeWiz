package com.github.scribeWizTeam.scribewiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

class HelpFragment : Fragment() {

    private val helpTopics = listOf(
        "Topic 1",
        "Topic 2",
        "Topic 3",
        // Add more topics here
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
                    modifier = Modifier.fillMaxSize().padding(all = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Help",
                        style = MaterialTheme.typography.h4,
                        fontSize = 50.sp
                    )

                    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                        items(helpTopics) { topic ->
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .fillMaxWidth(),
                                elevation = 4.dp
                            ) {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = topic,
                                    style = MaterialTheme.typography.h6
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
