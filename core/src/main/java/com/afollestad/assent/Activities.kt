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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import com.afollestad.assent.internal.Assent.Companion.ensureFragment
import com.afollestad.assent.rationale.RationaleHandler
import com.afollestad.assent.rationale.RealShouldShowRationale
import com.afollestad.assent.rationale.ShouldShowRationale

typealias Callback = (result: AssentResult) -> Unit

/**
 * Performs a permission request, asking for all given [permissions], and
 * invoking the [callback] with the result.
 */
fun Activity.askForPermissions(
  vararg permissions: Permission,
  requestCode: Int = 20,
  rationaleHandler: RationaleHandler? = null,
  callback: Callback
) {
  val prefs: Prefs = RealPrefs(this)
  val shouldShowRationale: ShouldShowRationale = RealShouldShowRationale(this, prefs)
  startPermissionRequest(
    ensure = { activity -> ensureFragment(activity) },
    permissions = permissions,
    requestCode = requestCode,
    shouldShowRationale = shouldShowRationale,
    rationaleHandler = rationaleHandler,
    callback = callback
  )
}

/**
 * Like [askForPermissions], but only executes the [execute] callback if all given
 * [permissions] are granted.
 */
fun Activity.runWithPermissions(
  vararg permissions: Permission,
  requestCode: Int = 40,
  rationaleHandler: RationaleHandler? = null,
  execute: Callback
) {
  askForPermissions(
    *permissions,
    requestCode = requestCode,
    rationaleHandler = rationaleHandler
  ) {
    if (it.isAllGranted(*permissions)) {
      execute.invoke(it)
    }
  }
}

/**
 * Launches app settings for the current app. Useful when permissions are permanently
 * denied.
 */
fun Activity.showSystemAppDetailsPage() {
  startActivity(
    Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      data = Uri.parse("package:$packageName")
    }
  )
}
