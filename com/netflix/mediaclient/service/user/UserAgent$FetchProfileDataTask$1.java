// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.user;

import com.netflix.mediaclient.ui.verifyplay.PinVerifier$PinType;
import com.netflix.mediaclient.javabridge.ui.ActivationTokens;
import com.netflix.mediaclient.ui.lolomo.PrefetchLolomoABTestUtils;
import com.netflix.mediaclient.ui.profiles.RestrictedProfilesReceiver;
import com.netflix.mediaclient.util.AndroidUtils;
import com.netflix.mediaclient.service.configuration.PersistentConfig;
import com.netflix.mediaclient.ui.experience.BrowseExperience;
import com.netflix.mediaclient.service.voip.VoipAuthorizationTokensUpdater;
import com.netflix.mediaclient.util.StatusUtils;
import com.netflix.mediaclient.service.logging.client.model.RootCause;
import com.netflix.mediaclient.util.PrivacyUtils;
import com.netflix.mediaclient.ui.kids.KidsUtils;
import com.netflix.mediaclient.service.webclient.model.leafs.ThumbMessaging;
import com.netflix.mediaclient.webapi.WebApiCommand;
import com.netflix.mediaclient.servicemgr.IMSLClient$MSLUserCredentialRegistry;
import com.netflix.mediaclient.service.webclient.model.leafs.EogAlert;
import com.netflix.model.leafs.OnRampEligibility$Action;
import com.netflix.mediaclient.android.app.NetflixImmutableStatus;
import com.netflix.mediaclient.util.l10n.UserLocale;
import com.netflix.mediaclient.service.webclient.model.leafs.UmaAlert;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import java.util.Iterator;
import android.support.v4.content.LocalBroadcastManager;
import com.netflix.mediaclient.android.app.BackgroundTask;
import com.netflix.mediaclient.util.NetflixPreference;
import com.netflix.mediaclient.android.app.NetflixStatus;
import com.netflix.mediaclient.StatusCode;
import com.netflix.mediaclient.service.logging.client.model.Error;
import com.netflix.mediaclient.servicemgr.SignInLogging$SignInType;
import com.netflix.mediaclient.util.log.SignInLogUtils;
import com.netflix.mediaclient.servicemgr.IClientLogging$CompletionReason;
import com.netflix.mediaclient.ui.profiles.ProfileSelectionActivity;
import com.netflix.mediaclient.NetflixApplication;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.content.Intent;
import com.netflix.mediaclient.service.webclient.model.leafs.NrmConfigData;
import org.json.JSONException;
import org.json.JSONTokener;
import org.json.JSONArray;
import java.util.ArrayList;
import com.netflix.mediaclient.util.PreferenceUtils;
import com.netflix.mediaclient.service.NetflixService;
import com.netflix.mediaclient.service.webclient.model.leafs.AccountData;
import com.netflix.mediaclient.android.app.CommonStatus;
import com.netflix.mediaclient.service.webclient.model.leafs.User;
import com.netflix.mediaclient.javabridge.ui.Registration;
import com.netflix.mediaclient.javabridge.ui.Nrdp;
import java.util.List;
import com.netflix.mediaclient.javabridge.ui.DeviceAccount;
import com.netflix.mediaclient.javabridge.ui.EventListener;
import com.netflix.mediaclient.service.configuration.ConfigurationAgentWebCallback;
import com.netflix.mediaclient.service.webclient.UserCredentialRegistry;
import com.netflix.mediaclient.service.ServiceAgent$UserAgentInterface;
import com.netflix.mediaclient.service.ServiceAgent;
import com.netflix.mediaclient.service.player.subtitles.text.TextStyle;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.android.app.Status;
import com.netflix.mediaclient.service.webclient.model.leafs.UserProfile;

class UserAgent$FetchProfileDataTask$1 extends SimpleUserAgentWebCallback
{
    final /* synthetic */ UserAgent$FetchProfileDataTask this$1;
    
    UserAgent$FetchProfileDataTask$1(final UserAgent$FetchProfileDataTask this$1) {
        this.this$1 = this$1;
    }
    
    @Override
    public void onProfileDataFetched(final UserProfile userProfile, final Status status) {
        if (status.isSucces() && this.this$1.this$0.isLatestProfileDataValid(userProfile)) {
            if (StringUtils.safeEquals(this.this$1.this$0.mCurrentUserProfile.toString(), userProfile.toString())) {
                Log.d("nf_service_useragent", "onProfileDataFetched nothing changed ignore.. ");
                return;
            }
            this.this$1.this$0.updateAndPersistProfilesList(userProfile);
            if (!StringUtils.safeEquals(this.this$1.this$0.mCurrentUserProfile.getLanguagesInCsv(), userProfile.getLanguagesInCsv())) {
                Log.d("nf_service_useragent", "onProfileDataFetched language changed, update ");
                this.this$1.this$0.setUserPreferredLanguages(userProfile.getLanguages());
            }
            this.this$1.this$0.mSubtitleSettings = TextStyle.buildSubtitleSettings(userProfile.getSubtitlePreference());
            this.this$1.this$0.setCurrentUserProfile(userProfile);
            this.this$1.this$0.persistCurrentProfileGuid(this.this$1.this$0.mCurrentUserProfile);
        }
        else if (Log.isLoggable()) {
            Log.e("nf_service_useragent", "Ignore onProfileDataFetched failed (skipping userProfile update) with statusCode=" + status.getStatusCode());
        }
    }
}
