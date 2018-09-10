/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import android.support.v4.app.Fragment
import com.afollestad.assent.internal.Data.Companion.LOCK
import com.afollestad.assent.internal.Data.Companion.assureFragment
import com.afollestad.assent.internal.Data.Companion.get
import com.afollestad.assent.internal.PendingRequest
import com.afollestad.assent.internal.equalsPermissions

fun Fragment.isAllGranted(vararg permissions: Permission) =
  activity?.isAllGranted(*permissions) ?: throw IllegalStateException(
      "Fragment's Activity is null."
  )

fun Fragment.askForPermissions(
  vararg permissions: Permission,
  requestCode: Int = 60,
  callback: Callback
) = synchronized(LOCK) {

  val currentRequest = get().currentPendingRequest
  if (currentRequest != null &&
      currentRequest.permissions.equalsPermissions(*permissions)
  ) {
    // Request matches permissions, append a callback
    currentRequest.callbacks.add(callback)
    return@askForPermissions
  }

  // Create a new pending request since none exist for these permissions
  val newPendingRequest = PendingRequest(
      permissions = permissions.toList(),
      requestCode = requestCode,
      callbacks = mutableListOf(callback)
  )

  if (currentRequest == null) {
    // There is no active request so we can execute immediately
    get().currentPendingRequest = newPendingRequest
    assureFragment(this@askForPermissions).perform(newPendingRequest)
  } else {
    // There is an active request, append this new one to the queue
    if (currentRequest.requestCode == requestCode) {
      newPendingRequest.requestCode = requestCode + 1
    }
    get().requestQueue += newPendingRequest
  }
}

fun Fragment.runWithPermissions(
  vararg permissions: Permission,
  requestCode: Int = 80,
  execute: RunMe
) = askForPermissions(*permissions, requestCode = requestCode) {
  if (it.isAllGranted(*permissions)) {
    execute.invoke(Unit)
  }
}
