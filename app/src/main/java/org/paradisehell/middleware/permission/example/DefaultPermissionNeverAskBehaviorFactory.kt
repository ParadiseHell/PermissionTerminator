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
package org.paradisehell.middleware.permission.example

import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import org.paradisehell.middleware.permission.behavior.NeverAskRequest
import org.paradisehell.middleware.permission.behavior.PermissionNeverAskBehavior


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class DefaultPermissionNeverAskBehaviorFactory : PermissionNeverAskBehavior.Factory {
    companion object {
        const val TYPE = 1
    }

    override fun create() = DefaultPermissionNeverAskBehavior()

    inner class DefaultPermissionNeverAskBehavior : PermissionNeverAskBehavior {
        override fun onNeverAsk(
            activity: FragmentActivity,
            grantedPermissionList: List<String>,
            deniedPermissionList: List<String>,
            neverAskPermissionList: List<String>,
            request: NeverAskRequest
        ) {
            val permissionNameList = neverAskPermissionList.map { permission ->
                val lastIndex = permission.lastIndexOf(".")
                permission.substring(lastIndex + 1)
            }
            val dialog = AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Tip")
                .setMessage("$permissionNameList are never asked again.")
                .setNegativeButton("Cancel") { _, _ -> request.cancel() }
                .setPositiveButton("Go Setting") { _, _ -> request.launchSettingActivity() }
                .create()
            dialog.show()
        }
    }
}

