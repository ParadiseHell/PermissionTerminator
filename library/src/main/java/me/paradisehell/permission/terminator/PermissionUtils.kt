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
package me.paradisehell.permission.terminator

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


/**
 * A Util to check permission
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
internal class PermissionUtils {
    companion object {
        /**
         * Check whether the given permission is granted.
         *
         * @param context an [Context]
         * @param permission a permission
         *
         * @return `true` if the permission is granted, `false` otherwise
         */
        fun isPermissionGranted(context: Context, permission: String): Boolean {
            when (permission) {
                SYSTEM_ALERT_WINDOW -> {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(context)
                    } else {
                        true
                    }
                }
                REQUEST_INSTALL_PACKAGES -> {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.packageManager.canRequestPackageInstalls()
                    } else {
                        true
                    }
                }
                ACCESS_NOTIFICATION_POLICY -> {
                    return NotificationManagerCompat.from(context).areNotificationsEnabled()
                }
                WRITE_SETTINGS -> {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.System.canWrite(context)
                    } else {
                        true
                    }
                }
                MANAGE_EXTERNAL_STORAGE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        return Environment.isExternalStorageManager()
                    }
                }
            }
            return ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
        }

        /**
         * Check whether should show request permission rational to explain why the permission
         * is requested.
         *
         * @param activity an [Activity]
         * @param permission a permission
         *
         * @return `true` if should show permission ration to explain the given permission,
         * `false` otherwise
         */
        fun shouldShowRequestPermissionRationale(
            activity: Activity,
            permission: String
        ): Boolean {
            // check special permission first
            when (permission) {
                SYSTEM_ALERT_WINDOW,
                REQUEST_INSTALL_PACKAGES,
                ACCESS_NOTIFICATION_POLICY,
                WRITE_SETTINGS,
                MANAGE_EXTERNAL_STORAGE -> {
                    return true
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return activity.shouldShowRequestPermissionRationale(permission)
            }
            return true
        }
    }
}