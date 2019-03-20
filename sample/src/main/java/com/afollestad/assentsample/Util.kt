package com.afollestad.assentsample

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

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
