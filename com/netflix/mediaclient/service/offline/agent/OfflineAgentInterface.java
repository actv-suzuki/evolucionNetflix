// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.offline.agent;

import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.netflix.mediaclient.servicemgr.interface_.offline.OfflinePlayableUiList;
import com.netflix.mediaclient.servicemgr.interface_.offline.DownloadVideoQuality;

public interface OfflineAgentInterface
{
    public static final String CATEGORY_NF_OFFLINE = "com.netflix.mediaclient.intent.category.offline";
    
    void addOfflineAgentListener(final OfflineAgentListener p0);
    
    void deleteAllOfflineContent();
    
    void deleteOfflinePlayable(final String p0);
    
    DownloadVideoQuality getCurrentDownloadVideoQuality();
    
    OfflinePlayableUiList getLatestOfflinePlayableList();
    
    boolean getRequiresUnmeteredNetwork();
    
    boolean isOfflineFeatureEnabled();
    
    void pauseDownload(final String p0);
    
    void refreshUIData();
    
    void removeOfflineAgentListener(final OfflineAgentListener p0);
    
    void requestGeoPlayabilityUpdate();
    
    void requestOfflineViewing(final String p0, final VideoType p1, final PlayContext p2);
    
    void requestRefreshLicenseForPlayable(final String p0);
    
    void requestRenewPlayWindowForPlayable(final String p0);
    
    void resumeDownload(final String p0);
    
    void setDownloadVideoQuality(final DownloadVideoQuality p0);
    
    void setRequiresUnmeteredNetwork(final boolean p0);
    
    boolean setSkipAdultContent(final boolean p0);
}