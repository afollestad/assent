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
package com.afollestad.assent.internal

import com.afollestad.assent.AssentResult
import com.afollestad.assent.Callback
import com.afollestad.assent.Permission

internal fun Set<Permission>.equalsStrings(strings: Set<String>): Boolean {
  if (this.size != strings.size) {
    return false
  }
  for ((i, perm) in this.withIndex()) {
    if (perm.value != strings.elementAt(i)) {
      return false
    }
  }
  return true
}

internal fun Set<Permission>.allValues(): Array<String> =
  map { it.value }.toTypedArray()

internal fun List<Callback>.invokeAll(result: AssentResult) {
  for (callback in this) {
    callback.invoke(result)
  }
}
