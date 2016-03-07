package com.afollestad.assent;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AssentFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Assent.setFragment(this, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Assent.setFragment(this, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().isFinishing())
            Assent.setFragment(this, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AssentBase.LOG("AssentFragment", "onRequestPermissionsResult(): %d, %s, %s",
                requestCode, AssentBase.join(permissions), AssentBase.join(grantResults));
        Assent.handleResult(permissions, grantResults);
    }
}