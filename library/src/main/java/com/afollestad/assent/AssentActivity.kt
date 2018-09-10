/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.assent.Assent.Companion.onPermissionsResponse
import com.afollestad.assent.Assent.Companion.setAssentActivity

/** @author Aidan Follestad (afollestad) */
open class AssentActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setAssentActivity(this, this)
  }

  override fun onResume() {
    super.onResume()
    setAssentActivity(this, this)
  }

  override fun onPause() {
    if (isFinishing) {
      setAssentActivity(this, null)
    }
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
