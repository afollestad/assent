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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Callback
import com.afollestad.assent.Permission

internal fun List<Permission>.containsPermission(
  permission: Permission
) = this.indexOfFirst { it.value == permission.value } > -1

internal fun List<Permission>.equalsStrings(strings: Array<out String>): Boolean {
  if (this.size != strings.size) {
    return false
  }
  for ((i, perm) in this.withIndex()) {
    if (perm.value != strings[i]) {
      return false
    }
  }
  return true
}

internal fun List<Permission>.equalsPermissions(vararg permissions: Permission) =
  this.equalsPermissions(permissions.toList())

internal fun List<Permission>.equalsPermissions(permissions: List<Permission>): Boolean {
  if (this.size != permissions.size) {
    return false
  }
  for ((i, perm) in this.withIndex()) {
    if (perm.value != permissions[i].value) {
      return false
    }
  }
  return true
}

internal fun List<Permission>.allValues(): Array<out String> =
  this.map { it.value }.toTypedArray()

internal fun Array<out String>.toPermissions() =
  this.map { Permission.parse(it) }

internal fun List<Callback>.invokeAll(result: AssentResult) {
  for (callback in this) {
    callback.invoke(result)
  }
}

internal fun FragmentActivity.transact(action: FragmentTransaction.() -> Unit) {
  val transaction = supportFragmentManager.beginTransaction()
  transaction.action()
  transaction.commit()
  supportFragmentManager.executePendingTransactions()
}

internal fun Fragment.transact(action: FragmentTransaction.() -> Unit) {
  val transaction = fragmentManager!!.beginTransaction()
  transaction.action()
  transaction.commit()
  fragmentManager!!.executePendingTransactions()
}
