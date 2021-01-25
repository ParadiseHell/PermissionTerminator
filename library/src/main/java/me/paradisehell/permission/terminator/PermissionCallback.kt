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
package me.paradisehell.permission.terminator


/**
 * An interface to handle the result after requesting permissions
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
interface PermissionCallback {

    /**
     * Called when all permissions are granted
     *
     * @param grantedPermissionList a list contains granted permissions
     */
    fun onGranted(grantedPermissionList: List<String>)

    /**
     * Called when at least one permission is denied
     *
     * @param grantedPermissionList a list contains granted permissions
     * @param deniedPermissionList a list contains denied permissions which is always not empty
     */
    fun onDenied(grantedPermissionList: List<String>, deniedPermissionList: List<String>)

    /**
     * Called when at least one permission is never asked which has a higher priority
     * than [PermissionCallback.onDenied]
     *
     * @param grantedPermissionList a list contains granted permissions
     * @param deniedPermissionList a list contains denied permissions which is always not empty
     * @param neverAskPermissionList a list contains never asked permission which is always not
     * empty
     */
    fun onNeverAsked(
        grantedPermissionList: List<String>,
        deniedPermissionList: List<String>,
        neverAskPermissionList: List<String>
    )
}