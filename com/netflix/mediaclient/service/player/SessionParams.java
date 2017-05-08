// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.player;

import org.json.JSONException;
import com.netflix.mediaclient.Log;
import org.json.JSONObject;
import com.netflix.mediaclient.ui.bandwidthsetting.BandwidthUtility;
import android.content.Context;
import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.util.ConnectivityUtils$NetType;

public final class SessionParams
{
    private static final String PARAM_ENABLE_PREVIEW_CONTENT = "supportsPreviewContent";
    private static final String PARAM_IS_BROWSE_PLAY = "isBrowsePlay";
    private static final String PARAM_NET_TYPE = "nettype";
    private static final String PARAM_NET_TYPE_VALUE_MOBILE = "mobile";
    private static final String PARAM_NET_TYPE_VALUE_WIFI = "wifi";
    private static final String PARAM_NET_TYPE_VALUE_WIRED = "wired";
    private static final String PARAM_PIN_VERIFY_CAPABILITY = "pinCapableClient";
    public static final String PARAM_PLAY_CONTEXT = "uiplaycontext";
    private static final String PARAM_PLAY_CONTEXT_LIST_POS = "row";
    private static final String PARAM_PLAY_CONTEXT_REQ_ID = "request_id";
    private static final String PARAM_PLAY_CONTEXT_VIDEO_POS = "rank";
    private static final String PARAM_PLAY_MOBILE_ASN_FILTERING = "filterBasedOnMobileASN";
    protected static final String TAG = "nf_invoke";
    private boolean mDontFilterForMobileAsn;
    private ConnectivityUtils$NetType mNetType;
    private PlayContext mPlayContext;
    
    public SessionParams(final Context context, final PlayContext mPlayContext, final ConnectivityUtils$NetType mNetType) {
        if (mPlayContext == null) {
            throw new IllegalArgumentException("Play context can not be null!");
        }
        this.mPlayContext = mPlayContext;
        this.mNetType = mNetType;
        if (!BandwidthUtility.isDataSaverDisabled(context)) {
            this.mDontFilterForMobileAsn = true;
        }
    }
    
    public JSONObject getParams() {
        JSONObject jsonObject;
        while (true) {
            try {
                JSONObject jsonObject2;
                while (true) {
                    jsonObject = new JSONObject();
                    while (true) {
                        Label_0270: {
                            try {
                                if (ConnectivityUtils$NetType.mobile.equals(this.mNetType)) {
                                    jsonObject.put("nettype", (Object)"mobile");
                                }
                                else {
                                    if (!ConnectivityUtils$NetType.wifi.equals(this.mNetType)) {
                                        break Label_0270;
                                    }
                                    jsonObject.put("nettype", (Object)"wifi");
                                }
                                jsonObject.put("pinCapableClient", true);
                                if (this.mPlayContext != null) {
                                    jsonObject2 = new JSONObject();
                                    jsonObject2.put("request_id", (Object)this.mPlayContext.getRequestId());
                                    jsonObject2.put("row", this.mPlayContext.getListPos());
                                    jsonObject2.put("rank", this.mPlayContext.getVideoPos());
                                    jsonObject.put("uiplaycontext", (Object)jsonObject2);
                                    jsonObject.put("isBrowsePlay", this.mPlayContext.getBrowsePlay());
                                    if (Log.isLoggable()) {
                                        Log.d("nf_invoke", String.format("DEBUG info: reqId %s, listPos %d,  videoPos %d", this.mPlayContext.getRequestId(), this.mPlayContext.getListPos(), this.mPlayContext.getVideoPos()));
                                    }
                                }
                                if (this.mDontFilterForMobileAsn) {
                                    jsonObject.put("filterBasedOnMobileASN", false);
                                }
                                if (Log.isLoggable()) {
                                    Log.d("nf_invoke", String.format("DEBUG info: sessionParams: %s", jsonObject.toString()));
                                    return jsonObject;
                                }
                                return jsonObject;
                            }
                            catch (JSONException ex) {}
                            break;
                        }
                        if (ConnectivityUtils$NetType.wired.equals(this.mNetType)) {
                            jsonObject.put("nettype", (Object)"wired");
                            continue;
                        }
                        jsonObject.put("nettype", (Object)"mobile");
                        continue;
                    }
                }
                Log.e("nf_invoke", "Failed to create JSON object", (Throwable)jsonObject2);
                return jsonObject;
            }
            catch (JSONException jsonObject2) {
                jsonObject = null;
                continue;
            }
            break;
        }
        return jsonObject;
    }
}
