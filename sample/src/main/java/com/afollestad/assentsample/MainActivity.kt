/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.assentsample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assentsample.fragment.FragmentSampleActivity
import kotlinx.android.synthetic.main.activity_main.requestPermissionButton
import kotlinx.android.synthetic.main.activity_main.statusText

/** @author Aidan Follestad (afollestad) */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    fun performRequest() =
      askForPermissions(WRITE_EXTERNAL_STORAGE, CALL_PHONE) { result ->
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

  override fun onResume() {
    super.onResume()
    if (isAllGranted(WRITE_EXTERNAL_STORAGE, CALL_PHONE)) {
      statusText.setText(R.string.all_granted)
    } else {
      statusText.setText(R.string.none_granted)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.fragment) {
      startActivity<FragmentSampleActivity>()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
