package com.github.scribeWizTeam.scribewiz

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.activities.NavigationActivity
import com.github.scribeWizTeam.scribewiz.fragments.HelpFragment
import com.github.scribeWizTeam.scribewiz.util.FaqQueries
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpFragmentTest {

    // Creating mock FAQs to use in tests, instead of using FAQs which require scrolling.
    private val mockFaqs = mapOf(
        "Mock Question 1?" to "Mock Answer 1",
        "Mock Question 2?" to "Mock Answer 2"
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavigationActivity>()


    // This test verifies that the first FAQ query in the list matches the expected string
    @Test
    fun faqQueries_FirstQuery() {
        val expected = "To log in with Google, select 'Sign in with Google' on the login screen or from the profile screen. " +
                "Please note that you must have an existing Google account to use this feature."
        assertEquals(expected, FaqQueries.faqs["How to log in with Google?"])
    }

    // This test verifies that the "FAQs" title is displayed when the HelpFragment is launched
    @Test
    fun helpFragment_DisplayHelpTitle() {
        launchFragmentInContainer<HelpFragment>()
        composeTestRule.onNodeWithText("FAQs").assertIsDisplayed()
    }

    // This test verifies that each FAQ in the list is not empty
    @Test
    fun helpFragment_FAQsAreNotEmpty() {

        launchFragmentInContainer<HelpFragment>()
        val faqs = FaqQueries.faqs

        faqs.forEach { (faq, _) ->
            assert(faq.isNotEmpty())
        }
    }

    // This test verifies that the first FAQ's title is displayed when the HelpFragment is launched
    @Test
    fun helpFragment_DisplayFirstFAQTitle() {
        launchFragmentInContainer<HelpFragment>()
        val faqs = FaqQueries.faqs

        val firstFaq = faqs.keys.first()
        composeTestRule.onNodeWithText(firstFaq).assertIsDisplayed()
    }

    // This test verifies that the first FAQ's answer is displayed when the FAQ is clicked
    @Test
    fun helpFragment_DisplayFirstFAQAnswer() {
        // Launch the fragment in a container
        launchFragmentInContainer<HelpFragment>()

        // Retrieve the FAQs
        val faqs = FaqQueries.faqs

        // Get the first FAQ and its corresponding answer
        val firstFaq = faqs.entries.first()
        val question = firstFaq.key
        val answer = firstFaq.value

        // Ensure the FAQ question is displayed
        composeTestRule.onNodeWithText(question).assertIsDisplayed()

        // Assert that the answer is not displayed initially
        composeTestRule.onNodeWithText(answer).assertDoesNotExist()

        // Perform a click on the FAQ question
        composeTestRule.onNodeWithText(question).performClick()

        // Use a delay to wait for the animation
        Thread.sleep(300)

        // Assert that the answer is now displayed
        composeTestRule.onNodeWithText(answer).assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    @Test
    fun helpFragment_AllFAQsAnswersHiddenInitially() {
        launchFragmentInContainer<HelpFragment>()
        val faqs = FaqQueries.faqs

        faqs.forEach { (_, answer) ->
            // Check if the answer is initially hidden
            composeTestRule.onNodeWithText(answer).assertDoesNotExist()
        }
    }


}