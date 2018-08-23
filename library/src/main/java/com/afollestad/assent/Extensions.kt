/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

internal fun Array<Permission>.containsPermission(
  permission: Permission
) = this.indexOfFirst { it.value == permission.value } > -1

internal fun Array<Permission>.equalsStrings(strings: Array<out String>): Boolean {
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

internal fun Array<Permission>.allValues(): Array<out String> =
  this.map { it.value }.toTypedArray()

internal fun Array<out String>.toPermissions() =
  this.map { Permission.parse(it) }.toTypedArray()

internal fun List<Callback>.invokeAll(result: AssentResult) {
  for (callback in this) {
    callback.invoke(result)
  }
}
