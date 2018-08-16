/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.assent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/** @author Aidan Follestad (afollestad) */
open class AssentActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Assent.setActivity(this, this)
  }

  override fun onResume() {
    super.onResume()
    Assent.setActivity(this, this)
  }

  override fun onPause() {
    if (isFinishing) {
      Assent.setActivity(this, null)
    }
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