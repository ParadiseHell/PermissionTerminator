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
package me.paradisehell.permission.terminator.behavior

import androidx.fragment.app.FragmentActivity


/**
 * A [PermissionDenialBehavior] is designed to handle the result when at least a permission is
 * denied, and which can be reused.
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
interface PermissionDenialBehavior {
    /**
     * Called when at least a permission is denied, user who implements it must call
     * [DenialRequest.cancel] or [DenialRequest.requestAgain]
     *
     * @param activity a [FragmentActivity]
     * @param grantedPermissionList a lis of granted permissions
     * @param deniedPermissionList a list of denied permissions
     * @param request a [DenialRequest]
     */
    fun onDenied(
        activity: FragmentActivity,
        grantedPermissionList: List<String>,
        deniedPermissionList: List<String>,
        request: DenialRequest
    )

    /**
     * A Factory to create [PermissionDenialBehavior]
     */
    interface Factory {
        /**
         * Create a instance of [PermissionDenialBehavior]
         *
         * @return a instance of [PermissionDenialBehavior]
         */
        fun create(): PermissionDenialBehavior
    }
}