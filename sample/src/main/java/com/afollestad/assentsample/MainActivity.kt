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
import com.afollestad.assent.Permission.READ_CONTACTS
import com.afollestad.assent.Permission.READ_SMS
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.rationale.createSnackBarRationale
import com.afollestad.assentsample.fragment.FragmentSampleActivity
import kotlinx.android.synthetic.main.activity_main.requestPermissionButton
import kotlinx.android.synthetic.main.activity_main.rootView
import kotlinx.android.synthetic.main.activity_main.statusText

/** @author Aidan Follestad (afollestad) */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    fun performRequest() {
      val rationaleHandler = createSnackBarRationale(rootView) {
        onPermission(READ_CONTACTS, "Test rationale #1, please accept!")
        onPermission(WRITE_EXTERNAL_STORAGE, "Test rationale #1, please accept!")
        onPermission(READ_SMS, "Test rationale #3, please accept!")
      }
      askForPermissions(
          READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS,
          rationaleHandler = rationaleHandler
      ) { result ->
        val statusRes = when {
          result.isAllGranted(READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS) ->
            R.string.all_granted
          result.isAllDenied(READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS) ->
            R.string.none_granted
          else -> R.string.some_granted
        }
        statusText.setText(statusRes)
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
