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
import me.paradisehell.permission.terminator.behavior.PermissionDenialBehavior
import me.paradisehell.permission.terminator.behavior.PermissionNeverAskBehavior
import me.paradisehell.permission.terminator.behavior.PermissionRationalBehavior
import me.paradisehell.permission.terminator.processor.FallbackPermissionProcessorFactory
import me.paradisehell.permission.terminator.processor.SystemAlertWindowPermissionProcessorFactory
import me.paradisehell.permission.terminator.processor.PermissionProcessor
import me.paradisehell.permission.terminator.processor.RequestInstallPackagesPermissionProcessorFactory
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
                SystemAlertWindowPermissionProcessorFactory(),
                RequestInstallPackagesPermissionProcessorFactory(),
                FallbackPermissionProcessorFactory()
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

        // Behavior

        // Rational

        private val rationalFactories = mutableMapOf<Int, PermissionRationalBehavior.Factory>()
        private var defaultRationalType: Int? = null

        /**
         * Add a [PermissionRationalBehavior.Factory]
         *
         * @param factoryType the of [PermissionRationalBehavior.Factory]
         * @param factory a instance of [PermissionRationalBehavior.Factory]
         *
         * @throws [IllegalArgumentException] when add a same [factoryType]
         */
        fun addRationalBehaviorFactory(
            factoryType: Int,
            factory: PermissionRationalBehavior.Factory
        ) {
            val oldFactory = rationalFactories[factoryType]
            if (oldFactory != null) {
                throw IllegalArgumentException(
                    "Cannot add same type for different PermissionRationalBehavior.Factory " +
                            "[${oldFactory::class.java} , ${factory::class.java}]"
                )
            }
            rationalFactories[factoryType] = factory
        }

        /**
         * Set a default [PermissionRationalBehavior.Factory] to handle Rational situation
         */
        fun setDefaultRationalBehaviorFactoryType(factoryType: Int) {
            defaultRationalType = factoryType
        }

        /**
         * Get a [PermissionRationalBehavior] by type
         *
         * @param factoryType the type of [PermissionRationalBehavior.Factory]
         *
         * @return a instance of [PermissionRationalBehavior]
         */
        internal fun getRationalBehavior(factoryType: Int?): PermissionRationalBehavior? {
            val defaultType = defaultRationalType
            if (factoryType == null) {
                if (defaultType != null) {
                    return rationalFactories[defaultType]?.create()
                }
            } else {
                return rationalFactories[factoryType]?.create()
            }
            return null
        }

        // Denial
        private val denialFactories = mutableMapOf<Int, PermissionDenialBehavior.Factory>()
        private var defaultDenialType: Int? = null

        /**
         * Add a [PermissionDenialBehavior.Factory]
         *
         * @param factoryType the type of [PermissionDenialBehavior.Factory]
         * @param factory a instance of [PermissionDenialBehavior.Factory]
         *
         * @throws [IllegalArgumentException] when add a same [factoryType]
         */
        fun addDenialBehaviorFactory(factoryType: Int, factory: PermissionDenialBehavior.Factory) {
            val oldFactory = denialFactories[factoryType]
            if (oldFactory != null) {
                throw IllegalArgumentException(
                    "Cannot add same factory type for PermissionDenialBehavior.Factory " +
                            "[${oldFactory::class.java} , ${factory::class.java}]"
                )
            }
            denialFactories[factoryType] = factory
        }

        /**
         * Set a default [PermissionDenialBehavior.Factory] to handle Denial situation
         */
        fun setDefaultDenialBehaviorFactoryType(factoryType: Int) {
            defaultDenialType = factoryType
        }

        /**
         * Get a [PermissionDenialBehavior] by type
         *
         * @param factoryType the type of [PermissionDenialBehavior.Factory]
         *
         * @return a instance of [PermissionDenialBehavior]
         */
        internal fun getDenialBehavior(factoryType: Int?): PermissionDenialBehavior? {
            val defaultType = defaultDenialType
            if (factoryType == null) {
                if (defaultType != null) {
                    return denialFactories[defaultType]?.create()
                }
            } else {
                return denialFactories[factoryType]?.create()
            }
            return null
        }

        // NeverAsk

        private val neverAskFactories = mutableMapOf<Int, PermissionNeverAskBehavior.Factory>()
        private var defaultNeverAskType: Int? = null

        /**
         * Add a [PermissionNeverAskBehavior.Factory]
         *
         * @param factoryType the type of [PermissionNeverAskBehavior.Factory]
         * @param factory a instance of [PermissionNeverAskBehavior.Factory]
         *
         * @throws [IllegalArgumentException] when add a same [factoryType]
         */
        fun addNeverAskBehaviorFactory(
            factoryType: Int,
            factory: PermissionNeverAskBehavior.Factory
        ) {
            val oldFactory = neverAskFactories[factoryType]
            if (oldFactory != null) {
                throw IllegalArgumentException(
                    "Cannot add same factory type for PermissionNeverAskBehavior.Factory " +
                            "[${oldFactory::class.java} , ${factory::class.java}]"
                )
            }
            neverAskFactories[factoryType] = factory
        }

        /**
         * Set a default [PermissionNeverAskBehavior.Factory] to handle NeverAsk situation
         *
         * @param factoryType the type of [PermissionNeverAskBehavior.Factory]
         */
        fun setDefaultNeverAskBehaviorFactoryType(factoryType: Int) {
            defaultNeverAskType = factoryType
        }

        /**
         * Get a [PermissionNeverAskBehavior] by type
         *
         * @param factoryType the type of [PermissionNeverAskBehavior.Factory]
         *
         * @return a instance of [PermissionNeverAskBehavior]
         */
        internal fun getNeverAskBehavior(factoryType: Int?): PermissionNeverAskBehavior? {
            val defaultType = defaultNeverAskType
            if (factoryType == null) {
                if (defaultType != null) {
                    return neverAskFactories[defaultType]?.create()
                }
            } else {
                return neverAskFactories[factoryType]?.create()
            }
            return null
        }
    }
}