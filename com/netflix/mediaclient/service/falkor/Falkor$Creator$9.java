// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.falkor;

import com.netflix.falkor.ModelProxy;
import com.netflix.model.branches.PostPlayExperienceMap;
import com.netflix.falkor.Func;

final class Falkor$Creator$9 implements Func<PostPlayExperienceMap>
{
    final /* synthetic */ ModelProxy val$proxy;
    
    Falkor$Creator$9(final ModelProxy val$proxy) {
        this.val$proxy = val$proxy;
    }
    
    @Override
    public PostPlayExperienceMap call() {
        return new PostPlayExperienceMap(this.val$proxy);
    }
}
