package com.afollestad.assentsample

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class SampleApp : Application() {

  override fun onCreate() {
    super.onCreate()
    Timber.plant(DebugTree())
  }
}