// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.servicemgr.interface_.offline.realm;

import com.netflix.mediaclient.Log;
import java.io.File;
import android.content.Context;
import com.netflix.mediaclient.servicemgr.interface_.Playable;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.netflix.model.leafs.advisory.Advisory;
import com.netflix.mediaclient.servicemgr.interface_.details.EpisodeDetails;
import io.realm.RealmVideoDetailsRealmProxyInterface;
import io.realm.RealmModel;
import java.util.Iterator;
import com.netflix.mediaclient.servicemgr.interface_.offline.OfflineImageUtils;
import io.realm.RealmList;
import com.netflix.mediaclient.servicemgr.interface_.details.SeasonDetails;
import io.realm.Realm;
import com.netflix.mediaclient.service.NetflixService;
import java.util.List;
import com.netflix.mediaclient.servicemgr.interface_.details.VideoDetails;
import io.realm.Realm$Transaction;

final class RealmVideoDetails$1 implements Realm$Transaction
{
    final /* synthetic */ VideoDetails val$details;
    final /* synthetic */ String val$profileId;
    final /* synthetic */ List val$seasons;
    final /* synthetic */ NetflixService val$service;
    
    RealmVideoDetails$1(final VideoDetails val$details, final String val$profileId, final List val$seasons, final NetflixService val$service) {
        this.val$details = val$details;
        this.val$profileId = val$profileId;
        this.val$seasons = val$seasons;
        this.val$service = val$service;
    }
    
    @Override
    public void execute(final Realm realm) {
        final RealmVideoDetails realmVideoDetails = realm.createObject(RealmVideoDetails.class, this.val$details.getId());
        realmVideoDetails.fillForRealm(this.val$details);
        RealmVideoDetails.access$002(realmVideoDetails, this.val$profileId);
        if (this.val$seasons != null) {
            for (final SeasonDetails seasonDetails : this.val$seasons) {
                if (realmVideoDetails.realmGet$seasonLabels() == null) {
                    RealmVideoDetails.access$102(realmVideoDetails, new RealmList());
                }
                final RealmSeason realmSeason = new RealmSeason();
                realmSeason.setNumber(seasonDetails.getSeasonNumber());
                realmSeason.setLabel(seasonDetails.getSeasonLongSeqLabel());
                realmVideoDetails.realmGet$seasonLabels().add(realmSeason);
            }
        }
        RealmPlayable realmPlayable;
        if ((realmPlayable = realm.where(RealmPlayable.class).equalTo("playableId", this.val$details.getPlayable().getPlayableId()).findFirst()) == null) {
            realmPlayable = realm.copyToRealm(new RealmPlayable(this.val$details.getPlayable()));
        }
        realmVideoDetails.setPlayable(realmPlayable);
        OfflineImageUtils.cacheVideoDetailsImage(this.val$service, realmVideoDetails.getHorzDispUrl(), realmVideoDetails.getId());
    }
}
