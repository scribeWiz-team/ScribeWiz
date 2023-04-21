package com.github.scribeWizTeam.scribewiz

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.scribeWizTeam.scribewiz.Activities.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PermissionsManagerTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var readPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(READ_EXTERNAL_STORAGE)

    @Test
    @UiThreadTest // because of Toasts running on main UI thread
    fun callbackIsCalledWhenPermissionGranted() {
        val pm = PermissionsManager()
        var check = false
        val callback : () -> Unit = { check = true}

        pm.checkPermissionThenExecute(
            composeTestRule.activity,
            composeTestRule.activity,
            READ_EXTERNAL_STORAGE,
            callback
        )

        assertTrue(check)
    }
}