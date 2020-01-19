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
package com.afollestad.assent.testutil

import com.afollestad.assent.AssentResult
import com.afollestad.assent.Callback
import com.google.common.truth.Truth.assertThat

class AssertableCallback {
  private var results = mutableListOf<AssentResult>()

  val consumer: Callback = {
    results.add(it)
  }

  fun assertInvokes(vararg expected: AssentResult) {
    if (results.isEmpty()) {
      throw AssertionError("The callback was not invoked")
    }
    assertThat(results).isEqualTo(expected.toMutableList())
    results.clear()
  }

  fun assertDoesNotInvoke() {
    if (results.isNotEmpty()) {
      throw AssertionError("The callback was invoked")
    }
  }
}
