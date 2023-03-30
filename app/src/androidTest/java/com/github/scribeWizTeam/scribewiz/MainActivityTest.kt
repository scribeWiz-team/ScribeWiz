package com.github.scribeWizTeam.scribewiz


import org.junit.Rule

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testLoginPageAppears(){
        composeTestRule.onNodeWithText("Not signed in").assertIsDisplayed()
    }


}
