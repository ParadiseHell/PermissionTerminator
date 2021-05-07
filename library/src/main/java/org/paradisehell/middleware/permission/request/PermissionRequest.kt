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
package org.paradisehell.middleware.permission.request

import androidx.fragment.app.FragmentActivity
import org.paradisehell.middleware.permission.PermissionCallback
import org.paradisehell.middleware.permission.PermissionFragment
import org.paradisehell.middleware.permission.PermissionUtils
import org.paradisehell.middleware.permission.behavior.PermissionDenialBehavior
import org.paradisehell.middleware.permission.behavior.PermissionNeverAskBehavior
import org.paradisehell.middleware.permission.behavior.PermissionRationalBehavior
import org.paradisehell.middleware.permission.behavior.RationalRequest
import java.util.*


/**
 * A [PermissionRequest] contains all we need to request permissions
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
     * A flag to mark if this [PermissionRequest] is being processed
     */
    internal var isBeingProcessed = false

    /**
     * A flag to mark is this [PermissionRequest] is processing never ask again situation
     */
    internal var isNeverAsked = false

    /**
     * A list of permission which is granted
     */
    internal val grantedPermissionList = LinkedList<String>()

    /**
     * A list of permission which is denied
     */
    internal val deniedPermissionList = LinkedList<String>()

    /**
     * A list of permission which is never asked
     */
    internal val neverAskPermissionList = LinkedList<String>()

    /**
     * request permission actually
     */
    internal fun request() {
        // rest
        isBeingProcessed = false
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