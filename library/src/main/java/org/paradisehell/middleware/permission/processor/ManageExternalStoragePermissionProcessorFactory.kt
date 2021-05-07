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
package org.paradisehell.middleware.permission.processor

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.os.Build


/**
 * A Factory to handle [MANAGE_EXTERNAL_STORAGE] permission.
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class ManageExternalStoragePermissionProcessorFactory : PermissionProcessor.Factory {

    override fun create() = ManageExternalStoragePermissionProcessor()

    inner class ManageExternalStoragePermissionProcessor :
        AbstractStartActivityForResultPermissionProcessor() {
        override fun getPermission(): String? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return MANAGE_EXTERNAL_STORAGE
            } else {
                null
            }
        }
    }
}