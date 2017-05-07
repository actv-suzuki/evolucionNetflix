// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.protocol.nflx;

import android.app.Activity;
import com.netflix.mediaclient.util.NflxProtocolUtils;
import com.netflix.mediaclient.servicemgr.model.genre.GenreList;
import com.netflix.mediaclient.ui.home.HomeActivity;
import com.netflix.mediaclient.service.webclient.model.leafs.ListOfGenreSummary;
import com.netflix.mediaclient.android.app.Status;
import com.netflix.mediaclient.servicemgr.model.LoLoMo;
import com.netflix.mediaclient.servicemgr.SimpleManagerCallback;
import com.netflix.mediaclient.servicemgr.ManagerCallback;
import com.netflix.mediaclient.Log;
import java.util.Map;
import com.netflix.mediaclient.android.activity.NetflixActivity;

class GenreActionHandler extends BaseNflxHandlerWithoutDelayedActionSupport
{
    public GenreActionHandler(final NetflixActivity netflixActivity, final Map<String, String> map) {
        super(netflixActivity, map);
    }
    
    @Override
    public Response handle() {
        final String s = this.mParamsMap.get("genreid");
        if (s == null) {
            Log.v("NflxHandler", "Could not find genre ID");
            return Response.NOT_HANDLING;
        }
        this.mActivity.getServiceManager().getBrowse().fetchLoLoMoSummary(s, new FetchLoLoMoSummaryCallback(this.mActivity, s));
        return Response.HANDLING_WITH_DELAY;
    }
    
    class FetchLoLoMoSummaryCallback extends SimpleManagerCallback
    {
        private final NetflixActivity activity;
        private final String genreId;
        
        FetchLoLoMoSummaryCallback(final NetflixActivity activity, final String genreId) {
            this.genreId = genreId;
            this.activity = activity;
        }
        
        @Override
        public void onLoLoMoSummaryFetched(final LoLoMo loLoMo, final Status status) {
            if (status.isSucces()) {
                HomeActivity.showGenreList(this.activity, new ListOfGenreSummary(loLoMo.getNumLoMos(), -1, -1, "", loLoMo.getTitle(), this.genreId, false, loLoMo.getType().toString()));
            }
            NflxProtocolUtils.reportDelayedResponseHandled(this.activity);
        }
    }
}
