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
import me.paradisehell.permission.terminator.behavior.NeverAskRequest
import me.paradisehell.permission.terminator.processor.PermissionProcessor
import me.paradisehell.permission.terminator.request.PermissionRequest
import java.util.*


/**
 * A [PermissionFragment] is used to request permission actually
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
     * A list of [PermissionProcessor] which to request permissions and handle the result, we can
     * also call this list `A Processor Chain`
     */
    private val processors = LinkedList<PermissionProcessor<Any>>()

    /**
     * A map of [ActivityResultLauncher] which can get easily by a [PermissionProcessor]
     */
    private val processorLauncherMap =
        mutableMapOf<PermissionProcessor<Any>, ActivityResultLauncher<Any>>()

    /**
     * A flag to mark if this [PermissionFragment] can process [PermissionRequest] directly not
     * to wait the lifecycle method
     */
    private var canProcessRequestDirectly = false

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // collect processors and launchers
        val factories = PermissionTerminator.getPermissionProcessorFactories()
        factories.forEach { factory ->
            @Suppress("UNCHECKED_CAST")
            val processor = factory.create() as PermissionProcessor<Any>
            processors.add(processor)
            processorLauncherMap[processor] = processor.createLauncher(
                this, PermissionResultCallback()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        canProcessRequestDirectly = true
        processNextPermissionRequest()
    }

    override fun onPause() {
        super.onPause()
        canProcessRequestDirectly = false
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
     * Peek a [PermissionRequest] from [PermissionFragment.permissionRequestQueue] and process it
     *
     * @param removeFirst whether remove first [PermissionRequest] in the queue
     */
    private fun processNextPermissionRequest(removeFirst: Boolean = false) {
        // check removeFirst
        if (removeFirst && permissionRequestQueue.isNotEmpty()) {
            permissionRequestQueue.removeFirst()
        }
        val currentRequest = permissionRequestQueue.peek() ?: return
        if (currentRequest.isBeingProcessed) {
            handlePermissionRequestWithNeverAsk(currentRequest)
            return
        }
        currentRequest.isBeingProcessed = true
        requestPermissionWithProcessorChain()
    }

    /**
     * handle a [PermissionRequest] when user choose `Never Ask gain` and launch SettingActivity
     * and back again
     */
    private fun handlePermissionRequestWithNeverAsk(request: PermissionRequest) {
        if (request.isNeverAsked.not()) {
            return
        }
        request.isNeverAsked = false
        if (request.neverAskPermissionList.isEmpty()) {
            return
        }
        // check if permission is granted
        val neverAskPermission = request.neverAskPermissionList.first()
        if (PermissionUtils.isPermissionGranted(requireContext(), neverAskPermission).not()) {
            // check if permission is still never ask
            if (PermissionUtils.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    neverAskPermission
                )
            ) {
                // just denied
                request.neverAskPermissionList.removeFirst()
                request.deniedPermissionList.addLast(neverAskPermission)
                request.callback.onDenied(
                    request.grantedPermissionList,
                    request.deniedPermissionList
                )
            } else {
                // still Never Ask Again
                request.callback.onNeverAsked(
                    request.grantedPermissionList,
                    request.deniedPermissionList,
                    request.neverAskPermissionList
                )
            }
            // process next PermissionRequest
            processNextPermissionRequest(true)
            return
        }
        // permission is granted
        request.neverAskPermissionList.removeFirst()
        request.grantedPermissionList.add(neverAskPermission)
        if (request.grantedPermissionList.size == request.permissionList.size) {
            request.callback.onGranted(request.grantedPermissionList)
            // process next PermissionRequest
            processNextPermissionRequest(true)
            return
        } else {
            requestPermissionWithProcessorChain()
        }
    }

    /**
     * request permissions by [PermissionFragment.processors]
     */
    private fun requestPermissionWithProcessorChain() {
        if (canProcessCurrentPermissionRequest().not()) {
            return
        }
        val currentRequest = permissionRequestQueue.peek()!!
        val permission = currentRequest.deniedPermissionList.first
        run {
            processors.forEach { processor ->
                if (processor.canProcessPermission(permission)) {
                    val launcher = processorLauncherMap[processor]!!
                    processor.requestPermission(requireActivity(), launcher, permission)
                    return@run
                }
            }
        }
    }

    /**
     * Check whether we can process current [PermissionRequest] or not, we have something to do
     * below :
     *
     * 1. if current [PermissionFragment] is recycled, we can not handle current [PermissionRequest]
     * 2. if current [PermissionRequest] is null, we can not handle current [PermissionRequest]
     * 3. if current [PermissionRequest] is not in processing, we can not handle current
     * [PermissionRequest]
     * 4. if current [PermissionRequest] has denied permission needed to request
     */
    private fun canProcessCurrentPermissionRequest(): Boolean {
        // 1. check if current PermissionFragment is recycled
        if (activity == null) {
            return false
        }
        // 2. check if current PermissionRequest is null
        val currentRequest = permissionRequestQueue.peek() ?: return false
        // 3. check if current PermissionRequest is in processing
        if (currentRequest.isBeingProcessed.not()) {
            return false
        }
        // 4. check if there is a denied permission need to request
        currentRequest.deniedPermissionList.peek() ?: return false
        return true
    }

    /**
     * A [PermissionResultCallback] to handle the result of requesting permissions
     */
    inner class PermissionResultCallback : PermissionProcessor.Callback {
        override fun onGranted() {
            if (canProcessCurrentPermissionRequest().not()) {
                return
            }
            // get current PermissionRequest and current permission
            val currentRequest = permissionRequestQueue.peek()!!
            val permission = currentRequest.deniedPermissionList.removeFirst()
            currentRequest.grantedPermissionList.add(permission)
            // check if grantedPermissionList's size is equal permissionList
            if (currentRequest.permissionList.size == currentRequest.grantedPermissionList.size) {
                // invoke granted callback
                currentRequest.callback.onGranted(currentRequest.grantedPermissionList)
                // process next PermissionRequest
                processNextPermissionRequest(true)
                return
            }
            // process the next permission of current PermissionRequest
            requestPermissionWithProcessorChain()
        }

        override fun onDenied() {
            if (canProcessCurrentPermissionRequest().not()) {
                return
            }
            // get current PermissionRequest and current permission
            val currentRequest = permissionRequestQueue.peek()!!
            val permission = currentRequest.deniedPermissionList.first
            // check whether the permission is just denied not never ask again
            val justDenied = PermissionUtils.shouldShowRequestPermissionRationale(
                requireActivity(), permission
            )
            if (justDenied) {
                // check whether PermissionDenialBehavior is exist or not, if so invoke it
                if (currentRequest.denialBehavior != null) {
                    currentRequest.denialBehavior.onDenied(
                        requireActivity(),
                        currentRequest.grantedPermissionList,
                        currentRequest.deniedPermissionList,
                        DenialRequest(
                            currentRequest,
                            onCancel = {
                                processNextPermissionRequest(true)
                            },
                            onRequestAgain = {
                                if (permissionRequestQueue.isNotEmpty()) {
                                    permissionRequestQueue.removeFirst()
                                }
                            }
                        )
                    )
                    return
                }
                // invoke denied callback
                currentRequest.callback.onDenied(
                    currentRequest.grantedPermissionList,
                    currentRequest.deniedPermissionList
                )
                // process next PermissionRequest
                processNextPermissionRequest(true)
                return
            }
            // Never Ask Again
            currentRequest.neverAskPermissionList.add(permission)
            currentRequest.deniedPermissionList.removeFirst()
            // check whether PermissionNeverAskBehavior is exist or not, if so invoke it
            if (currentRequest.neverAskBehavior != null) {
                currentRequest.neverAskBehavior.onNeverAsk(
                    requireActivity(),
                    currentRequest.grantedPermissionList,
                    currentRequest.deniedPermissionList,
                    currentRequest.neverAskPermissionList,
                    NeverAskRequest(
                        currentRequest,
                        onCancel = {
                            processNextPermissionRequest(true)
                        }
                    )
                )
                return
            }
            // invoke neverAsked callback
            currentRequest.callback.onNeverAsked(
                currentRequest.grantedPermissionList,
                currentRequest.deniedPermissionList,
                currentRequest.neverAskPermissionList
            )
            // process next PermissionRequest
            processNextPermissionRequest(true)
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
                // check whether this PermissionFragment can process PermissionRequest directly,
                // if not waiting the lifecycle methods to be called to handle permissions,
                // otherwise process the PermissionRequest directly
                if (fragment.canProcessRequestDirectly) {
                    fragment.processNextPermissionRequest()
                }
            }
        }
    }
}