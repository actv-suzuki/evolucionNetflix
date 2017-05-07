// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.mdx;

import com.netflix.mediaclient.servicemgr.ServiceManager;
import com.netflix.mediaclient.servicemgr.ServiceManagerUtils;
import com.netflix.mediaclient.ui.Asset;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.app.Activity;
import com.netflix.mediaclient.event.nrdp.mdx.StateEvent;
import com.netflix.mediaclient.event.nrdp.mdx.InitErrorEvent;
import com.netflix.mediaclient.event.nrdp.mdx.InitEvent;
import com.netflix.mediaclient.servicemgr.model.details.MovieDetails;
import com.netflix.mediaclient.event.nrdp.mdx.discovery.RemoteDeviceReadyEvent;
import com.netflix.mediaclient.event.nrdp.mdx.discovery.DeviceLostEvent;
import com.netflix.mediaclient.event.nrdp.mdx.discovery.DeviceFoundEvent;
import com.netflix.mediaclient.event.UIEvent;
import com.netflix.mediaclient.ui.player.MDXControllerActivity;
import com.netflix.mediaclient.service.user.UserAgentBroadcastIntents;
import android.support.v4.content.LocalBroadcastManager;
import java.util.Collection;
import android.util.Pair;
import java.nio.ByteBuffer;
import com.netflix.mediaclient.javabridge.ui.mdxcontroller.TransactionId;
import com.netflix.mediaclient.android.app.CommonStatus;
import com.netflix.mediaclient.servicemgr.model.VideoType;
import android.content.IntentFilter;
import android.annotation.SuppressLint;
import android.os.PowerManager;
import android.net.wifi.WifiManager;
import com.netflix.mediaclient.servicemgr.model.details.EpisodeDetails;
import java.util.Iterator;
import com.netflix.mediaclient.service.mdx.notification.MdxNotificationManagerFactory;
import android.app.PendingIntent;
import com.netflix.mediaclient.javabridge.ui.Mdx;
import com.netflix.mediaclient.service.NetflixService;
import com.netflix.mediaclient.servicemgr.IMdxSharedState;
import android.content.Context;
import com.netflix.mediaclient.service.browse.BrowseAgentCallback;
import android.text.TextUtils;
import com.netflix.mediaclient.android.app.Status;
import com.netflix.mediaclient.servicemgr.model.details.PostPlayVideo;
import java.util.List;
import com.netflix.mediaclient.service.browse.SimpleBrowseAgentCallback;
import com.netflix.mediaclient.servicemgr.MdxPostplayState;
import com.netflix.mediaclient.util.StringUtils;
import android.app.Service;
import android.app.Notification;
import com.netflix.mediaclient.util.AndroidUtils;
import android.content.Intent;
import java.util.Map;
import java.util.HashMap;
import com.netflix.mediaclient.Log;
import android.net.wifi.WifiManager$WifiLock;
import com.netflix.mediaclient.util.WebApiUtils;
import com.netflix.mediaclient.servicemgr.model.details.VideoDetails;
import com.netflix.mediaclient.javabridge.ui.mdxcontroller.RemoteDevice;
import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.os.PowerManager$WakeLock;
import com.netflix.mediaclient.service.mdx.notification.IMdxNotificationManager;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.HandlerThread;
import android.os.Handler;
import com.netflix.mediaclient.javabridge.ui.EventListener;
import com.netflix.mediaclient.service.mdx.cast.CastManager;
import android.graphics.Bitmap;
import com.netflix.mediaclient.media.BifManager;
import com.netflix.mediaclient.service.mdx.notification.MdxNotificationManager;
import com.netflix.mediaclient.javabridge.ui.mdxcontroller.MdxController;
import com.netflix.mediaclient.servicemgr.IMdx;
import com.netflix.mediaclient.service.ServiceAgent;

public class MdxAgent extends ServiceAgent implements IMdx, PropertyUpdateListener, MdxNotificationIntentRetriever, TargetSelectorInterface, SwitchTargetInterface, MdxImageLoaderInterface, SessionWatchDogInterface
{
    private static final int DEFAULT_INTEGER = -1;
    public static final String EVENT485_TRANSFERFROM_LOCAL = "local_playback_transfer";
    public static final String EVENT526_TYPE_FAIL = "association_failed";
    public static final String EVENT526_TYPE_FOUND = "found";
    public static final String EVENT526_TYPE_LOST = "lost";
    public static final String EVENT537_TYPE_CANCEL_PLAYBACK = "cancel playback";
    public static final String EVENT537_TYPE_LOCAL_PLAYBACK = "local playback";
    public static final String EVENT537_TYPE_TARGET_PLAYBACK = "target playback";
    private static final String TAG = "nf_mdx_agent";
    private BifManager mBifManager;
    private Bitmap mBoxartBitmap;
    private CastManager mCastManager;
    private CommandHandler mCommandHandler;
    private String mCurrentTargetUuid;
    private boolean mDisableWebSocket;
    private final EventListener mDiscoveryEventListener;
    private boolean mEnableCast;
    private final Runnable mInitMdxNative;
    private final Handler mMdxAgentWorkerHandler;
    private HandlerThread mMdxAgentWorkerThread;
    private MdxImageLoader mMdxBoxartLoader;
    private MdxController mMdxController;
    private final AtomicBoolean mMdxNativeExitCompleted;
    private IMdxNotificationManager mMdxNotificationManager;
    private MdxNrdpLogger mMdxNrdpLogger;
    private MdxSessionWatchDog mMdxSessionWatchDog;
    private ClientNotifier mNotifier;
    private PowerManager$WakeLock mPartialWakeLock;
    private final AtomicBoolean mReady;
    private RemoteControlClientManager mRemoteControlClientManager;
    private final BroadcastReceiver mStartStopErrorReceiver;
    private int mStartTime;
    private final EventListener mStateEventListener;
    private SwitchTarget mSwitchTarget;
    private String mTargetDialUuid;
    private String mTargetFriendlyName;
    private TargetManager mTargetManager;
    private final ArrayList<RemoteDevice> mTargetMap;
    private TargetSelector mTargetSelector;
    private String mTargetUuid;
    private int mTrackId;
    private final BroadcastReceiver mUserAgentReceiver;
    private boolean mUserIsLogin;
    private VideoDetails mVideoDetails;
    private VideoDetails mVideoDetailsPostplay;
    private WebApiUtils.VideoIds mVideoIds;
    private WebApiUtils.VideoIds mVideoIdsPostplay;
    private WifiManager$WifiLock mWifiLock;
    
