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
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions

internal class DialogRationaleHandler(
  private val context: Activity,
  @StringRes private val dialogTitle: Int,
  requester: Requester
) : RationaleHandler(context, requester) {
  private var dialog: AlertDialog? = null

  override fun showRationale(
    permission: Permission,
    message: CharSequence,
    confirm: ConfirmCallback
  ) {
    dialog = AlertDialog.Builder(context)
      .setTitle(dialogTitle)
      .setMessage(message)
      .setPositiveButton(android.R.string.ok) { dialog, _ ->
        (dialog as AlertDialog).setOnDismissListener(null)
        confirm(isConfirmed = true)
      }
      .setOnDismissListener {
        confirm(isConfirmed = false)
      }
      .show()
  }

  override fun onDestroy() {
    dialog?.dismiss()
    dialog = null
  }
}

fun Fragment.createDialogRationale(
  @StringRes dialogTitle: Int,
  block: RationaleHandler.() -> Unit
): RationaleHandler {
  return DialogRationaleHandler(
    dialogTitle = dialogTitle,
    context = activity ?: error("Fragment not attached"),
    requester = ::askForPermissions
  ).apply(block)
}

fun Activity.createDialogRationale(
  @StringRes dialogTitle: Int,
  block: RationaleHandler.() -> Unit
): RationaleHandler {
  return DialogRationaleHandler(
    dialogTitle = dialogTitle,
    context = this,
    requester = ::askForPermissions
  ).apply(block)
}
