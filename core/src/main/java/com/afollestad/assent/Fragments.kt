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

package com.afollestad.assent

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import com.afollestad.assent.internal.Assent.Companion.ensureFragment
import com.afollestad.assent.internal.log
import com.afollestad.assent.rationale.RationaleHandler

/**
 * Returns true if ALL given [permissions] have been granted.
 */
@CheckResult fun Fragment.isAllGranted(vararg permissions: Permission) =
  activity?.isAllGranted(*permissions) ?: error("Fragment's Activity is null.")

/**
 * Performs a permission request, asking for all given [permissions], and
 * invoking the [callback] with the result.
 */
fun Fragment.askForPermissions(
  vararg permissions: Permission,
  requestCode: Int = 60,
  rationaleHandler: RationaleHandler? = null,
  callback: Callback
) = startPermissionRequest(
    attacher = { fragment -> ensureFragment(fragment) },
    permissions = permissions,
    requestCode = requestCode,
    rationaleHandler = rationaleHandler?.withOwner(this),
    callback = callback
)

/**
 * Like [askForPermissions], but only executes the [execute] callback if all given
 * [permissions] are granted.
 */
fun Fragment.runWithPermissions(
  vararg permissions: Permission,
  requestCode: Int = 80,
  rationaleHandler: RationaleHandler? = null,
  execute: Callback
) {
  log("runWithPermissions($permissions)")
  askForPermissions(
      *permissions,
      requestCode = requestCode,
      rationaleHandler = rationaleHandler?.withOwner(this)
  ) {
    if (it.isAllGranted(*permissions)) {
      execute.invoke(it)
    }
  }
}
