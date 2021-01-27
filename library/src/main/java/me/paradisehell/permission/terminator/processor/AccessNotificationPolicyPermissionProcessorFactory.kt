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

import android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import me.paradisehell.permission.terminator.IntentUtils
import me.paradisehell.permission.terminator.PermissionUtils


/**
 * A Factory to handle [ACCESS_NOTIFICATION_POLICY] permission.
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class AccessNotificationPolicyPermissionProcessorFactory : PermissionProcessor.Factory<Intent> {
    override fun create() = NotificationServicePermissionProcessor()

    inner class NotificationServicePermissionProcessor : PermissionProcessor<Intent> {
        override fun createLauncher(
            fragment: Fragment,
            callback: PermissionProcessor.Callback
        ): ActivityResultLauncher<Intent> {
            return fragment.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                fragment.context ?: return@registerForActivityResult
                if (PermissionUtils.isPermissionGranted(
                        fragment.requireContext(), ACCESS_NOTIFICATION_POLICY
                    )
                ) {
                    callback.onGranted()
                } else {
                    callback.onDenied()
                }
            }
        }

        override fun canProcessPermission(permission: String): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permission == ACCESS_NOTIFICATION_POLICY
            } else {
                false
            }
        }

        override fun requestPermission(
            activity: FragmentActivity,
            launcher: ActivityResultLauncher<Intent>,
            permission: String
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                launcher.launch(
                    IntentUtils.getPermissionIntent(activity, ACCESS_NOTIFICATION_POLICY)
                )
            }
        }
    }
}