// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.pservice;

import com.netflix.mediaclient.util.UriUtil;
import com.netflix.mediaclient.servicemgr.interface_.CWVideo;
import com.netflix.mediaclient.servicemgr.interface_.Playable;
import com.netflix.model.branches.FalkorVideo;
import com.netflix.mediaclient.servicemgr.interface_.Billboard;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.google.gson.annotations.SerializedName;

public class PVideo
{
    @SerializedName("boxartUrl")
    public String boxartUrl;
    @SerializedName("horzDispUrl")
    public String horzDispUrl;
    @SerializedName("id")
    public String id;
    @SerializedName("isAgeProtected")
    public boolean isAgeProtected;
    @SerializedName("isAutoPlayEnabled")
    public boolean isAutoPlayEnabled;
    @SerializedName("isEpisode")
    public boolean isEpisode;
    @SerializedName("isNextPlayableEpisode")
    public boolean isNextPlayableEpisode;
    @SerializedName("isPinProtected")
    public boolean isPinProtected;
    @SerializedName("isPlayable")
    public boolean isPlayable;
    @SerializedName("isPlayableEpisode")
    public boolean isPlayableEpisode;
    @SerializedName("isPreviewProtected")
    public boolean isPreviewProtected;
    @SerializedName("playableEndtime")
    public int playableEndtime;
    @SerializedName("playableEpisodeNumber")
    public int playableEpisodeNumber;
    @SerializedName("playableId")
    public String playableId;
    @SerializedName("playableParentId")
    public String playableParentId;
    @SerializedName("playableParentTitle")
    public String playableParentTitle;
    @SerializedName("playableRuntime")
    public int playableRuntime;
    @SerializedName("playableSeasonNumAbbrLabel")
    public String playableSeasonNumAbbrLabel;
    @SerializedName("playableSeasonNumber")
    public int playableSeasonNumber;
    @SerializedName("playableTitle")
    public String playableTitle;
    @SerializedName("plyableBookmarkPos")
    public int plyableBookmarkPos;
    @SerializedName("synopsis")
    public String synopsis;
    @SerializedName("title")
    public String title;
    @SerializedName("trickplayUrl")
    public String trickplayUrl;
    @SerializedName("tvCardUrl")
    public String tvCardUrl;
    @SerializedName("videoType")
    public VideoType videoType;
    
    public PVideo(final Billboard billboard) {
        this((FalkorVideo)billboard);
        this.copyPlayableInfo(this, billboard);
    }
    
    public PVideo(final CWVideo cwVideo) {
        this((FalkorVideo)cwVideo);
        this.copyPlayableInfo(this, cwVideo);
        this.trickplayUrl = UriUtil.buildStillUrlFromPos(cwVideo, true);
    }
    
    public PVideo(final FalkorVideo falkorVideo) {
        this.id = falkorVideo.getId();
        this.boxartUrl = falkorVideo.getBoxshotUrl();
        this.videoType = falkorVideo.getType();
        this.title = falkorVideo.getTitle();
        this.horzDispUrl = falkorVideo.getHorzDispUrl();
        this.tvCardUrl = falkorVideo.getTitleImgUrl();
    }
    
    private PVideo copyPlayableInfo(final PVideo pVideo, final Playable playable) {
        pVideo.isPlayable = true;
        pVideo.isPlayableEpisode = playable.isPlayableEpisode();
        pVideo.isNextPlayableEpisode = playable.isNextPlayableEpisode();
        pVideo.isAutoPlayEnabled = playable.isAutoPlayEnabled();
        pVideo.isAgeProtected = playable.isAgeProtected();
        pVideo.isPinProtected = playable.isPinProtected();
        pVideo.isPreviewProtected = playable.isPreviewProtected();
        pVideo.plyableBookmarkPos = playable.getPlayableBookmarkPosition();
        pVideo.playableRuntime = playable.getRuntime();
        pVideo.playableEndtime = playable.getEndtime();
        pVideo.playableId = playable.getPlayableId();
        pVideo.playableTitle = playable.getPlayableTitle();
        pVideo.playableParentId = playable.getTopLevelId();
        pVideo.playableParentTitle = playable.getParentTitle();
        pVideo.playableEpisodeNumber = playable.getEpisodeNumber();
        pVideo.playableSeasonNumber = playable.getSeasonNumber();
        pVideo.playableSeasonNumAbbrLabel = playable.getSeasonAbbrSeqLabel();
        return pVideo;
    }
}
