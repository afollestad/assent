package com.afollestad.assentsample.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.READ_CALENDAR
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.askForPermissions
import com.afollestad.assentsample.R.layout
import kotlinx.android.synthetic.main.child_fragment_sample.requestPermissionButtonChild

/** @author Aidan Follestad (afollestad) */
class ExampleChildFragment : Fragment() {

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(layout.child_fragment_sample, container, false)

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    askForPermissions(CALL_PHONE) {
      requestPermissionButtonChild.setOnClickListener {
        askForPermissions(WRITE_EXTERNAL_STORAGE, CALL_PHONE) { }
      }
    }
  }
}