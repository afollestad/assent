package com.afollestad.assentsample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentActivity;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AssentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Assent.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {
                    // Permission granted or denied
                }
            }, 69, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}