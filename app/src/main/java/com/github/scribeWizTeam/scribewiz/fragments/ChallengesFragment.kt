package com.github.scribeWizTeam.scribewiz.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.activities.ChallengeNotesActivity
import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import com.github.scribeWizTeam.scribewiz.ui.theme.ScribeWizTheme
import java.text.SimpleDateFormat
import java.util.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChallengesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChallengesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ScribeWizTheme() {
                    if (!isTest) {
                        val challenges =
                            remember {
                                ChallengeModel.challengesAvailable().toMutableStateList()
                            }
                        ChallengeList(challenges = challenges)
                    } else {
                        val challenges =
                            remember {
                                ChallengeModel.challengesAvailableTest().toMutableStateList()
                            }
                        ChallengeList(challenges = challenges)
                    }
                }


            }
        }
    }

    companion object {
        var isTest: Boolean = false //Test mode

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChallengesFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChallengesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    @Composable
    fun SpecificChallengeButton(challenge: ChallengeModel) {

        fun challengeOnClick() {

            val openChallenge = Intent(context, ChallengeNotesActivity::class.java)
            openChallenge.putExtra("challengeId", challenge.id)
            startActivity(openChallenge)
        }

        Button(
            onClick = ::challengeOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("challengeButton")
        ) {
            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxWidth()
            ) {
                Text(text = challenge.name ?: "No name specified")
                Text(text = niceDurationDateFormatting(challenge.startDate, challenge.endDate))
            }

        }
    }

    @Composable
    fun ChallengeList(challenges: List<ChallengeModel>) {

        Column {
            Spacer(modifier = Modifier.height(8.dp))
            challenges.forEach { challenge ->
                SpecificChallengeButton(challenge = challenge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    private fun niceDurationDateFormatting(
        startingDate: Date?,
        endDate: Date?
    ): String {

        if (startingDate == null && endDate == null) {
            return "No date specified"
        }
        if (startingDate == null) {
            return "To ${dateFormatting(endDate!!)}"
        }
        if (endDate == null) {
            return "From ${dateFormatting(startingDate)}"
        }

        return "From ${dateFormatting(startingDate)} to ${dateFormatting(endDate)}"
    }

    private fun dateFormatting(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy, HH:mm")
        return formatter.format(date) ?: throw Exception("There was a problem with your date")
    }
}