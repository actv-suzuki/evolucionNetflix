// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.auth.api;

import com.google.android.gms.common.api.Api$zzb;
import com.google.android.gms.common.api.GoogleApiClient$OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient$ConnectionCallbacks;
import com.google.android.gms.common.internal.zzf;
import android.os.Looper;
import android.content.Context;
import com.google.android.gms.common.api.Api$ApiOptions$NoOptions;
import com.google.android.gms.internal.zzkb;
import com.google.android.gms.common.api.Api$zza;

final class Auth$3 extends Api$zza<zzkb, Api$ApiOptions$NoOptions>
{
    public zzkb zzc(final Context context, final Looper looper, final zzf zzf, final Api$ApiOptions$NoOptions api$ApiOptions$NoOptions, final GoogleApiClient$ConnectionCallbacks googleApiClient$ConnectionCallbacks, final GoogleApiClient$OnConnectionFailedListener googleApiClient$OnConnectionFailedListener) {
        return new zzkb(context, looper, zzf, googleApiClient$ConnectionCallbacks, googleApiClient$OnConnectionFailedListener);
    }
}
