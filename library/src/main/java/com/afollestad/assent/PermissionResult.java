package com.afollestad.assent;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import java.io.Serializable;

/** @author Aidan Follestad (afollestad) */
class PermissionResult implements Serializable {

  private String mPermission;
  private boolean mGranted;

  protected PermissionResult(@NonNull String permission, int granted) {
    mPermission = permission;
    mGranted = granted == PackageManager.PERMISSION_GRANTED;
  }

  @NonNull
  public String getPermission() {
    return mPermission;
  }

  public boolean isGranted() {
    return mGranted;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", mPermission, mGranted ? "Granted" : "Denied");
  }
}
