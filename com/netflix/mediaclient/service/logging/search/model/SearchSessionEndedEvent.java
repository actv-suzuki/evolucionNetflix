// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging.search.model;

import com.netflix.mediaclient.servicemgr.IClientLogging$ModalView;
import com.netflix.mediaclient.service.logging.client.model.EventType;
import com.netflix.mediaclient.service.logging.client.model.DeviceUniqueId;
import com.netflix.mediaclient.service.logging.client.model.SessionEndedEvent;

public final class SearchSessionEndedEvent extends SessionEndedEvent
{
    private static final String APP_SESSION_NAME = "search";
    private static final String CATEGORY = "search";
    private static final String NAME = "session.ended";
    
    public SearchSessionEndedEvent(final long n) {
        super("search", new DeviceUniqueId(), n);
        this.setupAttributes();
    }
    
    private void setupAttributes() {
        this.type = EventType.sessionEnded;
        this.sessionName = "search";
        this.modalView = IClientLogging$ModalView.search;
        this.category = "search";
        this.name = "session.ended";
    }
}
