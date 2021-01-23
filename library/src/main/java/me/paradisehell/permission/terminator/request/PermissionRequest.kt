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
package me.paradisehell.permission.terminator.request

import androidx.fragment.app.FragmentActivity
import me.paradisehell.permission.terminator.PermissionCallback
import me.paradisehell.permission.terminator.PermissionFragment
import me.paradisehell.permission.terminator.PermissionUtils


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
@Suppress("ArrayInDataClass")
internal data class PermissionRequest(
    val activity: FragmentActivity,
    val permissionList: List<String>,
    val callback: PermissionCallback
) {
    /**
     * A flag to mark if this [PermissionRequest] is processing
     */
    var isProcessing = false

    /**
     * A list of permission which is granted
     */
    val grantedPermissionList = mutableListOf<String>()

    /**
     * A list of permission which is denied
     */
    val deniedPermissionList = mutableListOf<String>()

    /**
     * A list of permission which is never asked
     */
    val neverAskPermissionList = mutableListOf<String>()

    fun request() {
        // rest
        isProcessing = false
        grantedPermissionList.clear()
        deniedPermissionList.clear()
        neverAskPermissionList.clear()
        // check permission granted situation
        permissionList.forEach { permission ->
            if (PermissionUtils.isPermissionGranted(activity, permission)) {
                grantedPermissionList.add(permission)
            } else {
                deniedPermissionList.add(permission)
            }
        }
        // check if all permission is granted
        if (grantedPermissionList.size == permissionList.size) {
            callback.onGranted(permissionList)
            return
        }
        // check whether should show permission rational or not
        val rationalPermissionList = deniedPermissionList.filter { permission ->
            PermissionUtils.shouldShowRequestPermissionRationale(activity, permission)
        }
        if (rationalPermissionList.isNotEmpty()) {
            // TODO show permission rational
        }
        PermissionFragment.requestPermission(this)
    }
}