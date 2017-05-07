// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging.ads;

import java.io.IOException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import android.content.Context;

public class GooglePlayAdvertisingIdProvider implements AdvertisingIdProvider
{
    private String mAdvertisingId;
    private boolean mIsLAT;
    
    GooglePlayAdvertisingIdProvider(final Context context) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException, IOException {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }
        final AdvertisingIdClient.Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        this.mAdvertisingId = advertisingIdInfo.getId();
        this.mIsLAT = advertisingIdInfo.isLimitAdTrackingEnabled();
    }
    
    @Override
    public String getId() {
        return this.mAdvertisingId;
    }
    
    @Override
    public boolean isLimitAdTrackingEnabled() {
        return this.mIsLAT;
    }
    
    @Override
    public boolean isResettable() {
        return true;
    }
}
