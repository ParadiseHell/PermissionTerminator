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

import android.Manifest.permission.SYSTEM_ALERT_WINDOW
import android.content.Intent


/**
 * Use to handle [SYSTEM_ALERT_WINDOW] permission
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class SystemAlertWindowPermissionProcessorFactory : PermissionProcessor.Factory<Intent> {

    override fun create() = OverlayPermissionProcessor()

    inner class OverlayPermissionProcessor : AbstractStartActivityForResultPermissionProcessor() {
        override fun getPermission(): String? {
            return SYSTEM_ALERT_WINDOW
        }
    }
}