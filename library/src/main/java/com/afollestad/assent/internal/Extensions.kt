/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent.internal

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Callback
import com.afollestad.assent.Permission

internal fun List<Permission>.containsPermission(
  permission: Permission
) = this.indexOfFirst { it.value == permission.value } > -1

internal fun List<Permission>.equalsStrings(strings: Array<out String>): Boolean {
  if (this.size != strings.size) {
    return false
  }
  for ((i, perm) in this.withIndex()) {
    if (perm.value != strings[i]) {
      return false
    }
  }
  return true
}

internal fun List<Permission>.equalsPermissions(vararg permissions: Permission) =
  this.equalsPermissions(permissions.toList())

internal fun List<Permission>.equalsPermissions(permissions: List<Permission>): Boolean {
  if (this.size != permissions.size) {
    return false
  }
  for ((i, perm) in this.withIndex()) {
    if (perm.value != permissions[i].value) {
      return false
    }
  }
  return true
}

internal fun List<Permission>.allValues(): Array<out String> =
  this.map { it.value }.toTypedArray()

internal fun Array<out String>.toPermissions() =
  this.map { Permission.parse(it) }

internal fun List<Callback>.invokeAll(result: AssentResult) {
  for (callback in this) {
    callback.invoke(result)
  }
}

internal fun FragmentActivity.transact(action: FragmentTransaction.() -> Unit) {
  val transaction = supportFragmentManager.beginTransaction()
  transaction.action()
  transaction.commit()
  supportFragmentManager.executePendingTransactions()
}

internal fun Fragment.transact(action: FragmentTransaction.() -> Unit) {
  val transaction = fragmentManager!!.beginTransaction()
  transaction.action()
  transaction.commit()
  fragmentManager!!.executePendingTransactions()
}
