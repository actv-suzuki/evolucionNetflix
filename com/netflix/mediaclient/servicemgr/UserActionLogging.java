// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.servicemgr;

import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.service.logging.client.model.DataContext;
import android.content.Intent;
import org.json.JSONObject;
import com.netflix.mediaclient.service.logging.client.model.UIError;

public interface UserActionLogging
{
    public static final String[] ACTIONS = { "com.netflix.mediaclient.intent.action.LOG_UIA_SIGNUP_START", "com.netflix.mediaclient.intent.action.LOG_UIA_SIGNUP_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_ADD_TO_PLAYLIST_START", "com.netflix.mediaclient.intent.action.LOG_UIA_ADD_TO_PLAYLIST_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_LOGIN_START", "com.netflix.mediaclient.intent.action.LOG_UIA_LOGIN_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_REMOVE_FROM_PLAYLIST_START", "com.netflix.mediaclient.intent.action.LOG_UIA_REMOVE_FROM_PLAYLIST_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_NAVIGATION_START", "com.netflix.mediaclient.intent.action.LOG_UIA_NAVIGATION_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_RATE_TITLE_START", "com.netflix.mediaclient.intent.action.LOG_UIA_RATE_TITLE_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_REGISTER_START", "com.netflix.mediaclient.intent.action.LOG_UIA_REGISTER_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_SEARCH_START", "com.netflix.mediaclient.intent.action.LOG_UIA_SEARCH_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_START_PLAY_START", "com.netflix.mediaclient.intent.action.LOG_UIA_START_PLAY_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_SUBMIT_PAYMENT_START", "com.netflix.mediaclient.intent.action.LOG_UIA_SUBMIT_PAYMENT_ENDED", "com.netflix.mediaclient.intent.action.LOG_UIA_UPGRADE_STREAMS_START", "com.netflix.mediaclient.intent.action.LOG_UIA_UPGRADE_STREAMS_ENDED" };
    public static final String ADD_TO_PLAYLIST_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_ADD_TO_PLAYLIST_ENDED";
    public static final String ADD_TO_PLAYLIST_START = "com.netflix.mediaclient.intent.action.LOG_UIA_ADD_TO_PLAYLIST_START";
    public static final String EXTRA_CMD = "cmd";
    public static final String EXTRA_ERROR = "error";
    public static final String EXTRA_ERROR_CODE = "error_code";
    public static final String EXTRA_HTTP_RESPONSE = "http_response";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_PAYMENT_TYPE = "payment_type";
    public static final String EXTRA_RANK = "rank";
    public static final String EXTRA_RATING = "rating";
    public static final String EXTRA_REASON = "reason";
    public static final String EXTRA_STREAMS = "streams";
    public static final String EXTRA_SUCCESS = "sucess";
    public static final String EXTRA_TERM = "term";
    public static final String EXTRA_TITLE_RANK = "title_rank";
    public static final String EXTRA_VIEW = "view";
    public static final String LOGIN_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_LOGIN_ENDED";
    public static final String LOGIN_START = "com.netflix.mediaclient.intent.action.LOG_UIA_LOGIN_START";
    public static final String NAVIGATION_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_NAVIGATION_ENDED";
    public static final String NAVIGATION_START = "com.netflix.mediaclient.intent.action.LOG_UIA_NAVIGATION_START";
    public static final String RATE_TITLE_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_RATE_TITLE_ENDED";
    public static final String RATE_TITLE_START = "com.netflix.mediaclient.intent.action.LOG_UIA_RATE_TITLE_START";
    public static final String REGISTER_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_REGISTER_ENDED";
    public static final String REGISTER_START = "com.netflix.mediaclient.intent.action.LOG_UIA_REGISTER_START";
    public static final String REMOVE_FROM_PLAYLIST_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_REMOVE_FROM_PLAYLIST_ENDED";
    public static final String REMOVE_FROM_PLAYLIST_START = "com.netflix.mediaclient.intent.action.LOG_UIA_REMOVE_FROM_PLAYLIST_START";
    public static final String SEARCH_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_SEARCH_ENDED";
    public static final String SEARCH_START = "com.netflix.mediaclient.intent.action.LOG_UIA_SEARCH_START";
    public static final String SIGNUP_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_SIGNUP_ENDED";
    public static final String SIGNUP_START = "com.netflix.mediaclient.intent.action.LOG_UIA_SIGNUP_START";
    public static final String START_PLAY_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_START_PLAY_ENDED";
    public static final String START_PLAY_START = "com.netflix.mediaclient.intent.action.LOG_UIA_START_PLAY_START";
    public static final String SUBMIT_PAYMENT_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_SUBMIT_PAYMENT_ENDED";
    public static final String SUBMIT_PAYMENT_START = "com.netflix.mediaclient.intent.action.LOG_UIA_SUBMIT_PAYMENT_START";
    public static final String UPGRADE_STREAMS_ENDED = "com.netflix.mediaclient.intent.action.LOG_UIA_UPGRADE_STREAMS_ENDED";
    public static final String UPGRADE_STREAMS_START = "com.netflix.mediaclient.intent.action.LOG_UIA_UPGRADE_STREAMS_START";
    
