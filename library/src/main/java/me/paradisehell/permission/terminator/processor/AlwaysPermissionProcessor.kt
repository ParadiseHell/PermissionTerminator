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

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class AlwaysPermissionProcessor : PermissionProcessor<String> {

    override fun createLauncher(
        fragment: Fragment,
        callback: PermissionProcessor.Callback
    ): ActivityResultLauncher<String> {
        return fragment.registerForActivityResult(RequestPermission()) { granted ->
            if (granted) {
                callback.onGranted()
            } else {
                callback.onDenied()
            }
        }
    }

    override fun canProcessPermission(permission: String) = true

    override fun requestPermission(launcher: ActivityResultLauncher<*>, permission: String) {
        (launcher as ActivityResultLauncher<String>).launch(permission)
    }
}