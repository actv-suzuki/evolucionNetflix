// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging.presentation.volley;

import com.android.volley.VolleyError;
import com.netflix.mediaclient.Log;
import java.util.Map;
import com.netflix.mediaclient.service.logging.presentation.PresentationRequest;
import com.netflix.mediaclient.service.logging.presentation.PresentationWebCallback;

public class PresentationEventRequest extends PresentationVolleyWebClientRequest<String>
{
    private static final String TAG = "nf_presentation";
    private final PresentationWebCallback mCallback;
    private final String mDeliveryRequestId;
    private final PresentationRequest mRequestObj;
    
    public PresentationEventRequest(final String mDeliveryRequestId, final PresentationRequest mRequestObj, final PresentationWebCallback mCallback) {
        this.mCallback = mCallback;
        this.mDeliveryRequestId = mDeliveryRequestId;
        this.mRequestObj = mRequestObj;
    }
    
    public Map<String, String> getParams() {
        final Map<String, String> requestParams = this.mRequestObj.toRequestParams();
        if (Log.isLoggable("nf_presentation", 2)) {
            Log.d("nf_presentation", "params size " + requestParams.toString().length());
        }
        return requestParams;
    }
    
    @Override
    protected void onFailure(final int n) {
        if (Log.isLoggable("nf_presentation", 2)) {
            Log.v("nf_presentation", "presentationEvent FAIL : " + n);
        }
        this.mCallback.onEventsDeliveryFailed(this.mDeliveryRequestId);
    }
    
    @Override
    protected void onSuccess(final String s) {
        Log.v("nf_presentation", "presentationEvent OK : ");
        this.mCallback.onEventsDelivered(this.mDeliveryRequestId);
    }
    
    @Override
    protected String parseResponse(final String s) throws VolleyError {
        return "OK";
    }
}