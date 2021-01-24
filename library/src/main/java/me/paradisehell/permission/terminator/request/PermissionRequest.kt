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
import me.paradisehell.permission.terminator.behavior.PermissionDenialBehavior
import me.paradisehell.permission.terminator.behavior.PermissionNeverAskBehavior
import me.paradisehell.permission.terminator.behavior.PermissionRationalBehavior
import me.paradisehell.permission.terminator.behavior.RationalRequest


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
@Suppress("ArrayInDataClass")
data class PermissionRequest(
    internal val activity: FragmentActivity,
    internal val permissionList: List<String>,
    internal val rationalBehavior: PermissionRationalBehavior?,
    internal val denialBehavior: PermissionDenialBehavior?,
    internal val neverAskBehavior: PermissionNeverAskBehavior?,
    internal val callback: PermissionCallback
) {
    /**
     * A flag to mark if this [PermissionRequest] is processing
     */
    internal var isProcessing = false

    /**
     * A list of permission which is granted
     */
    internal val grantedPermissionList = mutableListOf<String>()

    /**
     * A list of permission which is denied
     */
    internal val deniedPermissionList = mutableListOf<String>()

    /**
     * A list of permission which is never asked
     */
    internal val neverAskPermissionList = mutableListOf<String>()

    internal fun request() {
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
        if (rationalBehavior != null) {
            val rationalPermissionList = deniedPermissionList.filter { permission ->
                PermissionUtils.shouldShowRequestPermissionRationale(activity, permission)
            }
            if (rationalPermissionList.isNotEmpty()) {
                rationalBehavior.explain(activity, rationalPermissionList, RationalRequest(this))
                return
            }
        }
        PermissionFragment.requestPermission(this)
    }
}