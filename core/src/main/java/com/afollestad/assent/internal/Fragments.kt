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

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

internal fun FragmentActivity.transact(action: FragmentTransaction.(Context) -> Unit) =
  supportFragmentManager.let {
    it.beginTransaction()
        .apply {
          action(this@transact)
          commit()
        }
    it.executePendingTransactions()
  }

internal fun Fragment.transact(action: FragmentTransaction.(Context) -> Unit) {
  childFragmentManager.beginTransaction()
      .apply {
        action(activity ?: error("Fragment's activity is null."))
        commit()
      }
  childFragmentManager.executePendingTransactions()
}
