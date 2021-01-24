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

import me.paradisehell.permission.terminator.PermissionCallback
import me.paradisehell.permission.terminator.PermissionFragment
import me.paradisehell.permission.terminator.request.PermissionRequest


/**
 * A [RationalRequest] is a wrapper contains a [PermissionRequest]
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
data class RationalRequest(
    private val request: PermissionRequest
) {
    /**
     * Cancel to request permission, which will invoke [PermissionCallback.onDenied]
     */
    fun cancel() {
        request.callback.onDenied(request.grantedPermissionList, request.deniedPermissionList)
    }

    /**
     * Request permissions
     */
    fun request() {
        PermissionFragment.requestPermission(request)
    }
}