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

import com.afollestad.assent.GrantResult.DENIED
import com.afollestad.assent.GrantResult.GRANTED
import com.afollestad.assent.GrantResult.PERMANENTLY_DENIED

/** @author Aidan Follestad (@afollestad) */
enum class GrantResult {
  GRANTED,
  DENIED,
  PERMANENTLY_DENIED
}

internal fun Permission.withGrantResult(
  isGranted: Boolean,
  shouldShowRationale: ShouldShowRationale
): Pair<Permission, GrantResult> =
  this to when {
    shouldShowRationale.isPermanentlyDenied(this) -> PERMANENTLY_DENIED
    isGranted -> GRANTED
    else -> DENIED
  }
