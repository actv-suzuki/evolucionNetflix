// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.servicemgr.interface_.details;

import com.netflix.mediaclient.servicemgr.interface_.Playable;
import com.netflix.model.leafs.advisory.Advisory;
import java.util.List;
import com.netflix.mediaclient.servicemgr.interface_.Video;
import com.netflix.mediaclient.servicemgr.interface_.Ratable;
import com.netflix.mediaclient.servicemgr.interface_.FeatureEnabledProvider;
import com.netflix.mediaclient.servicemgr.interface_.ArtworkUrlProvider;

public interface VideoDetails extends ArtworkUrlProvider, FeatureEnabledProvider, Ratable, Video
{
    String getActors();
    
    List<Advisory> getAdvisories();
    
    String getBifUrl();
    
    String getCatalogIdUrl();
    
    String getCertification();
    
    String getCopyright();
    
    String getDefaultTrailer();
    
    long getExpirationTime();
    
    String getGenres();
    
    String getHighResolutionLandscapeBoxArtUrl();
    
    String getHighResolutionPortraitBoxArtUrl();
    
    int getMaturityLevel();
    
    Playable getPlayable();
    
    String getQuality();
    
    String getStoryUrl();
    
    String getSupplementalMessage();
    
    String getSynopsis();
    
    String getTitleCroppedImgUrl();
    
    String getTitleImgUrl();
    
    String getTvCardUrl();
    
    int getYear();
    
    boolean hasTrailers();
    
    boolean hasWatched();
    
    boolean isAvailableToStream();
    
    boolean isInQueue();
    
    boolean isNSRE();
    
    boolean isOriginal();
    
    boolean isSupplementalVideo();
    
    boolean shouldRefreshVolatileData();
}
