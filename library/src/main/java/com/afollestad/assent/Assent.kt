/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("unused")

package com.afollestad.assent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log

typealias Callback = (result: AssentResult) -> Unit

internal typealias RequestExecutor = (request: PendingRequest) -> Unit

/** @author Aidan Follestad (afollestad) */
class Assent private constructor() {

  internal var activity: Activity? = null
  internal var fragment: Fragment? = null
  internal val context: Context
    get() = when {
      activity != null -> activity!!
      fragment != null -> fragment!!.activity!!
      else ->
        throw IllegalStateException("No Activity of Fragment was set.")
    }

  internal val requestQueue = Queue<PendingRequest>()
  internal var currentPendingRequest: PendingRequest? = null

  companion object {

    private val LOCK = Any()

    @SuppressLint("StaticFieldLeak")
    private var instance: Assent? = null
    internal val safeInstance: Assent
      get() {
        if (instance == null)
          instance = Assent()
        return instance!!
      }

    @JvmStatic fun setActivity(
      caller: Activity,
      activity: Activity?
    ) {
      if (activity == null) {
        val current = safeInstance.activity
        if (current != null && caller == current) {
          // The caller is nullifying itself
          safeInstance.activity = null
        }
      } else {
        safeInstance.activity = activity
        safeInstance.fragment = null
      }
    }

    @JvmStatic fun setFragment(
      caller: Fragment,
      fragment: Fragment?
    ) {
      if (fragment == null) {
        val current = safeInstance.fragment
        if (current != null && caller == current) {
          // The caller is nullifying itself
          safeInstance.fragment = null
        }
      } else {
        safeInstance.fragment = fragment
        safeInstance.activity = null
      }
    }

    @JvmStatic fun request(
      permission: Permission,
      requestCode: Int = 69,
      callback: Callback
    ) = request(arrayOf(permission), requestCode, callback)

    @JvmStatic fun request(
      permissions: Array<Permission>,
      requestCode: Int = 69,
      callback: Callback
    ) = synchronized(LOCK) {
      val currentRequest = safeInstance.currentPendingRequest
      if (currentRequest != null &&
          currentRequest.permissions.contentEquals(permissions)
      ) {
        // Request matches permissions, append a callback
        currentRequest.callbacks.add(callback)
        return@synchronized
      }

      // Create a new pending request since none exist for these permissions
      val newPendingRequest = PendingRequest(
          permissions = permissions,
          requestCode = requestCode,
          callbacks = mutableListOf(callback)
      )

      if (currentRequest == null) {
        // There is no active request so we can execute immediately
        safeInstance.currentPendingRequest = newPendingRequest
        requestExecutor(newPendingRequest)
      } else {
        // There is an active request, append this new one to the queue
        if (currentRequest.requestCode == requestCode) {
          newPendingRequest.requestCode = requestCode + 1
        }
        safeInstance.requestQueue += newPendingRequest
      }
    }

    @JvmStatic fun isAllGranted(vararg permissions: Permission): Boolean {
      val context = safeInstance.context
      for (perm in permissions) {
        val granted = checkSelfPermission(context, perm.value) == PERMISSION_GRANTED
        if (!granted) return false
      }
      return true
    }

    @JvmStatic fun response(
      permissions: Array<out String>,
      grantResults: IntArray
    ) = synchronized(LOCK) {

      val currentRequest = safeInstance.currentPendingRequest
      if (currentRequest == null) {
        Log.w(
            "Assent",
            "response() called but there's no current pending request."
        )
        return@synchronized
      }

      if (currentRequest.permissions.equalsStrings(permissions)) {
        // Execute the response
        val result = AssentResult(
            permissions = permissions.toPermissions(),
            grantResults = grantResults
        )
        currentRequest.callbacks.invokeAll(result)
        safeInstance.currentPendingRequest = null
      } else {
        Log.w(
            "Assent",
            "response() called with a result that doesn't match the current pending request."
        )
        return@synchronized
      }

      if (safeInstance.requestQueue.isNotEmpty()) {
        // Execute the next request in the queue
        safeInstance.currentPendingRequest = safeInstance.requestQueue.pop()
        requestExecutor(safeInstance.currentPendingRequest!!)
      }
    }

    fun destroy() {
      if (instance != null) {
        with(instance!!) {
          activity = null
          fragment = null
          currentPendingRequest = null
          requestQueue.clear()
        }
        instance = null
      }
    }

    /** Allows us to override in unit tests. */
    internal var requestExecutor: RequestExecutor = this::defaultRequestExecutor

    private fun defaultRequestExecutor(request: PendingRequest) {
      when {
        safeInstance.fragment != null ->
          safeInstance.fragment!!.requestPermissions(
              request.permissions.allValues(),
              request.requestCode
          )
        safeInstance.activity != null ->
          ActivityCompat.requestPermissions(
              safeInstance.activity!!,
              request.permissions.allValues(),
              request.requestCode
          )
        else -> throw IllegalStateException(
            "No Activity or Fragment attached to dispatch a request!"
        )
      }
    }
  }
}
