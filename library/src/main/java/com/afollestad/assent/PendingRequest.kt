/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

/** @author Aidan Follestad (afollestad) */
internal data class PendingRequest(
  val permissions: Array<Permission>,
  var requestCode: Int,
  val callbacks: MutableList<Callback>
) {
  override fun hashCode(): Int {
    return permissions.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return other != null &&
        other is PendingRequest &&
        this.permissions.contentEquals(other.permissions)
  }
}
