// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging.social.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.netflix.mediaclient.servicemgr.IClientLogging;

public final class RecommendPanelScrolledEvent extends BaseSocialDiscreteEvent
{
    protected static final String NAME = "recommendPanelScrolled";
    private String mOriginatingRequestGuid;
    private int mTrackId;
    private IClientLogging.ModalView mView;
    
    public RecommendPanelScrolledEvent(final IClientLogging.ModalView mView, final String mOriginatingRequestGuid, final int mTrackId) {
        super("recommendPanelScrolled");
        this.mView = mView;
        this.mOriginatingRequestGuid = mOriginatingRequestGuid;
        this.mTrackId = mTrackId;
    }
    
    @Override
    protected JSONObject getData() throws JSONException {
        JSONObject data;
        if ((data = super.getData()) == null) {
            data = new JSONObject();
        }
        data.put("trackId", this.mTrackId);
        if (this.mView != null) {
            data.put("view", (Object)this.mView.name());
        }
        if (this.mOriginatingRequestGuid != null) {
            data.put("originatingRequestGuid", (Object)this.mOriginatingRequestGuid);
        }
        return data;
    }
}
