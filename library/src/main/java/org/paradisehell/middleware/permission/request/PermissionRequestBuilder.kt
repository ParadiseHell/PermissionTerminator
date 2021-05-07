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

import android.util.Log
import androidx.fragment.app.FragmentActivity
import org.paradisehell.middleware.permission.PermissionCallback
import org.paradisehell.middleware.permission.PermissionTerminator
import org.paradisehell.middleware.permission.behavior.PermissionDenialBehavior
import org.paradisehell.middleware.permission.behavior.PermissionNeverAskBehavior
import org.paradisehell.middleware.permission.behavior.PermissionRationalBehavior
import java.util.*


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class PermissionRequestBuilder(private val activity: FragmentActivity?) {

    private val permissionList = LinkedList<String>()
    private val permissionSet = mutableSetOf<String>()
    private var rationalType: Int? = null
    private var disableRational = false
    private var denialType: Int? = null
    private var disableDenial = false
    private var neverAskType: Int? = null
    private var disableNeverAsk = false
    private lateinit var callback: PermissionCallback

    /**
     * Add permissions to this request
     *
     * @param permissions an array of permissions
     */
    fun permissions(vararg permissions: String) = apply {
        permissions.forEach { permission ->
            if (permissionSet.contains(permission).not()) {
                permissionList.add(permission)
            }
            permissionSet.add(permission)
        }
    }

    /**
     * Add permissions to this request
     *
     * @param permissions a list of permissions
     */
    fun permissions(permissions: List<String>) = apply {
        permissions.forEach { permission ->
            if (permissionSet.contains(permission).not()) {
                permissionList.add(permission)
            }
            permissionSet.add(permission)
        }
    }

    /**
     * With a special [PermissionRationalBehavior]
     *
     * @param rationalType the type of [PermissionRationalBehavior]
     */
    fun withRationalBehavior(rationalType: Int) = apply {
        this.rationalType = rationalType
    }

    /**
     * Disable to use [PermissionRationalBehavior]
     */
    fun disableRationalBehavior() = apply {
        disableRational = true
    }

    /**
     * With a special [PermissionDenialBehavior]
     *
     * @param denialType the type of [PermissionDenialBehavior]
     */
    fun withDenialBehavior(denialType: Int) = apply {
        this.denialType = denialType
    }

    /**
     * Disable to use [PermissionDenialBehavior]
     */
    fun disableDenialBehavior() = apply {
        disableDenial = true
    }

    /**
     * With a special [PermissionNeverAskBehavior]
     *
     * @param neverAskType the type of [PermissionNeverAskBehavior]
     */
    fun withNeverAskBehavior(neverAskType: Int) = apply {
        this.neverAskType = neverAskType
    }

    /**
     * Disable to use [PermissionNeverAskBehavior]
     */
    fun disableNeverAskBehavior() = apply {
        disableNeverAsk = true
    }


    /**
     * Request permissions
     *
     * @param callback a [PermissionCallback] to listen result
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun request(callback: PermissionCallback) {
        this.callback = callback
        buildRequest()?.request()
    }

    /**
     * Request permissions.
     * Note that : [onDenied] and [onNeverAsked] have a default perform, because we support you
     * to use [PermissionTerminator.setDefaultDenialBehaviorFactoryType] and
     * [PermissionTerminator.setDefaultNeverAskBehaviorFactoryType] to set a default perform for
     * these situations. So we can only focus on [onGranted] situation.
     *
     * @param onGranted called when all permissions are granted
     * @param onDenied called when at least a permission is denied
     * @param onNeverAsked called when at least a permission is never asked
     */
    @JvmOverloads
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
        request(object :
            PermissionCallback {
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

    private fun buildRequest(): PermissionRequest? {
        if (activity == null) {
            return null
        }
        // check is permission is empty
        if (permissionList.isEmpty()) {
            Log.w(
                PermissionTerminator.TAG,
                "No permission is added!!!" +
                        "please call PermissionRequestBuilder#permissions() method " +
                        "before call PermissionRequestBuilder#request()"
            )
            return null
        }
        // check behavior
        val rational = if (disableRational) {
            null
        } else {
            PermissionTerminator.getRationalBehavior(rationalType)
        }
        val denial = if (disableDenial) {
            null
        } else {
            PermissionTerminator.getDenialBehavior(denialType)
        }
        val neverAsk = if (disableNeverAsk) {
            null
        } else {
            PermissionTerminator.getNeverAskBehavior(neverAskType)
        }
        return PermissionRequest(activity, permissionList, rational, denial, neverAsk, callback)
    }

    companion object {
        internal val INVALID = PermissionRequestBuilder(null)
    }
}