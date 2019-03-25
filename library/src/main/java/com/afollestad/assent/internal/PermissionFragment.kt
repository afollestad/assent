/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.assent.internal

import androidx.fragment.app.Fragment
import com.afollestad.assent.AssentResult
import com.afollestad.assent.internal.Assent.Companion.ensureFragment
import com.afollestad.assent.internal.Assent.Companion.forgetFragment
import com.afollestad.assent.internal.Assent.Companion.get
import timber.log.Timber.d as log
import timber.log.Timber.w as warn

/** @author Aidan Follestad (afollestad) */
class PermissionFragment : Fragment() {

  internal fun perform(request: PendingRequest) {
    log("perform($request)")
    this.requestPermissions(request.permissions.allValues(), request.requestCode)
  }

  internal fun detach() {
    if (parentFragment != null) {
      log("Detaching PermissionFragment from parent fragment $parentFragment")
      parentFragment?.transact {
        detach(this@PermissionFragment)
        remove(this@PermissionFragment)
      }
    } else if (activity != null) {
      log("Detaching PermissionFragment from Activity $activity")
      activity?.transact {
        detach(this@PermissionFragment)
        remove(this@PermissionFragment)
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    log("onRequestPermissionsResult(\n\tpermissions = $permissions,\n\tgrantResults = $grantResults\n))")
    onPermissionsResponse(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}

internal fun Fragment.onPermissionsResponse(
  permissions: Array<out String>,
  grantResults: IntArray
) = synchronized(Assent.LOCK) {
  log("onPermissionsResponse(\n\tpermissions = $permissions,\n\tgrantResults = $grantResults\n))")

  val currentRequest = get().currentPendingRequest
  if (currentRequest == null) {
    warn("response() called but there's no current pending request.")
    return@synchronized
  }

  if (currentRequest.permissions.equalsStrings(permissions)) {
    // Execute the response
    val result = AssentResult(
        permissions = permissions.toPermissions(),
        grantResults = grantResults
    )
    log("Executing response for $permissions")
    currentRequest.callbacks.invokeAll(result)
    get().currentPendingRequest = null
  } else {
    warn(
        "onPermissionsResponse() called with a result that doesn't match the current pending request."
    )
    return@synchronized
  }

  if (get().requestQueue.isNotEmpty()) {
    // Execute the next request in the queue
    val nextRequest = get().requestQueue.pop()
    get().currentPendingRequest = nextRequest
    log("Executing next request in the queue")
    ensureFragment(this@onPermissionsResponse).perform(nextRequest)
  } else {
    // No more requests to execute, we can destroy the Fragment
    log("Nothing more in the queue to execute, forgetting the PermissionFragment.")
    forgetFragment()
  }
}
