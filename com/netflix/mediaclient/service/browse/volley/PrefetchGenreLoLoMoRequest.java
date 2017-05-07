// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.browse.volley;

import java.util.List;
import com.netflix.mediaclient.servicemgr.LoMoType;
import com.netflix.mediaclient.service.webclient.model.leafs.ListOfMoviesSummary;
import java.util.ArrayList;
import com.netflix.mediaclient.service.webclient.volley.FalcorParseUtils;
import com.netflix.mediaclient.service.webclient.volley.FalcorServerException;
import java.util.concurrent.TimeUnit;
import com.netflix.mediaclient.service.browse.BrowseAgent;
import com.netflix.mediaclient.service.webclient.volley.FalcorParseException;
import com.google.gson.JsonObject;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.service.ServiceAgent;
import android.content.Context;
import com.netflix.mediaclient.service.browse.cache.SoftCache;
import com.netflix.mediaclient.service.browse.BrowseAgentCallback;
import com.netflix.mediaclient.service.browse.cache.HardCache;
import com.netflix.mediaclient.service.webclient.volley.FalcorVolleyWebClientRequest;

public class PrefetchGenreLoLoMoRequest extends FalcorVolleyWebClientRequest<String>
{
    private static final String FIELD_PATH = "path";
    private static final String FIELD_TOPGENRE = "topGenre";
    private static final String TAG = "nf_service_browse_prefetchgenrelolomorequest";
    private final HardCache hardCache;
    private final int mFromLoMo;
    private final int mFromVideo;
    private final String mGenreId;
    private final int mToLoMo;
    private final int mToVideo;
    private final String pqlQuery;
    private final String pqlQuery2;
    private long qDurationInMs;
    private long qEnd;
    private final long qStart;
    private long rDurationInMs;
    private long rEnd;
    private final long rStart;
    private final BrowseAgentCallback responseCallback;
    private final SoftCache softCache;
    
    public PrefetchGenreLoLoMoRequest(final Context context, final ServiceAgent.ConfigurationAgentInterface configurationAgentInterface, final HardCache hardCache, final SoftCache softCache, final String mGenreId, final int mFromLoMo, final int mToLoMo, final int mFromVideo, final int mToVideo, final BrowseAgentCallback responseCallback) {
        super(context, configurationAgentInterface);
        this.responseCallback = responseCallback;
        this.mGenreId = mGenreId;
        this.mFromLoMo = mFromLoMo;
        this.mToLoMo = mToLoMo;
        this.mFromVideo = mFromVideo;
        this.mToVideo = mToVideo;
        this.hardCache = hardCache;
        this.softCache = softCache;
        this.pqlQuery = "['topGenre', '" + mGenreId + "',{'from':" + mFromLoMo + ",'to':" + mToLoMo + "},'summary']";
        this.pqlQuery2 = "['topGenre','" + mGenreId + "',{'from':" + mFromLoMo + ",'to':" + mToLoMo + "},{'from':" + mFromVideo + ",'to':" + mToVideo + "},'summary']";
        if (Log.isLoggable("nf_service_browse_prefetchgenrelolomorequest", 2)) {
            Log.v("nf_service_browse_prefetchgenrelolomorequest", "PQL = " + this.pqlQuery);
            Log.v("nf_service_browse_prefetchgenrelolomorequest", "PQL2 = " + this.pqlQuery2);
        }
        this.rStart = System.nanoTime();
        this.qStart = System.nanoTime();
    }
    
    public static void putGenreLoLoMoIdInBrowseCache(final HardCache hardCache, final String s, final JsonObject jsonObject) throws FalcorParseException {
        try {
            putGenreLoLoMoIdInBrowseCache(hardCache, s, jsonObject.getAsJsonArray("path").get(1).getAsString());
        }
        catch (Exception ex) {
            Log.v("nf_service_browse_prefetchgenrelolomorequest", "Genre LoLoMoId path missing in: " + jsonObject.toString());
            throw new FalcorParseException("Missing Genre lolomoId", ex);
        }
    }
    
    private static void putGenreLoLoMoIdInBrowseCache(final HardCache hardCache, final String s, final String s2) {
        final String buildBrowseGenreLoLoMoCacheKey = BrowseAgent.buildBrowseGenreLoLoMoCacheKey(s);
        Log.d("nf_service_browse_prefetchgenrelolomorequest", String.format("greneId %s grenreLoLoMoId %s", s, s2));
        BrowseAgent.putInBrowseCache(hardCache, buildBrowseGenreLoLoMoCacheKey, s2);
    }
    
    private void putGenreLoMoInBrowseCache(String buildBrowseCacheKey, final Object o) {
        buildBrowseCacheKey = BrowseAgent.buildBrowseCacheKey(BrowseAgent.CACHE_KEY_PREFIX_GENRE_VIDEOS, buildBrowseCacheKey, String.valueOf(this.mFromVideo), String.valueOf(this.mToVideo));
        BrowseAgent.putInBrowseCache(this.softCache, buildBrowseCacheKey, o);
    }
    
