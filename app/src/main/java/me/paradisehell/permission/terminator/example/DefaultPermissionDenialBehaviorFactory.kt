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
package me.paradisehell.permission.terminator.example

import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import me.paradisehell.permission.terminator.behavior.DenialRequest
import me.paradisehell.permission.terminator.behavior.PermissionDenialBehavior


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class DefaultPermissionDenialBehaviorFactory : PermissionDenialBehavior.Factory {
    companion object {
        const val TYPE = 1
    }

    override fun create() = DefaultPermissionDenialBehavior()

    inner class DefaultPermissionDenialBehavior : PermissionDenialBehavior {
        override fun onDenied(
            activity: FragmentActivity,
            grantedPermissionList: List<String>,
            deniedPermissionList: List<String>,
            request: DenialRequest
        ) {
            val permissionNameList = deniedPermissionList.map { permission ->
                val lastIndex = permission.lastIndexOf(".")
                permission.substring(lastIndex + 1)
            }
            val dialog = AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Tip")
                .setMessage("$permissionNameList are denied.")
                .setNegativeButton("Cancel") { _, _ -> request.cancel() }
                .setPositiveButton("Request Again") { _, _ -> request.requestAgain() }
                .create()
            dialog.show()
        }
    }
}