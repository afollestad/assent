/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent.internal

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

internal class Data {

  internal val requestQueue = Queue<PendingRequest>()
  internal var currentPendingRequest: PendingRequest? = null
  internal var permissionFragment: PermissionFragment? = null

  companion object {

    val LOCK = Any()
    var instance: Data? = null

    private const val TAG_ACTIVITY = "[assent_permission_fragment/activity]"
    private const val TAG_FRAGMENT = "[assent_permission_fragment/fragment]"

    fun get(): Data {
      if (instance == null) {
        instance = Data()
      }
      return instance!!
    }

    fun assureFragment(context: Context): PermissionFragment = with(get()) {
      if (permissionFragment == null) {
        permissionFragment = PermissionFragment()
        when (context) {
          is FragmentActivity -> context.transact {
            add(permissionFragment!!, TAG_ACTIVITY)
          }
          else -> throw UnsupportedOperationException(
              "Unable to assure the permission Fragment on Context $context"
          )
        }
      }
      return permissionFragment!!
    }

    fun assureFragment(context: Fragment): PermissionFragment = with(get()) {
      if (permissionFragment == null) {
        permissionFragment = PermissionFragment()
        context.transact {
          add(permissionFragment!!, TAG_FRAGMENT)
        }
      }
      return permissionFragment!!
    }

    fun forgetFragment() = with(get()) {
      permissionFragment?.detach()
      permissionFragment = null
    }
  }
}
