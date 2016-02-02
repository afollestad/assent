package com.afollestad.assent;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Aidan Follestad (afollestad)
 */
class AssentBase {

    @NonNull
    protected static String getCacheKey(@NonNull String... permissions) {
        StringBuilder result = new StringBuilder();
        for (String perm : permissions) {
            result.append(perm);
            result.append("\0");
        }
        return result.toString();
    }

    @NonNull
    protected static String join(int[] array) {
        if (array == null || array.length == 0) return "(empty)";
        // Estimate how long the string will be for the initial capacity, doubles in capacity when limit is reached
        StringBuilder sb = new StringBuilder((2 * array.length) + 4 + (2 * (array.length - 1)));
        sb.append("[ ");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }

    @NonNull
    protected static String join(String[] array) {
        if (array == null || array.length == 0) return "(empty)";
        // Estimate how long the string will be for the initial capacity, doubles in capacity when limit is reached
        StringBuilder sb = new StringBuilder((array[0].length() * array.length) + 4 + (2 * (array.length - 1)));
        sb.append("[ ");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }

    protected static void LOG(@NonNull String message, @Nullable Object... args) {
        if (args != null)
            Log.d("Assent", String.format(message, args));
        else
            Log.d("Assent", message);
    }

    /**
     * See http://developer.android.com/guide/topics/security/permissions.html#normal-dangerous for
     * a list of 'dangerous' permissions that require a permission request on API 23.
     */

    public static final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;

    public static final String CAMERA = Manifest.permission.CAMERA;

    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;

    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    public static final String WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG;
    public static final String ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL;
    public static final String USE_SIP = Manifest.permission.USE_SIP;
    public static final String PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS;

    public static final String BODY_SENSORS = Manifest.permission.BODY_SENSORS;

    public static final String SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String READ_SMS = Manifest.permission.READ_SMS;
    public static final String RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH;
    public static final String RECEIVE_MMS = Manifest.permission.RECEIVE_MMS;

    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
}
