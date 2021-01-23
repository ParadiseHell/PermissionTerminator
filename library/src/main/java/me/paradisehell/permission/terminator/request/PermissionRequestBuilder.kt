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


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class PermissionRequestBuilder(private val activity: FragmentActivity?) {
    private val permissionList = mutableListOf<String>()

    fun permissions(permission: String, vararg permissions: String) = apply {
        val permissionSet = mutableSetOf<String>()
        permissionList.add(permission)
        permissionSet.add(permission)
        permissions.forEach { p ->
            if (permissionSet.contains(p).not()) {
                permissionList.add(p)
                permissionSet.add(p)
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun request(callback: PermissionCallback) {
        if (activity == null) {
            return
        }
        if (permissionList.isEmpty()) {
            throw RuntimeException(
                "Please call PermissionRequestBuilder#permisssions method fist !!!"
            )
        }
        val request = PermissionRequest(activity, permissionList, callback)
        request.request()
    }

    fun request(
        onGranted: (grantedPermissionList: List<String>) -> Unit,
        onDenied: (
            grantedPermissionList: List<String>,
            deniedPermissionList: List<String>
        ) -> Unit = { _, _ -> },
        onNeverAsked: (
            grantedPermissionList: List<String>,
            deniedPermissionList: List<String>,
            neverAskPermissionList: List<String>
        ) -> Unit = { _, _, _ -> }
    ) {
        request(object : PermissionCallback {
            override fun onGranted(grantedPermissionList: List<String>) {
                onGranted.invoke(grantedPermissionList)
            }

            override fun onDenied(
                grantedPermissionList: List<String>,
                deniedPermissionList: List<String>
            ) {
                onDenied.invoke(grantedPermissionList, deniedPermissionList)
            }

            override fun onNeverAsked(
                grantedPermissionList: List<String>,
                deniedPermissionList: List<String>,
                neverAskPermissionList: List<String>
            ) {
                onNeverAsked.invoke(
                    grantedPermissionList,
                    deniedPermissionList,
                    neverAskPermissionList
                )
            }
        })
    }

    companion object {
        internal val INVALID = PermissionRequestBuilder(null)
    }
}