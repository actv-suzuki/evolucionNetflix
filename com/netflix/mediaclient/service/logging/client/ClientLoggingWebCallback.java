// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging.client;

public interface ClientLoggingWebCallback
{
    void onEventsDelivered(final String p0);
    
    void onEventsDeliveryFailed(final String p0);
}