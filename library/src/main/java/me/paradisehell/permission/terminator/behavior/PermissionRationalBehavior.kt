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
 * A [PermissionRationalBehavior] is designed to explain why the permissions given is needed to be
 * requested, and which can be reused.
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
interface PermissionRationalBehavior {

    /**
     * Called when at least a permission is needed to explain why it is needed to be requested,
     * user who implements it must call [RationalRequest.cancel] or [RationalRequest.request]
     *
     * @param activity a [FragmentActivity]
     * @param rationalPermissionList a list of permission need to be explained
     * @param request a [RationalRequest]
     */
    fun explain(
        activity: FragmentActivity,
        rationalPermissionList: List<String>,
        request: RationalRequest
    )

    /**
     * A Factory to create a [PermissionRationalBehavior]
     */
    interface Factory {
        /**
         * Create a instance of [PermissionRationalBehavior]
         *
         * @return a instance of [PermissionRationalBehavior]
         */
        fun create(): PermissionRationalBehavior
    }
}