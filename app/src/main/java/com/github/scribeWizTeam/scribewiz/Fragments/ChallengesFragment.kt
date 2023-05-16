package com.github.scribeWizTeam.scribewiz.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.scribeWizTeam.scribewiz.R
import com.github.scribeWizTeam.scribewiz.models.ChallengeModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChallengesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChallengesFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
                val challenges =
                    remember {
                        ChallengeModel.challengesAvailable().toMutableStateList()
                    }
                ChallengeList(challenges = challenges)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChallengesFragment.
         */
        // TODO: Rename and change types and number of parameters
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
    fun specificChallengeButton(challenge: ChallengeModel) {

        fun challengeOnClick(): Unit {

        }

        Button(
            onClick = ::challengeOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxWidth()
            ) {
                Text(text = challenge.name)
                Text(text = niceDurationDateFormatting(challenge.dateBeginning, challenge.dateEnd))
            }

        }
    }

    @Composable
    fun ChallengeList(challenges: List<ChallengeModel>) {

        Column {
            Spacer(modifier = Modifier.height(8.dp))
            challenges.forEach { challenge ->
                specificChallengeButton(challenge = challenge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    private fun niceDurationDateFormatting(
        startingDate: LocalDateTime?,
        endDate: LocalDateTime?
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

    private fun dateFormatting(date: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, HH:mm\"")
        return date.format(formatter) ?: throw Exception("There was a problem with your date")
    }
}