// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.barker.details;

import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.servicemgr.interface_.details.SeasonDetails;
import com.netflix.mediaclient.android.app.CommonStatus;
import com.netflix.mediaclient.servicemgr.interface_.Video;
import java.util.Collection;
import android.support.v7.widget.GridLayoutManager$SpanSizeLookup;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView$ItemDecoration;
import android.annotation.SuppressLint;
import com.netflix.mediaclient.ui.experience.BrowseExperience;
import com.netflix.mediaclient.ui.kids.KidsUtils;
import android.view.View$OnClickListener;
import android.support.v7.widget.RecyclerView$OnScrollListener;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView$Adapter;
import android.support.v7.widget.RecyclerView$LayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.FrameLayout$LayoutParams;
import com.netflix.mediaclient.android.widget.NetflixActionBar;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.servicemgr.interface_.details.Similarable;
import com.netflix.mediaclient.ui.details.DetailsActivity;
import android.widget.AdapterView$OnItemSelectedListener;
import com.netflix.mediaclient.util.DeviceUtils;
import android.graphics.drawable.Drawable;
import com.netflix.mediaclient.util.api.Api16Util;
import android.os.Parcelable;
import com.netflix.mediaclient.android.fragment.NetflixDialogFrag;
import com.netflix.mediaclient.servicemgr.interface_.details.VideoDetails;
import com.netflix.mediaclient.servicemgr.interface_.details.ShowDetails;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import com.netflix.mediaclient.android.app.Status;
import android.view.ViewGroup;
import com.netflix.mediaclient.ui.details.VideoDetailsViewGroup;
import com.netflix.mediaclient.android.widget.LoadingAndErrorWrapper;
import com.netflix.mediaclient.android.widget.RecyclerViewHeaderAdapter;
import android.support.v7.widget.RecyclerView;
import java.util.Stack;
import com.netflix.mediaclient.ui.details.DetailsPageParallaxScrollListener;
import com.netflix.mediaclient.util.ItemDecorationBarkerGrid;
import com.netflix.mediaclient.servicemgr.interface_.details.EpisodeDetails;
import java.util.List;
import com.netflix.mediaclient.ui.details.CopyrightView;
import com.netflix.mediaclient.ui.mdx.CastPlayerHelper$CastPlayerDialog;
import com.netflix.mediaclient.ui.details.ServiceManagerProvider;
import com.netflix.mediaclient.ui.details.IHandleBackPress;
import com.netflix.mediaclient.servicemgr.ManagerStatusListener;
import com.netflix.mediaclient.android.widget.ErrorWrapper$Callback;
import com.netflix.mediaclient.ui.details.EpisodesFrag;
import android.view.ViewGroup$LayoutParams;
import android.widget.AbsListView$LayoutParams;
import android.widget.ImageView$ScaleType;
import com.netflix.mediaclient.android.widget.VideoDetailsClickListener;
import com.netflix.mediaclient.ui.common.PlayContextProvider;
import com.netflix.mediaclient.android.widget.VideoView;
import android.view.View;
import android.content.Context;
import com.netflix.mediaclient.ui.barker.BarkerUtils;
import com.netflix.mediaclient.android.widget.RecyclerViewHeaderAdapter$IViewCreator;

class BarkerShowDetailsFrag$7 implements RecyclerViewHeaderAdapter$IViewCreator
{
    final /* synthetic */ BarkerShowDetailsFrag this$0;
    
    BarkerShowDetailsFrag$7(final BarkerShowDetailsFrag this$0) {
        this.this$0 = this$0;
    }
    
    private int getImageHeight() {
        return (int)(this.getImageWidth() * 1.43f);
    }
    
    private int getImageWidth() {
        return (BarkerUtils.getDetailsPageContentWidth((Context)this.this$0.getActivity()) - this.this$0.innerGridDecoration.getAllSpaceWidth()) / this.this$0.getNumColumns();
    }
    
    @Override
    public View createItemView() {
        final VideoView videoView = new VideoView(this.this$0.recyclerView.getContext());
        videoView.setAdjustViewBounds(true);
        videoView.setClickListener(new BarkerShowDetailsFrag$BarkerRelatedVideoDetailsClickListener(this.this$0, this.this$0.getNetflixActivity(), (PlayContextProvider)videoView));
        videoView.setScaleType(ImageView$ScaleType.FIT_XY);
        videoView.setLayoutParams((ViewGroup$LayoutParams)new AbsListView$LayoutParams(-1, this.getImageHeight()));
        videoView.setIsHorizontal(false);
        return (View)videoView;
    }
}
