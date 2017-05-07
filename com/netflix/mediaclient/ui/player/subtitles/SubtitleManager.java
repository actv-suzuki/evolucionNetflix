// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.player.subtitles;

import com.netflix.mediaclient.service.player.subtitles.SubtitleScreen;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import com.netflix.mediaclient.javabridge.ui.IMedia$SubtitleProfile;

public interface SubtitleManager
{
    boolean canHandleSubtitleProfile(final IMedia$SubtitleProfile p0);
    
    void clear();
    
    void clearPendingUpdates();
    
    NetflixActivity getContext();
    
    IMedia$SubtitleProfile getSubtitleProfile();
    
    void onPlayerOverlayVisibiltyChange(final boolean p0);
    
    void onSubtitleChange(final SubtitleScreen p0);
    
    void onSubtitleRemove();
    
    void setSubtitleVisibility(final boolean p0);
}