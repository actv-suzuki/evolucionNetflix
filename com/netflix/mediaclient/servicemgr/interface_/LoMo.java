// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.servicemgr.interface_;

import java.util.List;
import android.os.Parcelable;

public interface LoMo extends Parcelable, BasicLoMo
{
    List<String> getMoreImages();
    
    int getNumVideos();
    
    boolean isBillboard();
    
    void setId(final String p0);
    
    void setListPos(final int p0);
}
