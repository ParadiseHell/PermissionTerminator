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
package org.paradisehell.middleware.permission.behavior

import androidx.fragment.app.FragmentActivity


/**
 *
 * A [PermissionNeverAskBehavior] is designed to handle the result when at least a permission is
 * marked never asked again, and which can be reused.
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
interface PermissionNeverAskBehavior {

    /**
     * Called at least a permission is marked never asked
     */
    fun onNeverAsk(
        activity: FragmentActivity,
        grantedPermissionList: List<String>,
        deniedPermissionList: List<String>,
        neverAskPermissionList: List<String>,
        request: NeverAskRequest
    )

    /**
     * A Factory to create [PermissionNeverAskBehavior]
     */
    interface Factory {
        /**
         * Create a instance of [PermissionNeverAskBehavior]
         *
         * @return a instance of [PermissionNeverAskBehavior]
         */
        fun create(): PermissionNeverAskBehavior
    }
}