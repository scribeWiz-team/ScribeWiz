package com.github.scribeWizTeam.scribewiz

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
@ExperimentalUnsignedTypes
@RunWith(AndroidJUnit4::class)
class NotesDisplayedActivityTest {
    @Test
    fun testLaunchActivityWithIntent(){
        val intent = Intent(ApplicationProvider.getApplicationContext(), NotesDisplayedActivity::class.java)

    }







}