    void endAcknowledgeSignupSession(final IClientLogging.CompletionReason p0, final UIError p1, final IClientLogging.ModalView p2);
    
    void endAddToPlaylistSession(final IClientLogging.CompletionReason p0, final UIError p1, final int p2);
    
    void endLoginSession(final IClientLogging.CompletionReason p0, final UIError p1);
    
    void endNavigationSession(final IClientLogging.ModalView p0, final IClientLogging.CompletionReason p1, final UIError p2);
    
    void endRateTitleSession(final IClientLogging.CompletionReason p0, final UIError p1, final Integer p2, final int p3);
    
    void endRegisterSession(final IClientLogging.CompletionReason p0, final UIError p1);
    
    void endRemoveFromPlaylistSession(final IClientLogging.CompletionReason p0, final UIError p1);
    
    void endSearchSession(final long p0, final IClientLogging.CompletionReason p1, final UIError p2);
    
    void endStartPlaySession(final IClientLogging.CompletionReason p0, final UIError p1, final Integer p2);
    
    void endSubmitPaymentSession(final IClientLogging.CompletionReason p0, final UIError p1, final boolean p2, final PaymentType p3, final JSONObject p4);
    
    void endUpgradeStreamsSession(final IClientLogging.CompletionReason p0, final UIError p1, final Streams p2);
    
    boolean handleIntent(final Intent p0, final boolean p1);
    
    void setDataContext(final DataContext p0);
    
    void startAcknowledgeSignupSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startAddToPlaylistSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startLoginSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startNavigationSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startRateTitleSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startRegisterSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startRemoveFromPlaylistSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startSearchSession(final long p0, final CommandName p1, final IClientLogging.ModalView p2, final String p3);
    
    void startStartPlaySession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startSubmitPaymentSession(final CommandName p0, final IClientLogging.ModalView p1);
    
    void startUpgradeStreamsSession(final CommandName p0, final IClientLogging.ModalView p1, final Streams p2);
    
    public enum CommandName
    {
        rating;
    }
    
    public enum PaymentType
    {
        boletto, 
        creditCard, 
        directDebit, 
        ideal, 
        paypal;
    }
    
    public enum Streams
    {
        _1("1"), 
        _2("2"), 
        _3("3"), 
        _4("4");
        
        private String mValue;
        
        private Streams(final String mValue) {
            this.mValue = mValue;
        }
        
        public static Streams find(final String s) {
            if (!StringUtils.isEmpty(s)) {
                final Streams[] values = values();
                for (int length = values.length, i = 0; i < length; ++i) {
                    final Streams streams;
                    if ((streams = values[i]).getValue().equals(s)) {
                        return streams;
                    }
                }
                return null;
            }
            return null;
        }
        
        public String getValue() {
            return this.mValue;
        }
    }
}