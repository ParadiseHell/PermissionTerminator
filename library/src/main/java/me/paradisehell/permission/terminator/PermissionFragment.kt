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
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import me.paradisehell.permission.terminator.behavior.DenialRequest
import me.paradisehell.permission.terminator.processor.PermissionProcessor
import me.paradisehell.permission.terminator.request.PermissionRequest
import java.util.*


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
internal class PermissionFragment : Fragment() {
    /**
     * A queue of [PermissionRequest] to store [PermissionRequest] instances. since requesting a
     * permission is illegal when the [PermissionFragment] is not attached to an [Activity].
     */
    private val permissionRequestQueue = LinkedList<PermissionRequest>()

    /**
     * The index of the permission list which is requesting
     */
    private var permissionIndex = -1

    /**
     * A list of [PermissionProcessor] which to request permissions and handle the result, we can
     * also call this list `A Processor Chain`
     */
    private val processors = mutableListOf<PermissionProcessor<out Any?>>()

    /**
     * A map of [ActivityResultLauncher] which can get easily by a [PermissionProcessor]
     */
    private val processorLauncherMap =
        mutableMapOf<PermissionProcessor<out Any?>, ActivityResultLauncher<out Any>>()

    /**
     * A flag to mark if this [PermissionFragment] can process [PermissionRequest]
     */
    private var canProcessPermissionRequestImmediately = false

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // collect processors and launchers
        val factories = PermissionTerminator.getPermissionProcessorFactories()
        factories.forEach { factory ->
            val processor = factory.create()
            processors.add(processor)
            processorLauncherMap[processor] = processor.createLauncher(
                this, PermissionResultCallback()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        canProcessPermissionRequestImmediately = true
        processNextPermissionRequest()
    }

    override fun onPause() {
        super.onPause()
        canProcessPermissionRequestImmediately = false
    }

    override fun onDestroy() {
        processors.clear()
        processorLauncherMap.values.forEach { launcher ->
            launcher.unregister()
        }
        processorLauncherMap.clear()
        super.onDestroy()
    }

    // private methods

    /**
     * Peek a [PermissionRequest] from [PermissionFragment.permissionRequestQueue] and handle it
     */
    private fun processNextPermissionRequest() {
        val currentRequest = permissionRequestQueue.peek() ?: return
        if (currentRequest.isProcessing) {
            return
        }
        currentRequest.isProcessing = true
        // rest permission index
        permissionIndex = 0
        requestPermissionWithProcessorChain()
    }

    /**
     * request permissions by [PermissionFragment.processors]
     */
    private fun requestPermissionWithProcessorChain() {
        if (canHandleCurrentPermissionRequest().not()) {
            return
        }
        val currentRequest = permissionRequestQueue.peek()!!
        val permission = currentRequest.deniedPermissionList[permissionIndex]
        run {
            processors.forEach { processor ->
                if (processor.canProcessPermission(permission)) {
                    val launcher = processorLauncherMap[processor]!!
                    @Suppress("UNCHECKED_CAST")
                    processor.requestPermission(launcher as ActivityResultLauncher<Any>, permission)
                    return@run
                }
            }
        }
    }

    /**
     * Check whether we can handle current [PermissionRequest] or not, we have something to do
     * below :
     *
     * 1. if current [PermissionFragment] is recycled, we can not handle current [PermissionRequest]
     * 2. if current [PermissionRequest] is null, we can not handle current [PermissionRequest]
     * 3. if current [PermissionRequest] is not in processing, we can not handle current
     * [PermissionRequest]
     * 4. if [PermissionFragment.permissionIndex] is out of range, we can not handle current
     * [PermissionRequest]
     */
    private fun canHandleCurrentPermissionRequest(): Boolean {
        // 1. check if current PermissionFragment is recycled
        if (activity == null) {
            return false
        }
        // 2. check if current PermissionRequest is null
        val currentRequest = permissionRequestQueue.peek() ?: return false
        // 3. check if current PermissionRequest is in processing
        if (currentRequest.isProcessing.not()) {
            return false
        }
        // 4. check if the permissionIndex is out of range
        if (permissionIndex < 0 || permissionIndex >= currentRequest.deniedPermissionList.size) {
            return false
        }
        return true
    }

    /**
     * A [PermissionResultCallback] to handle the result of requesting permissions
     */
    inner class PermissionResultCallback : PermissionProcessor.Callback {
        override fun onGranted() {
            if (canHandleCurrentPermissionRequest().not()) {
                return
            }
            // get current PermissionRequest and current permission
            val currentRequest = permissionRequestQueue.peek()!!
            val permission = currentRequest.deniedPermissionList[permissionIndex]
            currentRequest.grantedPermissionList.add(permission)
            // check if grantedPermissionList's size is equal permissionList
            if (currentRequest.permissionList.size == currentRequest.grantedPermissionList.size) {
                // invoke granted callback
                currentRequest.callback.onGranted(currentRequest.grantedPermissionList)
                // process next PermissionRequest
                permissionRequestQueue.removeFirst()
                processNextPermissionRequest()
                return
            }
            // process the next permission of current PermissionRequest
            permissionIndex++
            requestPermissionWithProcessorChain()
        }

        override fun onDenied() {
            if (canHandleCurrentPermissionRequest().not()) {
                return
            }
            // get current PermissionRequest and current permission
            val currentRequest = permissionRequestQueue.peek()!!
            val permission = currentRequest.deniedPermissionList[permissionIndex]
            // check whether the permission is marked never ask again
            if (PermissionUtils.shouldShowRequestPermissionRationale(
                    requireActivity(), permission
                )
            ) {
                // permission is just denied

                // check whether PermissionDenialBehavior is exist or not, if so invoke it
                if (currentRequest.denialBehavior != null) {
                    currentRequest.denialBehavior.onDenied(
                        requireActivity(),
                        currentRequest.grantedPermissionList,
                        currentRequest.deniedPermissionList,
                        DenialRequest(currentRequest)
                    )
                } else {
                    // invoke denied callback
                    currentRequest.callback.onDenied(
                        currentRequest.grantedPermissionList,
                        currentRequest.deniedPermissionList
                    )
                }
            } else {
                // permission is marked never ask again
                currentRequest.neverAskPermissionList.add(permission)
                // invoke neverAsked callback
                currentRequest.callback.onNeverAsked(
                    currentRequest.grantedPermissionList,
                    currentRequest.deniedPermissionList,
                    currentRequest.neverAskPermissionList
                )
                // check whether PermissionNeverAskBehavior is exist or not, if so invoke it
                if (currentRequest.neverAskBehavior != null) {
                    currentRequest.neverAskBehavior.onNeverAsk(
                        requireActivity(),
                        currentRequest.grantedPermissionList,
                        currentRequest.deniedPermissionList,
                        currentRequest.neverAskPermissionList
                    )
                }
            }
            // process next PermissionRequest
            permissionRequestQueue.removeFirst()
            processNextPermissionRequest()
        }
    }

    internal companion object {
        /**
         * The tag of [PermissionFragment]
         */
        private const val FRAGMENT_TAG = "me.paradisehell.permission.terminator.PermissionFragment"

        /**
         * request permission by the [PermissionRequest] given
         *
         * @param request a [PermissionRequest]
         */
        fun requestPermission(request: PermissionRequest) {
            request.activity.supportFragmentManager.apply {
                // check whether PermissionFragment is exist or not
                var fragment = findFragmentByTag(FRAGMENT_TAG)
                if (fragment == null) {
                    // create a PermissionFragment and add it
                    fragment = PermissionFragment()
                    beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss()
                }
                if (fragment !is PermissionFragment) {
                    return
                }
                // add the PermissionRequest to the queue
                fragment.permissionRequestQueue.add(request)
                // check whether this PermissionFragment can process PermissionRequest immediately,
                // if not waiting the lifecycle methods to be called to handle permissions,
                // otherwise process the PermissionRequest immediately
                if (fragment.canProcessPermissionRequestImmediately) {
                    fragment.processNextPermissionRequest()
                }
            }
        }
    }
}