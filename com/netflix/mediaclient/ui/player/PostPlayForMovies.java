// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.player;

import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.ui.common.PlayContextImp;
import com.netflix.mediaclient.servicemgr.interface_.details.PostPlayContext;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.netflix.mediaclient.servicemgr.interface_.details.VideoDetails;
import com.netflix.mediaclient.servicemgr.interface_.Ratable;
import com.netflix.mediaclient.ui.details.NetflixRatingBar$RatingBarDataProvider;
import com.netflix.mediaclient.util.ViewUtils$Visibility;
import com.netflix.mediaclient.util.gfx.ImageLoader$StaticImgConfig;
import com.netflix.mediaclient.servicemgr.IClientLogging$AssetType;
import android.content.Context;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.util.ViewUtils;
import android.view.View$OnClickListener;
import android.annotation.SuppressLint;
import android.view.View$OnTouchListener;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.servicemgr.interface_.details.PostPlayVideo;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import android.widget.TextView;
import com.netflix.mediaclient.android.widget.AdvancedImageView;
import com.netflix.mediaclient.ui.details.NetflixRatingBar;
import java.util.List;
import com.netflix.mediaclient.util.gfx.ImageLoader$ImageLoaderListener;
import android.view.View;

public final class PostPlayForMovies extends PostPlay
{
    private final int LIST_SIZE;
    private View mBackgroundContainer;
    private final ImageLoader$ImageLoaderListener mImageLoaderListener;
    private View mMetadata;
    private final List<View> mPlayButtons;
    private NetflixRatingBar mRatingBar;
    private final List<AdvancedImageView> mRecommendationBoxArts;
    private int mSelected;
    private TextView mVideoDetails;
    private final AtomicBoolean mVideoFullScreen;
    private VideoWindowForPostplay mVideoWindow;
    
