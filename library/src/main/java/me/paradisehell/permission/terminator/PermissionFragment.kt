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

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import me.paradisehell.permission.terminator.processor.PermissionProcessor
import me.paradisehell.permission.terminator.request.PermissionRequest
import java.util.*


/**
 *
 * @author Tao Cheng (tao@paradisehell.org)
 */
internal class PermissionFragment : Fragment() {
    private val permissionRequestQueue = LinkedList<PermissionRequest>()
    private var permissionIndex = -1
    private val processors = mutableListOf<PermissionProcessor<*>>()
    private val processorLauncherMap =
        mutableMapOf<PermissionProcessor<*>, ActivityResultLauncher<*>>()
    private var canProcessPermissionImmediately = false

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
        canProcessPermissionImmediately = true
        processNextPermissionRequest()
    }

    override fun onDestroy() {
        processors.clear()
        processorLauncherMap.values.forEach { launcher ->
            launcher.unregister()
        }
        processorLauncherMap.clear()
        super.onDestroy()
    }

    private fun processNextPermissionRequest() {
        val currentRequest = permissionRequestQueue.peek() ?: return
        if (currentRequest.isProcessing) {
            return
        }
        currentRequest.isProcessing = true
        permissionIndex = 0
        requestPermissionWithProcessChain()
    }

    private fun requestPermissionWithProcessChain() {
        val currentRequest = permissionRequestQueue.peek() ?: return
        if (currentRequest.isProcessing.not()) {
            return
        }
        if (permissionIndex < 0 || permissionIndex > currentRequest.deniedPermissionList.size) {
            return
        }
        val permission = currentRequest.deniedPermissionList[permissionIndex]
        run {
            processors.forEach { processor ->
                if (processor.canProcessPermission(permission)) {
                    val launcher = processorLauncherMap[processor] ?: return@forEach
                    processor.requestPermission(launcher, permission)
                    return@run
                }
            }
        }
    }

    inner class PermissionResultCallback : PermissionProcessor.Callback {
        override fun onGranted() {
            val currentRequest = permissionRequestQueue.peek() ?: return
            if (currentRequest.isProcessing.not()) {
                return
            }
            if (permissionIndex < 0 || permissionIndex > currentRequest.deniedPermissionList.size) {
                return
            }
            val permission = currentRequest.deniedPermissionList[permissionIndex]
            currentRequest.grantedPermissionList.add(permission)
            if (currentRequest.permissionList.size == currentRequest.grantedPermissionList.size) {
                permissionRequestQueue.removeFirst()
                currentRequest.callback.onGranted(currentRequest.grantedPermissionList)
                //
                processNextPermissionRequest()
                return
            }
            permissionIndex++
            requestPermissionWithProcessChain()
        }

        override fun onDenied() {
            val currentRequest = permissionRequestQueue.peek() ?: return
            if (currentRequest.isProcessing.not()) {
                return
            }
            if (permissionIndex < 0 || permissionIndex > currentRequest.deniedPermissionList.size) {
                return
            }
            val permission = currentRequest.deniedPermissionList[permissionIndex]
            if (PermissionUtils.shouldShowRequestPermissionRationale(
                    requireActivity(), permission
                )
            ) {
                currentRequest.callback.onDenied(
                    currentRequest.grantedPermissionList,
                    currentRequest.deniedPermissionList
                )
            } else {
                currentRequest.neverAskPermissionList.add(permission)
                currentRequest.callback.onNeverAsked(
                    currentRequest.grantedPermissionList,
                    currentRequest.deniedPermissionList,
                    currentRequest.neverAskPermissionList
                )
            }
            permissionRequestQueue.removeFirst()
            //
            processNextPermissionRequest()
        }
    }

    internal companion object {

        private const val FRAGMENT_TAG = "me.paradisehell.permission.terminator.PermissionFragment"

        fun requestPermission(request: PermissionRequest) {
            request.activity.supportFragmentManager.apply {
                var fragment = findFragmentByTag(FRAGMENT_TAG)
                if (fragment == null) {
                    fragment = PermissionFragment()
                    beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss()
                }
                if (fragment !is PermissionFragment) {
                    return
                }
                fragment.permissionRequestQueue.add(request)
                if (fragment.canProcessPermissionImmediately) {
                    fragment.processNextPermissionRequest()
                }
            }
        }
    }
}