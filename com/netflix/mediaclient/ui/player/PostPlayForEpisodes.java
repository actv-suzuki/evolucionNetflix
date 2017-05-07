// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.player;

import com.netflix.mediaclient.util.gfx.ImageLoader$StaticImgConfig;
import com.netflix.mediaclient.servicemgr.IClientLogging$AssetType;
import android.content.Context;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.ui.common.PlayContextImp;
import com.netflix.mediaclient.servicemgr.interface_.details.PostPlayContext;
import com.netflix.mediaclient.servicemgr.interface_.details.PostPlayVideo;
import com.netflix.mediaclient.util.ViewUtils;
import com.netflix.mediaclient.util.ViewUtils$Visibility;
import com.netflix.mediaclient.Log;
import android.widget.TextView;
import android.view.View;

public class PostPlayForEpisodes extends PostPlay
{
    private boolean mAutoPlayEnabled;
    protected View mAutoPlayView;
    protected TextView mInfoTitleView;
    private int mTimer;
    protected int mTimerValue;
    protected TextView mTimerView;
    private final Runnable onEverySecond;
    
    PostPlayForEpisodes(final PlayerActivity playerActivity) {
        super(playerActivity);
        this.mAutoPlayEnabled = true;
        this.onEverySecond = new PostPlayForEpisodes$1(this);
        this.init();
    }
    
    private void init() {
        this.mTimerValue = this.mContext.getResources().getInteger(2131361799);
        this.mAutoPlayEnabled = this.isAutoPlayEnabled();
        if (Log.isLoggable()) {
            Log.d("nf_postplay", "PostPlayForEpisodes:: timer max value " + this.mTimerValue);
        }
        if (!this.mAutoPlayEnabled && this.mTimerView != null) {
            ViewUtils.setVisibility(this.mAutoPlayView, ViewUtils$Visibility.INVISIBLE);
        }
        this.initInfoContainer();
        this.initButtons();
    }
    
    @Override
    public void destroy() {
        super.destroy();
        if (this.mAutoPlayEnabled) {
            this.mContext.getHandler().removeCallbacks(this.onEverySecond);
        }
    }
    
    @Override
    protected void doTransitionFromPostPlay() {
        final PlayerActivity mContext = this.mContext;
        if (this.mBackground.getVisibility() == 0) {
            mContext.performUpAction();
        }
    }
    
    @Override
    protected void doTransitionToPostPlay() {
        if (this.mAutoPlayEnabled) {
            Log.d("nf_postplay", "Auto play is enabled");
            this.mTimer = this.mTimerValue;
            this.mContext.getHandler().postDelayed(this.onEverySecond, 1000L);
            return;
        }
        Log.d("nf_postplay", "Auto play is disabled");
    }
    
    @Override
    public void endOfPlay() {
        super.endOfPlay();
        this.setBackgroundImageVisible(true);
    }
    
    protected void findViews() {
        this.mInfoTitleView = (TextView)this.mContext.findViewById(2131427734);
        this.mAutoPlayView = this.mContext.findViewById(2131427733);
        this.mTimerView = (TextView)this.mContext.findViewById(2131427735);
    }
    
    @Override
    protected void handlePlayNow(final boolean b) {
        if (this.mContext == null || this.mContext.destroyed()) {
            Log.d("nf_postplay", "Activity is alredy destroyed, ignore play now!");
        }
        else {
            Log.d("nf_postplay", "Play NEXT episode!");
            if (this.mPostPlayVideos.size() == 0) {
                Log.d("nf_postplay", "mPostPlayVideos size is zero");
                CharSequence text;
                if (this.mTitle.getText() != null) {
                    text = this.mTitle.getText();
                }
                else {
                    text = "null";
                }
                this.mContext.getServiceManager().getClientLogging().getErrorLogging().logHandledException(String.format("SPY-7987 - PostPlayVideos empty for title  %s", text));
                return;
            }
            if (this.mPostPlayContexts.size() == 0) {
                Log.d("nf_postplay", "mPostPlayContexts size is zero");
                CharSequence text2;
                if (this.mTitle.getText() != null) {
                    text2 = this.mTitle.getText();
                }
                else {
                    text2 = "null";
                }
                this.mContext.getServiceManager().getClientLogging().getErrorLogging().logHandledException(String.format("SPY-7987 - PostPlayContexts empty for title  %s", text2));
            }
            final PostPlayVideo postPlayVideo = this.mPostPlayVideos.get(0);
            PlayContext playContext = null;
            if (this.mPostPlayContexts != null) {
                playContext = playContext;
                if (this.mPostPlayContexts.size() > 1) {
                    playContext = new PlayContextImp(this.mPostPlayContexts.get(0).getRequestId(), this.mPostPlayContexts.get(0).getTrackId(), 0, 0);
                }
            }
            if (postPlayVideo != null) {
                this.mContext.playNextVideo(postPlayVideo.getPlayable(), playContext, b);
            }
        }
    }
    
