/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.assentsample

import android.os.Bundle
import com.afollestad.assent.Assent.Companion.askForPermissions
import com.afollestad.assent.Assent.Companion.isAllGranted
import com.afollestad.assent.AssentActivity
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import kotlinx.android.synthetic.main.activity_main.requestPermissionButton
import kotlinx.android.synthetic.main.activity_main.statusText

/** @author Aidan Follestad (afollestad) */
class MainActivity : AssentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (isAllGranted(WRITE_EXTERNAL_STORAGE, CALL_PHONE)) {
      statusText.setText(R.string.all_granted)
    } else {
      statusText.setText(R.string.none_granted)
    }

    fun performRequest() =
      askForPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CALL_PHONE)) { result ->
        when {
          result.isAllGranted(WRITE_EXTERNAL_STORAGE, CALL_PHONE) ->
            statusText.setText(R.string.all_granted)
          result.isAllDenied(WRITE_EXTERNAL_STORAGE, CALL_PHONE) ->
            statusText.setText(R.string.none_granted)
          else ->
            statusText.setText(R.string.some_granted)
        }
      }

    requestPermissionButton.setOnClickListener { performRequest() }
  }
}
