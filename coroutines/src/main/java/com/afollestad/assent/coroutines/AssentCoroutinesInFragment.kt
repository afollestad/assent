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
package com.afollestad.assent.coroutines

import androidx.fragment.app.Fragment
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.rationale.RationaleHandler
import com.afollestad.assent.runWithPermissions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Performs a permission request, asking for all given [permissions],
 * and returning the result.
 */
suspend fun Fragment.awaitPermissionsResult(
  vararg permissions: Permission,
  requestCode: Int = 60,
  rationaleHandler: RationaleHandler? = null
): AssentResult {
  checkMainThread()
  return suspendCoroutine { continuation ->
    askForPermissions(
        permissions = *permissions,
        requestCode = requestCode,
        rationaleHandler = rationaleHandler
    ) { result ->
      continuation.resume(result)
    }
  }
}

/**
 * Like [awaitPermissionsResult], but only returns if all given
 * permissions are granted. So be warned, this method will wait
 * indefinitely if permissions are not all granted.
 */
suspend fun Fragment.awaitPermissionsGranted(
  vararg permissions: Permission,
  requestCode: Int = 80,
  rationaleHandler: RationaleHandler? = null
): AssentResult {
  checkMainThread()
  return suspendCoroutine { continuation ->
    runWithPermissions(
        permissions = *permissions,
        requestCode = requestCode,
        rationaleHandler = rationaleHandler
    ) { result ->
      continuation.resume(result)
    }
  }
}
