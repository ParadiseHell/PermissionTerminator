/*
 * Copyright (C) 2021 ParadiseHell.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.paradisehell.permission.terminator.processor

import android.Manifest.permission.SYSTEM_ALERT_WINDOW
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import me.paradisehell.permission.terminator.PermissionUtils


/**
 * Use to handle [SYSTEM_ALERT_WINDOW] permission
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class OverlayPermissionProcessorFactory : PermissionProcessor.Factory<Intent> {

    override fun create() = OverlayPermissionProcessor()

    inner class OverlayPermissionProcessor : PermissionProcessor<Intent> {
        override fun createLauncher(
            fragment: Fragment,
            callback: PermissionProcessor.Callback
        ): ActivityResultLauncher<Intent> {
            return fragment.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                // Do not need to check ActivityResult, Because it may get a Activity.RESULT_CANCEL
                // resultCode

                // Check if fragment is recycled
                val activity = fragment.activity ?: return@registerForActivityResult
                // just check whether permission is granted or not
                if (PermissionUtils.isPermissionGranted(activity, SYSTEM_ALERT_WINDOW)) {
                    callback.onGranted()
                } else {
                    callback.onDenied()
                }
            }
        }

        override fun canProcessPermission(permission: String): Boolean {
            return permission == SYSTEM_ALERT_WINDOW
        }

        override fun requestPermission(
            activity: FragmentActivity,
            launcher: ActivityResultLauncher<Intent>,
            permission: String
        ) {
            var intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity.packageName}")
                )
            } else {
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${activity.packageName}")
                )
            }
            if (intent.resolveActivity(activity.packageManager) == null) {
                intent = Intent(Settings.ACTION_SETTINGS)
            }
            launcher.launch(intent)
        }
    }
}