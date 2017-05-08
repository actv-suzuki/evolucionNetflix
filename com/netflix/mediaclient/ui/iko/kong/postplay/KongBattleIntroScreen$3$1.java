// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.iko.kong.postplay;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ObjectAnimator;
import android.text.Html;
import com.netflix.mediaclient.util.ViewUtils;
import android.graphics.Paint;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.util.ThreadUtils;
import com.netflix.mediaclient.ui.iko.kong.model.KongInteractivePostPlayModel$KongSound;
import com.netflix.mediaclient.ui.iko.kong.model.KongInteractivePostPlayModel;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.widget.ImageView$ScaleType;
import com.netflix.mediaclient.android.widget.PressedStateHandler$DelayedOnClickListener;
import android.app.Activity;
import android.content.Context;
import com.netflix.mediaclient.util.DeviceUtils;
import android.view.animation.LinearInterpolator;
import android.graphics.BitmapFactory$Options;
import com.netflix.mediaclient.android.widget.AdvancedImageView;
import com.netflix.mediaclient.util.OnAnimationEndListener;
import android.view.animation.Interpolator;
import android.view.View$OnClickListener;
import android.animation.Animator$AnimatorListener;
import android.view.ViewGroup;
import com.netflix.mediaclient.android.widget.PressAnimationFrameLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;
import com.netflix.mediaclient.Log;

class KongBattleIntroScreen$3$1 implements Runnable
{
    final /* synthetic */ KongBattleIntroScreen$3 this$1;
    
    KongBattleIntroScreen$3$1(final KongBattleIntroScreen$3 this$1) {
        this.this$1 = this$1;
    }
    
    @Override
    public void run() {
        if (this.this$1.this$0.postPlayManager.isPostPlayPaused()) {
            Log.d("KongBattleIntroScreen", "Post play is in paused state. Ignoring request to start countdown timer.");
            return;
        }
        this.this$1.this$0.finishTimeCounterSeconds = this.this$1.this$0.autoPlayInterval;
        this.this$1.this$0.startTimer();
    }
}