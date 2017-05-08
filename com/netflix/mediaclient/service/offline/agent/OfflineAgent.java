// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.offline.agent;

import android.content.Intent;
import com.netflix.mediaclient.servicemgr.interface_.offline.OfflinePlayableUiList;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.service.offline.utils.OfflineUtils;
import com.android.volley.Network;
import com.android.volley.Cache;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;
import com.netflix.mediaclient.servicemgr.interface_.user.UserProfile;
import com.netflix.mediaclient.service.browse.BrowseAgentCallback;
import com.netflix.mediaclient.servicemgr.interface_.VideoType;
import com.netflix.mediaclient.service.logging.client.model.Error;
import com.netflix.mediaclient.servicemgr.IClientLogging$ModalView;
import com.netflix.mediaclient.servicemgr.IClientLogging$CompletionReason;
import com.netflix.mediaclient.util.ThreadUtils;
import com.netflix.mediaclient.service.offline.download.OfflinePlayable$PlayableMaintenanceCallBack;
import com.netflix.mediaclient.android.app.NetflixImmutableStatus;
import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.util.log.OfflineLogUtils;
import com.netflix.mediaclient.util.NetflixTransactionIdGenerator;
import com.netflix.mediaclient.servicemgr.interface_.offline.DownloadState;
import android.media.UnsupportedSchemeException;
import android.media.NotProvisionedException;
import com.netflix.mediaclient.service.offline.registry.ChecksumException;
import com.netflix.mediaclient.service.offline.license.OfflineLicenseManagerImpl;
import com.netflix.mediaclient.service.offline.manifest.OfflineManifestManagerImpl;
import com.netflix.mediaclient.service.player.bladerunnerclient.BladeRunnerClient;
import com.netflix.mediaclient.android.app.CommonStatus;
import com.netflix.mediaclient.util.PreferenceUtils;
import com.netflix.mediaclient.servicemgr.interface_.offline.DownloadVideoQuality;
import com.netflix.mediaclient.android.app.NetflixStatus;
import com.netflix.mediaclient.StatusCode;
import com.netflix.mediaclient.util.LogUtils;
import com.netflix.mediaclient.service.offline.download.OfflinePlayableImpl;
import com.netflix.mediaclient.service.offline.utils.OfflinePathUtils;
import com.netflix.mediaclient.service.offline.download.PlayableProgressInfo;
import com.netflix.mediaclient.service.resfetcher.volley.ResourceHttpStack;
import com.android.volley.toolbox.HttpStack;
import android.os.Looper;
import java.util.Iterator;
import com.netflix.mediaclient.service.offline.download.OfflinePlayablePersistentData;
import com.netflix.mediaclient.service.offline.registry.RegistryData;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.service.player.OfflinePlaybackInterface$OfflineManifest;
import com.netflix.mediaclient.servicemgr.interface_.offline.StopReason;
import com.netflix.mediaclient.servicemgr.interface_.offline.OfflinePlayableViewData;
import android.content.Context;
import com.netflix.mediaclient.service.NetflixService;
import com.netflix.mediaclient.android.app.Status;
import java.util.HashMap;
import java.util.ArrayList;
import com.netflix.mediaclient.service.configuration.ConfigurationAgent;
import com.netflix.mediaclient.service.user.UserAgent;
import com.android.volley.RequestQueue;
import io.realm.Realm;
import com.netflix.mediaclient.service.offline.registry.OfflineRegistry;
import com.netflix.mediaclient.servicemgr.interface_.offline.OfflinePlayableUiListImpl;
import com.netflix.mediaclient.service.offline.download.OfflinePlayable;
import java.util.List;
import com.netflix.mediaclient.service.player.OfflinePlaybackInterface$ManifestCallback;
import java.util.Map;
import com.netflix.mediaclient.service.offline.manifest.OfflineManifestManager;
import com.netflix.mediaclient.service.offline.license.OfflineLicenseManager;
import com.netflix.mediaclient.service.offline.download.OfflinePlayableListener;
import com.netflix.mediaclient.service.ServiceAgent$ConfigurationAgentInterface;
import android.os.HandlerThread;
import com.netflix.mediaclient.service.player.OfflinePlaybackInterface;
import com.netflix.mediaclient.service.IntentCommandHandler;
import com.netflix.mediaclient.service.ServiceAgent;

public class OfflineAgent extends ServiceAgent implements IntentCommandHandler, BroadcastReceiverHelper$BroadcastHelperListener, MaintenanceJobHandler$MaintenanceJobHandlerCallback, OfflineAgentInterface, OfflineAgentServiceInterface, OfflinePlaybackInterface
{
    private static final int CW_FETCH_COUNT = 10;
    private static final String TAG = "nf_offlineAgent";
    private static final long VIEW_TIME_TO_QUALIFY_FIRST_VIEW = 30000L;
    private final OfflineAgentListenerHelper mAgentListenerHelper;
    private OfflineAgent$BackGroundHandler mBackGroundHandler;
    private HandlerThread mBackgroundThread;
    private BroadcastReceiverHelper mBroadcastReceiverHelper;
    private final ServiceAgent$ConfigurationAgentInterface mConfigurationAgent;
    private DownloadController mDownloadController;
    private final DownloadController$DownloadControllerListener mDownloadControllerListener;
    private DownloadNotificationManager mDownloadNotificationManager;
    private final OfflinePlayableListener mMainThreadOfflinePlayableListener;
    private MaintenanceJobHandler mMaintenanceJobHandler;
    private OfflineAgentInterface$OfflineFeatureAvailability mOfflineFeatureAvailability;
    private OfflineLicenseManager mOfflineLicenseManager;
    private OfflineManifestManager mOfflineManifestManager;
    private OfflineNrdpLogger mOfflineNrdpLogger;
    private final Map<Long, OfflinePlaybackInterface$ManifestCallback> mOfflinePlayManifestRequestMap;
    private final List<OfflinePlayable> mOfflinePlayableList;
    private final OfflinePlayableUiListImpl mOfflinePlayableUiList;
    private OfflineRegistry mOfflineRegistry;
    private String mPlayableIdInFlight;
    private Realm mRealm;
    private boolean mRegistryDirty;
    private RequestQueue mRequestQueue;
    private boolean mSkipAdultContent;
    private final UserAgent mUserAgent;
    
