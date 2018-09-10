/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.content.ContextCompat
import com.afollestad.assent.internal.Data.Companion.LOCK
import com.afollestad.assent.internal.Data.Companion.assureFragment
import com.afollestad.assent.internal.Data.Companion.get
import com.afollestad.assent.internal.PendingRequest
import com.afollestad.assent.internal.equalsPermissions

typealias Callback = (result: AssentResult) -> Unit
typealias RunMe = (Unit) -> Unit

fun Context.isAllGranted(vararg permissions: Permission): Boolean {
  for (perm in permissions) {
    val granted = ContextCompat.checkSelfPermission(
        this, perm.value
    ) == PERMISSION_GRANTED
    if (!granted) return false
  }
  return true
}

fun Context.askForPermissions(
  vararg permissions: Permission,
  requestCode: Int = 20,
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

fun Context.runWithPermissions(
  vararg permissions: Permission,
  requestCode: Int = 40,
  execute: RunMe
) = askForPermissions(*permissions, requestCode = requestCode) {
  if (it.isAllGranted(*permissions)) {
    execute.invoke(Unit)
  }
}
