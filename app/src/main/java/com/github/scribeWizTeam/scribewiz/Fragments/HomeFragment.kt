import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.github.scribeWizTeam.scribewiz.Activities.ChallengeNotesActivity

class HomeFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

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
                        text = "Home",
                        style = MaterialTheme.typography.h4,
                        fontSize = 50.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        val intent = Intent(context, ChallengeNotesActivity::class.java)
                        intent.putExtra("challengeId", "YourChallengeId")
                        intent.putExtra("userId", "YourUserId")
                        startActivity(intent)
                    }) {
                        Text(text = "Go to Challenge")
                    }
                }
            }
        }
    }

}