    public OfflineAgent(final ConfigurationAgent mConfigurationAgent, final UserAgent mUserAgent) {
        this.mOfflinePlayableList = new ArrayList<OfflinePlayable>();
        this.mOfflinePlayableUiList = new OfflinePlayableUiListImpl();
        this.mRegistryDirty = false;
        this.mOfflinePlayManifestRequestMap = new HashMap<Long, OfflinePlaybackInterface$ManifestCallback>();
        this.mOfflineFeatureAvailability = OfflineAgentInterface$OfflineFeatureAvailability.NOT_INITIALIZED;
        this.mAgentListenerHelper = new OfflineAgentListenerHelper();
        this.mMainThreadOfflinePlayableListener = new OfflineAgent$1(this);
        this.mDownloadControllerListener = new OfflineAgent$15(this);
        this.mConfigurationAgent = mConfigurationAgent;
        this.mUserAgent = mUserAgent;
    }
    
    private void addRequestToHandler(final int n) {
        this.mBackGroundHandler.obtainMessage(n).sendToTarget();
    }
    
    private void addRequestToHandler(final int n, final String s) {
        this.mBackGroundHandler.obtainMessage(n, (Object)s).sendToTarget();
    }
    
    private boolean buildFalkorDataAndPlayableListFromPersistentStore() {
        Log.i("nf_offlineAgent", "buildFalkorDataAndPlayableListFromPersistentStore");
        final long currentTimeMillis = System.currentTimeMillis();
        this.mOfflineRegistry = OfflineRegistry.create(this.getContext());
        if (this.mOfflineRegistry == null) {
            Log.e("nf_offlineAgent", "can't create OfflineRegistry");
            return false;
        }
        if (this.mOfflineRegistry.getGeoCountryCode() == null) {
            this.mOfflineRegistry.setGeoCountryCode(this.mConfigurationAgent.getGeoCountryCode());
        }
        if (this.mOfflineRegistry.hasAtLeastOnePlayable()) {
            this.startQueueIfRequired();
        }
        this.mOfflinePlayableList.clear();
        for (final RegistryData registryData : this.mOfflineRegistry.getRegistryList()) {
            final Iterator<OfflinePlayablePersistentData> iterator2 = registryData.mOfflinePlayablePersistentDataList.iterator();
            while (iterator2.hasNext()) {
                this.mOfflinePlayableList.add(this.createOfflineViewable(registryData.mOfflineRootStorageDirPath, iterator2.next()));
            }
        }
        this.refreshUIData();
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "buildFalkorDataAndPlayableListFromPersistentStore took=" + (System.currentTimeMillis() - currentTimeMillis));
        }
        return true;
    }
    
    private void buildNewUiList() {
        final HashMap<String, OfflinePlayable> hashMap = new HashMap<String, OfflinePlayable>();
        for (final OfflinePlayable offlinePlayable : this.mOfflinePlayableList) {
            hashMap.put(offlinePlayable.getPlayableId(), offlinePlayable);
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i("nf_offlineAgent", "buildNewUiList already in main regenerate");
            this.mOfflinePlayableUiList.regenerate(this.mRealm, (Map<String, OfflinePlayableViewData>)hashMap, this.mSkipAdultContent);
            return;
        }
        this.getMainHandler().post((Runnable)new OfflineAgent$9(this, hashMap));
    }
    
    private HttpStack createHttpStack() {
        return new ResourceHttpStack(this.getConfigurationAgent());
    }
    
    private OfflinePlayable createOfflineViewable(final String s, final OfflinePlayablePersistentData offlinePlayablePersistentData) {
        return new OfflinePlayableImpl(this.getContext(), offlinePlayablePersistentData, new PlayableProgressInfo(), OfflinePathUtils.getDirectoryPathForViewable(s, offlinePlayablePersistentData.mPlayableId), this.mRequestQueue, this.mOfflineManifestManager, this.mOfflineLicenseManager, this.mMainThreadOfflinePlayableListener, this.mBackgroundThread, this.mOfflineNrdpLogger, this.getService().getClientLogging());
    }
    
    private void doSaveToRegistryInBGThread(final Context context) {
        if (context != null) {
            if (!this.mOfflineRegistry.persistRegistry()) {
                Log.e("nf_offlineAgent", "doSaveToRegistryInBGThread can't persist registry");
                LogUtils.reportErrorSafely("persistRegistry failed", null);
                this.sendError(new NetflixStatus(StatusCode.DL_CANT_PERSIST_REGISTRY));
            }
            return;
        }
        Log.e("nf_offlineAgent", "doSaveToRegistryInBGThread context is null");
    }
    
    private Realm getRealm() {
        return this.mRealm;
    }
    
    private DownloadVideoQuality getVideoQualityFromPref() {
        return DownloadVideoQuality.create(PreferenceUtils.getStringPref(this.getContext(), "download_video_quality", DownloadVideoQuality.DEFAULT.getValue()));
    }
    
    private void handleAgentDestroyRequest() {
        Log.i("nf_offlineAgent", "handleAgentDestroyRequest");
        if (this.mBroadcastReceiverHelper != null) {
            this.mBroadcastReceiverHelper.destroy();
        }
        if (this.mOfflineLicenseManager != null) {
            this.mOfflineLicenseManager.destroy();
        }
        if (this.mDownloadController != null) {
            this.mDownloadController.destroy();
        }
        this.stopAllDownloadsAndPersistRegistry(StopReason.WaitingToBeStarted);
        if (this.mRequestQueue != null) {
            Log.i("nf_offlineAgent", "Stopping Volley RequestQueue");
            this.mRequestQueue.stop();
            this.mRequestQueue = null;
        }
        final Iterator<OfflinePlayable> iterator = this.mOfflinePlayableList.iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
        }
        this.mOfflinePlayableList.clear();
        this.stopBackgroundThread();
        super.destroy();
        Log.i("nf_offlineAgent", "destroyInBgThread done");
    }
    
    private void handleAgentInitRequest() {
        if (!this.getService().getMSLClient().enabled()) {
            this.mOfflineFeatureAvailability = OfflineAgentInterface$OfflineFeatureAvailability.NA_MSL_CLIENT_DISABLED;
            this.initCompleted(CommonStatus.OK);
            return;
        }
        if (this.mConfigurationAgent.getOfflineConfig().isOfflineFeatureDisabled()) {
            this.mOfflineFeatureAvailability = OfflineAgentInterface$OfflineFeatureAvailability.NA_DISABLED_FROM_END_POINT;
            this.initCompleted(CommonStatus.OK);
            return;
        }
        this.mOfflineNrdpLogger = new OfflineNrdpLoggerNoOp(this.getNrdController().getNrdp());
        this.mAgentListenerHelper.setNetflixPowerManager(this.getService().getNetflixPowerManager());
        final BladeRunnerClient bladeRunnerClient = new BladeRunnerClient(this.getContext(), this.getMSLClient(), this.getConfigurationAgent(), this.getUserAgent());
        this.mOfflineManifestManager = new OfflineManifestManagerImpl(bladeRunnerClient, this.mBackgroundThread, this.getPdsAgentForDownload(), this.getLoggingAgent());
        try {
            this.mOfflineLicenseManager = new OfflineLicenseManagerImpl(this.mBackgroundThread.getLooper(), bladeRunnerClient);
            try {
                if (!this.buildFalkorDataAndPlayableListFromPersistentStore()) {
                    this.initCompleted(new NetflixStatus(StatusCode.OK));
                    this.mOfflineFeatureAvailability = OfflineAgentInterface$OfflineFeatureAvailability.NA_OFFLINE_STORAGE_NOT_AVAILABLE;
                    return;
                }
                goto Label_0240;
            }
            catch (ChecksumException ex) {
                if (Log.isLoggable()) {
                    Log.e("nf_offlineAgent", ex, "ChecksumException", new Object[0]);
                }
                this.initCompleted(new NetflixStatus(StatusCode.DL_REGISTRY_CHECKSUM_FAILED));
            }
        }
        catch (NotProvisionedException ex2) {}
        catch (UnsupportedSchemeException ex3) {
            goto Label_0225;
        }
    }
    
    private void handleCreateRequest(final OfflineAgent$CreateRequest offlineAgent$CreateRequest) {
        final String mPlayableId = offlineAgent$CreateRequest.mPlayableId;
        final PlayContext mPlayContext = offlineAgent$CreateRequest.mPlayContext;
        final String currentProfileGuid = this.mUserAgent.getCurrentProfileGuid();
        this.startQueueIfRequired();
        OfflinePlayable offlineViewableByPlayableId;
        final OfflinePlayable offlinePlayable = offlineViewableByPlayableId = OfflineAgentHelper.getOfflineViewableByPlayableId(mPlayableId, this.mOfflinePlayableList);
        if (offlinePlayable != null) {
            offlineViewableByPlayableId = offlinePlayable;
            if (offlinePlayable.getDownloadState() == DownloadState.CreateFailed) {
                Log.i("nf_offlineAgent", "handleCreateRequest removing CreateFailed item");
                this.mOfflinePlayableList.remove(offlinePlayable);
                this.mOfflineRegistry.removePlayable(offlinePlayable.getOfflineViewablePersistentData(), false);
                offlineViewableByPlayableId = null;
            }
        }
        if (offlineViewableByPlayableId == null) {
            if (Log.isLoggable()) {
                Log.i("nf_offlineAgent", "handleCreateRequest creating new item " + mPlayableId);
            }
            final OfflinePlayablePersistentData offlineContentPersistentData = OfflinePlayablePersistentData.createOfflineContentPersistentData(mPlayableId, mPlayContext, NetflixTransactionIdGenerator.generateOxId(), currentProfileGuid, this.getVideoQualityFromPref().getValue());
            offlineContentPersistentData.mDxId = NetflixTransactionIdGenerator.generateDxId();
            OfflineLogUtils.reportAddCachedVideoStart(this.getContext(), offlineContentPersistentData.mOxId);
            final OfflinePlayable offlineViewable = this.createOfflineViewable(this.mOfflineRegistry.getCurrentOfflineStorageDirPath(), offlineContentPersistentData);
            this.mOfflineRegistry.addToCurrentRegistryData(offlineContentPersistentData);
            this.mOfflinePlayableList.add(offlineViewable);
            this.processNextCreateRequest();
            return;
        }
        Log.e("nf_offlineAgent", "handleCreateRequest already requested");
        this.sendResponseForCreate(mPlayableId, new NetflixStatus(StatusCode.DL_TITTLE_ALREADY_REQUESTED_FOR_DOWNLOAD));
    }
    
    private void handleDeleteAllRequest(final boolean b) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "handleDeleteAllRequest " + b);
        }
        NetflixImmutableStatus ok = CommonStatus.OK;
        for (final OfflinePlayable offlinePlayable : this.mOfflinePlayableList) {
            if (offlinePlayable.getPlayableId().equals(this.mPlayableIdInFlight)) {
                Log.i("nf_offlineAgent", "handleDeleteRequest not deleting in-flight item");
            }
            else {
                OfflineLogUtils.reportRemoveCachedVideoStart(this.getContext(), offlinePlayable.getOfflineViewablePersistentData().mOxId);
                this.mOfflineRegistry.removePlayable(offlinePlayable.getOfflineViewablePersistentData(), true);
                final Status deleteDownload = offlinePlayable.deleteDownload();
                if (deleteDownload.isError()) {
                    if (Log.isLoggable()) {
                        Log.e("nf_offlineAgent", "handleDeleteAllRequest can't delete playableId=" + offlinePlayable.getPlayableId() + " got error=" + deleteDownload);
                    }
                    ok = (NetflixImmutableStatus)deleteDownload;
                }
                this.reportDeleteConsolidatedLogging(deleteDownload, offlinePlayable);
            }
        }
        this.mOfflineRegistry.setPrimaryProfileGuid("");
        this.mOfflinePlayableList.clear();
        this.mDownloadController.onAllPlayableDeleted();
        if (b) {
            this.mOfflineRegistry.deleteDeletedList();
        }
        this.getMainHandler().post((Runnable)new OfflineAgent$6(this));
        this.saveToRegistry();
        this.refreshUIData();
        this.sendAllDeleted(ok);
    }
    
    private void handleDeleteRequest(final String s) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "handleDeleteRequest playableId=" + s);
        }
        final OfflinePlayable offlineViewableByPlayableId = OfflineAgentHelper.getOfflineViewableByPlayableId(s, this.mOfflinePlayableList);
        if (offlineViewableByPlayableId == null) {
            Log.e("nf_offlineAgent", "handleDeleteRequest offlinePlayable not found");
            return;
        }
        if (offlineViewableByPlayableId.getPlayableId().equals(this.mPlayableIdInFlight)) {
            Log.i("nf_offlineAgent", "handleDeleteRequest not deleting in-flight item");
            this.sendDownloadDeleted(s, new NetflixStatus(StatusCode.DL_BUSY_TRY_DELETE_AGAIN));
            return;
        }
        OfflineLogUtils.reportRemoveCachedVideoStart(this.getContext(), offlineViewableByPlayableId.getOfflineViewablePersistentData().mOxId);
        this.mOfflinePlayableList.remove(offlineViewableByPlayableId);
        this.mOfflineRegistry.removePlayable(offlineViewableByPlayableId.getOfflineViewablePersistentData(), true);
        final Status deleteDownload = offlineViewableByPlayableId.deleteDownload();
        this.reportDeleteConsolidatedLogging(deleteDownload, offlineViewableByPlayableId);
        this.mDownloadController.onDeleted(s);
        this.saveToRegistry();
        this.buildNewUiList();
        this.sendDownloadDeleted(s, deleteDownload);
        this.startDownloadIfAllowed();
        this.getMainHandler().post((Runnable)new OfflineAgent$5(this, s));
    }
    
    private void handleDownloadMaintenanceJob() {
        Log.i("nf_offlineAgent", "handleDownloadMaintenanceJob");
        if (this.mMaintenanceJobHandler != null) {
            this.mMaintenanceJobHandler.terminate();
        }
        (this.mMaintenanceJobHandler = new MaintenanceJobHandler(this, this.mOfflineLicenseManager, this.mOfflinePlayableList, this.mOfflineRegistry)).startMaintenanceJob();
    }
    
    private void handleDownloadResumeJob() {
        Log.i("nf_offlineAgent", "handleDownloadResumeJob");
        this.startDownloadIfAllowed();
        this.mDownloadController.onDownloadResumeJobDone();
    }
    
    private void handleGeoPlayabilityUpdated(final Map<String, Boolean> map) {
        Log.i("nf_offlineAgent", "handleGeoPlayabilityUpdated");
        OfflineAgentHelper.applyGeoPlayabilityFlags(map, this.mOfflinePlayableList);
        this.mOfflineRegistry.setGeoCountryCode(this.mConfigurationAgent.getGeoCountryCode());
        this.saveToRegistry();
    }
    
    private void handleLicenseRefreshForAll() {
        final Iterator<OfflinePlayable> iterator = this.mOfflinePlayableList.iterator();
        while (iterator.hasNext()) {
            iterator.next().doMaintenanceWork(null);
        }
    }
    
    private void handleMayBeNewUser() {
        ThreadUtils.assertNotOnMain();
        if (!this.isOfflineFeatureDisabled()) {
            Log.i("nf_offlineAgent", "handleMayBeNewUser");
            if (OfflineAgentHelper.hasPrimaryProfileGuidChanged(this.mUserAgent, this.mOfflineRegistry)) {
                this.handleDeleteAllRequest(true);
            }
        }
    }
    
    private void handleOfflinePlaybackStart30Second(final Long n) {
        final OfflinePlayable offlineViewableByPlayableId = OfflineAgentHelper.getOfflineViewableByPlayableId(n.toString(), this.mOfflinePlayableList);
        if (offlineViewableByPlayableId != null) {
            offlineViewableByPlayableId.notifyOfflinePlayStarted();
        }
    }
    
    private void handlePauseRequest(final String s) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "handlePauseRequest playableId=" + s);
        }
        final OfflinePlayable offlineViewableByPlayableId = OfflineAgentHelper.getOfflineViewableByPlayableId(s, this.mOfflinePlayableList);
        if (offlineViewableByPlayableId != null) {
            if (offlineViewableByPlayableId.getDownloadState() != DownloadState.Complete) {
                offlineViewableByPlayableId.stopDownload(StopReason.StoppedFromAgentAPI);
                this.saveToRegistry();
                this.sendDownloadStopped(offlineViewableByPlayableId);
            }
            else if (Log.isLoggable()) {
                Log.e("nf_offlineAgent", "handlePauseRequest trying to pause a completed item" + s);
            }
        }
        else if (Log.isLoggable()) {
            Log.e("nf_offlineAgent", "handlePauseRequest playableId not found " + s);
        }
    }
    
    private void handleRequestForGeoPlayability() {
        Log.i("nf_offlineAgent", "handleRequestForGeoPlayability");
        this.sendGeoPlayabilityRequest();
    }
    
    private void handleResumeRequest(final String s, final boolean b) {
        this.saveToRegistry();
        final OfflinePlayable offlineViewableByPlayableId = OfflineAgentHelper.getOfflineViewableByPlayableId(s, this.mOfflinePlayableList);
        if (offlineViewableByPlayableId == null) {
            Log.e("nf_offlineAgent", "handleResumeRequest not found playableId=%s", s);
        }
        else if (offlineViewableByPlayableId.getDownloadState() != DownloadState.Stopped) {
            if (Log.isLoggable()) {
                Log.e("nf_offlineAgent", "handleResumeRequest not stopped, state=" + offlineViewableByPlayableId.getDownloadState());
            }
        }
        else {
            offlineViewableByPlayableId.getOfflineViewablePersistentData().setDownloadStateStopped(StopReason.WaitingToBeStarted);
            if (!this.mDownloadController.canThisPlayableBeResumedByUser(offlineViewableByPlayableId)) {
                Log.i("nf_offlineAgent", "handleResumeRequest not starting playableId=%s", s);
                this.sendDownloadStopped(offlineViewableByPlayableId);
                return;
            }
            Log.i("nf_offlineAgent", "handleResumeRequest starting playableId=%s", s);
            offlineViewableByPlayableId.startDownload();
            if (b) {
                this.sendDownloadResumedByUser(offlineViewableByPlayableId);
            }
        }
    }
    
    private void handleThreadException(final Thread thread, final Throwable t) {
        if (Log.isLoggable()) {
            Log.e("nf_offlineAgent", t, "uncaughtException threadName=" + Thread.currentThread().getName(), new Object[0]);
        }
        while (true) {
            try {
                Log.e("nf_offlineAgent", "uncaughtException stopping all downloads");
                this.mBackGroundHandler.removeCallbacksAndMessages((Object)null);
                final boolean stopAllDownloads = this.stopAllDownloads(StopReason.WaitingToBeStarted);
                if (Log.isLoggable()) {
                    Log.e("nf_offlineAgent", "wasAnythingStopped=" + stopAllDownloads);
                }
                this.mDownloadController.onThreadException();
                Log.e("nf_offlineAgent", "uncaughtException stopped all downloads");
                Log.e("nf_offlineAgent", "passing to defaultExceptionHandler");
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, t);
            }
            catch (Exception ex) {
                Log.e("nf_offlineAgent", ex, "setUncaughtExceptionHandler error stopping downloads", new Object[0]);
                continue;
            }
            break;
        }
    }
    
    private boolean isOfflineFeatureDisabled() {
        return this.mOfflineFeatureAvailability != OfflineAgentInterface$OfflineFeatureAvailability.AVAILABLE || this.mConfigurationAgent.getOfflineConfig().isOfflineFeatureDisabled();
    }
    
    private void onDownloadPauseOrResumeByUser(final boolean b) {
        this.mOfflineRegistry.setDownloadsPausedByUser(b);
        this.mDownloadController.setDownloadsAreStoppedByUser(b);
    }
    
    private void onGeoPlayabilityUpdated(final Map<String, Boolean> map) {
        Log.i("nf_offlineAgent", "onGeoPlayabilityUpdated");
        if (this.isOfflineFeatureDisabled()) {
            return;
        }
        this.mBackGroundHandler.post((Runnable)new OfflineAgent$4(this, map));
    }
    
    private void processNextCreateRequest() {
        Log.i("nf_offlineAgent", "processNextCreateRequest");
        if (this.mPlayableIdInFlight == null) {
            final OfflinePlayable nextCreatingStatePlayable = OfflineAgentHelper.findNextCreatingStatePlayable(this.mOfflinePlayableList);
            if (nextCreatingStatePlayable != null) {
                if (Log.isLoggable()) {
                    Log.i("nf_offlineAgent", "processNextCreateRequest processing playableId=" + nextCreatingStatePlayable.getPlayableId());
                }
                this.mPlayableIdInFlight = nextCreatingStatePlayable.getPlayableId();
                if (!this.mUserAgent.isUserLoggedIn()) {
                    Log.e("nf_offlineAgent", "handleCreateRequest use not logged-in");
                    this.sendResponseForCreate(this.mPlayableIdInFlight, new NetflixStatus(StatusCode.DL_USER_NOT_LOGGED_IN));
                }
                else {
                    if (!OfflineAgentHelper.ensureEnoughDiskSpaceForNewRequest(this.mOfflineRegistry.getCurrentOfflineStorageDirPath(), this.mOfflinePlayableList)) {
                        Log.e("nf_offlineAgent", "handleCreateRequest not enough space");
                        this.sendResponseForCreate(this.mPlayableIdInFlight, new NetflixStatus(StatusCode.DL_NOT_ENOUGH_FREE_SPACE));
                        return;
                    }
                    nextCreatingStatePlayable.initialize();
                }
            }
        }
        else if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "processNextCreateRequest already processing, mPlayableIdInFlight=" + this.mPlayableIdInFlight);
        }
    }
    
    private void reportDeleteConsolidatedLogging(final Status status, final OfflinePlayable offlinePlayable) {
        final String mOxId = offlinePlayable.getOfflineViewablePersistentData().mOxId;
        if (offlinePlayable.getDownloadState() != DownloadState.Complete) {
            OfflineLogUtils.reportDownloadEnded(this.getContext(), mOxId, null, IClientLogging$CompletionReason.canceled, status.getError());
        }
        if (status.isError()) {
            OfflineLogUtils.reportRemoveCachedVideoEnded(this.getContext(), mOxId, null, IClientLogging$CompletionReason.failed, status.getError());
            return;
        }
        OfflineLogUtils.reportRemoveCachedVideoEnded(this.getContext(), mOxId, null, IClientLogging$CompletionReason.success, null);
    }
    
    private void saveToRegistry() {
        Log.i("nf_offlineAgent", "saveToRegistry");
        this.mRegistryDirty = true;
        this.mBackGroundHandler.post((Runnable)new OfflineAgent$13(this));
    }
    
    private void sendAllDeleted(final Status status) {
        this.mAgentListenerHelper.onAllPlayablesDeleted(this.getMainHandler(), status);
    }
    
    private void sendDownloadCompleted(final OfflinePlayableViewData offlinePlayableViewData) {
        this.mAgentListenerHelper.onDownloadCompleted(this.getMainHandler(), offlinePlayableViewData);
    }
    
    private void sendDownloadDeleted(final String s, final Status status) {
        this.mAgentListenerHelper.onOfflinePlayableDeleted(this.getMainHandler(), s, status);
    }
    
    private void sendDownloadResumedByUser(final OfflinePlayable offlinePlayable) {
        this.mAgentListenerHelper.onDownloadResumedByUser(this.getMainHandler(), offlinePlayable);
    }
    
    private void sendDownloadStopped(final OfflinePlayableViewData offlinePlayableViewData) {
        this.mAgentListenerHelper.onDownloadStopped(this.getMainHandler(), offlinePlayableViewData, offlinePlayableViewData.getStopReason());
    }
    
    private void sendError(final Status status) {
        this.mAgentListenerHelper.onError(this.getMainHandler(), status);
    }
    
    private void sendGeoPlayabilityRequest() {
        DownloadGeoPlayabilityHelper.sendGeoPlayabilityRequest(OfflineAgentHelper.getCompletedVideoIds(this.mOfflinePlayableList), this.getBrowseAgent(), new OfflineAgent$3(this));
    }
    
    private void sendLicenseRefreshDone(final OfflinePlayable offlinePlayable, final Status status) {
        this.mAgentListenerHelper.onLicenseRefreshDone(this.getMainHandler(), offlinePlayable, status);
    }
    
    private void sendOfflineManifestFromMainThread(final OfflinePlaybackInterface$OfflineManifest offlinePlaybackInterface$OfflineManifest, final OfflinePlaybackInterface$ManifestCallback offlinePlaybackInterface$ManifestCallback, final long n, final Status status) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "sendOfflineManifestFromMainThread offlineManifest=" + offlinePlaybackInterface$OfflineManifest + "status=" + status);
        }
        this.getMainHandler().post((Runnable)new OfflineAgent$18(this, offlinePlaybackInterface$OfflineManifest, offlinePlaybackInterface$ManifestCallback, n, status));
    }
    
    private void sendPlayWindowRenewDone(final OfflinePlayable offlinePlayable, final Status status) {
        this.mAgentListenerHelper.onPlayWindowRenewDone(this.getMainHandler(), offlinePlayable, status);
    }
    
    private void sendProgress(final OfflinePlayableViewData offlinePlayableViewData, final int n) {
        this.mAgentListenerHelper.onOfflinePlayableProgress(this.getMainHandler(), offlinePlayableViewData, n);
    }
    
    private void sendResponseForCreate(final String s, final Status persistentError) {
        Log.i("nf_offlineAgent", "sendResponseForCreate");
        this.mPlayableIdInFlight = null;
        final OfflinePlayable offlineViewableByPlayableId = OfflineAgentHelper.getOfflineViewableByPlayableId(s, this.mOfflinePlayableList);
        if (offlineViewableByPlayableId == null) {
            if (Log.isLoggable()) {
                Log.e("nf_offlineAgent", "sendResponseForCreate not found playableId=" + s);
            }
        }
        else {
            if (persistentError.isSucces()) {
                OfflineLogUtils.reportAddCachedVideoEnded(this.getContext(), offlineViewableByPlayableId.getOfflineViewablePersistentData().mOxId, IClientLogging$ModalView.addCachedVideoButton, IClientLogging$CompletionReason.success, null);
                offlineViewableByPlayableId.getOfflineViewablePersistentData().setDownloadStateStopped(StopReason.WaitingToBeStarted);
            }
            else {
                offlineViewableByPlayableId.getOfflineViewablePersistentData().setPersistentError(persistentError);
                OfflineLogUtils.reportAddCachedVideoEnded(this.getContext(), offlineViewableByPlayableId.getOfflineViewablePersistentData().mOxId, IClientLogging$ModalView.addCachedVideoButton, IClientLogging$CompletionReason.failed, persistentError.getError());
                offlineViewableByPlayableId.getOfflineViewablePersistentData().setCreateFailedState();
            }
            this.doSaveToRegistryInBGThread(this.getContext());
            this.buildNewUiList();
            this.mAgentListenerHelper.onCreateRequestResponse(this.getMainHandler(), s, persistentError);
            this.processNextCreateRequest();
            if (persistentError.isSucces()) {
                this.handleResumeRequest(s, false);
            }
        }
    }
    
    private void serializeMetadataToDisc(final String s, final VideoType videoType) {
        final UserProfile currentProfile = this.getService().getCurrentProfile();
        if (videoType == VideoType.MOVIE) {
            this.getBrowseAgent().fetchMovieDetails(s, null, new OfflineAgent$19(this, currentProfile, s));
        }
        else if (videoType == VideoType.EPISODE) {
            this.getBrowseAgent().fetchEpisodeDetails(s, null, new OfflineAgent$20(this, currentProfile, s));
        }
    }
    
    private void startBackgroundThread() {
        (this.mBackgroundThread = new HandlerThread("nf_of_bg", 10)).start();
        this.mBackGroundHandler = new OfflineAgent$BackGroundHandler(this, this.mBackgroundThread.getLooper());
    }
    
    private void startDownloadIfAllowed() {
        if (this.mUserAgent.isUserLoggedIn()) {
            final OfflinePlayable nextPlayableForDownload = this.mDownloadController.getNextPlayableForDownload();
            if (nextPlayableForDownload != null) {
                if (Log.isLoggable()) {
                    Log.i("nf_offlineAgent", "starting the download for " + nextPlayableForDownload.getPlayableId());
                }
                nextPlayableForDownload.startDownload();
            }
            else if (Log.isLoggable()) {
                Log.i("nf_offlineAgent", "no downloadable item found, count=" + this.mOfflinePlayableList.size());
            }
            return;
        }
        Log.e("nf_offlineAgent", "startDownloadIfAllowed user is not logged in");
    }
    
    private void startQueueIfRequired() {
        if (this.mRequestQueue != null) {
            return;
        }
        final int downloadAgentThreadPoolSize = this.mConfigurationAgent.getDownloadAgentThreadPoolSize();
        if (Log.isLoggable()) {
            Log.d("nf_offlineAgent", "Creating Volley RequestQueue with threadPoolSize of " + downloadAgentThreadPoolSize);
        }
        (this.mRequestQueue = new RequestQueue(new NoCache(), new BasicNetwork(this.createHttpStack()), downloadAgentThreadPoolSize)).start();
    }
    
    private boolean stopAllDownloads(final StopReason stopReason) {
        final Iterator<OfflinePlayable> iterator = this.mOfflinePlayableList.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final OfflinePlayable offlinePlayable = iterator.next();
            if (OfflineUtils.isDownloading(offlinePlayable)) {
                offlinePlayable.stopDownload(stopReason);
                this.sendDownloadStopped(offlinePlayable);
                b = true;
            }
        }
        return b;
    }
    
    private void stopAllDownloadsAndPersistRegistry(final StopReason stopReason) {
        if (this.stopAllDownloads(stopReason)) {
            Log.i("nf_offlineAgent", "stopAllDownloadsAndPersistRegistry something was stopped");
            this.doSaveToRegistryInBGThread(this.getContext());
        }
    }
    
    private void stopBackgroundThread() {
        if (this.mBackGroundHandler != null) {
            this.mBackGroundHandler.removeCallbacksAndMessages((Object)null);
            this.mBackGroundHandler = null;
        }
        if (this.mBackgroundThread != null) {
            final HandlerThread mBackgroundThread = this.mBackgroundThread;
            this.mBackgroundThread = null;
            mBackgroundThread.quit();
        }
    }
    
    private void updatePrimaryProfileGuidIfMissing() {
        final String primaryProfileGuid = this.mUserAgent.getPrimaryProfileGuid();
        final String primaryProfileGuid2 = this.mOfflineRegistry.getPrimaryProfileGuid();
        if (StringUtils.isNotEmpty(primaryProfileGuid) && StringUtils.isEmpty(primaryProfileGuid2)) {
            this.mOfflineRegistry.setPrimaryProfileGuid(primaryProfileGuid);
            if (Log.isLoggable()) {
                Log.i("nf_offlineAgent", "updatePrimaryProfileGuidIfMissing " + primaryProfileGuid);
            }
        }
    }
    
    @Override
    public void abortManifestRequest(final long n) {
        if (this.isReady()) {
            this.mBackGroundHandler.post((Runnable)new OfflineAgent$17(this, n));
        }
        else if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "abortManifestRequest OfflineAgent not ready error movieId=" + n);
        }
    }
    
    @Override
    public void addOfflineAgentListener(final OfflineAgentListener offlineAgentListener) {
        ThreadUtils.assertOnMain();
        this.mAgentListenerHelper.addOfflineAgentListener(this.getMainHandler(), offlineAgentListener);
    }
    
    @Override
    public void deleteAllOfflineContent() {
        ThreadUtils.assertOnMain();
        this.addRequestToHandler(8);
    }
    
    @Override
    public void deleteOfflinePlayable(final String s) {
        ThreadUtils.assertOnMain();
        this.onDownloadPauseOrResumeByUser(false);
        this.addRequestToHandler(2, s);
    }
    
    @Override
    public void destroy() {
        Log.i("nf_offlineAgent", "destroy");
        this.mOfflineFeatureAvailability = OfflineAgentInterface$OfflineFeatureAvailability.NOT_INITIALIZED;
        this.mAgentListenerHelper.agentDestroying();
        if (this.mBackGroundHandler != null) {
            this.addRequestToHandler(5);
        }
        if (this.getMainHandler() != null) {
            this.getMainHandler().post((Runnable)new OfflineAgent$8(this));
        }
    }
    
    @Override
    protected void doInit() {
        Log.i("nf_offlineAgent", "OfflineAgent doInit");
        this.getMainHandler().post((Runnable)new OfflineAgent$7(this));
        this.stopBackgroundThread();
        this.startBackgroundThread();
        this.addRequestToHandler(0);
    }
    
    @Override
    public IntentCommandHandler getCommandHandler() {
        return this;
    }
    
    @Override
    public DownloadVideoQuality getCurrentDownloadVideoQuality() {
        return this.getVideoQualityFromPref();
    }
    
    @Override
    public OfflinePlayableUiList getLatestOfflinePlayableList() {
        ThreadUtils.assertOnMain();
        return this.mOfflinePlayableUiList;
    }
    
    @Override
    public boolean getRequiresUnmeteredNetwork() {
        ThreadUtils.assertOnMain();
        return this.mDownloadController.requiresUnmeteredConnectionForDownload();
    }
    
    @Override
    public void handleCommand(final Intent intent) {
        final IntentCommandGroupType groupType = IntentCommandGroupType.getGroupType(intent);
        switch (OfflineAgent$21.$SwitchMap$com$netflix$mediaclient$service$offline$agent$IntentCommandGroupType[groupType.ordinal()]) {
            default: {
                if (Log.isLoggable()) {
                    Log.e("nf_offlineAgent", "unsupported IntentCommandGroupType=" + groupType);
                }
            }
            case 1: {
                this.mDownloadNotificationManager.handleDownloadNotificationIntent(intent);
            }
        }
    }
    
    @Override
    public boolean isOfflineFeatureEnabled() {
        return !this.isOfflineFeatureDisabled();
    }
    
    @Override
    public void notifyPause(final long n) {
    }
    
    @Override
    public void notifyPlay(final long n) {
        this.mBackGroundHandler.sendMessageDelayed(this.mBackGroundHandler.obtainMessage(9, (Object)n), 30000L);
    }
    
    @Override
    public void notifyPlayError(final long n) {
        this.mBackGroundHandler.removeMessages(9, (Object)n);
    }
    
    @Override
    public void notifyPlayProgress(final long n, final long n2) {
    }
    
    @Override
    public void notifyStop(final long n) {
        this.mBackGroundHandler.removeMessages(9, (Object)n);
    }
    
    @Override
    public void onAccountDataFetched() {
        ThreadUtils.assertNotOnMain();
        Log.i("nf_offlineAgent", "onAccountDataFetched");
        this.handleMayBeNewUser();
        this.handleLicenseRefreshForAll();
        if (DownloadGeoPlayabilityHelper.hasGeoCountryChanged(this.mOfflineRegistry.getGeoCountryCode(), this.mConfigurationAgent.getGeoCountryCode())) {
            this.sendGeoPlayabilityRequest();
        }
    }
    
    @Override
    public void onAllMaintenanceJobDone() {
        Log.i("nf_offlineAgent", "onAllMaintenanceJobDone");
        this.addRequestToHandler(10);
    }
    
    @Override
    public void onStreamingPlayStartReceived() {
        ThreadUtils.assertNotOnMain();
        if (this.isOfflineFeatureDisabled()) {
            return;
        }
        this.mDownloadController.notifyStreamingStarted();
    }
    
    @Override
    public void onStreamingPlayStopReceived() {
        ThreadUtils.assertNotOnMain();
        if (this.isOfflineFeatureDisabled()) {
            return;
        }
        this.mDownloadController.notifyStreamingStopped();
    }
    
    @Override
    public void onTrimMemory(final int n) {
        if (this.mOfflineManifestManager != null) {
            this.mOfflineManifestManager.onTrimMemory(n);
        }
    }
    
    @Override
    public void onUserAccountActive() {
        ThreadUtils.assertNotOnMain();
        Log.i("nf_offlineAgent", "onUserAccountActive");
        this.handleMayBeNewUser();
    }
    
    @Override
    public void onUserAccountInActive() {
        ThreadUtils.assertNotOnMain();
        if (this.isOfflineFeatureDisabled()) {
            return;
        }
        this.stopAllDownloadsAndPersistRegistry(StopReason.AccountInActive);
        this.getMainHandler().post((Runnable)new OfflineAgent$14(this));
    }
    
    @Override
    public void pauseDownload(final String s) {
        ThreadUtils.assertOnMain();
        this.onDownloadPauseOrResumeByUser(true);
        this.addRequestToHandler(3, s);
    }
    
    @Override
    public void refreshUIData() {
        this.buildNewUiList();
    }
    
    @Override
    public void removeOfflineAgentListener(final OfflineAgentListener offlineAgentListener) {
        ThreadUtils.assertOnMain();
        this.mAgentListenerHelper.removeOfflineAgentListener(this.getMainHandler(), offlineAgentListener);
    }
    
    @Override
    public void requestGeoPlayabilityUpdate() {
        ThreadUtils.assertOnMain();
        this.addRequestToHandler(11);
    }
    
    @Override
    public void requestOfflineManifest(final long n, final OfflinePlaybackInterface$ManifestCallback offlinePlaybackInterface$ManifestCallback) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "requestOfflineManifest movieId=" + n);
        }
        if (this.isOfflineFeatureDisabled()) {
            offlinePlaybackInterface$ManifestCallback.onManifestResponse(n, null, new NetflixStatus(StatusCode.DL_OFFLINE_AGENT_NOT_READY));
            return;
        }
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "requestOfflineManifest posting runnable movieId=" + n);
        }
        this.mBackGroundHandler.post((Runnable)new OfflineAgent$16(this, n, offlinePlaybackInterface$ManifestCallback));
    }
    
    @Override
    public void requestOfflineViewing(final String s, final VideoType videoType, final PlayContext playContext) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "requestOfflineViewing playableId=" + s);
        }
        this.updatePrimaryProfileGuidIfMissing();
        this.onDownloadPauseOrResumeByUser(false);
        final OfflineAgent$CreateRequest offlineAgent$CreateRequest = new OfflineAgent$CreateRequest(this, s, playContext);
        this.serializeMetadataToDisc(s, videoType);
        this.mBackGroundHandler.sendMessageAtFrontOfQueue(this.mBackGroundHandler.obtainMessage(1, (Object)offlineAgent$CreateRequest));
    }
    
    @Override
    public void requestRefreshLicenseForPlayable(final String s) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "requestRefreshLicenseForPlayable playableId=" + s);
        }
        this.mBackGroundHandler.post((Runnable)new OfflineAgent$11(this, s));
    }
    
    @Override
    public void requestRenewPlayWindowForPlayable(final String s) {
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "requestRenewPlayWindowForPlayable playableId=" + s);
        }
        this.mBackGroundHandler.post((Runnable)new OfflineAgent$12(this, s));
    }
    
    @Override
    public void resumeDownload(final String s) {
        ThreadUtils.assertOnMain();
        this.onDownloadPauseOrResumeByUser(false);
        this.addRequestToHandler(4, s);
    }
    
    @Override
    public void setDownloadVideoQuality(final DownloadVideoQuality downloadVideoQuality) {
        PreferenceUtils.putStringPref(this.getContext(), "download_video_quality", downloadVideoQuality.getValue());
    }
    
    @Override
    public void setRequiresUnmeteredNetwork(final boolean b) {
        ThreadUtils.assertOnMain();
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "setRequiresUnmeteredNetwork requires=" + b);
        }
        this.mBackGroundHandler.post((Runnable)new OfflineAgent$10(this, b));
    }
    
    @Override
    public boolean setSkipAdultContent(final boolean mSkipAdultContent) {
        final boolean b = mSkipAdultContent != this.mSkipAdultContent;
        this.mSkipAdultContent = mSkipAdultContent;
        return b;
    }
}
