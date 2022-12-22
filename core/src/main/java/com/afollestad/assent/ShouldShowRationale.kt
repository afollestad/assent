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
package com.afollestad.assent

import android.app.Activity
import androidx.annotation.CheckResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

interface ShouldShowRationale {
  fun check(permission: Permission): Boolean

  @CheckResult fun isPermanentlyDenied(permission: Permission): Boolean
}

internal class DefaultShouldShowRationale(
  private val activity: Activity,
  private val prefs: Prefs = DefaultPrefs(activity)
) : ShouldShowRationale {

  override fun check(permission: Permission): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.value)
      .also { shouldShow ->
        if (shouldShow) prefs.set(permission.key(), true)
      }
  }

  /**
   * Android provides a utility method, `shouldShowRequestPermissionRationale()`, that returns:
   *   - `true` if the user has previously denied the request...
   *   - `false` if a user has denied a permission and selected the "Don't ask again" option in
   *      the permission request dialog...
   *   - `false` if a device policy prohibits the permission.
   */
  override fun isPermanentlyDenied(permission: Permission): Boolean {
    val showRationaleWasTrue: Boolean = prefs[permission.key()] ?: false
    return showRationaleWasTrue && !permission.isGranted() && !check(permission)
  }

  /**
   * Provides a sanity check to avoid falsely returning true in [isPermanentlyDenied]. See
   * [https://github.com/afollestad/assent/issues/16].
   */
  private fun Permission.isGranted(): Boolean =
    ContextCompat.checkSelfPermission(activity, value) == PERMISSION_GRANTED

  private fun Permission.key() = "${KEY_SHOULD_SHOW_RATIONALE}_$value"
}

private const val KEY_SHOULD_SHOW_RATIONALE = "show_rationale_"
