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
package com.afollestad.assentsample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

private var toast: Toast? = null

fun Activity.toast(message: String) {
  toast?.cancel()
  toast = Toast.makeText(this, message, LENGTH_SHORT)
      .apply { show() }
}

inline fun <reified T : Activity> Context.startActivity() {
  startActivity(Intent(this, T::class.java))
}

fun FragmentManager.transact(block: FragmentTransaction.() -> Unit) {
  beginTransaction().apply {
    block()
    commit()
  }
}

fun FragmentActivity.transact(block: FragmentTransaction.() -> Unit) {
  supportFragmentManager.transact(block)
}

fun Fragment.transact(block: FragmentTransaction.() -> Unit) {
  childFragmentManager.transact(block)
}

val View.viewScope: CoroutineScope
  get() {
    val scope: CoroutineScope? = getTag(R.id.tag_view_coroutine_scope) as? CoroutineScope
    if (scope != null) {
      return scope
    }
    return LifecycleCoroutineScope(this, SupervisorJob() + Dispatchers.Main)
  }

internal class LifecycleCoroutineScope(
  private var view: View?,
  context: CoroutineContext
) : CoroutineScope {
  init {
    view!!.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) = Unit
      override fun onViewDetachedFromWindow(v: View) {
        coroutineContext.cancel()
        view = null
      }
    })
  }

  override val coroutineContext: CoroutineContext = context
}