    public MdxAgent() {
        this.mUserIsLogin = false;
        this.mTargetMap = new ArrayList<RemoteDevice>();
        this.mVideoIds = new WebApiUtils.VideoIds();
        this.mVideoIdsPostplay = new WebApiUtils.VideoIds();
        this.mDisableWebSocket = false;
        this.mEnableCast = false;
        this.mInitMdxNative = new Runnable() {
            @Override
            public void run() {
                if (MdxAgent.this.mMdxNativeExitCompleted.get()) {
                    Log.d("nf_mdx_agent", "notifyIsUserLogin: login, init native");
                    MdxAgent.this.getMainHandler().removeCallbacks(MdxAgent.this.mInitMdxNative);
                    MdxAgent.this.mReady.set(false);
                    MdxAgent.this.addStateEventListener();
                    MdxAgent.this.addDiscoveryEventListener();
                    MdxAgent.this.addPairingEventListener(MdxAgent.this.mTargetManager);
                    MdxAgent.this.addSessionEventListener(MdxAgent.this.mTargetManager);
                    MdxAgent.this.mMdxController.init(new HashMap<String, String>(), MdxAgent.this.mDisableWebSocket, MdxAgent.this.getService().getConfiguration().getMdxBlackListTargets());
                    MdxAgent.this.mTargetMap.clear();
                    return;
                }
                Log.d("nf_mdx_agent", "notifyIsUserLogin: login, already exited check back in 1 sec ");
                MdxAgent.this.getMainHandler().postDelayed(MdxAgent.this.mInitMdxNative, 1000L);
            }
        };
        this.mStateEventListener = new StateEventListener();
        this.mDiscoveryEventListener = new DiscoveryEventListener();
        this.mStartStopErrorReceiver = new BroadcastReceiver() {
            private void doMDXPlayBackEnd(final Intent intent) {
                if (!Utils.isInPostPlay(intent)) {
                    Log.d("nf_mdx_agent", "MdxAgent: receive MDXUPDATE_PLAYBACKEND");
                    MdxAgent.this.mMdxSessionWatchDog.stop();
                    MdxAgent.this.clearVideoDetails();
                    MdxAgent.this.stopAllNotifications();
                    MdxAgent.this.releaseWiFi();
                    if (MdxAgent.this.mTargetSelector != null) {
                        MdxAgent.this.mTargetSelector.targetBecomeInactive(MdxAgent.this.mCurrentTargetUuid);
                    }
                    if (MdxAgent.this.mSwitchTarget != null) {
                        MdxAgent.this.mSwitchTarget.targetPlaybackStopped(MdxAgent.this.mCurrentTargetUuid);
                    }
                }
            }
            
            private void doMDXPlaybackStart() {
                Log.d("nf_mdx_agent", "MdxAgent: receive MDXUPDATE_PLAYBACKSTART");
                MdxAgent.this.mMdxSessionWatchDog.start();
                MdxAgent.this.lockWiFi();
                MdxAgent.this.ensureManagers();
                if (AndroidUtils.getAndroidVersion() < 21) {
                    MdxAgent.this.mRemoteControlClientManager.start(false, null, MdxAgent.this.mCurrentTargetUuid);
                    MdxAgent.this.updateMdxRemoteClient(false);
                    if (MdxAgent.this.mBoxartBitmap != null) {
                        MdxAgent.this.mRemoteControlClientManager.setState(false, false, false);
                        MdxAgent.this.mRemoteControlClientManager.setBoxart(MdxAgent.this.mBoxartBitmap);
                    }
                }
                MdxAgent.this.mMdxNotificationManager.startNotification((Notification)MdxAgent.this.getMdxNotification(false).second, MdxAgent.this.getService(), false);
                MdxAgent.this.mMdxNotificationManager.setPlayerStateNotify(false, false);
                MdxAgent.this.mMdxNotificationManager.setBoxartNotify(MdxAgent.this.mBoxartBitmap);
                if (MdxAgent.this.mTargetSelector != null) {
                    MdxAgent.this.mTargetSelector.targetBecomeActive(MdxAgent.this.mCurrentTargetUuid);
                }
            }
            
            private void doMDXPostPlay(final Intent intent) {
                final String string = intent.getExtras().getString("postplayState");
                if (!StringUtils.isEmpty(string)) {
                    final MdxPostplayState mdxPostplayState = new MdxPostplayState(string);
                    if (mdxPostplayState.isInCountdown()) {
                        this.doMDXPostPlayCountdownStart(intent, string);
                    }
                    else if (mdxPostplayState.isInPrompt()) {
                        this.doMDXPostPlayPrompt(intent, string);
                    }
                }
            }
            
            private void doMDXPostPlayCountdownStart(final Intent intent, final String s) {
                MdxAgent.this.ensureManagers();
                this.updateVideoIdsForPostplay(s);
                if (AndroidUtils.getAndroidVersion() < 21) {
                    MdxAgent.this.mRemoteControlClientManager.start(true, MdxAgent.this.mVideoDetailsPostplay, MdxAgent.this.mCurrentTargetUuid);
                    MdxAgent.this.updateMdxRemoteClient(true);
                    MdxAgent.this.mRemoteControlClientManager.setState(false, false, true);
                }
                MdxAgent.this.mMdxNotificationManager.startNotification((Notification)MdxAgent.this.getMdxNotification(true).second, MdxAgent.this.getService(), true);
                MdxAgent.this.mMdxNotificationManager.setUpNextStateNotify(false, false, true);
            }
            
            private void doMDXPostPlayPrompt(final Intent intent, final String s) {
                MdxAgent.this.getBrowseAgent().fetchPostPlayVideos(String.valueOf(MdxAgent.this.getVideoIds().episodeId), new SimpleBrowseAgentCallback() {
                    @Override
                    public void onPostPlayVideosFetched(final List<PostPlayVideo> list, final Status status) {
                        if (Log.isLoggable("nf_mdx_agent", 2)) {
                            Log.v("nf_mdx_agent", "onPostPlayVideosFetched, res: " + status);
                        }
                        if (status.isSucces() && list.size() > 0) {
                            final String id = list.get(0).getId();
                            if (!TextUtils.isEmpty((CharSequence)id)) {
                                MdxAgent.this.updateMdxNotificationAndLockscreenWithNextSeries(id);
                            }
                        }
                    }
                });
            }
            
            private void dpMDXSimplePlaybackState(final Intent intent) {
                final boolean booleanExtra = intent.getBooleanExtra("paused", false);
                final boolean booleanExtra2 = intent.getBooleanExtra("transitioning", false);
                final boolean inPostPlay = Utils.isInPostPlay(intent);
                if (Log.isLoggable("nf_mdx_agent", 3)) {
                    Log.d("nf_mdx_agent", "MdxAgent: simplePlaybackState : paused " + booleanExtra + ", transitioning " + booleanExtra2);
                }
                MdxAgent.this.ensureManagers();
                if (AndroidUtils.getAndroidVersion() < 21) {
                    MdxAgent.this.mRemoteControlClientManager.setState(booleanExtra, booleanExtra2, inPostPlay);
                }
                MdxAgent.this.mMdxNotificationManager.setPlayerStateNotify(booleanExtra, booleanExtra2);
            }
            
            private void updateVideoIdsForPostplay(final String s) {
                final MdxPostplayState mdxPostplayState = new MdxPostplayState(s);
                if (mdxPostplayState.isInCountdown()) {
                    final MdxPostplayState.PostplayTitle[] postplayTitle = mdxPostplayState.getPostplayTitle();
                    if (postplayTitle.length > 0 && postplayTitle[0].isEpisode() && postplayTitle[0].getId() > 0) {
                        MdxAgent.this.mVideoIdsPostplay = new WebApiUtils.VideoIds();
                        MdxAgent.this.mVideoIdsPostplay.episode = true;
                        MdxAgent.this.mVideoIdsPostplay.episodeId = postplayTitle[0].getId();
                        MdxAgent.this.fetchVideoDetail(false, true);
                    }
                }
            }
            
            public void onReceive(final Context context, final Intent intent) {
                if (intent.hasCategory("com.netflix.mediaclient.intent.category.MDX")) {
                    if (intent.getAction().equals("com.netflix.mediaclient.intent.action.MDXUPDATE_PLAYBACKEND")) {
                        this.doMDXPlayBackEnd(intent);
                    }
                    else {
                        if (intent.getAction().equals("com.netflix.mediaclient.intent.action.MDXUPDATE_PLAYBACKSTART")) {
                            this.doMDXPlaybackStart();
                            return;
                        }
                        if ("com.netflix.mediaclient.intent.action.MDXUPDATE_STATE".equals(intent.getAction())) {
                            if (MdxAgent.this.getSharedState() != null && MdxAgent.this.getSharedState().getMdxPlaybackState() == IMdxSharedState.MdxPlaybackState.Transitioning) {
                                MdxAgent.this.ensureManagers();
                                if (MdxAgent.this.mMdxNotificationManager.isInPostPlay()) {
                                    MdxAgent.this.mMdxNotificationManager.stopPostplayNotification(MdxAgent.this.getService());
                                    if (AndroidUtils.getAndroidVersion() < 21) {
                                        MdxAgent.this.mRemoteControlClientManager.stop();
                                    }
                                }
                            }
                        }
                        else {
                            if ("com.netflix.mediaclient.intent.action.MDXUPDATE_POSTPLAY".equals(intent.getAction())) {
                                this.doMDXPostPlay(intent);
                                return;
                            }
                            if (intent.getAction().equals("com.netflix.mediaclient.intent.action.MDXUPDATE_SIMPLE_PLAYBACKSTATE")) {
                                this.dpMDXSimplePlaybackState(intent);
                                return;
                            }
                            if (intent.getAction().equals("com.netflix.mediaclient.intent.action.MDXUPDATE_ERROR")) {
                                final int intExtra = intent.getIntExtra("errorCode", 0);
                                MdxAgent.this.stopAllNotifications();
                                if (MdxAgent.this.mNotifier != null) {
                                    final MdxSharedState sharedState = MdxAgent.this.mNotifier.getSharedState(MdxAgent.this.mCurrentTargetUuid);
                                    if (sharedState != null) {
                                        boolean b = false;
                                        if (IMdxSharedState.MdxPlaybackState.Loading == sharedState.getMdxPlaybackState() || IMdxSharedState.MdxPlaybackState.Transitioning == sharedState.getMdxPlaybackState()) {
                                            b = true;
                                        }
                                        if (intExtra >= 100 && intExtra < 200 && b) {
                                            Log.d("nf_mdx_agent", "MdxAgent: received error, clear video detail");
                                            MdxAgent.this.clearVideoDetails();
                                        }
                                    }
                                }
                                if (intExtra >= 100 && intExtra < 200) {
                                    MdxAgent.this.resetTargetSelection();
                                }
                            }
                        }
                    }
                }
            }
        };
        this.mUserAgentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent == null) {
                    Log.v("nf_mdx_agent", "Null intent");
                }
                else {
                    final String action = intent.getAction();
                    if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_PROFILE_ACTIVE".equals(action)) {
                        Log.d("nf_mdx_agent", "useprofile is active");
                        MdxAgent.this.notifyIsUserLogin(true);
                        return;
                    }
                    if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_PROFILE_DEACTIVE".equals(action)) {
                        Log.d("nf_mdx_agent", "useprofile is not active");
                        MdxAgent.this.notifyIsUserLogin(false);
                        return;
                    }
                    if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_ACCOUNT_ACTIVE".equals(action)) {
                        Log.d("nf_mdx_agent", "user account is activated");
                        return;
                    }
                    if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_ACCOUNT_DEACTIVE".equals(action)) {
                        Log.d("nf_mdx_agent", "user account is deactivated");
                    }
                }
            }
        };
        Log.d("nf_mdx_agent", "MdxAgent: start");
        this.mReady = new AtomicBoolean(false);
        this.mMdxNativeExitCompleted = new AtomicBoolean(true);
        this.mCurrentTargetUuid = new String();
        (this.mMdxAgentWorkerThread = new HandlerThread("MdxAgentWorker")).start();
        this.mMdxAgentWorkerHandler = new Handler(this.mMdxAgentWorkerThread.getLooper());
    }
    
    private void addDiscoveryEventListener() {
        this.mMdxController.addEventListener(Events.mdx_discovery_devicefound.getName(), this.mDiscoveryEventListener);
        this.mMdxController.addEventListener(Events.mdx_discovery_devicelost.getName(), this.mDiscoveryEventListener);
        this.mMdxController.addEventListener(Events.mdx_discovery_remoteDeviceReady.getName(), this.mDiscoveryEventListener);
    }
    
    private void addPairingEventListener(final EventListener eventListener) {
        this.mMdxController.addEventListener(Events.mdx_pair_pairingresponse.getName(), eventListener);
        this.mMdxController.addEventListener(Events.mdx_pair_regpairresponse.getName(), eventListener);
        this.mMdxController.addEventListener(Events.mdx_pair_pairingdeleted.getName(), eventListener);
    }
    
    private void addSessionEventListener(final EventListener eventListener) {
        this.mMdxController.addEventListener(Events.mdx_session_startSessionResponse.getName(), eventListener);
        this.mMdxController.addEventListener(Events.mdx_session_messagedelivered.getName(), eventListener);
        this.mMdxController.addEventListener(Events.mdx_session_message.getName(), eventListener);
        this.mMdxController.addEventListener(Events.mdx_session_messagingerror.getName(), eventListener);
        this.mMdxController.addEventListener(Events.mdx_session_sessionended.getName(), eventListener);
    }
    
    private void addStateEventListener() {
        this.removeStateEventListener();
        this.mMdxController.addEventListener(Events.mdx_init.getName(), this.mStateEventListener);
        this.mMdxController.addEventListener(Events.mdx_initerror.getName(), this.mStateEventListener);
        this.mMdxController.addEventListener(Events.mdx_mdxstate.getName(), this.mStateEventListener);
    }
    
    private void clearVideoDetails() {
        this.mVideoIds = new WebApiUtils.VideoIds();
        this.mVideoDetails = null;
    }
    
    private void createBifManager(final String s) {
        if (this.mBifManager != null) {
            this.mBifManager.release();
        }
        this.mBifManager = new BifManager(this.getContext(), s);
    }
    
    private PendingIntent createNotificationButtonIntent(final Intent intent) {
        intent.setClass(this.getContext(), (Class)NetflixService.class).addCategory("com.netflix.mediaclient.intent.category.MDX").putExtra("uuid", this.mCurrentTargetUuid);
        return PendingIntent.getService(this.getContext(), 0, intent, 134217728);
    }
    
    private void ensureManagers() {
        if (this.mRemoteControlClientManager == null && AndroidUtils.getAndroidVersion() < 21) {
            this.mRemoteControlClientManager = new RemoteControlClientManager(this.getContext());
        }
        if (this.mMdxNotificationManager == null) {
            this.mMdxNotificationManager = MdxNotificationManagerFactory.create(this.getContext(), true, this);
        }
    }
    
    private void fetchVideoDetail(final boolean b, final boolean b2) {
        if ((b2 && this.mVideoIdsPostplay.episode) || (!b2 && this.mVideoIds.episode)) {
            final EpisodeBrowseAgentCallback episodeBrowseAgentCallback = new EpisodeBrowseAgentCallback(b, b2);
            final BrowseAgentInterface browseAgent = this.getBrowseAgent();
            int n;
            if (b2) {
                n = this.mVideoIdsPostplay.episodeId;
            }
            else {
                n = this.mVideoIds.episodeId;
            }
            browseAgent.fetchEpisodeDetails(String.valueOf(n), episodeBrowseAgentCallback);
            return;
        }
        this.getBrowseAgent().fetchMovieDetails(String.valueOf(this.mVideoIds.catalogId), new MovieBrowseAgentCallback(b));
    }
    
    private String getCurrentEpisodeTitle() {
        if (this.mVideoDetails == null) {
            return null;
        }
        return this.getContext().getString(2131493259, new Object[] { this.mVideoDetails.getPlayable().getSeasonNumber(), this.mVideoDetails.getPlayable().getEpisodeNumber(), this.mVideoDetails.getTitle() });
    }
    
    private RemoteDevice getDeviceFromUuid(final String s) {
        while (true) {
        Label_0112:
            while (true) {
                final RemoteDevice remoteDevice;
                final String uuid;
                synchronized (this.mTargetMap) {
                    if (this.mTargetMap.isEmpty()) {
                        return null;
                    }
                    final Iterator<RemoteDevice> iterator = this.mTargetMap.iterator();
                    if (!iterator.hasNext()) {
                        break Label_0112;
                    }
                    remoteDevice = iterator.next();
                    uuid = remoteDevice.uuid;
                    final String dialUuid = remoteDevice.dialUuid;
                    if (StringUtils.isNotEmpty(dialUuid) && dialUuid.equals(s)) {
                        return remoteDevice;
                    }
                }
                if (StringUtils.isNotEmpty(uuid) && uuid.equals(s)) {
                    // monitorexit(list)
                    return remoteDevice;
                }
                continue;
            }
            // monitorexit(list)
            return null;
        }
    }
    
    private String getNextEpisodeTitle() {
        if (this.mVideoDetailsPostplay == null) {
            return null;
        }
        final EpisodeDetails episodeDetails = (EpisodeDetails)this.mVideoDetailsPostplay;
        return this.getContext().getString(2131493259, new Object[] { episodeDetails.getSeasonNumber(), episodeDetails.getEpisodeNumber(), episodeDetails.getTitle() });
    }
    
    private void handleAccountConfig() {
        this.mDisableWebSocket = this.getConfigurationAgent().isDisableWebsocket();
        this.mEnableCast = this.getConfigurationAgent().isEnableCast();
        if (this.mEnableCast) {
            (this.mCastManager = new CastManager(this.getContext(), this.getMainHandler(), this.mMdxAgentWorkerHandler, this.getConfigurationAgent().getEsnProvider().getEsn(), this.mMdxNrdpLogger)).setCastWhiteList(this.getConfigurationAgent().getCastWhiteList());
            if (StringUtils.isNotEmpty(this.mCurrentTargetUuid)) {
                this.mCastManager.setTargetId(this.mCurrentTargetUuid);
            }
            return;
        }
        this.mCastManager = null;
    }
    
    private boolean isSameDevice(final String s, final String s2) {
        boolean b = true;
        if (StringUtils.isEmpty(s) || StringUtils.isEmpty(s2)) {
            b = false;
        }
        else if (!s.equals(s2)) {
            synchronized (this.mTargetMap) {
                if (this.mTargetMap.isEmpty()) {
                    return false;
                }
                for (final RemoteDevice remoteDevice : this.mTargetMap) {
                    final String uuid = remoteDevice.uuid;
                    final String dialUuid = remoteDevice.dialUuid;
                    if ((s.equals(dialUuid) && s2.equals(uuid)) || (s.equals(uuid) && s2.equals(dialUuid))) {
                        return true;
                    }
                }
            }
            // monitorexit(list)
            return false;
        }
        return b;
    }
    
    @SuppressLint({ "InlinedApi" })
    private void lockWiFi() {
        this.releaseWiFi();
        final WifiManager wifiManager = (WifiManager)this.getContext().getSystemService("wifi");
        if (wifiManager != null) {
            Log.d("nf_mdx_agent", "WiFi lock acquiring...");
            (this.mWifiLock = wifiManager.createWifiLock(3, "nf_mdx_agent")).acquire();
            Log.d("nf_mdx_agent", "WiFi lock acquired.");
        }
        final PowerManager powerManager = (PowerManager)this.getContext().getSystemService("power");
        if (powerManager != null && this.mPartialWakeLock == null) {
            this.mPartialWakeLock = powerManager.newWakeLock(1, "nf_mdx_agent");
        }
        if (this.mPartialWakeLock != null && !this.mPartialWakeLock.isHeld()) {
            this.mPartialWakeLock.acquire();
        }
    }
    
    private void reconcileSelectedTargetInfo() {
        if (!StringUtils.isEmpty(this.mCurrentTargetUuid)) {
            final boolean b = false;
            final RemoteDevice deviceFromUuid = this.getDeviceFromUuid(this.mCurrentTargetUuid);
            RemoteDevice remoteDevice;
            boolean b2;
            if (deviceFromUuid == null) {
                if (this.getDeviceFromUuid(this.mTargetUuid) != null) {
                    remoteDevice = this.getDeviceFromUuid(this.mTargetUuid);
                    this.mCurrentTargetUuid = this.mTargetUuid;
                    b2 = true;
                }
                else if (this.getDeviceFromUuid(this.mTargetDialUuid) != null) {
                    remoteDevice = this.getDeviceFromUuid(this.mTargetDialUuid);
                    this.mCurrentTargetUuid = this.mTargetDialUuid;
                    b2 = true;
                }
                else {
                    for (final RemoteDevice remoteDevice2 : this.mTargetMap) {
                        if (StringUtils.isNotEmpty(this.mTargetFriendlyName) && this.mTargetFriendlyName.equals(remoteDevice2.friendlyName)) {
                            if (StringUtils.isNotEmpty(remoteDevice2.dialUuid)) {
                                this.mCurrentTargetUuid = remoteDevice2.dialUuid;
                                break;
                            }
                            this.mCurrentTargetUuid = remoteDevice2.uuid;
                            break;
                        }
                    }
                    if (Log.isLoggable("nf_mdx_agent", 3)) {
                        Log.d("nf_mdx_agent", "MdxAgent: taregt no longer exist " + this.mCurrentTargetUuid);
                    }
                    return;
                }
            }
            else {
                remoteDevice = deviceFromUuid;
                b2 = b;
                if (StringUtils.isNotEmpty(deviceFromUuid.dialUuid)) {
                    remoteDevice = deviceFromUuid;
                    b2 = b;
                    if (!deviceFromUuid.dialUuid.equals(this.mCurrentTargetUuid)) {
                        this.mCurrentTargetUuid = deviceFromUuid.dialUuid;
                        b2 = true;
                        remoteDevice = deviceFromUuid;
                    }
                }
            }
            final String uuid = remoteDevice.uuid;
            final String dialUuid = remoteDevice.dialUuid;
            final String friendlyName = remoteDevice.friendlyName;
            boolean b3 = b2;
            if (StringUtils.isNotEmpty(uuid)) {
                b3 = b2;
                if (!uuid.equals(this.mTargetUuid)) {
                    this.mTargetUuid = uuid;
                    b3 = true;
                }
            }
            boolean b4 = b3;
            if (StringUtils.isNotEmpty(dialUuid)) {
                b4 = b3;
                if (!uuid.equals(this.mTargetDialUuid)) {
                    this.mTargetDialUuid = dialUuid;
                    b4 = true;
                }
            }
            boolean b5 = b4;
            if (StringUtils.isNotEmpty(friendlyName)) {
                b5 = b4;
                if (!uuid.equals(this.mTargetFriendlyName)) {
                    this.mTargetFriendlyName = friendlyName;
                    b5 = true;
                }
            }
            if (b5 && this.mTargetSelector != null) {
                this.mTargetSelector.updateSelectedTarget(this.mCurrentTargetUuid, this.mTargetUuid, this.mTargetDialUuid, this.mTargetFriendlyName);
            }
        }
    }
    
    private void registerStartStopReceiver() {
        final IntentFilter intentFilter = new IntentFilter("com.netflix.mediaclient.intent.action.MDXUPDATE_PLAYBACKEND");
        intentFilter.addAction("com.netflix.mediaclient.intent.action.MDXUPDATE_PLAYBACKSTART");
        intentFilter.addAction("com.netflix.mediaclient.intent.action.MDXUPDATE_POSTPLAY");
        intentFilter.addAction("com.netflix.mediaclient.intent.action.MDXUPDATE_SIMPLE_PLAYBACKSTATE");
        intentFilter.addAction("com.netflix.mediaclient.intent.action.MDXUPDATE_ERROR");
        intentFilter.addAction("com.netflix.mediaclient.intent.action.MDXUPDATE_STATE");
        intentFilter.addCategory("com.netflix.mediaclient.intent.category.MDX");
        intentFilter.setPriority(999);
        this.getContext().registerReceiver(this.mStartStopErrorReceiver, intentFilter);
    }
    
    private void releaseWiFi() {
        if (this.mWifiLock != null && this.mWifiLock.isHeld()) {
            Log.d("nf_mdx_agent", "WiFi lock was held, release...");
            this.mWifiLock.release();
            Log.d("nf_mdx_agent", "WiFi lock released.");
        }
        if (this.mPartialWakeLock != null && this.mPartialWakeLock.isHeld()) {
            this.mPartialWakeLock.release();
        }
    }
    
    private void removeDiscoveryEventListener() {
        this.mMdxController.removeEventListener(Events.mdx_discovery_devicefound.getName(), this.mDiscoveryEventListener);
        this.mMdxController.removeEventListener(Events.mdx_discovery_devicelost.getName(), this.mDiscoveryEventListener);
        this.mMdxController.removeEventListener(Events.mdx_discovery_remoteDeviceReady.getName(), this.mDiscoveryEventListener);
    }
    
    private void removePairingEventListener(final EventListener eventListener) {
        this.mMdxController.removeEventListener(Events.mdx_pair_pairingresponse.getName(), eventListener);
        this.mMdxController.removeEventListener(Events.mdx_pair_regpairresponse.getName(), eventListener);
        this.mMdxController.removeEventListener(Events.mdx_pair_pairingdeleted.getName(), eventListener);
    }
    
    private void removeSessionEventListener(final EventListener eventListener) {
        this.mMdxController.removeEventListener(Events.mdx_session_startSessionResponse.getName(), eventListener);
        this.mMdxController.removeEventListener(Events.mdx_session_messagedelivered.getName(), eventListener);
        this.mMdxController.removeEventListener(Events.mdx_session_message.getName(), eventListener);
        this.mMdxController.removeEventListener(Events.mdx_session_messagingerror.getName(), eventListener);
        this.mMdxController.removeEventListener(Events.mdx_session_sessionended.getName(), eventListener);
    }
    
    private void removeStateEventListener() {
        this.mMdxController.removeEventListener(Events.mdx_init.getName(), this.mStateEventListener);
        this.mMdxController.removeEventListener(Events.mdx_initerror.getName(), this.mStateEventListener);
        this.mMdxController.removeEventListener(Events.mdx_mdxstate.getName(), this.mStateEventListener);
    }
    
    private void resetTargetSelection() {
        Log.e("nf_mdx_agent", "MdxAgent: resetTargetSelection");
        this.mCurrentTargetUuid = new String();
        this.mTargetUuid = new String();
        this.mTargetDialUuid = new String();
        this.mTargetFriendlyName = new String();
        if (this.mTargetSelector != null) {
            this.mTargetSelector.selectNewTarget(this.mCurrentTargetUuid, this.mTargetUuid, this.mTargetDialUuid, this.mTargetFriendlyName);
        }
    }
    
    private void sessionGone() {
        if (this.mTargetManager != null) {
            this.mTargetManager.targetGone(this.mCurrentTargetUuid);
        }
        this.mNotifier.error(this.mCurrentTargetUuid, 201, "stop connecting to target");
        this.mNotifier.playbackEnd(this.mCurrentTargetUuid, null);
    }
    
    private void unregisterStartStopReceiver() {
        try {
            this.getContext().unregisterReceiver(this.mStartStopErrorReceiver);
        }
        catch (Exception ex) {
            Log.i("nf_mdx_agent", "unregistermStartStopReceiver " + ex);
        }
    }
    
    private void updateMdxNotification(final boolean b, final String s, final String s2, final boolean b2) {
        this.ensureManagers();
        synchronized (this.mMdxNotificationManager) {
            this.mMdxNotificationManager.setTitlesNotify(b, s, s2);
        }
    }
    
    private void updateMdxRemoteClient(final boolean b) {
        if ((!b || this.mVideoDetailsPostplay != null) && (b || this.mVideoDetails != null)) {
            VideoDetails videoDetails;
            if (b) {
                videoDetails = this.mVideoDetailsPostplay;
            }
            else {
                videoDetails = this.mVideoDetails;
            }
            if (videoDetails.getType() == VideoType.EPISODE) {
                String s;
                if (b) {
                    s = this.getContext().getString(2131493251);
                }
                else {
                    s = videoDetails.getPlayable().getParentTitle();
                }
                String s2;
                if (b && videoDetails instanceof EpisodeDetails) {
                    s2 = this.getNextEpisodeTitle();
                }
                else {
                    s2 = this.getCurrentEpisodeTitle();
                }
                if (AndroidUtils.getAndroidVersion() < 21) {
                    this.mRemoteControlClientManager.start(b, videoDetails, this.mCurrentTargetUuid);
                    this.mRemoteControlClientManager.setTitles(s, s2);
                }
            }
            else if (AndroidUtils.getAndroidVersion() < 21) {
                this.mRemoteControlClientManager.setTitles(videoDetails.getTitle(), null);
            }
        }
    }
    
    @Override
    public void destroy() {
        this.getMainHandler().removeCallbacks(this.mInitMdxNative);
        this.mMdxAgentWorkerThread.quit();
        while (true) {
            try {
                this.mMdxAgentWorkerThread.join();
                this.mMdxAgentWorkerThread = null;
                this.unregisterUserAgentReceiver();
                if (this.mMdxController != null) {
                    this.mMdxController.removePropertyUpdateListener();
                    this.removeStateEventListener();
                    this.removeDiscoveryEventListener();
                    this.removePairingEventListener(this.mTargetManager);
                    this.removeSessionEventListener(this.mTargetManager);
                    this.mMdxController = null;
                }
                if (this.mBifManager != null) {
                    this.mBifManager.release();
                    this.mBifManager = null;
                }
                if (AndroidUtils.getAndroidVersion() < 21 && this.mRemoteControlClientManager != null) {
                    this.mRemoteControlClientManager.stop();
                    this.mRemoteControlClientManager.destroy();
                    this.mRemoteControlClientManager = null;
                }
                if (this.mCastManager != null) {
                    this.mCastManager.destroy();
                }
                this.unregisterStartStopReceiver();
                super.destroy();
            }
            catch (InterruptedException ex) {
                Log.e("nf_mdx_agent", "MdxAgent: mMdxAgentWorkerThread interrupted");
                continue;
            }
            break;
        }
    }
    
    @Override
    protected void doInit() {
        boolean b = true;
        Log.e("nf_mdx_agent", "MdxAgent: doInit");
        if (this.getNrdController() == null || this.getNrdController().getNrdp() == null) {
            this.initCompleted(CommonStatus.NRD_ERROR);
            return;
        }
        this.mMdxController = this.getNrdController().getNrdp().getMdxController();
        this.mMdxNrdpLogger = new MdxNrdpLogger(this.getNrdController().getNrdp());
        this.mNotifier = new ClientNotifier(this.getContext());
        this.mTargetManager = new TargetManager(this.mNotifier, this.mMdxController, this.getConfigurationAgent().getEsnProvider().getEsn(), this.mMdxNrdpLogger);
        this.mCommandHandler = new CommandHandler(this.mTargetManager);
        if (this.mMdxController == null || this.mNotifier == null || this.mTargetManager == null || this.mCommandHandler == null) {
            this.initCompleted(CommonStatus.INTERNAL_ERROR);
            return;
        }
        this.mReady.set(false);
        this.mMdxNativeExitCompleted.set(true);
        this.mTargetMap.clear();
        this.mMdxController.setPropertyUpdateListener((MdxController.PropertyUpdateListener)this);
        TransactionId.setTransactionIdSource(this.getNrdController().getNrdp());
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "MdxAgent: change XID base from " + System.currentTimeMillis() + " ==> " + this.getNrdController().getNrdp().now());
        }
        this.mMdxAgentWorkerHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                MdxAgent.this.mTargetSelector = new TargetSelector(MdxAgent.this.getContext(), (TargetSelector.TargetSelectorInterface)MdxAgent.this);
            }
        });
        this.mSwitchTarget = new SwitchTarget(this.mTargetManager, (SwitchTarget.SwitchTargetInterface)this);
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "MdxAgent: doInit mCurrentTargetUuid: " + this.mCurrentTargetUuid);
        }
        this.mMdxBoxartLoader = new MdxImageLoader((Context)this.getService(), this.getResourceFetcher(), (MdxImageLoader.MdxImageLoaderInterface)this, this.mMdxAgentWorkerHandler);
        this.mMdxSessionWatchDog = new MdxSessionWatchDog((MdxSessionWatchDog.SessionWatchDogInterface)this, this.mMdxAgentWorkerHandler);
        this.registerUserAgentReceiver();
        final UserAgentInterface userAgent = this.getUserAgent();
        if (userAgent != null) {
            if (!StringUtils.isNotEmpty(userAgent.getCurrentProfileGuid()) || !userAgent.isUserLoggedIn()) {
                b = false;
            }
            this.notifyIsUserLogin(b);
        }
        else {
            Log.e("nf_mdx_agent", "MdxAgent: userAgent is not ready yet, skip init");
        }
        this.registerStartStopReceiver();
        if (AndroidUtils.getAndroidVersion() < 21) {
            this.mRemoteControlClientManager = new RemoteControlClientManager(this.getContext());
        }
        this.initCompleted(CommonStatus.OK);
    }
    
    @Override
    public ByteBuffer getBifFrame(final int n) {
        if (this.mBifManager != null) {
            return this.mBifManager.getIndexFrame(n);
        }
        return null;
    }
    
    @Override
    public String getCurrentTarget() {
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "MdxAgent: getCurrentTarget : " + this.mCurrentTargetUuid);
        }
        return this.mCurrentTargetUuid;
    }
    
    public Pair<Integer, Notification> getMdxNotification(final boolean b) {
        final WebApiUtils.VideoIds videoIds = this.mTargetManager.getVideoIds();
        this.ensureManagers();
        if (videoIds != null) {
            if (videoIds.episode != this.mVideoIds.episode || (videoIds.episode && videoIds.episodeId != this.mVideoIds.episodeId) || videoIds.catalogId != this.mVideoIds.catalogId) {
                this.mVideoIds = videoIds;
                this.fetchVideoDetail(false, b);
                return this.mMdxNotificationManager.getNotification(b);
            }
            Log.d("nf_mdx_agent", "MdxAgent: videoIds are all same");
            this.mNotifier.movieMetaDataAvailable(this.mCurrentTargetUuid);
            if (this.mBoxartBitmap != null) {
                this.mMdxNotificationManager.setBoxart(this.mBoxartBitmap);
            }
            if (this.mVideoDetails != null && !this.mVideoIds.episode) {
                this.mMdxNotificationManager.setTitlesNotify(false, this.mVideoDetails.getTitle(), null);
            }
        }
        else {
            Log.d("nf_mdx_agent", "MdxAgent: new videoIds is null");
        }
        return this.mMdxNotificationManager.getNotification(b);
    }
    
    @Override
    public PendingIntent getNoActionIntent() {
        return PendingIntent.getService(this.getContext(), 0, new Intent(), 0);
    }
    
    @Override
    public PendingIntent getPauseIntent() {
        return this.createNotificationButtonIntent(new Intent("com.netflix.mediaclient.intent.action.MDX_PAUSE"));
    }
    
    @Override
    public PendingIntent getPlayNextIntent() {
        if (this.mVideoDetails != null && this.mVideoDetails instanceof EpisodeDetails) {
            final EpisodeDetails episodeDetails = (EpisodeDetails)this.mVideoDetails;
            final Intent intent = new Intent("com.netflix.mediaclient.intent.action.MDX_PLAY_VIDEOIDS");
            final String nextEpisodeId = episodeDetails.getNextEpisodeId();
            final String parentId = this.mVideoDetails.getPlayable().getParentId();
            if (!TextUtils.isEmpty((CharSequence)parentId) && !TextUtils.isEmpty((CharSequence)nextEpisodeId)) {
                intent.putExtra("episodeId", Integer.parseInt(nextEpisodeId));
                intent.putExtra("catalogId", Integer.parseInt(parentId));
                intent.putExtra("playNext", true);
                return this.createNotificationButtonIntent(intent);
            }
        }
        return null;
    }
    
    @Override
    public PendingIntent getResumeIntent() {
        return this.createNotificationButtonIntent(new Intent("com.netflix.mediaclient.intent.action.MDX_RESUME"));
    }
    
    @Override
    public IMdxSharedState getSharedState() {
        if (StringUtils.isNotEmpty(this.mCurrentTargetUuid)) {
            return this.mNotifier.getSharedState(this.mCurrentTargetUuid);
        }
        return null;
    }
    
    @Override
    public PendingIntent getSkipbackIntent(final int n) {
        return this.createNotificationButtonIntent(new Intent("com.netflix.mediaclient.intent.action.MDX_SKIP").putExtra("time", n));
    }
    
    @Override
    public PendingIntent getStopIntent() {
        return this.createNotificationButtonIntent(new Intent("com.netflix.mediaclient.intent.action.MDX_STOP"));
    }
    
    @Override
    public Pair<String, String>[] getTargetList() {
        Pair[] array;
        int n;
        while (true) {
            while (true) {
                Label_0324: {
                    final RemoteDevice remoteDevice;
                    synchronized (this.mTargetMap) {
                        if (this.mTargetMap.isEmpty()) {
                            return null;
                        }
                        array = new Pair[this.mTargetMap.size()];
                        final Iterator<RemoteDevice> iterator = this.mTargetMap.iterator();
                        n = 0;
                        if (!iterator.hasNext()) {
                            break;
                        }
                        remoteDevice = iterator.next();
                        final String uuid = remoteDevice.uuid;
                        final String dialUuid = remoteDevice.dialUuid;
                        final String friendlyName = remoteDevice.friendlyName;
                        if (StringUtils.isNotEmpty(dialUuid)) {
                            final int n2 = n + 1;
                            array[n] = Pair.create((Object)dialUuid, (Object)friendlyName);
                            n = n2;
                            if (Log.isLoggable("nf_mdx_agent", 3)) {
                                Log.d("nf_mdx_agent", "MdxAgent: getTargetList : " + dialUuid + " : " + friendlyName);
                                n = n2;
                                break Label_0324;
                            }
                            break Label_0324;
                        }
                        else if (StringUtils.isNotEmpty(uuid)) {
                            final int n3 = n + 1;
                            array[n] = Pair.create((Object)uuid, (Object)friendlyName);
                            n = n3;
                            if (Log.isLoggable("nf_mdx_agent", 3)) {
                                Log.d("nf_mdx_agent", "MdxAgent: getTargetList : " + uuid + " : " + friendlyName);
                                n = n3;
                                break Label_0324;
                            }
                            break Label_0324;
                        }
                    }
                    Log.e("nf_mdx_agent", "MdxAgent: uuid and dialUuid are invalid " + remoteDevice);
                }
                continue;
            }
        }
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "MdxAgent: getTargetList has " + n + " targets");
        }
        // monitorexit(list)
        return (Pair<String, String>[])array;
    }
    
    @Override
    public VideoDetails getVideoDetail() {
        return this.mVideoDetails;
    }
    
    @Override
    public WebApiUtils.VideoIds getVideoIds() {
        return this.mVideoIds;
    }
    
    public WebApiUtils.VideoIds getVideoIdsPostplay() {
        return this.mVideoIdsPostplay;
    }
    
    public boolean handleCommand(final Intent intent) {
        if (StringUtils.isNotEmpty(this.mCurrentTargetUuid) && !this.mTargetManager.isTargetHaveContext(this.mCurrentTargetUuid)) {
            this.mTargetManager.targetSelected(this.getDeviceFromUuid(this.mCurrentTargetUuid));
        }
        if (intent.hasCategory("com.netflix.mediaclient.intent.category.MDX") && "com.netflix.mediaclient.intent.action.MDX_PLAY_VIDEOIDS".equals(intent.getAction())) {
            final String stringExtra = intent.getStringExtra("uuid");
            if (StringUtils.isEmpty(stringExtra) || !stringExtra.equals(this.mCurrentTargetUuid)) {
                Log.e("nf_mdx_agent", "MdxAgent: MDX_PLAY_VIDEOIDS is for uuid: " + stringExtra + "vs. " + this.mCurrentTargetUuid);
                return true;
            }
            final int intExtra = intent.getIntExtra("catalogId", -1);
            final int intExtra2 = intent.getIntExtra("episodeId", -1);
            int intExtra3;
            if ((intExtra3 = intent.getIntExtra("trackId", -1)) == -1) {
                Log.w("nf_mdx_agent", "MdxAgent: MDX_PLAY_VIDEOIDS has invalid trackId");
                intExtra3 = 13804431;
            }
            final int intExtra4 = intent.getIntExtra("time", -1);
            this.mTrackId = intExtra3;
            this.mStartTime = intExtra4;
            boolean episode = true;
            if (intExtra2 == -1) {
                episode = false;
            }
            if (Log.isLoggable("nf_mdx_agent", 3)) {
                Log.d("nf_mdx_agent", "MdxAgent: PLAYER_PLAY existing: " + this.mVideoIds.episode + ",catalogId: " + this.mVideoIds.catalogId + ",episodeId:" + this.mVideoIds.episodeId);
                Log.d("nf_mdx_agent", "MdxAgent: PLAYER_PLAY request: " + episode + ",catalogId: " + intExtra + ",episodeId:" + intExtra2);
            }
            if (intent.getBooleanExtra("playNext", false)) {
                this.stopAllNotifications();
            }
            if (this.mVideoIds.episode != episode || this.mVideoIds.catalogId != intExtra || (episode && this.mVideoIds.episodeId != intExtra2)) {
                this.mNotifier.commandPlayReceived(this.mCurrentTargetUuid);
                this.mVideoIds.episode = episode;
                this.mVideoIds.catalogId = intExtra;
                this.mVideoIds.episodeId = intExtra2;
                if (this.mBifManager != null) {
                    this.mBifManager.release();
                    this.mBifManager = null;
                }
                this.fetchVideoDetail(true, "com.netflix.mediaclient.intent.action.MDXUPDATE_POSTPLAY".equals(intent.getAction()));
                return true;
            }
            Log.d("nf_mdx_agent", "MdxAgent: videoIds are same, start play");
            this.mTargetManager.playerPlay(this.mCurrentTargetUuid, this.mVideoIds.catalogIdUrl, this.mTrackId, this.mVideoIds.episodeIdUrl, this.mStartTime);
            this.mNotifier.movieMetaDataAvailable(this.mCurrentTargetUuid);
            this.logPlaystart(false);
            return true;
        }
        else {
            if ("com.netflix.mediaclient.intent.action.MDX_SELECT_TARGET".equals(intent.getAction())) {
                final String stringExtra2 = intent.getStringExtra("uuid");
                Log.d("nf_mdx_agent", "MdxAgent: select target " + stringExtra2);
                this.setCurrentTarget(stringExtra2);
                return true;
            }
            if (intent.hasCategory("com.netflix.mediaclient.intent.category.MDXRCC")) {
                Log.d("nf_mdx_agent", "MdxAgent: get nf_mdx_RemoteControlClient intent");
                intent.putExtra("uuid", this.mCurrentTargetUuid);
                if ("com.netflix.mediaclient.intent.action.MDX_TOGGLE_PAUSE".equals(intent.getAction())) {
                    Log.d("nf_mdx_agent", "MdxAgent: get nf_mdx_RemoteControlClient intent toggle pause");
                    if (this.mRemoteControlClientManager != null && AndroidUtils.getAndroidVersion() < 21) {
                        if (this.mRemoteControlClientManager.isPaused()) {
                            intent.setAction("com.netflix.mediaclient.intent.action.MDX_RESUME");
                        }
                        else {
                            intent.setAction("com.netflix.mediaclient.intent.action.MDX_PAUSE");
                        }
                    }
                }
            }
            if ("com.netflix.mediaclient.intent.action.MDX_STOP".equals(intent.getAction())) {
                this.stopAllNotifications();
            }
            this.mCommandHandler.handleCommandIntent(intent);
            return true;
        }
    }
    
    public boolean hasActiveSession() {
        return this.mTargetManager != null && this.mTargetManager.hasActiveSession();
    }
    
    @Override
    public boolean isBifReady() {
        return this.mBifManager != null && this.mBifManager.isBifReady();
    }
    
    @Override
    public boolean isPaused() {
        return this.mRemoteControlClientManager != null && AndroidUtils.getAndroidVersion() < 21 && this.mRemoteControlClientManager.isPaused();
    }
    
    @Override
    public boolean isReady() {
        return this.mReady.get();
    }
    
    @Override
    public boolean isTargetLaunchingOrLaunched() {
        if (this.mTargetManager != null) {
            Log.v("nf_mdx_agent", "checking isTargetLaunchingOrLaunched");
            return this.mTargetManager.isTargetLaunchingOrLaunched(this.mCurrentTargetUuid);
        }
        return false;
    }
    
    void logPlaystart(final boolean b) {
        String s = null;
        if (b) {
            s = "local_playback_transfer";
        }
        this.getService().getClientLogging().getCustomerEventLogging().logMdxPlaybackStart(this.mVideoIds.catalogIdUrl, this.mVideoIds.episodeIdUrl, s, this.mTrackId);
    }
    
    public void notifyIsUserLogin(final boolean mUserIsLogin) {
        while (true) {
            Label_0048: {
                synchronized (this) {
                    if (this.mUserIsLogin != mUserIsLogin) {
                        this.mUserIsLogin = mUserIsLogin;
                        if (!this.mUserIsLogin) {
                            break Label_0048;
                        }
                        this.handleAccountConfig();
                        this.mInitMdxNative.run();
                    }
                    return;
                }
            }
            this.getMainHandler().removeCallbacks(this.mInitMdxNative);
            if (!this.mReady.get()) {
                Log.d("nf_mdx_agent", "notifyIsUserLogin: logout, was not ready ignore");
                return;
            }
            Log.d("nf_mdx_agent", "notifyIsUserLogin: logout, exit native");
            if (this.mCastManager != null) {
                this.mCastManager.stop();
            }
            this.mReady.set(false);
            this.mMdxNativeExitCompleted.set(false);
            this.mMdxController.exit();
            this.removeDiscoveryEventListener();
            this.removePairingEventListener(this.mTargetManager);
            this.removeSessionEventListener(this.mTargetManager);
            this.clearVideoDetails();
            this.sessionGone();
            this.mTargetMap.clear();
            if (this.mNotifier != null) {
                this.mNotifier.notready();
            }
        }
    }
    
    @Override
    public void onBitmapReady(final Bitmap mBoxartBitmap) {
        this.mBoxartBitmap = mBoxartBitmap;
        if (AndroidUtils.getAndroidVersion() < 21 && this.mRemoteControlClientManager != null) {
            this.mRemoteControlClientManager.setBoxart(this.mBoxartBitmap);
        }
        if (this.mMdxNotificationManager != null) {
            this.mMdxNotificationManager.setBoxartNotify(this.mBoxartBitmap);
        }
    }
    
    @Override
    public long onGetTimeOfMostRecentIncomingMessage() {
        if (this.mTargetManager != null) {
            return this.mTargetManager.getTimeOfMostRecentIncomingMessage();
        }
        return 0L;
    }
    
    @Override
    public void onIsReady(final boolean b) {
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "MdxAgent: onIsReady " + b);
        }
    }
    
    @Override
    public void onRemoteDeviceMap(final ArrayList<RemoteDevice> list) {
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "MdxAgent: onRemoteDeviceMap " + list);
        }
        synchronized (this.mTargetMap) {
            this.mTargetMap.clear();
            this.mTargetMap.addAll(list);
            this.reconcileSelectedTargetInfo();
            // monitorexit(this.mTargetMap)
            if (this.mNotifier != null) {
                this.mNotifier.targetList();
            }
        }
    }
    
    @Override
    public void onSessionWatchDogExpired() {
        if (this.mNotifier != null) {
            this.mNotifier.playbackEnd(this.mCurrentTargetUuid, null);
        }
    }
    
    @Override
    public void onSetToNewTarget(final String currentTarget) {
        this.setCurrentTarget(currentTarget);
    }
    
    @Override
    public void onStickinessExpired() {
        this.setCurrentTarget(new String());
        if (this.mNotifier != null) {
            this.mNotifier.targetList();
        }
    }
    
    @Override
    public void onTargetSelectorLoaded(final String mCurrentTargetUuid, final String mTargetUuid, final String mTargetDialUuid, final String mTargetFriendlyName) {
        this.mCurrentTargetUuid = mCurrentTargetUuid;
        this.mTargetUuid = mTargetUuid;
        this.mTargetDialUuid = mTargetDialUuid;
        this.mTargetFriendlyName = mTargetFriendlyName;
        if (this.mCastManager != null && StringUtils.isNotEmpty(this.mCurrentTargetUuid)) {
            this.mCastManager.setTargetId(this.mCurrentTargetUuid);
        }
    }
    
    void registerUserAgentReceiver() {
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(this.mUserAgentReceiver, UserAgentBroadcastIntents.getNotificationIntentFilter());
    }
    
    @Override
    public void setCurrentTarget(final String mCurrentTargetUuid) {
        if (StringUtils.isEmpty(mCurrentTargetUuid)) {
            this.sessionGone();
            this.clearVideoDetails();
            this.getService().getClientLogging().getCustomerEventLogging().logMdxTargetSelection("local playback");
            this.resetTargetSelection();
        }
        else if (!mCurrentTargetUuid.equals(this.mCurrentTargetUuid)) {
            this.clearVideoDetails();
            this.mCurrentTargetUuid = mCurrentTargetUuid;
            final RemoteDevice deviceFromUuid = this.getDeviceFromUuid(this.mCurrentTargetUuid);
            if (deviceFromUuid == null) {
                Log.e("nf_mdx_agent", "MdxAgent: no such device for " + this.mCurrentTargetUuid);
                this.resetTargetSelection();
                return;
            }
            this.mTargetManager.targetSelected(deviceFromUuid);
            this.getService().getClientLogging().getCustomerEventLogging().logMdxTargetSelection("target playback");
            this.mTargetUuid = deviceFromUuid.uuid;
            this.mTargetDialUuid = deviceFromUuid.dialUuid;
            this.mTargetFriendlyName = deviceFromUuid.friendlyName;
            if (this.mTargetSelector != null) {
                this.mTargetSelector.selectNewTarget(this.mCurrentTargetUuid, this.mTargetUuid, this.mTargetDialUuid, this.mTargetFriendlyName);
            }
        }
    }
    
    @Override
    public boolean setDialUuidAsCurrentTarget(final String currentTarget) {
        if (StringUtils.isNotEmpty(currentTarget)) {
            if (currentTarget.equals(this.mCurrentTargetUuid)) {
                return true;
            }
            if (this.getDeviceFromUuid(currentTarget) != null) {
                this.setCurrentTarget(currentTarget);
                return true;
            }
        }
        return false;
    }
    
    public void stopAllNotifications() {
        this.ensureManagers();
        if (AndroidUtils.getAndroidVersion() < 21) {
            this.mRemoteControlClientManager.stop();
        }
        this.mMdxNotificationManager.stopNotification(this.getService());
        this.mMdxNotificationManager.cancelNotification();
        this.mMdxNotificationManager = null;
        this.mBoxartBitmap = null;
        MDXControllerActivity.finishMDXController(this.getContext());
    }
    
    public void stopPostplayNotification() {
        this.ensureManagers();
        this.mMdxNotificationManager.stopPostplayNotification(this.getService());
    }
    
    @Override
    public void switchPlaybackFromTarget(final String s, final int n) {
        if (Log.isLoggable("nf_mdx_agent", 3)) {
            Log.d("nf_mdx_agent", "switchPlaybackFromTarget to " + s + ", @" + n);
        }
        if (this.mSwitchTarget != null) {
            this.mSwitchTarget.startSwitch(this.mCurrentTargetUuid, s, this.mVideoIds, n, this.mTrackId);
        }
        if (StringUtils.isEmpty(s)) {
            this.getService().getClientLogging().getCustomerEventLogging().logMdxTargetSelection("local playback");
            return;
        }
        this.getService().getClientLogging().getCustomerEventLogging().logMdxTargetSelection("target playback");
    }
    
    void unregisterUserAgentReceiver() {
        try {
            LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(this.mUserAgentReceiver);
        }
        catch (Exception ex) {
            Log.i("nf_mdx_agent", "unregisterUserAgenReceiver " + ex);
        }
    }
    
    public void updateMdxNotificationAndLockscreenWithNextSeries(final String s) {
        if (StringUtils.isNotEmpty(s)) {
            this.mVideoIdsPostplay = new WebApiUtils.VideoIds();
            this.mVideoIdsPostplay.episode = true;
            final WebApiUtils.VideoIds mVideoIdsPostplay = this.mVideoIdsPostplay;
            int intValue;
            if (StringUtils.isNumeric(s)) {
                intValue = Integer.valueOf(s);
            }
            else {
                intValue = -1;
            }
            mVideoIdsPostplay.episodeId = intValue;
            this.ensureManagers();
            this.fetchVideoDetail(false, true);
            if (AndroidUtils.getAndroidVersion() < 21) {
                this.mRemoteControlClientManager.start(true, this.mVideoDetailsPostplay, this.mCurrentTargetUuid);
                this.mRemoteControlClientManager.setState(false, false, true);
            }
            this.mMdxNotificationManager.startNotification((Notification)this.getMdxNotification(true).second, this.getService(), true);
            this.mMdxNotificationManager.setUpNextStateNotify(false, false, true);
        }
    }
    
    class DiscoveryEventListener implements EventListener
    {
        @Override
        public void received(final UIEvent uiEvent) {
            if (uiEvent instanceof DeviceFoundEvent) {
                final DeviceFoundEvent deviceFoundEvent = (DeviceFoundEvent)uiEvent;
                final String uuid = deviceFoundEvent.getRemoteDevice().uuid;
                final String dialUuid = deviceFoundEvent.getRemoteDevice().dialUuid;
                if (MdxAgent.this.isSameDevice(uuid, MdxAgent.this.mCurrentTargetUuid) || MdxAgent.this.isSameDevice(dialUuid, MdxAgent.this.mCurrentTargetUuid)) {
                    final RemoteDevice access$2000 = MdxAgent.this.getDeviceFromUuid(MdxAgent.this.mCurrentTargetUuid);
                    if (access$2000 != null) {
                        MdxAgent.this.mTargetManager.targetFound(access$2000);
                    }
                }
                if (MdxAgent.this.mNotifier != null) {
                    MdxAgent.this.mNotifier.targetList();
                }
                MdxAgent.this.getService().getClientLogging().getCustomerEventLogging().logMdxTarget("found", uuid, dialUuid, deviceFoundEvent.getRemoteDevice().serviceType);
            }
            else if (uiEvent instanceof DeviceLostEvent) {
                final String[] devices = ((DeviceLostEvent)uiEvent).getDevices();
                for (int length = devices.length, i = 0; i < length; ++i) {
                    final String s = devices[i];
                    if (MdxAgent.this.isSameDevice(s, MdxAgent.this.mCurrentTargetUuid) || MdxAgent.this.mTargetManager.isTargetHaveContext(s)) {
                        if (MdxAgent.this.mNotifier != null) {
                            MdxAgent.this.mNotifier.error(MdxAgent.this.mCurrentTargetUuid, 200, "device lost");
                        }
                        MdxAgent.this.sessionGone();
                        MdxAgent.this.mMdxNrdpLogger.logDebug("current target device lost");
                    }
                    MdxAgent.this.getService().getClientLogging().getCustomerEventLogging().logMdxTarget("lost", s, null, null);
                }
                if (MdxAgent.this.mNotifier != null) {
                    MdxAgent.this.mNotifier.targetList();
                }
            }
            else if (uiEvent instanceof RemoteDeviceReadyEvent) {
                final RemoteDeviceReadyEvent remoteDeviceReadyEvent = (RemoteDeviceReadyEvent)uiEvent;
                if (MdxAgent.this.isSameDevice(remoteDeviceReadyEvent.getUuid(), MdxAgent.this.mCurrentTargetUuid)) {
                    if (remoteDeviceReadyEvent.getLaunchStatus() == 1) {
                        Log.d("nf_mdx_agent", "MdxAgent: RemoteDeviceReadyEvent, app's launched");
                        MdxAgent.this.mTargetManager.targetLaunched(MdxAgent.this.mCurrentTargetUuid, true);
                        MdxAgent.this.mMdxNrdpLogger.logDebug("current target device launched");
                        return;
                    }
                    Log.d("nf_mdx_agent", "MdxAgent: RemoteDeviceReadyEvent, app's launch failed");
                    MdxAgent.this.mTargetManager.targetLaunched(MdxAgent.this.mCurrentTargetUuid, false);
                    if (MdxAgent.this.mNotifier != null) {
                        final RemoteDevice access$2001 = MdxAgent.this.getDeviceFromUuid(MdxAgent.this.mCurrentTargetUuid);
                        String friendlyName = new String();
                        if (access$2001 != null) {
                            friendlyName = access$2001.friendlyName;
                        }
                        MdxAgent.this.mNotifier.error(MdxAgent.this.mCurrentTargetUuid, 106, friendlyName);
                        MdxAgent.this.mMdxNrdpLogger.logDebug("current target device fails to launched");
                    }
                }
            }
        }
    }
    
    class EpisodeBrowseAgentCallback extends SimpleBrowseAgentCallback
    {
        private final boolean isPostPlay;
        private final boolean triggeredByCommand;
        VideoDetails vidDetails;
        WebApiUtils.VideoIds vidIds;
        
        EpisodeBrowseAgentCallback(final boolean triggeredByCommand, final boolean isPostPlay) {
            this.triggeredByCommand = triggeredByCommand;
            this.isPostPlay = isPostPlay;
        }
        
        private void assignVideoDetails(final VideoDetails vidDetails) {
            this.vidDetails = vidDetails;
            if (!this.isPostPlay) {
                MdxAgent.this.mVideoDetails = vidDetails;
                return;
            }
            MdxAgent.this.mVideoDetailsPostplay = vidDetails;
        }
        
        private void assignVideoIds(final WebApiUtils.VideoIds vidIds) {
            this.vidIds = vidIds;
            if (this.isPostPlay) {
                MdxAgent.this.mVideoIdsPostplay = vidIds;
                return;
            }
            MdxAgent.this.mVideoIds = vidIds;
        }
        
        @Override
        public void onEpisodeDetailsFetched(final EpisodeDetails episodeDetails, final Status status) {
            if (Log.isLoggable("nf_mdx_agent", 2)) {
                Log.v("nf_mdx_agent", "onEpisodeDetailsFetched, res: " + status);
            }
            if (status.isError()) {
                return;
            }
            this.assignVideoDetails(episodeDetails);
            final String highResolutionPortraitBoxArtUrl = episodeDetails.getHighResolutionPortraitBoxArtUrl();
            if (MdxAgent.this.mMdxBoxartLoader != null) {
                MdxAgent.this.mMdxBoxartLoader.fetchImage(highResolutionPortraitBoxArtUrl);
            }
            final String bifUrl = episodeDetails.getBifUrl();
            if (StringUtils.isNotEmpty(bifUrl)) {
                MdxAgent.this.createBifManager(bifUrl);
            }
            MdxAgent.this.mNotifier.movieMetaDataAvailable(MdxAgent.this.mCurrentTargetUuid);
            if (this.triggeredByCommand) {
                this.assignVideoIds(new WebApiUtils.VideoIds(episodeDetails.getPlayable().isPlayableEpisode(), episodeDetails.getEpisodeIdUrl(), episodeDetails.getCatalogIdUrl(), Integer.parseInt(episodeDetails.getId()), Integer.parseInt(episodeDetails.getShowId())));
                MdxAgent.this.mTargetManager.playerPlay(MdxAgent.this.mCurrentTargetUuid, this.vidIds.catalogIdUrl, MdxAgent.this.mTrackId, this.vidIds.episodeIdUrl, MdxAgent.this.mStartTime);
                MdxAgent.this.logPlaystart(false);
            }
            MdxAgent.this.updateMdxRemoteClient(this.isPostPlay);
            MdxAgent.this.updateMdxNotification(true, this.vidDetails.getPlayable().getParentTitle(), MdxAgent.this.getContext().getString(2131493259, new Object[] { this.vidDetails.getPlayable().getSeasonNumber(), this.vidDetails.getPlayable().getEpisodeNumber(), this.vidDetails.getTitle() }), this.isPostPlay);
        }
    }
    
    class MovieBrowseAgentCallback extends SimpleBrowseAgentCallback
    {
        private final boolean triggeredByCommand;
        
        MovieBrowseAgentCallback(final boolean triggeredByCommand) {
            this.triggeredByCommand = triggeredByCommand;
        }
        
        @Override
        public void onMovieDetailsFetched(final MovieDetails movieDetails, final Status status) {
            if (Log.isLoggable("nf_mdx_agent", 2)) {
                Log.v("nf_mdx_agent", "onMovieDetailsFetched, res: " + status);
            }
            if (status.isSucces()) {
                MdxAgent.this.mVideoDetails = movieDetails;
                final String highResolutionPortraitBoxArtUrl = movieDetails.getHighResolutionPortraitBoxArtUrl();
                if (MdxAgent.this.mMdxBoxartLoader != null) {
                    MdxAgent.this.mMdxBoxartLoader.fetchImage(highResolutionPortraitBoxArtUrl);
                }
                final String bifUrl = movieDetails.getBifUrl();
                if (StringUtils.isNotEmpty(bifUrl)) {
                    MdxAgent.this.createBifManager(bifUrl);
                }
                MdxAgent.this.mNotifier.movieMetaDataAvailable(MdxAgent.this.mCurrentTargetUuid);
                if (this.triggeredByCommand) {
                    MdxAgent.this.mVideoIds = new WebApiUtils.VideoIds(movieDetails.getPlayable().isPlayableEpisode(), null, movieDetails.getCatalogIdUrl(), 0, Integer.parseInt(movieDetails.getId()));
                    MdxAgent.this.mTargetManager.playerPlay(MdxAgent.this.mCurrentTargetUuid, MdxAgent.this.mVideoIds.catalogIdUrl, MdxAgent.this.mTrackId, MdxAgent.this.mVideoIds.episodeIdUrl, MdxAgent.this.mStartTime);
                    MdxAgent.this.logPlaystart(false);
                }
                MdxAgent.this.updateMdxNotification(false, MdxAgent.this.mVideoDetails.getTitle(), null, false);
            }
        }
    }
    
    class StateEventListener implements EventListener
    {
        @Override
        public void received(final UIEvent uiEvent) {
            if (uiEvent instanceof InitEvent) {
                MdxAgent.this.mReady.set(true);
                MdxAgent.this.mTargetMap.clear();
                MdxAgent.this.mNotifier.ready();
                if (MdxAgent.this.mCastManager != null) {
                    MdxAgent.this.mCastManager.start();
                }
            }
            else if (uiEvent instanceof InitErrorEvent) {
                MdxAgent.this.mMdxNrdpLogger.logDebug("MDX init error");
                MdxAgent.this.mReady.set(false);
                MdxAgent.this.mMdxNativeExitCompleted.set(true);
                if (MdxAgent.this.mNotifier != null) {
                    MdxAgent.this.mNotifier.error(MdxAgent.this.mCurrentTargetUuid, 103, ((InitErrorEvent)uiEvent).getErrorDesc());
                }
            }
            else if (uiEvent instanceof StateEvent) {
                if (((StateEvent)uiEvent).isReady()) {
                    MdxAgent.this.mMdxNrdpLogger.logDebug("MDX state READY");
                    MdxAgent.this.mReady.set(true);
                    MdxAgent.this.mTargetMap.clear();
                    if (MdxAgent.this.mNotifier != null) {
                        MdxAgent.this.mNotifier.ready();
                    }
                    if (MdxAgent.this.mCastManager != null) {
                        MdxAgent.this.mCastManager.start();
                    }
                }
                else {
                    MdxAgent.this.mMdxNrdpLogger.logDebug("MDX state NOT_READY");
                    MdxAgent.this.mReady.set(false);
                    MdxAgent.this.mMdxNativeExitCompleted.set(true);
                    MdxAgent.this.mTargetMap.clear();
                    if (MdxAgent.this.mNotifier != null) {
                        MdxAgent.this.mNotifier.notready();
                    }
                    MdxAgent.this.sessionGone();
                    if (MdxAgent.this.mCastManager != null) {
                        MdxAgent.this.mCastManager.stop();
                    }
                }
            }
        }
    }
    
    public static class Utils
    {
        public static Intent createIntent(final Activity activity, final String s, final String s2) {
            final Intent intent = new Intent(s);
            intent.setClass((Context)activity, (Class)NetflixService.class);
            intent.addCategory("com.netflix.mediaclient.intent.category.MDX");
            intent.putExtra("uuid", s2);
            return intent;
        }
        
        public static boolean isInPostPlay(final Intent intent) {
            if (intent.hasExtra("postplayState")) {
                final String string = intent.getExtras().getString("postplayState");
                if (!StringUtils.isEmpty(string)) {
                    final MdxPostplayState mdxPostplayState = new MdxPostplayState(string);
                    if (mdxPostplayState.isInCountdown() || mdxPostplayState.isInPrompt()) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        private static boolean isSameAsCurrentlyPlaying(final String s, final String s2, final WebApiUtils.VideoIds videoIds) {
            if (StringUtils.isEmpty(s2) && StringUtils.isNotEmpty(s) && StringUtils.isNumeric(s) && videoIds.catalogId == Integer.valueOf(s)) {
                Log.v("nf_mdx_agent", "same movie");
                return true;
            }
            if (StringUtils.isNotEmpty(s2) && StringUtils.isNumeric(s2) && videoIds.episodeId == Integer.valueOf(s2)) {
                Log.v("nf_mdx_agent", "same show");
                return true;
            }
            return false;
        }
        
        public static boolean playVideo(final NetflixActivity netflixActivity, final Asset asset, final boolean b) {
            if (asset.isEpisode()) {
                Log.d("nf_mdx_agent", "Playing episode");
                return playVideo(netflixActivity, asset.getParentId(), asset.getPlayableId(), asset.getTrackId(), asset.getPlaybackBookmark(), b);
            }
            Log.d("nf_mdx_agent", "Playing movie");
            return playVideo(netflixActivity, asset.getPlayableId(), null, asset.getTrackId(), asset.getPlaybackBookmark(), b);
        }
        
        private static boolean playVideo(final NetflixActivity netflixActivity, final String s, final String s2, final int n, final int n2, final boolean b) {
            if (Log.isLoggable("nf_mdx_agent", 2)) {
                Log.v("nf_mdx_agent", "Starting playback movieId " + s + ", epId " + s2 + ", trackId " + n + ", bookmark " + n2);
            }
            final ServiceManager serviceManager = netflixActivity.getServiceManager();
            if (!ServiceManagerUtils.isMdxAgentAvailable(serviceManager)) {
                Log.w("nf_mdx_agent", "MDX agent not available - can't play video");
            }
            else {
                final WebApiUtils.VideoIds videoIds = serviceManager.getMdx().getVideoIds();
                if (b || videoIds == null || !isSameAsCurrentlyPlaying(s, s2, videoIds)) {
                    final String currentTarget = serviceManager.getMdx().getCurrentTarget();
                    final Intent intent = createIntent(netflixActivity, "com.netflix.mediaclient.intent.action.MDX_PLAY_VIDEOIDS", currentTarget);
                    if (s != null) {
                        intent.putExtra("catalogId", Integer.parseInt(s));
                    }
                    if (s2 != null) {
                        intent.putExtra("episodeId", Integer.parseInt(s2));
                    }
                    intent.putExtra("trackId", n);
                    intent.putExtra("time", n2);
                    netflixActivity.startService(intent);
                    Log.v("nf_mdx_agent", "play done");
                    netflixActivity.startService(createIntent(netflixActivity, "com.netflix.mediaclient.intent.action.MDX_GETCAPABILITY", currentTarget));
                    return true;
                }
            }
            return false;
        }
    }
}
