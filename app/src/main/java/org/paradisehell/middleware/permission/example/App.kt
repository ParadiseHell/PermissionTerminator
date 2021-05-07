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
package org.paradisehell.middleware.permission.example

import android.app.Application
import org.paradisehell.middleware.permission.PermissionTerminator


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Rational
        PermissionTerminator.addRationalBehaviorFactory(
            DefaultPermissionRationalBehaviorFactory.TYPE,
            DefaultPermissionRationalBehaviorFactory()
        )
        PermissionTerminator.setDefaultRationalBehaviorFactoryType(
            DefaultPermissionRationalBehaviorFactory.TYPE
        )
        // Denial
        PermissionTerminator.addDenialBehaviorFactory(
            DefaultPermissionDenialBehaviorFactory.TYPE,
            DefaultPermissionDenialBehaviorFactory()
        )
        PermissionTerminator.setDefaultDenialBehaviorFactoryType(
            DefaultPermissionDenialBehaviorFactory.TYPE
        )
        // Never Ask
        PermissionTerminator.addNeverAskBehaviorFactory(
            DefaultPermissionNeverAskBehaviorFactory.TYPE,
            DefaultPermissionNeverAskBehaviorFactory()
        )
        PermissionTerminator.setDefaultNeverAskBehaviorFactoryType(
            DefaultPermissionNeverAskBehaviorFactory.TYPE
        )
    }
}