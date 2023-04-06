package com.github.scribeWizTeam.scribewiz

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionsManager {

    /**
     * Check for permission before executing some code. If the permission is not granted, ask the user for permission.
     *
     * @param caller the activity/fragment caller
     * @param context the context of the activity/fragment
     * @param permission the permission to check
     * @param callback the code to execute if the permission is granted and
     */
    fun checkPermissionThenExecute(caller: ActivityResultCaller, context: Context, permission: String, callback: ()->Unit) {

        when (PackageManager.PERMISSION_GRANTED) {
            // check directly for permission
            ContextCompat.checkSelfPermission(context, permission) -> {
                // You can use the API that requires the permission.
                callback()
            }

            // shouldShowRequestPermissionRationale(null, null) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
            //showInContextUI(...)
            // }
            else -> {
                val requestPermissionLauncher = caller.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        callback()
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                }

                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(permission)
            }
        }

    }
}