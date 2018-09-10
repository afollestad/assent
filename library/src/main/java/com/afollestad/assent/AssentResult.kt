/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.afollestad.assent

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.afollestad.assent.internal.containsPermission

/**
 * Wraps a result for a permission request, which provides utility
 * methods and is sent through callbacks.
 *
 * @author Aidan Follestad (afollestad)
 */
class AssentResult(
  val permissions: List<Permission>,
  val grantResults: IntArray
) {
  init {
    if (permissions.size != grantResults.size) {
      throw IllegalStateException("Permissions and grant results sizes should match.")
    }
  }

  /** Returns true if this result contains the given permission. */
  fun containsPermissions(permission: Permission) =
    this.permissions.containsPermission(permission)

  fun isAllGranted(permissions: List<Permission>): Boolean {
    for (perm in permissions) {
      val index = this.permissions.indexOfFirst { it.value == perm.value }
      if (index == -1) {
        throw IllegalArgumentException(
            "Permission ${perm.name} doesn't exist in this result set."
        )
      }
      val granted = this.grantResults[index] == PERMISSION_GRANTED
      if (!granted) return false
    }
    return true
  }

  /** Returns true if all permissions in the given array have been granted. */
  fun isAllGranted(vararg permissions: Permission) = isAllGranted(permissions.toList())

  /** Returns true if all permissions in the given array have been denied. */
  fun isAllDenied(vararg permissions: Permission): Boolean {
    for (perm in permissions) {
      val index = this.permissions.indexOfFirst { it.value == perm.value }
      if (index == -1) {
        throw IllegalArgumentException(
            "Permission ${perm.name} doesn't exist in this result set."
        )
      }
      val granted = this.grantResults[index] == PERMISSION_DENIED
      if (!granted) return false
    }
    return true
  }
}
