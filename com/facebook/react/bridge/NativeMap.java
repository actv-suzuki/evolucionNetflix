// 
// Decompiled by Procyon v0.5.30
// 

package com.facebook.react.bridge;

import com.facebook.jni.HybridData;
import com.facebook.proguard.annotations.DoNotStrip;

@DoNotStrip
public abstract class NativeMap
{
    @DoNotStrip
    private HybridData mHybridData;
    
    static {
        ReactBridge.staticInit();
    }
    
    public NativeMap(final HybridData mHybridData) {
        this.mHybridData = mHybridData;
    }
    
    @Override
    public native String toString();
}
