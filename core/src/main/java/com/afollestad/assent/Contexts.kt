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
package com.afollestad.assent

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import com.afollestad.assent.internal.Assent.Companion.get
import com.afollestad.assent.internal.PendingRequest
import com.afollestad.assent.internal.PermissionFragment
import com.afollestad.assent.internal.equalsPermissions
import com.afollestad.assent.internal.log
import com.afollestad.assent.rationale.RationaleHandler
import com.afollestad.assent.rationale.ShouldShowRationale

/** @return `true` if ALL given [permissions] have been granted. */
@CheckResult fun Context.isAllGranted(vararg permissions: Permission): Boolean {
  return permissions.all {
    ContextCompat.checkSelfPermission(
        this, it.value
    ) == PERMISSION_GRANTED
  }
}

internal fun <T : Any> T.startPermissionRequest(
  ensure: (T) -> PermissionFragment,
  permissions: Array<out Permission>,
  requestCode: Int = 20,
  shouldShowRationale: ShouldShowRationale,
  rationaleHandler: RationaleHandler? = null,
  callback: Callback
) {
  log("startPermissionRequest(%s)", permissions.joinToString())
  // This invalidates the `shouldShowRationale` cache to help detect permanently denied early.
  permissions.forEach { shouldShowRationale.check(it) }

  if (rationaleHandler != null) {
    rationaleHandler.requestPermissions(permissions, requestCode, callback)
    return
  }

  val currentRequest: PendingRequest? = get().currentPendingRequest
  if (currentRequest != null &&
      currentRequest.permissions.equalsPermissions(*permissions)
  ) {
    // Request matches permissions, append a callback
    log(
        "Callback appended to existing matching request for %s",
        permissions.joinToString()
    )
    currentRequest.callbacks.add(callback)
    return
  }

  // Create a new pending request since none exist for these permissions
  val newPendingRequest = PendingRequest(
      permissions = permissions.toSet(),
      requestCode = requestCode,
      callbacks = mutableListOf(callback)
  )

  if (currentRequest == null) {
    // There is no active request so we can execute immediately
    get().currentPendingRequest = newPendingRequest
    log("New request, performing now")
    ensure(this).perform(newPendingRequest)
  } else {
    // There is an active request, append this new one to the queue
    if (currentRequest.requestCode == requestCode) {
      newPendingRequest.requestCode = requestCode + 1
    }
    log("New request queued for when the current is complete")
    get().requestQueue += newPendingRequest
  }
}
