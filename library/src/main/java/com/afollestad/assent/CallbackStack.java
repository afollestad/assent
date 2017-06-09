package com.afollestad.assent;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Iterator;

/** @author Aidan Follestad (afollestad) */
class CallbackStack implements Iterable<AssentCallback> {

  private int mRequestCode;
  private final String[] mPermissions;
  private final ArrayList<AssentCallback> mCallbacks;
  private boolean mExecuted;

  public CallbackStack(int requestCode, String[] permissions) {
    mRequestCode = requestCode;
    mPermissions = permissions;
    mCallbacks = new ArrayList<>(2);
    mExecuted = false;
  }

  public void push(@NonNull AssentCallback callback) {
    mCallbacks.add(callback);
  }

  public void setRequestCode(int requestCode) {
    mRequestCode = requestCode;
  }

  public void sendResult(@NonNull PermissionResultSet result) {
    synchronized (mCallbacks) {
      for (AssentCallback cb : mCallbacks) cb.onPermissionResult(result);
    }
  }

  public void execute(@NonNull Activity context) {
    mExecuted = true;
    ActivityCompat.requestPermissions(context, mPermissions, mRequestCode);
  }

  public void execute(@NonNull android.support.v4.app.Fragment context) {
    mExecuted = true;
    context.requestPermissions(mPermissions, mRequestCode);
  }

  public void execute(@NonNull android.app.Fragment context) {
    mExecuted = true;
    FragmentCompat.requestPermissions(context, mPermissions, mRequestCode);
  }

  public boolean isExecuted() {
    return mExecuted;
  }

  public int size() {
    return mCallbacks.size();
  }

  @Override
  public Iterator<AssentCallback> iterator() {
    return mCallbacks.iterator();
  }
}
