/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import android.os.Bundle
import android.support.v4.app.Fragment

/** @author Aidan Follestad (afollestad) */
open class AssentFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Assent.setFragment(this, this)
  }

  override fun onResume() {
    super.onResume()
    Assent.setFragment(this, this)
  }

  override fun onPause() {
    Assent.setFragment(this, null)
    super.onPause()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    Assent.response(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}
