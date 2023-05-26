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
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import com.github.scribeWizTeam.scribewiz.util.FaqQueries

class HelpFragment : Fragment() {
    private val faqs = FaqQueries.faqs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                ScribeWizTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "FAQs",
                            style = MaterialTheme.typography.h5,
                            fontSize = 20.sp
                        )

                        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
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

}

@Composable
fun ExpandableCard(title: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
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