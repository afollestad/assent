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

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.google.android.material.snackbar.Snackbar

internal class SnackBarRationaleHandler(
  private val root: View,
  context: Activity,
  requester: Requester
) : RationaleHandler(context, requester) {

  override fun showRationale(
    permission: Permission,
    message: CharSequence,
    confirm: ConfirmCallback
  ) {
    val dismissListener = object : Snackbar.Callback() {
      override fun onDismissed(
        transientBottomBar: Snackbar?,
        event: Int
      ) = confirm(isConfirmed = false)
    }
    Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE)
        .apply {
          setAction(android.R.string.ok) {
            removeCallback(dismissListener)
            confirm(isConfirmed = true)
          }
          addCallback(dismissListener)
          show()
        }
  }

  override fun onDestroy() = Unit
}

fun Fragment.createSnackBarRationale(
  root: View,
  block: RationaleHandler.() -> Unit
): RationaleHandler {
  return SnackBarRationaleHandler(
      root = root,
      context = activity ?: error("Fragment not attached"),
      requester = ::askForPermissions
  ).apply(block)
}

fun Activity.createSnackBarRationale(
  root: View,
  block: RationaleHandler.() -> Unit
): RationaleHandler {
  return SnackBarRationaleHandler(
      root = root,
      context = this,
      requester = ::askForPermissions
  ).apply(block)
}
