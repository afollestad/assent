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
@file:Suppress("unused")

package com.afollestad.assent.rationale

import android.app.Activity
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Callback
import com.afollestad.assent.DefaultPrefs
import com.afollestad.assent.DefaultShouldShowRationale
import com.afollestad.assent.GrantResult.DENIED
import com.afollestad.assent.GrantResult.PERMANENTLY_DENIED
import com.afollestad.assent.Permission
import com.afollestad.assent.Prefs
import com.afollestad.assent.ShouldShowRationale
import com.afollestad.assent.internal.log
import com.afollestad.assent.internal.maybeObserveLifecycle
import com.afollestad.assent.plus
import kotlin.properties.Delegates.notNull

typealias Requester = (Array<out Permission>, Int, RationaleHandler?, Callback) -> Unit

abstract class RationaleHandler(
  private val context: Activity,
  private val requester: Requester,
  shouldShowRationale: ShouldShowRationale? = null
) {
  private val messages = mutableMapOf<Permission, CharSequence>()
  private var requestCode: Int by notNull()
  private var callback: Callback by notNull()
  private var remainingRationalePermissions: MutableSet<Permission> by notNull()
  private val prefs: Prefs = DefaultPrefs(context)
  private var showRationale: ShouldShowRationale =
    shouldShowRationale ?: DefaultShouldShowRationale(context, prefs)

  private var simplePermissionsResult: AssentResult? = null
  private var rationalePermissionsResult: AssentResult? = null
  private var owner: Any = context

  @CheckResult internal fun withOwner(owner: Any) = apply { this.owner = owner }

  fun onPermission(
    permission: Permission,
    @StringRes message: Int
  ) = onPermission(permission, context.getText(message))

  fun onPermission(
    permission: Permission,
    message: CharSequence
  ) {
    messages[permission] = message
  }

  fun requestPermissions(
    permissions: Array<out Permission>,
    requestCode: Int,
    finalCallback: Callback
  ) {
    this.requestCode = requestCode
    this.callback = finalCallback

    remainingRationalePermissions = permissions
      .filter {
        showRationale.check(it) ||
          showRationale.isPermanentlyDenied(it)
      }
      .toMutableSet()
    val simplePermissions = permissions.filterNot { showRationale.check(it) }

    log(
      "Found %d permissions that DO require a rationale: %s",
      remainingRationalePermissions.size,
      remainingRationalePermissions.joinToString()
    )
    if (simplePermissions.isEmpty()) {
      log("No simple permissions to request")
      requestRationalePermissions()
      return
    }

    requester(simplePermissions.toTypedArray(), requestCode, null) {
      simplePermissionsResult = it
      requestRationalePermissions()
    }
  }

  abstract fun showRationale(
    permission: Permission,
    message: CharSequence,
    confirm: ConfirmCallback
  )

  abstract fun onDestroy()

  private fun requestRationalePermissions() {
    val nextInQueue = remainingRationalePermissions.firstOrNull() ?: return finish()
    log("Showing rationale for permission %s", nextInQueue)
    owner.maybeObserveLifecycle(ON_DESTROY) { onDestroy() }

    if (showRationale.isPermanentlyDenied(nextInQueue)) {
      onPermanentlyDeniedDetected(nextInQueue)
      return
    }

    showRationale(
      nextInQueue,
      getMessageFor(nextInQueue),
      ConfirmCallback { confirmed ->
        if (confirmed) {
          onUserConfirmedRationale(nextInQueue)
        } else {
          onUserDeniedRationale(nextInQueue)
        }
      }
    )
  }

  private fun onUserConfirmedRationale(permission: Permission) {
    log("Got rationale confirm signal for permission %s", permission)
    requester(arrayOf(permission), requestCode, null) {
      rationalePermissionsResult += it
      remainingRationalePermissions.remove(permission)
      requestRationalePermissions()
    }
  }

  private fun onUserDeniedRationale(permission: Permission) {
    log("Got rationale deny signal for permission %s", permission)
    rationalePermissionsResult += AssentResult(mapOf(permission to DENIED))
    remainingRationalePermissions.remove(permission)
    requestRationalePermissions()
  }

  private fun onPermanentlyDeniedDetected(permission: Permission) {
    log("Permission %s is permanently denied.", permission)
    rationalePermissionsResult += AssentResult(mapOf(permission to PERMANENTLY_DENIED))
    remainingRationalePermissions.remove(permission)
    requestRationalePermissions()
  }

  private fun finish() {
    log("finish()")
    val simpleResult = simplePermissionsResult
    val rationaleResult = rationalePermissionsResult
    when {
      simpleResult != null && rationaleResult != null -> {
        callback(simpleResult + rationaleResult)
      }
      simpleResult != null -> callback(simpleResult)
      rationaleResult != null -> callback(rationaleResult)
    }
  }

  private fun getMessageFor(permission: Permission): CharSequence {
    return messages[permission] ?: error("No message provided for $permission")
  }
}
