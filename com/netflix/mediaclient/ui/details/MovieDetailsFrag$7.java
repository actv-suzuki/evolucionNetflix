// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.details;

import android.content.Context;
import com.netflix.mediaclient.ui.barker.details.BarkerHelper;
import com.netflix.mediaclient.Log;

class MovieDetailsFrag$7 implements Runnable
{
    final /* synthetic */ MovieDetailsFrag this$0;
    
    MovieDetailsFrag$7(final MovieDetailsFrag this$0) {
        this.this$0 = this$0;
    }
    
    @Override
    public void run() {
        Log.v("MovieDetailsFrag", "Resetting parallax views");
        this.this$0.parallaxScroller.resetDynamicViewsYPosition();
        if (BarkerHelper.isInTest((Context)this.this$0.getActivity())) {
            this.this$0.recyclerView.post((Runnable)new MovieDetailsFrag$7$1(this));
        }
    }
}