    protected void initButtons() {
        if (this.mPlayButton != null) {
            this.mPlayButton.setVisibility(0);
        }
        if (this.mStopButton != null) {
            this.mStopButton.setVisibility(8);
        }
        if (this.mMoreButton != null) {
            this.mMoreButton.setVisibility(8);
        }
    }
    
    protected void initInfoContainer() {
        if (this.mInfoTitleView != null) {
            this.mInfoTitleView.setText(this.mContext.getResources().getText(2131493282));
        }
        if (this.mTimerView != null) {
            this.mTimerView.setVisibility(0);
        }
        if (this.mBackground != null) {
            this.mBackground.setVisibility(4);
        }
    }
    
    @Override
    public void onPause() {
        if (this.mAutoPlayEnabled) {
            this.mContext.getHandler().removeCallbacks(this.onEverySecond);
        }
    }
    
    @Override
    public void onResume() {
        if (this.mInPostPlay && this.mAutoPlayEnabled) {
            this.mContext.getHandler().removeCallbacks(this.onEverySecond);
            this.mContext.getHandler().post(this.onEverySecond);
        }
    }
    
    protected void onTimerEnd() {
        this.mPlayButton.setEnabled(false);
        this.handlePlayNow(true);
    }
    
    @Override
    public void postPlayDismissed() {
        super.postPlayDismissed();
        if (this.mAutoPlayEnabled) {
            this.mContext.getHandler().removeCallbacks(this.onEverySecond);
        }
    }
    
    @Override
    protected void updateOnPostPlayVideosFetched() {
        Log.d("nf_postplay", "updateOnPostPlayVideosFetched start");
        if (this.mPostPlayVideos == null || this.mPostPlayVideos.size() < 1) {
            Log.e("nf_postplay", "We do not have any data! Do nothing!");
            return;
        }
        final PostPlayVideo postPlayVideo = this.mPostPlayVideos.get(0);
        if (postPlayVideo == null) {
            Log.e("nf_postplay", "We do not have any data! Do nothing!");
            return;
        }
        this.updateViews(postPlayVideo);
    }
    
    protected void updateViews(final PostPlayVideo postPlayVideo) {
        String title = postPlayVideo.getTitle();
        if (title == null) {
            title = "";
        }
        final String storyUrl = postPlayVideo.getStoryUrl();
        String s = postPlayVideo.getInterestingUrl();
        if (postPlayVideo.getType() != VideoType.EPISODE) {
            s = postPlayVideo.getStoryUrl();
        }
        final String string = this.mContext.getResources().getString(2131493285, new Object[] { title });
        if (this.mBackground != null) {
            if (!StringUtils.isEmpty(storyUrl) && this.mContext.isTablet()) {
                NetflixActivity.getImageLoader((Context)this.mContext).showImg(this.mBackground, storyUrl, IClientLogging$AssetType.merchStill, string, ImageLoader$StaticImgConfig.DARK, true, 1);
            }
            else if (!StringUtils.isEmpty(s) && !this.mContext.isTablet()) {
                NetflixActivity.getImageLoader((Context)this.mContext).showImg(this.mBackground, s, IClientLogging$AssetType.merchStill, string, ImageLoader$StaticImgConfig.DARK, true, 1);
            }
        }
        final String string2 = this.mContext.getResources().getString(2131493240, new Object[] { postPlayVideo.getPlayable().getSeasonNumber(), postPlayVideo.getPlayable().getEpisodeNumber(), title });
        if (Log.isLoggable()) {
            Log.d("nf_postplay", "Title: " + string2);
        }
        if (this.mTitle != null) {
            this.mTitle.setText((CharSequence)string2);
        }
        final String synopsis = postPlayVideo.getSynopsis();
        if (this.mSynopsis != null && this.mSynopsis.getVisibility() == 0 && StringUtils.isNotEmpty(synopsis)) {
            this.mSynopsis.setText((CharSequence)synopsis);
        }
        if (Log.isLoggable()) {
            Log.d("nf_postplay", "Synopsis: " + postPlayVideo.getSynopsis());
        }
    }
}
