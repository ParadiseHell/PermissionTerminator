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
package org.paradisehell.middleware.permission.processor

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.paradisehell.middleware.permission.IntentUtils
import org.paradisehell.middleware.permission.PermissionUtils


/**
 * An abstract [PermissionProcessor] which implements how to request permission using
 * [ActivityResultContracts.StartActivityForResult] Contract.
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
abstract class AbstractStartActivityForResultPermissionProcessor : PermissionProcessor<Intent> {

    /**
     * Get the permission that the current [PermissionProcessor] can process. For some reason, it
     * will be null like [Build.VERSION.SDK_INT] is not suit current device.
     *
     * @return the permission that the current [PermissionProcessor] can process
     */
    abstract fun getPermission(): String?

    final override fun createLauncher(
        fragment: Fragment,
        callback: PermissionProcessor.Callback
    ): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            fragment.context ?: return@registerForActivityResult
            if (PermissionUtils.isPermissionGranted(
                    fragment.requireContext(), getPermission()!!
                )
            ) {
                callback.onGranted()
            } else {
                callback.onDenied()
            }
        }
    }

    final override fun canProcessPermission(permission: String): Boolean {
        return getPermission() == permission
    }

    final override fun requestPermission(
        activity: FragmentActivity,
        launcher: ActivityResultLauncher<Intent>,
        permission: String
    ) {
        if (permission == getPermission()) {
            launcher.launch(IntentUtils.getPermissionIntent(activity, permission))
        }
    }
}