    private void putGenreLoMoSummaryInBrowseCache(final Object o) {
        BrowseAgent.putInBrowseCache(this.hardCache, BrowseAgent.buildBrowseCacheKey(BrowseAgent.CACHE_KEY_PREFIX_GENRE_LOMO, this.mGenreId, String.valueOf(this.mFromLoMo), String.valueOf(this.mToLoMo)), o);
    }
    
    @Override
    protected String[] getPQLQueries() {
        final String pqlQuery = this.pqlQuery;
        final String pqlQuery2 = this.pqlQuery2;
        this.qEnd = System.nanoTime();
        this.qDurationInMs = TimeUnit.MILLISECONDS.convert(this.qEnd - this.qStart, TimeUnit.NANOSECONDS);
        Log.d("nf_service_browse_prefetchgenrelolomorequest", String.format("prefetchGenre PQL took %d ms ", this.qDurationInMs));
        return new String[] { pqlQuery, pqlQuery2 };
    }
    
    @Override
    protected void onFailure(final int n) {
        if (this.responseCallback != null) {
            Log.d("nf_service_browse_prefetchgenrelolomorequest", "prefetchGenreLoLoMo finished onFailure statusCode=" + n);
            this.responseCallback.onGenreLoLoMoPrefetched(n);
        }
    }
    
    @Override
    protected void onSuccess(final String s) {
        if (this.responseCallback != null) {
            Log.d("nf_service_browse_prefetchgenrelolomorequest", "prefetchGenreLoLoMo finished onSuccess");
            this.responseCallback.onGenreLoLoMoPrefetched(0);
        }
    }
    
    @Override
    protected String parseFalcorResponse(String s) throws FalcorParseException, FalcorServerException {
        this.rEnd = System.nanoTime();
        this.rDurationInMs = TimeUnit.MILLISECONDS.convert(this.rEnd - this.rStart, TimeUnit.NANOSECONDS);
        Log.d("nf_service_browse_prefetchgenrelolomorequest", String.format("prefetchGenre request took %d ms ", this.rDurationInMs));
        if (Log.isLoggable("nf_service_browse_prefetchgenrelolomorequest", 2)) {}
        final long nanoTime = System.nanoTime();
        final JsonObject dataObj = FalcorParseUtils.getDataObj("nf_service_browse_prefetchgenrelolomorequest", s);
        if (FalcorParseUtils.isEmpty(dataObj)) {
            throw new FalcorParseException("GenreLoLoMoPrefetch empty!!!");
        }
        JsonObject asJsonObject;
        while (true) {
            while (true) {
                Label_0295: {
                    try {
                        asJsonObject = dataObj.getAsJsonObject("topGenre").getAsJsonObject(this.mGenreId);
                        s = (String)new ArrayList();
                        for (int i = this.mFromLoMo; i <= this.mToLoMo; ++i) {
                            final String string = Integer.toString(i);
                            if (asJsonObject.has(string)) {
                                final JsonObject asJsonObject2 = asJsonObject.getAsJsonObject(string);
                                final ListOfMoviesSummary listOfMoviesSummary = FalcorParseUtils.getPropertyObject(asJsonObject2, "summary", ListOfMoviesSummary.class);
                                if (listOfMoviesSummary != null) {
                                    listOfMoviesSummary.setListPos(i);
                                }
                                ((List<ListOfMoviesSummary>)s).add(listOfMoviesSummary);
                                final LoMoType type = listOfMoviesSummary.getType();
                                final int mFromVideo = this.mFromVideo;
                                final int mToVideo = this.mToVideo;
                                if (listOfMoviesSummary.isBillboard()) {
                                    break Label_0295;
                                }
                                final boolean b = true;
                                this.putGenreLoMoInBrowseCache(listOfMoviesSummary.getId(), FetchVideosRequest.buildVideoList(type, asJsonObject2, mFromVideo, mToVideo, b));
                            }
                        }
                        break;
                    }
                    catch (Exception ex) {
                        Log.v("nf_service_browse_prefetchgenrelolomorequest", "String response to parse = " + s);
                        throw new FalcorParseException("response missing expected json objects", ex);
                    }
                }
                final boolean b = false;
                continue;
            }
        }
        if (((List)s).size() > 0) {
            this.putGenreLoMoSummaryInBrowseCache(s);
            putGenreLoLoMoIdInBrowseCache(this.hardCache, this.mGenreId, asJsonObject);
        }
        final long nanoTime2 = System.nanoTime();
        this.rEnd = nanoTime2;
        this.rDurationInMs = TimeUnit.MILLISECONDS.convert(this.rEnd - this.rStart, TimeUnit.NANOSECONDS);
        Log.d("nf_service_browse_prefetchgenrelolomorequest", String.format("prefetch pasing took took %d ms ", TimeUnit.MILLISECONDS.convert(nanoTime2 - nanoTime, TimeUnit.NANOSECONDS)));
        Log.d("nf_service_browse_prefetchgenrelolomorequest", String.format("prefetch success - took %d ms ", this.rDurationInMs));
        return Integer.toString(0);
    }
}