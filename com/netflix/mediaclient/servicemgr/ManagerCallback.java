// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.servicemgr;

import com.netflix.mediaclient.servicemgr.model.Video;
import com.netflix.mediaclient.service.webclient.model.leafs.social.SocialNotificationsList;
import com.netflix.mediaclient.service.webclient.model.leafs.social.SocialNotificationSummary;
import com.netflix.mediaclient.servicemgr.model.SearchVideoList;
import com.netflix.mediaclient.servicemgr.model.details.ShowDetails;
import com.netflix.mediaclient.servicemgr.model.details.SeasonDetails;
import com.netflix.mediaclient.servicemgr.model.search.ISearchResults;
import com.netflix.mediaclient.servicemgr.model.details.PostPlayVideo;
import com.netflix.mediaclient.servicemgr.model.details.MovieDetails;
import com.netflix.mediaclient.servicemgr.model.LoMo;
import com.netflix.mediaclient.servicemgr.model.LoLoMo;
import com.netflix.mediaclient.servicemgr.model.details.KidsCharacterDetails;
import com.netflix.mediaclient.servicemgr.model.genre.Genre;
import com.netflix.mediaclient.servicemgr.model.genre.GenreList;
import com.netflix.mediaclient.service.webclient.model.leafs.social.FriendForRecommendation;
import com.netflix.mediaclient.servicemgr.model.details.EpisodeDetails;
import com.netflix.mediaclient.servicemgr.model.CWVideo;
import com.netflix.mediaclient.servicemgr.model.Billboard;
import com.netflix.mediaclient.android.app.Status;
import com.netflix.mediaclient.service.webclient.model.leafs.AvatarInfo;
import java.util.List;

public interface ManagerCallback
{
    void onAvailableAvatarsListFetched(final List<AvatarInfo> p0, final Status p1);
    
    void onBBVideosFetched(final List<Billboard> p0, final Status p1);
    
    void onCWVideosFetched(final List<CWVideo> p0, final Status p1);
    
    void onConnectWithFacebookComplete(final Status p0);
    
    void onEpisodeDetailsFetched(final EpisodeDetails p0, final Status p1);
    
    void onEpisodesFetched(final List<EpisodeDetails> p0, final Status p1);
    
    void onFriendsForRecommendationsListFetched(final List<FriendForRecommendation> p0, final Status p1);
    
    void onGenreListsFetched(final List<GenreList> p0, final Status p1);
    
    void onGenreLoLoMoPrefetched(final Status p0);
    
    void onGenresFetched(final List<Genre> p0, final Status p1);
    
    void onKidsCharacterDetailsFetched(final KidsCharacterDetails p0, final Boolean p1, final Status p2);
    
    void onLoLoMoPrefetched(final Status p0);
    
    void onLoLoMoSummaryFetched(final LoLoMo p0, final Status p1);
    
    void onLoMosFetched(final List<LoMo> p0, final Status p1);
    
    void onLoginComplete(final Status p0);
    
    void onLogoutComplete(final Status p0);
    
    void onMovieDetailsFetched(final MovieDetails p0, final Status p1);
    
    void onPinVerified(final boolean p0, final Status p1);
    
    void onPostPlayVideosFetched(final List<PostPlayVideo> p0, final Status p1);
    
    void onProfileListUpdateStatus(final Status p0);
    
    void onQueueAdd(final Status p0);
    
    void onQueueRemove(final Status p0);
    
    void onResourceFetched(final String p0, final String p1, final Status p2);
    
    void onSearchResultsFetched(final ISearchResults p0, final Status p1);
    
    void onSeasonDetailsFetched(final SeasonDetails p0, final Status p1);
    
    void onSeasonsFetched(final List<SeasonDetails> p0, final Status p1);
    
    void onShowDetailsFetched(final ShowDetails p0, final Status p1);
    
    void onSimilarVideosFetched(final SearchVideoList p0, final Status p1);
    
    void onSocialNotificationWasThanked(final SocialNotificationSummary p0, final Status p1);
    
    void onSocialNotificationsListFetched(final SocialNotificationsList p0, final Status p1);
    
    void onVideoHide(final Status p0);
    
    void onVideoRatingSet(final Status p0);
    
    void onVideosFetched(final List<Video> p0, final Status p1);
}
