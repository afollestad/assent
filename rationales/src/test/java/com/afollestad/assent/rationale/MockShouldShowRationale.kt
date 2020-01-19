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
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.afollestad.assent.rationale

import com.afollestad.assent.Permission

class MockShouldShowRationale : ShouldShowRationale {
  private val shouldShow = mutableSetOf<Permission>()
  private val shouldConfirm = mutableSetOf<Permission>()

  override fun check(permission: Permission): Boolean {
    return shouldShow.contains(permission)
  }

  fun showFor(permission: Permission) {
    shouldShow.add(permission)
  }

  fun doNotShowFor(permission: Permission) {
    shouldShow.remove(permission)
  }

  fun confirmFor(vararg permissions: Permission) {
    shouldConfirm.addAll(permissions)
  }

  fun doNotConfirmFor(vararg permissions: Permission) {
    shouldConfirm.removeAll(permissions)
  }

  fun reset() {
    shouldShow.clear()
  }

  fun handleShowRationale(
    permission: Permission,
    message: CharSequence,
    onContinue: ConfirmCallback
  ) {
    onContinue(shouldConfirm.contains(permission))
  }
}
