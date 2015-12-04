package com.afollestad.assent;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
public class PermissionResultSet {

    private final PermissionResult[] mResults;

    private PermissionResultSet(PermissionResult[] results) {
        mResults = results;
    }

    protected static PermissionResultSet create(@NonNull String[] permissions, int[] grantResults) {
        PermissionResult[] results = new PermissionResult[permissions.length];
        for (int i = 0; i < permissions.length; i++)
            results[i] = new PermissionResult(permissions[i], grantResults[i]);
        return new PermissionResultSet(results);
    }

    public Map<String, Boolean> getGrantedMap() {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String perm : getPermissions())
            map.put(perm, isGranted(perm));
        return map;
    }

    public String[] getPermissions() {
        String[] perms = new String[mResults.length];
        for (int i = 0; i < perms.length; i++)
            perms[i] = mResults[i].getPermission();
        return perms;
    }

    public boolean isGranted(@NonNull String permission) {
        synchronized (mResults) {
            for (PermissionResult result : mResults) {
                if (result.getPermission().equals(permission))
                    return result.isGranted();
            }
            return false;
        }
    }

    public boolean allPermissionsGranted() {
        synchronized (mResults) {
            boolean allGranted = true;
            for (PermissionResult result : mResults) {
                if (!result.isGranted()) {
                    allGranted = false;
                    break;
                }
            }
            return allGranted;
        }
    }
}