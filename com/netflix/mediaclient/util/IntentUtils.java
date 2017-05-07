// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.util;

import android.support.v4.content.LocalBroadcastManager;
import com.netflix.mediaclient.Log;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

public final class IntentUtils
{
    private static final String TAG;
    public static final int USER_HIGH_PRIORITY = 999;
    public static final int USER_LOW_PRIORITY = -999;
    
    static {
        TAG = IntentUtils.class.getSimpleName();
    }
    
    private static int getSafePriority(final int n) {
        int n2 = n;
        if (n < -1000) {
            n2 = -999;
        }
        else if (n > 1000) {
            return 999;
        }
        return n2;
    }
    
    public static boolean registerSafelyBroadcastReceiver(final Context context, final BroadcastReceiver broadcastReceiver, String s, final int n, final String... array) {
        if (context == null) {
            throw new IllegalArgumentException("Context is null");
        }
        if (broadcastReceiver == null) {
            throw new IllegalArgumentException("Receiver is null");
        }
        if (array == null || array.length < 1) {
            throw new IllegalArgumentException("No actions!");
        }
        if (StringUtils.isEmpty(s)) {
            throw new IllegalArgumentException("Category can not be null!");
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(s);
        for (int length = array.length, i = 0; i < length; ++i) {
            s = array[i];
            if (StringUtils.isNotEmpty(s)) {
                intentFilter.addAction(s);
            }
        }
        intentFilter.setPriority(getSafePriority(n));
        try {
            context.registerReceiver(broadcastReceiver, intentFilter);
            return true;
        }
        catch (Throwable t) {
            Log.e(IntentUtils.TAG, "Failed to register ", t);
            return false;
        }
    }
    
    public static boolean registerSafelyBroadcastReceiver(final Context context, final BroadcastReceiver broadcastReceiver, final String s, final String... array) {
        return registerSafelyBroadcastReceiver(context, broadcastReceiver, s, 999, array);
    }
    
    public static boolean registerSafelyLocalBroadcastReceiver(final Context context, final BroadcastReceiver broadcastReceiver, String s, final int n, final String... array) {
        if (context == null) {
            throw new IllegalArgumentException("Context is null");
        }
        if (broadcastReceiver == null) {
            throw new IllegalArgumentException("Receiver is null");
        }
        if (array == null || array.length < 1) {
            throw new IllegalArgumentException("No actions!");
        }
        if (StringUtils.isEmpty(s)) {
            throw new IllegalArgumentException("Category can not be null!");
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(s);
        for (int length = array.length, i = 0; i < length; ++i) {
            s = array[i];
            if (StringUtils.isNotEmpty(s)) {
                intentFilter.addAction(s);
            }
        }
        intentFilter.setPriority(getSafePriority(n));
        try {
            LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
            return true;
        }
        catch (Throwable t) {
            Log.e(IntentUtils.TAG, "Failed to register ", t);
            return false;
        }
    }
    
    public static boolean registerSafelyLocalBroadcastReceiver(final Context context, final BroadcastReceiver broadcastReceiver, final String s, final String... array) {
        return registerSafelyLocalBroadcastReceiver(context, broadcastReceiver, s, 999, array);
    }
    
    public static boolean unregisterSafelyBroadcastReceiver(final Context context, final BroadcastReceiver broadcastReceiver) {
        if (context == null) {
            Log.e(IntentUtils.TAG, "Context is null");
            return false;
        }
        if (broadcastReceiver == null) {
            Log.e(IntentUtils.TAG, "Receiver is null");
            return false;
        }
        try {
            context.unregisterReceiver(broadcastReceiver);
            return true;
        }
        catch (Throwable t) {
            Log.e(IntentUtils.TAG, "Failed to unregister ", t);
            return false;
        }
    }
    
    public static boolean unregisterSafelyLocalBroadcastReceiver(final Context context, final BroadcastReceiver broadcastReceiver) {
        if (context == null) {
            Log.e(IntentUtils.TAG, "Context is null");
            return false;
        }
        if (broadcastReceiver == null) {
            Log.e(IntentUtils.TAG, "Receiver is null");
            return false;
        }
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
            return true;
        }
        catch (Throwable t) {
            Log.e(IntentUtils.TAG, "Failed to unregister ", t);
            return false;
        }
    }
}
