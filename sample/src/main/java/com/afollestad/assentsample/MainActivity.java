package com.afollestad.assentsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentActivity;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AssentActivity {

    private TextView mStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatus = (TextView) findViewById(R.id.status);

        if (Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            mStatus.setText(R.string.permission_granted);
        } else {
            mStatus.setText(R.string.permission_is_not_granted);
        }

        findViewById(R.id.requestPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Assent.requestPermissions(new AssentCallback() {
                    @Override
                    public void onPermissionResult(PermissionResultSet result) {
                        if (result.allPermissionsGranted())
                            mStatus.setText(R.string.permission_granted);
                        else mStatus.setText(R.string.permission_is_not_granted);
                    }
                }, 69, Assent.WRITE_EXTERNAL_STORAGE);
            }
        });
    }
}