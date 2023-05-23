package com.github.scribeWizTeam.scribewiz

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
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
    fun checkPermissionThenExecute(
        caller: ActivityResultCaller,
        context: Context,
        permission: String,
        callback: () -> Unit
    ) {

        when (PackageManager.PERMISSION_GRANTED) {
            // check directly for permission
            ContextCompat.checkSelfPermission(context, permission) -> {
                // You can use the API that requires the permission.
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                callback()
            }

            else -> {
                val requestPermissionLauncher = caller.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                        callback()
                    } else {

                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }

                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(permission)
            }
        }

    }
}