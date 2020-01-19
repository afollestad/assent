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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.afollestad.assent.testutil

import android.content.pm.PackageManager
import com.afollestad.assent.Permission
import com.afollestad.assent.internal.PermissionFragment

data class QueuedRequest(
  val permissions: Array<out String>,
  val requestCode: Int
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as QueuedRequest

    if (!permissions.contentEquals(other.permissions)) return false
    if (requestCode != other.requestCode) return false

    return true
  }

  override fun hashCode(): Int {
    var result = permissions.contentHashCode()
    result = 31 * result + requestCode
    return result
  }
}

class MockResponseQueue(
  private val allowedPermissions: Set<Permission>,
  private val permissionFragment: PermissionFragment
) {
  private val queue = mutableListOf<QueuedRequest>()

  fun handle(
    permissions: Array<out String>,
    requestCode: Int
  ) {
    queue.add(QueuedRequest(permissions, requestCode))
  }

  fun respondToOne(which: QueuedRequest? = null) = with(which ?: queue.first()) {
    val grantResults = IntArray(permissions.size) { index ->
      val parsedPermission = Permission.parse(permissions[index])
      if (allowedPermissions.contains(parsedPermission)) {
        PackageManager.PERMISSION_GRANTED
      } else {
        PackageManager.PERMISSION_DENIED
      }
    }
    permissionFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
    queue.remove(this)
  }

  fun respondToAll() {
    if (queue.isEmpty()) {
      throw AssertionError("No requests to respond to")
    }
    while (queue.isNotEmpty()) {
      respondToOne()
    }
  }
}
