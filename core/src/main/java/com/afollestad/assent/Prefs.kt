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

import android.content.Context
import android.content.Context.MODE_PRIVATE

internal interface Prefs {
  fun set(
    key: String,
    value: Any
  )

  operator fun <T : Any> get(key: String): T?
}

internal class DefaultPrefs(context: Context) : Prefs {
  private val sharedPrefs = context.getSharedPreferences(KEY_ASSENT_PREFS, MODE_PRIVATE)

  override fun set(
    key: String,
    value: Any
  ) {
    with(sharedPrefs.edit()) {
      when (value) {
        is String -> putString(key, value)
        is Boolean -> putBoolean(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Float -> putFloat(key, value)
        else -> error("Cannot put value $value in shared preferences.")
      }
      apply()
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> get(key: String): T? {
    return sharedPrefs.all[key] as? T
  }
}

private const val KEY_ASSENT_PREFS = "[com.afollestad.assent-prefs]"
