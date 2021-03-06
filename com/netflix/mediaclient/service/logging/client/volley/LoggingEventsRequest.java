// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging.client.volley;

import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.android.app.Status;
import com.netflix.mediaclient.service.logging.client.ClientLoggingWebCallback;

public class LoggingEventsRequest extends ClientLoggingVolleyWebClientRequest<String>
{
    private static final String TAG = "nf_log";
    private ClientLoggingWebCallback mCallback;
    private String mDeliveryRequestId;
    private String mLoggingRequest;
    
    public LoggingEventsRequest(final String mDeliveryRequestId, final String mLoggingRequest, final ClientLoggingWebCallback mCallback) {
        this.mCallback = mCallback;
        this.mDeliveryRequestId = mDeliveryRequestId;
        this.mLoggingRequest = mLoggingRequest;
    }
    
    @Override
    protected String getPayload() {
        return this.mLoggingRequest;
    }
    
    @Override
    protected void onFailure(final Status status) {
        if (this.mCallback != null) {
            this.mCallback.onEventsDeliveryFailed(this.mDeliveryRequestId);
        }
    }
    
    @Override
    protected void onSuccess(final String s) {
        if (this.mCallback != null) {
            this.mCallback.onEventsDelivered(this.mDeliveryRequestId);
        }
    }
    
    @Override
    protected String parseResponse(final String s) {
        if (Log.isLoggable()) {
            Log.v("nf_log", "String response to parse = " + s);
        }
        return "OK";
    }
}