    public PostPlayForMovies(final PlayerActivity playerActivity) {
        super(playerActivity);
        this.LIST_SIZE = 3;
        this.mRecommendationBoxArts = new ArrayList<AdvancedImageView>(3);
        this.mPlayButtons = new ArrayList<View>(3);
        this.mSelected = -1;
        this.mVideoFullScreen = new AtomicBoolean(true);
        this.mImageLoaderListener = new PostPlayForMovies$2(this);
        this.init();
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    private void addBoxArt(final int n, final int n2) {
        final AdvancedImageView advancedImageView = (AdvancedImageView)this.mContext.findViewById(n);
        this.mRecommendationBoxArts.add(advancedImageView);
        if (advancedImageView == null) {
            Log.e("nf_postplay", "Image not found for index " + n2);
            return;
        }
        advancedImageView.setBackgroundResource(2130837574);
        advancedImageView.setOnTouchListener((View$OnTouchListener)new PostPlayForMovies$ChangeRecommendation(this, n2, null));
    }
    
    private void addPlayButton(final int n, final int n2) {
        final View viewById = this.mContext.findViewById(n);
        this.mPlayButtons.add(viewById);
        if (viewById == null) {
            Log.e("nf_postplay", "Play button not found for index " + n2);
            return;
        }
        viewById.setOnClickListener((View$OnClickListener)new PostPlayForMovies$1(this, n2));
    }
    
    private void executeTransitionIn() {
        ViewUtils.setVisibleOrGone(this.mMetadata, true);
        ViewUtils.setVisibleOrGone(this.mPlayButton, true);
        if (this.mVideoWindow.canVideoVindowResize()) {
            this.setBackgroundImageVisible(true);
        }
        this.mVideoWindow.animateIn();
    }
    
    private void executeTransitionOut() {
        this.mVideoWindow.animateOut(null);
        this.setBackgroundImageVisible(false);
        this.mVideoFullScreen.set(false);
    }
    
    private void init() {
        this.mVideoWindow = VideoWindowForPostplayFactory.createVideoWindow(this.mContext);
        this.addBoxArt(2131427747, 0);
        this.addBoxArt(2131427749, 1);
        this.addBoxArt(2131427751, 2);
        this.addPlayButton(2131427748, 0);
        this.addPlayButton(2131427750, 1);
        this.addPlayButton(2131427752, 2);
    }
    
    private void updateUi(final PostPlayVideo postPlayVideo, final int n) {
        if (postPlayVideo != null) {
            String title = postPlayVideo.getTitle();
            if (title == null) {
                title = "";
            }
            final String storyUrl = postPlayVideo.getStoryUrl();
            final String format = String.format(this.mContext.getResources().getString(2131493286), title);
            if (!StringUtils.isEmpty(storyUrl)) {
                NetflixActivity.getImageLoader((Context)this.mContext).showImg(this.mBackground, storyUrl, IClientLogging$AssetType.merchStill, format, ImageLoader$StaticImgConfig.DARK, true, 1);
            }
            for (int i = 0; i < 3; ++i) {
                final View view = this.mPlayButtons.get(i);
                if (i == n) {
                    ViewUtils.setVisibility(view, ViewUtils$Visibility.VISIBLE);
                }
                else {
                    ViewUtils.setVisibility(view, ViewUtils$Visibility.GONE);
                }
            }
            if (Log.isLoggable()) {
                Log.d("nf_postplay", "Title: " + title);
            }
            if (this.mTitle != null) {
                this.mTitle.setText((CharSequence)title);
            }
            if (this.mSynopsis != null) {
                String narrative;
                if (StringUtils.isEmpty(postPlayVideo.getNarrative())) {
                    narrative = "";
                }
                else {
                    narrative = postPlayVideo.getNarrative();
                }
                this.mSynopsis.setText((CharSequence)narrative);
            }
            if (Log.isLoggable()) {
                Log.d("nf_postplay", "Synopsis: " + postPlayVideo.getSynopsis());
            }
            if (this.mRatingBar != null) {
                this.mRatingBar.update(null, postPlayVideo);
            }
            if (this.mVideoDetails != null) {
                this.mVideoDetails.setText(StringUtils.getBasicMovieInfoString((Context)this.mContext, postPlayVideo));
            }
        }
    }
    
    @Override
    protected void doTransitionToPostPlay() {
        if (this.mPostPlayDismissed) {
            Log.d("nf_postplay", "Second time postplay");
            this.executeTransitionIn();
            this.mVideoWindow.setVisible(false);
            return;
        }
        Log.d("nf_postplay", "First time postplay");
    }
    
    @Override
    public void endOfPlay() {
        super.endOfPlay();
        this.mVideoWindow.setVisible(false);
        if (this.mSelected < 0) {
            this.mSelected = 0;
        }
        this.updateUi(this.mPostPlayVideos.get(this.mSelected), this.mSelected);
        this.setBackgroundImageVisible(true);
        ViewUtils.setVisibleOrGone(this.mMetadata, true);
        ViewUtils.setVisibleOrGone(this.mPlayButton, true);
    }
    
    @Override
    public void fetchPostPlayVideosIfNeeded(final String s, final VideoType videoType) {
        if (this.mContext.isTablet()) {
            Log.d("nf_postplay", "Fetch data for tablet only");
            super.fetchPostPlayVideosIfNeeded(s, videoType);
            return;
        }
        Log.d("nf_postplay", "Fetch data for tablet only, skip for phone");
    }
    
    @Override
    void findViews() {
        this.mRatingBar = (NetflixRatingBar)this.mContext.findViewById(2131427542);
        this.mVideoDetails = (TextView)this.mContext.findViewById(2131427746);
        this.mBackgroundContainer = this.mContext.findViewById(2131427753);
        this.mMetadata = this.mContext.findViewById(2131427745);
    }
    
    @Override
    protected void handlePlayNow(final boolean b) {
        Log.d("nf_postplay", "Play recommendation");
        if (this.mSelected < 0) {
            Log.e("nf_postplay", "Error state, movie was not selected");
        }
        else {
            final PostPlayVideo postPlayVideo = this.mPostPlayVideos.get(this.mSelected);
            final PostPlayContext postPlayContext = this.mPostPlayContexts.get(this.mSelected);
            if (postPlayVideo != null) {
                this.mContext.playNextVideo(postPlayVideo.getPlayable(), new PlayContextImp(postPlayContext.getRequestId(), postPlayContext.getTrackId(), 0, this.mSelected), b);
            }
        }
    }
    
    @Override
    protected boolean isPostPlayEnabled() {
        return super.isPostPlayEnabled() && this.mContext.isTablet();
    }
    
    @Override
    public void postPlayDismissed() {
        super.postPlayDismissed();
        this.executeTransitionOut();
    }
    
    @Override
    public void setBackgroundImageVisible(final boolean b) {
        if (this.mBackgroundContainer != null) {
            if (!b) {
                this.mBackgroundContainer.setVisibility(4);
                return;
            }
            this.mBackgroundContainer.setVisibility(0);
        }
    }
    
    @Override
    protected void updateOnPostPlayVideosFetched() {
        if (this.mPostPlayVideos == null || this.mPostPlayVideos.size() < 1) {
            Log.e("nf_postplay", "We do not have any data! Do nothing!");
        }
        else {
            for (int i = 0; i < this.mPostPlayVideos.size(); ++i) {
                final PostPlayVideo postPlayVideo = this.mPostPlayVideos.get(i);
                if (postPlayVideo != null) {
                    final AdvancedImageView advancedImageView = this.mRecommendationBoxArts.get(i);
                    if (advancedImageView != null) {
                        advancedImageView.setVisibility(0);
                        String title = postPlayVideo.getTitle();
                        if (title == null) {
                            title = "";
                        }
                        if (postPlayVideo.getStoryUrl() != null) {
                            NetflixActivity.getImageLoader((Context)this.mContext).getImg(postPlayVideo.getStoryUrl(), IClientLogging$AssetType.merchStill, 1920, 1080, this.mImageLoaderListener);
                        }
                        final String format = String.format(this.mContext.getResources().getString(2131493286), title);
                        if (postPlayVideo.getHorzDispUrl() != null) {
                            NetflixActivity.getImageLoader((Context)this.mContext).showImg(this.mRecommendationBoxArts.get(i), postPlayVideo.getHorzDispUrl(), IClientLogging$AssetType.merchStill, format, ImageLoader$StaticImgConfig.DARK, true, 1);
                        }
                    }
                }
                else {
                    this.mRecommendationBoxArts.get(i).setVisibility(8);
                }
            }
        }
    }
}
