// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.android.fragment;

import android.view.ViewGroup;
import android.app.Activity;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class LoadingView extends FrameLayout
{
    public LoadingView(final Context context) {
        super(context);
        this.init();
    }
    
    public LoadingView(final Context context, final AttributeSet set) {
        super(context, set);
        this.init();
    }
    
    public LoadingView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.init();
    }
    
    private void init() {
        this.setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        ((Activity)this.getContext()).getLayoutInflater().inflate(2130903098, (ViewGroup)this);
    }
}
