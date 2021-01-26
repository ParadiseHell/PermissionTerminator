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
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


/**
 * A [PermissionProcessor] is used to request a permission and handle the result.
 *
 * @param I type of the input required to launch by [ActivityResultLauncher]
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
interface PermissionProcessor<I> {

    /**
     * Create a [ActivityResultLauncher] to execute [ActivityResultContract]
     *
     * @param fragment a [Fragment] instance which needs to call [Fragment.registerForActivityResult]
     * to create a [ActivityResultLauncher]
     * @param callback a [Callback] to handle the result of requesting the permission given
     *
     * @return an [ActivityResultLauncher]
     */
    fun createLauncher(fragment: Fragment, callback: Callback): ActivityResultLauncher<I>

    /**
     * Check whether current [PermissionProcessor] can handle the permission given
     *
     * @param permission a permission need to request
     *
     * @return `true` if current [PermissionProcessor] can handle the permission given, `false`
     * otherwise
     */
    fun canProcessPermission(permission: String): Boolean

    /**
     * Request the permission given
     *
     * @param activity a [FragmentActivity]
     * @param launcher a [ActivityResultLauncher] which is used to process the permission given
     * @param permission a permission
     */
    fun requestPermission(
        activity: FragmentActivity,
        launcher: ActivityResultLauncher<I>,
        permission: String
    )

    /**
     * A [Callback] to handle the result of requesting a permission
     */
    interface Callback {

        /**
         * Called when the permission given is granted
         */
        fun onGranted()

        /**
         * Called when the permission given is denied
         */
        fun onDenied()
    }

    /**
     * A [Factory] to create a [PermissionProcessor]
     */
    interface Factory<I> {

        /**
         * Create a [PermissionProcessor]
         *
         * @return a [PermissionProcessor] instance
         */
        fun create(): PermissionProcessor<I>
    }
}