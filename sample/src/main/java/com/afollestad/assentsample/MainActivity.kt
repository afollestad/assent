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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission.READ_CONTACTS
import com.afollestad.assent.Permission.READ_SMS
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.rationale.createSnackBarRationale
import com.afollestad.assentsample.fragment.FragmentSampleActivity

/** @author Aidan Follestad (afollestad) */
class MainActivity : AppCompatActivity() {
  private val rootView: View by lazy {
    findViewById(R.id.rootView)
  }
  private val requestPermissionButton: View by lazy {
    findViewById(R.id.requestPermissionButton)
  }
  private val statusText: TextView by lazy {
    findViewById(R.id.statusText)
  }

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
    if (isAllGranted(READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS)) {
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
