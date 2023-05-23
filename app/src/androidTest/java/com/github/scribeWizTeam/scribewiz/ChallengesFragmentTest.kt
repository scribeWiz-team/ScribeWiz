package com.github.scribeWizTeam.scribewiz

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.fragment.app.testing.launchFragmentInContainer
import com.github.scribeWizTeam.scribewiz.fragments.ChallengesFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChallengesFragmentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        ChallengesFragment.isTest = true
        val scenario = launchFragmentInContainer<ChallengesFragment>()
        composeTestRule.setContent {
            ChallengesFragment()
        }
    }

    @Test
    fun testChallengesFragment() {
        composeTestRule.onNodeWithText("test1").assertExists()
        composeTestRule.onNodeWithText("test2").assertExists()
    }

    @After
    fun tearDown() {
        ChallengesFragment.isTest = false
    }
}