package com.afollestad.assentsample.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.askForPermissions
import com.afollestad.assentsample.R
import com.afollestad.assentsample.transact
import kotlinx.android.synthetic.main.fragment_sample.requestPermissionButtonMain

/** @author Aidan Follestad (afollestad) */
class ExampleFragment : Fragment() {

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_sample, container, false)

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    transact {
      replace(
          R.id.child_container,
          ExampleChildFragment()
      )
    }

    requestPermissionButtonMain.setOnClickListener {
      askForPermissions(WRITE_EXTERNAL_STORAGE, CALL_PHONE) { }
    }
  }
}