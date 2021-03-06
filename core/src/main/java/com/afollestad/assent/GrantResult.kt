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

import android.content.pm.PackageManager
import com.afollestad.assent.GrantResult.DENIED
import com.afollestad.assent.GrantResult.GRANTED
import com.afollestad.assent.GrantResult.PERMANENTLY_DENIED
import com.afollestad.assent.rationale.ShouldShowRationale

/** @author Aidan Follestad (@afollestad) */
enum class GrantResult {
  GRANTED,
  DENIED,
  PERMANENTLY_DENIED
}

internal fun Int.asGrantResult(
  forPermission: Permission,
  shouldShowRationale: ShouldShowRationale
): GrantResult {
  if (shouldShowRationale.isPermanentlyDenied(forPermission)) {
    return PERMANENTLY_DENIED
  }
  return when (this) {
    PackageManager.PERMISSION_GRANTED -> GRANTED
    else -> DENIED
  }
}

internal fun IntArray.mapGrantResults(
  permissions: Set<Permission>,
  shouldShowRationale: ShouldShowRationale
): List<GrantResult> {
  return mapIndexed { index, grantResult ->
    val permission: Permission = permissions.elementAt(index)
    grantResult.asGrantResult(permission, shouldShowRationale)
  }
}
