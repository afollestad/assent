package com.afollestad.assentsample.fragment

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assentsample.R
import com.afollestad.assentsample.transact

/** @author Aidan Follestad (afollestad) */
class FragmentSampleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_fragment)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    transact {
      replace(
          R.id.container,
          ExampleFragment()
      )
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
