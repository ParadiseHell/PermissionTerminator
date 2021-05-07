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

import android.Manifest.permission.REQUEST_INSTALL_PACKAGES
import android.os.Build


/**
 * A Factory to handle [REQUEST_INSTALL_PACKAGES] permission
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class RequestInstallPackagesPermissionProcessorFactory : PermissionProcessor.Factory {

    override fun create() = RequestInstallPackagesPermissionProcessor()

    inner class RequestInstallPackagesPermissionProcessor :
        AbstractStartActivityForResultPermissionProcessor() {

        override fun getPermission(): String? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                REQUEST_INSTALL_PACKAGES
            } else {
                null
            }
        }
    }
}