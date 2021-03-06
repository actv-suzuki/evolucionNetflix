// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.common.internal;

import android.net.Uri$Builder;
import android.text.TextUtils;
import android.content.Intent;
import android.net.Uri;

public class zzn
{
    private static final Uri zzagi;
    private static final Uri zzagj;
    
    static {
        zzagi = Uri.parse("http://plus.google.com/");
        zzagj = zzn.zzagi.buildUpon().appendPath("circles").appendPath("find").build();
    }
    
    public static Intent zzco(final String s) {
        final Uri fromParts = Uri.fromParts("package", s, (String)null);
        final Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(fromParts);
        return intent;
    }
    
    public static Intent zzpo() {
        final Intent intent = new Intent("com.google.android.clockwork.home.UPDATE_ANDROID_WEAR_ACTION");
        intent.setPackage("com.google.android.wearable.app");
        return intent;
    }
    
    private static Uri zzv(final String s, final String s2) {
        final Uri$Builder appendQueryParameter = Uri.parse("market://details").buildUpon().appendQueryParameter("id", s);
        if (!TextUtils.isEmpty((CharSequence)s2)) {
            appendQueryParameter.appendQueryParameter("pcampaignid", s2);
        }
        return appendQueryParameter.build();
    }
    
    public static Intent zzw(final String s, final String s2) {
        final Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(zzv(s, s2));
        intent.setPackage("com.android.vending");
        intent.addFlags(524288);
        return intent;
    }
}
