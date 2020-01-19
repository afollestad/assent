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

import android.content.pm.PackageManager
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Permission

class MockRequestResponder {
  private val allow = mutableSetOf<Permission>()
  private val requestLog = mutableListOf<Array<out Permission>>()

  fun allow(vararg permissions: Permission) {
    allow.addAll(permissions)
  }

  fun deny(vararg permissions: Permission) {
    allow.removeAll(permissions)
  }

  fun reset() {
    allow.clear()
    requestLog.clear()
  }

  fun log(): List<Array<out Permission>> {
    return requestLog
  }

  val requester: Requester = { permissions, _, _, callback ->
    requestLog.add(permissions)
    val grantResults = IntArray(permissions.size) {
      val permission: Permission = permissions[it]
      if (allow.contains(permission)) {
        PackageManager.PERMISSION_GRANTED
      } else {
        PackageManager.PERMISSION_DENIED
      }
    }
    val result = AssentResult(
        permissions.toList(),
        grantResults
    )
    callback(result)
  }
}
