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
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings


/**
 * A Utils to generate intent
 * @author Tao Cheng (tao@paradisehell.org)
 */
internal class IntentUtils {
    companion object {
        fun getPermissionIntent(context: Context, permission: String): Intent {
            var intent: Intent? = null
            when (permission) {
                REQUEST_INSTALL_PACKAGES -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent = Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${context.packageName}")
                        )
                    }
                }
                SYSTEM_ALERT_WINDOW -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                    }
                }
                ACCESS_NOTIFICATION_POLICY -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                    }
                }
            }
            if (intent != null && isIntentValid(intent, context)) {
                return intent
            }
            return getApplicationSettingIntent(context)
        }

        /**
         * Get a intent to current application setting activity
         *
         * @param context a [Context]
         */
        fun getApplicationSettingIntent(context: Context): Intent {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
            if (isIntentValid(intent, context)) {
                return intent
            }
            return Intent(Settings.ACTION_SETTINGS)
        }

        /**
         * Check if a given intent is valid
         *
         * @param intent a [Intent]
         * @param context a [Context]
         *
         * @return `true` if the given intent is valid, `false` otherwise.
         */
        private fun isIntentValid(intent: Intent, context: Context): Boolean {
            return intent.resolveActivity(context.packageManager) != null
        }
    }
}