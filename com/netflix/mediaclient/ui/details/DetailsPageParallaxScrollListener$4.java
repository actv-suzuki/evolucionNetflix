// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.details;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable$Orientation;
import java.util.Date;
import android.widget.FrameLayout$LayoutParams;
import com.netflix.mediaclient.android.widget.RecyclerViewHeaderAdapter;
import com.netflix.mediaclient.android.widget.NetflixActionBar;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup$LayoutParams;
import android.support.v7.widget.Toolbar$LayoutParams;
import com.netflix.mediaclient.util.DeviceUtils;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.netflix.mediaclient.util.AndroidUtils;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.View;
import android.support.v7.widget.RecyclerView$OnScrollListener;
import com.netflix.mediaclient.Log;

class DetailsPageParallaxScrollListener$4 implements Runnable
{
    final /* synthetic */ DetailsPageParallaxScrollListener this$0;
    final /* synthetic */ int val$seasonNumber;
    
    DetailsPageParallaxScrollListener$4(final DetailsPageParallaxScrollListener this$0, final int val$seasonNumber) {
        this.this$0 = this$0;
        this.val$seasonNumber = val$seasonNumber;
    }
    
    @Override
    public void run() {
        final int tryGetSeasonIndexBySeasonNumber = this.this$0.seasonsSpinner.tryGetSeasonIndexBySeasonNumber(this.val$seasonNumber);
        if (tryGetSeasonIndexBySeasonNumber < 0) {
            Log.v("DetailsPageParallaxScrollListener", "No valid season index found");
            return;
        }
        if (Log.isLoggable()) {
            Log.v("DetailsPageParallaxScrollListener", "Setting current season to: " + tryGetSeasonIndexBySeasonNumber);
        }
        this.this$0.seasonsSpinner.setSelection(tryGetSeasonIndexBySeasonNumber, true);
    }
}
