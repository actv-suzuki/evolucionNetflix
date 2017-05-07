// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.lomo;

import com.netflix.mediaclient.servicemgr.ServiceManager;
import com.netflix.mediaclient.servicemgr.BillboardInteractionType;
import android.view.View$OnClickListener;
import com.netflix.mediaclient.servicemgr.interface_.Playable;
import com.netflix.mediaclient.servicemgr.ServiceManagerUtils;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.netflix.mediaclient.servicemgr.interface_.Video;
import android.text.Html;
import com.netflix.mediaclient.util.gfx.ImageLoader$StaticImgConfig;
import com.netflix.mediaclient.servicemgr.IClientLogging$AssetType;
import com.netflix.mediaclient.util.api.Api17Util;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.util.SocialUtils;
import com.netflix.mediaclient.ui.common.PlayContextImp;
import com.netflix.mediaclient.servicemgr.interface_.trackable.Trackable;
import com.netflix.mediaclient.service.logging.client.model.Error;
import com.netflix.mediaclient.util.log.SocialLoggingUtils;
import com.netflix.mediaclient.servicemgr.IClientLogging$ModalView;
import com.netflix.mediaclient.util.log.ConsolidatedLoggingUtils;
import android.widget.RelativeLayout$LayoutParams;
import com.netflix.mediaclient.util.DeviceUtils;
import android.view.ViewGroup;
import com.netflix.mediaclient.ui.common.PlayContextProvider;
import android.util.AttributeSet;
import com.netflix.mediaclient.util.log.UIViewLogUtils;
import android.content.Context;
import com.netflix.mediaclient.ui.common.PlayContext;
import android.widget.TextView;
import com.netflix.mediaclient.android.widget.AdvancedImageView;
import com.netflix.mediaclient.android.widget.VideoDetailsClickListener;
import com.netflix.mediaclient.servicemgr.interface_.Billboard;
import android.widget.RelativeLayout;
import android.view.View;
import com.netflix.mediaclient.util.ViewUtils;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;

class BillboardView$1 implements ViewTreeObserver$OnGlobalLayoutListener
{
    final /* synthetic */ BillboardView this$0;
    
    BillboardView$1(final BillboardView this$0) {
        this.this$0 = this$0;
    }
    
    public void onGlobalLayout() {
        if (!((NetflixActivity)this.this$0.getContext()).destroyed()) {
            if (Log.isLoggable()) {
                Log.v("BillboardView", "vg height: " + this.this$0.infoViewGroup.getHeight() + ", h: " + this.this$0.getHeight());
            }
            if (this.this$0.getHeight() > 0 && this.this$0.infoViewGroup.getHeight() >= this.this$0.getHeight()) {
                Log.d("BillboardView", "Info view group is larger than view height - hiding some text");
                this.this$0.label.setVisibility(8);
                this.this$0.info.setVisibility(8);
            }
            if (this.this$0.getHeight() > 0) {
                ViewUtils.removeGlobalLayoutListener((View)this.this$0, (ViewTreeObserver$OnGlobalLayoutListener)this);
            }
        }
    }
}
