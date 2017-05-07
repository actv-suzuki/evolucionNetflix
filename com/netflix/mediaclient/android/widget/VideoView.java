// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.android.widget;

import com.netflix.mediaclient.util.gfx.ImageLoader;
import com.netflix.mediaclient.ui.common.PlayContextImp;
import com.netflix.mediaclient.servicemgr.Trackable;
import android.view.View;
import com.netflix.mediaclient.servicemgr.IClientLogging;
import com.netflix.mediaclient.ui.common.PlayContextProvider;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.util.AttributeSet;
import android.content.Context;
import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.servicemgr.Video;
import com.netflix.mediaclient.ui.lomo.VideoViewGroup;

public class VideoView extends AdvancedImageView implements IVideoView<Video>
{
    public static final float LOMO_BOXART_HEIGHT_TO_WIDTH_RATIO = 1.43f;
    protected VideoDetailsClickListener clicker;
    protected PlayContext playContext;
    
    public VideoView(final Context context) {
        super(context);
        this.init();
    }
    
    public VideoView(final Context context, final AttributeSet set) {
        super(context, set);
        this.init();
    }
    
    public VideoView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.init();
    }
    
    private void init() {
        this.playContext = PlayContext.EMPTY_CONTEXT;
        this.setFocusable(true);
        this.clicker = new VideoDetailsClickListener((NetflixActivity)this.getContext(), this);
    }
    
    @Override
    public PlayContext getPlayContext() {
        return this.playContext;
    }
    
    @Override
    public void hide() {
        NetflixActivity.getImageLoader(this.getContext()).showImg(this, null, null, null, false, false);
        this.setVisibility(4);
        this.clicker.remove((View)this);
    }
    
    public void update(final Video video, final Trackable trackable, int visibility, final boolean b) {
        this.playContext = new PlayContextImp(trackable, visibility);
        if (video.getBoxshotURL() == null) {
            visibility = 4;
        }
        else {
            visibility = 0;
        }
        this.setVisibility(visibility);
        this.clicker.update((View)this, video);
        final ImageLoader imageLoader = NetflixActivity.getImageLoader(this.getContext());
        final String boxshotURL = video.getBoxshotURL();
        final IClientLogging.AssetType boxArt = IClientLogging.AssetType.boxArt;
        final String title = video.getTitle();
        if (b) {
            visibility = 1;
        }
        else {
            visibility = 0;
        }
        imageLoader.showImg(this, boxshotURL, boxArt, title, true, true, visibility);
    }
}
