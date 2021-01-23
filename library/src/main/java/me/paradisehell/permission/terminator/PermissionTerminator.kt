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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import me.paradisehell.permission.terminator.processor.AlwaysPermissionProcessorFactory
import me.paradisehell.permission.terminator.processor.PermissionProcessor
import me.paradisehell.permission.terminator.request.PermissionRequestBuilder

/**
 * The entry to request permissions
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
class PermissionTerminator {
    companion object {
        // obtain PermissionRequestBuilder

        /**
         * obtain a [PermissionRequestBuilder] with a view
         *
         * @param view a [View]
         */
        fun with(view: View?): PermissionRequestBuilder {
            return with(view?.context)
        }

        /**
         * obtain a [PermissionRequestBuilder] with a view
         *
         * @param fragment a [Fragment]
         */
        fun with(fragment: Fragment?): PermissionRequestBuilder {
            return with(fragment?.context)
        }

        /**
         * obtain a [PermissionRequestBuilder] with an activity
         *
         * @param activity an [Activity]
         */
        fun with(activity: Activity?): PermissionRequestBuilder {
            return with(activity as? Context)
        }

        /**
         * obtain a [PermissionRequestBuilder] with a context
         *
         * @param context a [Context]
         */
        fun with(context: Context?): PermissionRequestBuilder {
            if (context == null) {
                return PermissionRequestBuilder.INVALID
            }
            if (context is FragmentActivity) {
                return PermissionRequestBuilder(context)
            }
            if (context is ContextWrapper) {
                return with(context.baseContext)
            }
            throw IllegalArgumentException("Context must be a FragmentActivity !!!")
        }

        // PermissionProcessorFactory
        private val defaultPermissionProcessorFactories =
            mutableListOf<PermissionProcessor.Factory<*>>(
                AlwaysPermissionProcessorFactory()
            )

        /**
         * A list of [PermissionProcessor.Factory] which user can add their custom implementations
         */
        private val permissionProcessorFactories = mutableListOf<PermissionProcessor.Factory<*>>()

        /**
         * Add a [PermissionProcessor.Factory] to handle permission.
         *
         * With this interface user can add a custom [PermissionProcessor.Factory] to fix bugs
         * which the library cannot handle or handled with an unexpected result.
         *
         * @param factory a [PermissionProcessor.Factory]
         */
        fun addPermissionProcessorFactory(factory: PermissionProcessor.Factory<*>) {
            permissionProcessorFactories.add(factory)
        }

        /**
         * Get a list of [PermissionProcessor.Factory] to handle permissions given
         *
         * @return a list of [PermissionProcessor.Factory]
         */
        internal fun getPermissionProcessorFactories(): List<PermissionProcessor.Factory<*>> {
            val factories = mutableListOf<PermissionProcessor.Factory<*>>()
            factories.addAll(permissionProcessorFactories)
            factories.addAll(defaultPermissionProcessorFactories)
            return factories
        }
    }
}