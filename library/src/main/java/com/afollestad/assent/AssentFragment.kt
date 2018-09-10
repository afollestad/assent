/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("unused")

package com.afollestad.assent

import android.os.Bundle
import android.support.v4.app.Fragment
import com.afollestad.assent.Assent.Companion.onPermissionsResponse
import com.afollestad.assent.Assent.Companion.setAssentFragment

/** @author Aidan Follestad (afollestad) */
open class AssentFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setAssentFragment(this, this)
  }

  override fun onResume() {
    super.onResume()
    setAssentFragment(this, this)
  }

  override fun onPause() {
    setAssentFragment(this, null)
    super.onPause()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    onPermissionsResponse(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}
