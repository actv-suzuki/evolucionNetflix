// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.falkor;

import com.netflix.falkor.ModelProxy;
import com.netflix.model.branches.FalkorSeason;
import com.netflix.falkor.Func;

final class Falkor$Creator$15 implements Func<FalkorSeason>
{
    final /* synthetic */ ModelProxy val$proxy;
    
    Falkor$Creator$15(final ModelProxy val$proxy) {
        this.val$proxy = val$proxy;
    }
    
    @Override
    public FalkorSeason call() {
        return new FalkorSeason(this.val$proxy);
    }
}
