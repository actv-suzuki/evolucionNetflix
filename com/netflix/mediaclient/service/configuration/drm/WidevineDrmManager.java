// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.configuration.drm;

import android.media.MediaDrm$ProvisionRequest;
import com.netflix.mediaclient.util.CryptoUtils;
import android.media.DeniedByServerException;
import android.os.Build;
import java.util.Arrays;
import com.netflix.mediaclient.android.app.BackgroundTask;
import android.annotation.SuppressLint;
import com.netflix.mediaclient.util.AndroidUtils;
import com.netflix.mediaclient.util.StringUtils;
import android.media.MediaDrm$CryptoSession;
import android.util.Base64;
import android.media.NotProvisionedException;
import java.util.HashMap;
import android.media.MediaDrm$KeyRequest;
import com.netflix.mediaclient.Log;
import android.media.UnsupportedSchemeException;
import com.netflix.mediaclient.util.PreferenceUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import com.netflix.mediaclient.service.ServiceAgent;
import com.netflix.mediaclient.servicemgr.ErrorLogging;
import android.content.Context;
import android.media.MediaDrm;
import java.util.UUID;
import android.annotation.TargetApi;
import android.media.MediaDrm$OnEventListener;

@TargetApi(18)
public class WidevineDrmManager implements MediaDrm$OnEventListener, DrmManager
{
    public static final String PROPERTY_SYSTEM_ID = "systemId";
    public static final String TAG;
    private static final UUID WIDEVINE_SCHEME;
    private static final byte[] init;
    private MediaDrm drm;
    private DrmReadyCallback mCallback;
    private Context mContext;
    private String mCurrentAccountId;
    private boolean mDrmSystemChanged;
    private ErrorLogging mErrorLogging;
    private AccountKeyMap mKeyIdsMap;
    private ServiceAgent.UserAgentInterface mUser;
    private AtomicBoolean mWidevineProvisioned;
    private CryptoSession nccpCryptoFactoryCryptoSession;
    
    static {
        TAG = WidevineDrmManager.class.getSimpleName();
        init = new byte[] { 10, 122, 0, 108, 56, 43 };
        WIDEVINE_SCHEME = new UUID(-1301668207276963122L, -6645017420763422227L);
    }
    
    WidevineDrmManager(final Context mContext, final ServiceAgent.UserAgentInterface mUser, final ErrorLogging mErrorLogging, final DrmReadyCallback mCallback) throws UnsupportedSchemeException {
        this.mWidevineProvisioned = new AtomicBoolean(false);
        this.nccpCryptoFactoryCryptoSession = new CryptoSession();
        if (mCallback == null) {
            throw new IllegalArgumentException();
        }
        this.mCallback = mCallback;
        this.mUser = mUser;
        this.mErrorLogging = mErrorLogging;
        this.mContext = mContext;
        (this.drm = new MediaDrm(WidevineDrmManager.WIDEVINE_SCHEME)).setOnEventListener((MediaDrm$OnEventListener)this);
        this.mKeyIdsMap = new AccountKeyMap(this.mContext);
        this.showProperties();
        if (this.isWidevinePluginChanged()) {
            this.reset();
            this.mDrmSystemChanged = true;
        }
        PreferenceUtils.putStringPref(this.mContext, "nf_drm_system_id", this.getDeviceType());
    }
    
    private void afterWidewineProvisioning() {
        this.mCallback.drmReady();
    }
    
    private void closeCryptoSessions(final byte[] array) {
        if (array == null || this.drm == null) {
            return;
        }
        Log.d(WidevineDrmManager.TAG, "closeCryptoSessions");
        try {
            this.drm.closeSession(array);
        }
        catch (Throwable t) {
            Log.w(WidevineDrmManager.TAG, "closeCryptoSessions failed !", t);
        }
    }
    
    private void closeSessionAndRemoveKeys(final byte[] array) {
        synchronized (this) {
            this.removeSessionKeys(array);
            this.closeCryptoSessions(array);
        }
    }
    
    private MediaDrm$KeyRequest createKeyRequest() throws NotProvisionedException {
        synchronized (this) {
            Log.d(WidevineDrmManager.TAG, "get NCCP session key request");
            this.closeCryptoSessions(this.nccpCryptoFactoryCryptoSession.pendingSessionId);
            Log.d(WidevineDrmManager.TAG, "Create a new crypto session");
            this.nccpCryptoFactoryCryptoSession.pendingSessionId = this.drm.openSession();
            return this.drm.getKeyRequest(this.nccpCryptoFactoryCryptoSession.pendingSessionId, WidevineDrmManager.init, "application/xml", 2, new HashMap());
        }
    }
    
    private boolean createNccpCryptoFactoryDrmSession() {
        while (true) {
            Label_0148: {
                try {
                    this.nccpCryptoFactoryCryptoSession.sessionId = this.drm.openSession();
                    if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                        if (this.nccpCryptoFactoryCryptoSession.sessionId == null) {
                            break Label_0148;
                        }
                        Log.d(WidevineDrmManager.TAG, "Device is provisioned. NCCP crypto factory session ID: " + new String(this.nccpCryptoFactoryCryptoSession.sessionId));
                    }
                    return true;
                }
                catch (NotProvisionedException ex) {
                    Log.e(WidevineDrmManager.TAG, "Device is not provisioned, start provisioning workflow!", (Throwable)ex);
                    this.startWidewineProvisioning();
                    return false;
                }
                catch (Throwable t) {
                    Log.e(WidevineDrmManager.TAG, "Fatal error, can not recover!", t);
                    this.mCallback.drmError(-100);
                    this.mErrorLogging.logHandledException("Failed to created NCCP crypto factory DRM session " + t.getMessage());
                    return false;
                }
            }
            Log.d(WidevineDrmManager.TAG, "Device is provisioned. NCCP crypto factory session ID: null");
            return true;
        }
    }
    
    private void dumpKeyReqyest(final byte[] array) {
        if (array != null) {
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "key request created: " + Base64.encodeToString(array, 2));
            }
            return;
        }
        Log.w(WidevineDrmManager.TAG, "key request returned null");
    }
    
    private MediaDrm$CryptoSession findMediaDrmCryptoSession() {
        final byte[] sessionId = this.nccpCryptoFactoryCryptoSession.sessionId;
        if (sessionId == null) {
            return null;
        }
        return this.drm.getCryptoSession(sessionId, getCipherAlgorithm(), getMacAlgorithm());
    }
    
    private static String getCipherAlgorithm() {
        return "AES/CBC/NoPadding";
    }
    
    private static String getMacAlgorithm() {
        return "HmacSHA256";
    }
    
    public static String getMediaDrmSecurityLevels() {
        try {
            final MediaDrm mediaDrm = new MediaDrm(WidevineDrmManager.WIDEVINE_SCHEME);
            final String propertyString = mediaDrm.getPropertyString("securityLevel");
            Log.d(WidevineDrmManager.TAG, "Widevine securityLevel [" + propertyString + "]");
            if (mediaDrm != null) {
                mediaDrm.release();
            }
            return propertyString;
        }
        catch (UnsupportedSchemeException ex) {
            return null;
        }
    }
    
    private boolean isValidKeyIds(final AccountKeyMap.KeyIds keyIds, final String s, final String s2) {
        return keyIds != null && StringUtils.isNotEmpty(keyIds.getKceKeyId()) && StringUtils.isNotEmpty(keyIds.getKchKeyId()) && StringUtils.isNotEmpty(keyIds.getKeySetId()) && ((StringUtils.isEmpty(s2) && StringUtils.isEmpty(s)) || (keyIds.getKchKeyId().equals(s2) && keyIds.getKceKeyId().equals(s)));
    }
    
    private boolean isWidevinePluginChanged() {
        final String stringPref = PreferenceUtils.getStringPref(this.mContext, "nf_drm_system_id", null);
        final String deviceType = this.getDeviceType();
        if (stringPref == null) {
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "System ID was not saved, user is not logged in, no need to report that plugin is changed: " + stringPref);
            }
        }
        else {
            if (!stringPref.equals(deviceType)) {
                if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                    Log.d(WidevineDrmManager.TAG, "System ID changed from " + stringPref + " to " + deviceType);
                }
                return true;
            }
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "System ID did not changed: " + stringPref);
                return false;
            }
        }
        return false;
    }
    
    @SuppressLint({ "NewApi" })
    public static boolean isWidewineSupported() {
        return AndroidUtils.getAndroidVersion() >= 18 && MediaDrm.isCryptoSchemeSupported(WidevineDrmManager.WIDEVINE_SCHEME);
    }
    
    private void mediaDrmFailure(final boolean b) {
        // monitorenter(this)
        if (b) {
            try {
                Log.d(WidevineDrmManager.TAG, "MediaDrm failed, unregister device and logout user");
                new BackgroundTask().execute(new Runnable() {
                    @Override
                    public void run() {
                        WidevineDrmManager.this.mUser.logoutUser();
                        Log.d(WidevineDrmManager.TAG, "Redo CDM provisioning");
                        WidevineDrmManager.this.init();
                    }
                });
            }
            finally {
            }
            // monitorexit(this)
        }
    }
    // monitorexit(this)
    
    private void removeSessionKeys(final byte[] array) {
        if (array == null || this.drm == null) {
            return;
        }
        Log.d(WidevineDrmManager.TAG, "removeSessionKeys");
        try {
            this.drm.removeKeys(array);
        }
        catch (Exception ex) {
            Log.e(WidevineDrmManager.TAG, "removeSessionKeys ", ex);
        }
    }
    
    private void reset() {
        PreferenceUtils.removePref(this.mContext, "nf_drm_system_id");
        this.closeSessionAndRemoveKeys(this.nccpCryptoFactoryCryptoSession.pendingSessionId);
        this.closeSessionAndRemoveKeys(this.nccpCryptoFactoryCryptoSession.sessionId);
        this.nccpCryptoFactoryCryptoSession.reset();
        this.mKeyIdsMap.clearMap();
    }
    
    private boolean restoreKeysToSession(final AccountKeyMap.KeyIds keyIds) {
        try {
            this.closeCryptoSessions(this.nccpCryptoFactoryCryptoSession.sessionId);
            this.nccpCryptoFactoryCryptoSession.sessionId = this.drm.openSession();
            this.drm.restoreKeys(this.nccpCryptoFactoryCryptoSession.sessionId, keyIds.getKeySetId().getBytes());
            this.nccpCryptoFactoryCryptoSession.kceKeyId = keyIds.getKceKeyId().getBytes();
            this.nccpCryptoFactoryCryptoSession.kchKeyId = keyIds.getKchKeyId().getBytes();
            Log.d(WidevineDrmManager.TAG, "restoreKeysToSession succeeded.");
            return true;
        }
        catch (Throwable t) {
            Log.e(WidevineDrmManager.TAG, "Failed to restore keys to DRM session");
            return false;
        }
    }
    
    private void showProperties() {
        if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
            Log.d(WidevineDrmManager.TAG, "vendor: " + this.drm.getPropertyString("vendor"));
            Log.d(WidevineDrmManager.TAG, "version: " + this.drm.getPropertyString("version"));
            Log.d(WidevineDrmManager.TAG, "description: " + this.drm.getPropertyString("description"));
            Log.d(WidevineDrmManager.TAG, "deviceId: " + Arrays.toString(this.drm.getPropertyByteArray("deviceUniqueId")));
            Log.d(WidevineDrmManager.TAG, "algorithms: " + this.drm.getPropertyString("algorithms"));
            Log.d(WidevineDrmManager.TAG, "security level: " + this.drm.getPropertyString("securityLevel"));
            Log.d(WidevineDrmManager.TAG, "system ID: " + this.drm.getPropertyString("systemId"));
            Log.i(WidevineDrmManager.TAG, "provisioningId: " + Arrays.toString(this.drm.getPropertyByteArray("provisioningUniqueId")));
        }
    }
    
    private void startWidewineProvisioning() {
        Object o = this.mWidevineProvisioned;
        synchronized (o) {
            this.mWidevineProvisioned.set(false);
            // monitorexit(o)
            o = this.drm.getProvisionRequest();
            new WidevineCDMProvisionRequestTask(((MediaDrm$ProvisionRequest)o).getData(), new WidewineProvisiongCallback() {
                final /* synthetic */ String val$url = ((MediaDrm$ProvisionRequest)o).getDefaultUrl();
                
                @Override
                public void done(final byte[] array) {
                    if (array != null) {
                        if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                            Log.d(WidevineDrmManager.TAG, "Got CDM provisiong " + new String(array));
                        }
                        try {
                            WidevineDrmManager.this.drm.provideProvisionResponse(array);
                            WidevineDrmManager.this.init();
                            return;
                        }
                        catch (DeniedByServerException ex) {
                            Log.d(WidevineDrmManager.TAG, "Server declined Widewine provisioning request. Server URL: " + this.val$url, (Throwable)ex);
                            WidevineDrmManager.this.mCallback.drmError(-101);
                            WidevineDrmManager.this.mErrorLogging.logHandledException(new Exception("Server declined Widewine provisioning request. Server URL: " + this.val$url + ". Build: " + Build.DISPLAY, (Throwable)ex));
                            return;
                        }
                        catch (Throwable t) {
                            Log.d(WidevineDrmManager.TAG, "Fatal error on seting Widewine provisioning response", t);
                            WidevineDrmManager.this.mErrorLogging.logHandledException(new Exception("Fatal error on seting Widewine provisioning response received from URL: " + this.val$url + ". Build: " + Build.DISPLAY, t));
                            if (WidevineDrmManager.this.mCallback != null) {
                                WidevineDrmManager.this.mCallback.drmError(-100);
                            }
                            return;
                        }
                    }
                    Log.e(WidevineDrmManager.TAG, "Failed to get provisiong certificate");
                    WidevineDrmManager.this.mCallback.drmError(-100);
                    WidevineDrmManager.this.mErrorLogging.logHandledException("Failed to get provisiong certificate. Response is null from URL " + this.val$url);
                }
            }).execute((Object[])new String[] { ((MediaDrm$ProvisionRequest)o).getDefaultUrl() });
        }
    }
    
    private void updateKeyResponseForNccpSession(byte[] provideKeyResponse, final byte[] kceKeyId, final byte[] kchKeyId) throws DeniedByServerException, NotProvisionedException {
        Log.d(WidevineDrmManager.TAG, "Provide key response...");
        provideKeyResponse = this.drm.provideKeyResponse(this.nccpCryptoFactoryCryptoSession.sessionId, provideKeyResponse);
        Log.d(WidevineDrmManager.TAG, "Save keys...");
        if (provideKeyResponse == null) {
            Log.e(WidevineDrmManager.TAG, "Something is wrong, this should not happen! KeySetId is null!");
            throw new NotProvisionedException("Something is wrong, this should not happen! KeySetId is null!");
        }
        this.nccpCryptoFactoryCryptoSession.kceKeyId = kceKeyId;
        this.nccpCryptoFactoryCryptoSession.kchKeyId = kchKeyId;
        this.mKeyIdsMap.addCurrentKeyIds(new String(provideKeyResponse), new String(kceKeyId), new String(kchKeyId));
        this.mDrmSystemChanged = false;
    }
    
    void clearKeys(final String s) {
        synchronized (this) {
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "clearKeys " + s);
            }
            this.mKeyIdsMap.removeCurrentKeyIds(s);
        }
    }
    
    byte[] decrypt(byte[] unpadPerPKCS5Padding, final byte[] array) {
        final MediaDrm$CryptoSession mediaDrmCryptoSession = this.findMediaDrmCryptoSession();
        if (mediaDrmCryptoSession == null) {
            Log.w(WidevineDrmManager.TAG, "decrypt - session NOT found!");
            return new byte[0];
        }
        if (this.nccpCryptoFactoryCryptoSession.kceKeyId == null) {
            Log.w(WidevineDrmManager.TAG, "decrypt - kce is null!");
            return new byte[0];
        }
        try {
            unpadPerPKCS5Padding = CryptoUtils.unpadPerPKCS5Padding(mediaDrmCryptoSession.decrypt(this.nccpCryptoFactoryCryptoSession.kceKeyId, unpadPerPKCS5Padding, array), 16);
            return unpadPerPKCS5Padding;
        }
        catch (Throwable t) {
            Log.e(WidevineDrmManager.TAG, "Failed to decrypt ", t);
            this.mediaDrmFailure(false);
            return new byte[0];
        }
    }
    
    public void destroy() {
        synchronized (this) {
            this.mWidevineProvisioned.set(false);
            this.closeSessionAndRemoveKeys(this.nccpCryptoFactoryCryptoSession.pendingSessionId);
            this.closeCryptoSessions(this.nccpCryptoFactoryCryptoSession.sessionId);
            if (this.drm != null) {
                this.drm.release();
                this.drm = null;
            }
        }
    }
    
    byte[] encrypt(byte[] array, final byte[] array2) {
        final MediaDrm$CryptoSession mediaDrmCryptoSession = this.findMediaDrmCryptoSession();
        if (mediaDrmCryptoSession == null) {
            Log.w(WidevineDrmManager.TAG, "encrypt - session NOT found!");
            return new byte[0];
        }
        if (this.nccpCryptoFactoryCryptoSession.kceKeyId == null) {
            Log.w(WidevineDrmManager.TAG, "encrypt - kce is null!");
            return new byte[0];
        }
        try {
            array = CryptoUtils.padPerPKCS5Padding(array, 16);
            array = mediaDrmCryptoSession.encrypt(this.nccpCryptoFactoryCryptoSession.kceKeyId, array, array2);
            return array;
        }
        catch (Throwable t) {
            Log.e(WidevineDrmManager.TAG, "Failed to encrypt ", t);
            this.mediaDrmFailure(false);
            return new byte[0];
        }
    }
    
    public byte[] getDeviceId() {
        byte[] array;
        if (this.drm == null) {
            Log.e(WidevineDrmManager.TAG, "Session MediaDrm is null! It should NOT happen!");
            array = null;
        }
        else {
            final byte[] propertyByteArray = this.drm.getPropertyByteArray("deviceUniqueId");
            if (propertyByteArray == null) {
                Log.e(WidevineDrmManager.TAG, "MediaDrm device ID is null! It should NOT happen!");
                return propertyByteArray;
            }
            array = propertyByteArray;
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "MediaDrm device ID is: " + new String(propertyByteArray));
                return propertyByteArray;
            }
        }
        return array;
    }
    
    public String getDeviceType() {
        String propertyString;
        if (this.drm == null) {
            Log.e(WidevineDrmManager.TAG, "Session MediaDrm is null! It should NOT happen!");
            propertyString = null;
        }
        else {
            final String s = propertyString = this.drm.getPropertyString("systemId");
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "MediaDrm system ID is: " + s);
                return s;
            }
        }
        return propertyString;
    }
    
    public int getDrmType() {
        return 1;
    }
    
    byte[] getNccpSessionKeyRequest() {
        synchronized (this) {
            Log.d(WidevineDrmManager.TAG, "get NCCP session key request");
            try {
                final byte[] data = this.createKeyRequest().getData();
                this.dumpKeyReqyest(data);
                return data;
            }
            catch (Throwable t) {
                Log.e(WidevineDrmManager.TAG, "Failed to get key request", t);
                this.mediaDrmFailure(false);
                final byte[] data = new byte[0];
            }
        }
    }
    
    public void init() {
        if (this.isWidevinePluginChanged()) {
            PreferenceUtils.putStringPref(this.mContext, "nf_drm_system_id", this.getDeviceType());
            this.mediaDrmFailure(true);
        }
        else if (this.createNccpCryptoFactoryDrmSession()) {
            Log.d(WidevineDrmManager.TAG, "NCCP Crypto Factory session is created");
            this.afterWidewineProvisioning();
        }
    }
    
    public void onEvent(final MediaDrm mediaDrm, final byte[] array, final int n, final int n2, final byte[] array2) {
        if (n == 1) {
            Log.d(WidevineDrmManager.TAG, "Provisioning is required");
        }
        else {
            if (n == 2) {
                Log.d(WidevineDrmManager.TAG, "MediaDrm event: Key required");
                return;
            }
            if (n == 3) {
                Log.d(WidevineDrmManager.TAG, "MediaDrm event: Key expired");
                return;
            }
            if (n == 4 && Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "MediaDrm event: Vendor defined: " + n);
            }
        }
    }
    
    boolean restoreKeys(final String mCurrentAccountId, final String s, final String s2) {
        boolean b = true;
        synchronized (this) {
            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                Log.d(WidevineDrmManager.TAG, "restoreKeys for " + mCurrentAccountId + ",kceKid: " + s + ",kchKid: " + s2);
            }
            if (mCurrentAccountId.equals(this.mCurrentAccountId)) {
                Log.d(WidevineDrmManager.TAG, "restoreKeys already loaded");
            }
            else {
                final AccountKeyMap.KeyIds restoreKeyIdsForAccount = this.mKeyIdsMap.restoreKeyIdsForAccount(mCurrentAccountId);
                this.mCurrentAccountId = mCurrentAccountId;
                if (!this.mDrmSystemChanged) {
                    b = (this.isValidKeyIds(restoreKeyIdsForAccount, s, s2) && this.restoreKeysToSession(restoreKeyIdsForAccount));
                }
            }
            return b;
        }
    }
    
    byte[] sign(byte[] sign) {
        final MediaDrm$CryptoSession mediaDrmCryptoSession = this.findMediaDrmCryptoSession();
        if (mediaDrmCryptoSession == null) {
            Log.w(WidevineDrmManager.TAG, "sign - session NOT found!");
            return new byte[0];
        }
        if (this.nccpCryptoFactoryCryptoSession.kchKeyId == null) {
            Log.w(WidevineDrmManager.TAG, "sign - kch is null!");
            return new byte[0];
        }
        try {
            sign = mediaDrmCryptoSession.sign(this.nccpCryptoFactoryCryptoSession.kchKeyId, sign);
            return sign;
        }
        catch (Throwable t) {
            Log.e(WidevineDrmManager.TAG, "Failed to sign message ", t);
            this.mediaDrmFailure(false);
            return new byte[0];
        }
    }
    
    boolean updateNccpSessionKeyResponse(final byte[] array, final byte[] array2, final byte[] array3, final String s) {
    Label_0046_Outer:
        while (true) {
            boolean b = false;
            while (true) {
                while (true) {
                    final byte[] array4;
                    Label_0213: {
                        synchronized (this) {
                            if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                                Log.d(WidevineDrmManager.TAG, "Update key response for account " + s);
                            }
                            break Label_0213;
                            b = false;
                            try {
                                final byte[] pendingSessionId = this.nccpCryptoFactoryCryptoSession.pendingSessionId;
                                if (pendingSessionId != null) {
                                    Log.d(WidevineDrmManager.TAG, "Update key response for pending session id " + new String(pendingSessionId));
                                    final byte[] activatePendingSessionId = this.nccpCryptoFactoryCryptoSession.activatePendingSessionId();
                                    if (s.equals(this.mCurrentAccountId)) {
                                        this.closeSessionAndRemoveKeys(activatePendingSessionId);
                                    }
                                    else {
                                        this.closeCryptoSessions(activatePendingSessionId);
                                    }
                                }
                                this.updateKeyResponseForNccpSession(array4, array2, array3);
                                b = true;
                            }
                            catch (Throwable t) {
                                Log.e(WidevineDrmManager.TAG, "We failed to update key response..." + t.getMessage() + ": ", t);
                                this.mediaDrmFailure(false);
                            }
                            Log.e(WidevineDrmManager.TAG, "Update key response has invlaid input");
                            return b;
                        }
                    }
                    if (array4 != null && array2 != null && array3 != null) {
                        continue Label_0046_Outer;
                    }
                    break;
                }
                continue;
            }
        }
    }
    
    boolean verify(final byte[] array, final byte[] array2) {
        Log.logByteArray(WidevineDrmManager.TAG, "Verify message", array);
        final MediaDrm$CryptoSession mediaDrmCryptoSession = this.findMediaDrmCryptoSession();
        boolean verify;
        if (mediaDrmCryptoSession == null) {
            Log.w(WidevineDrmManager.TAG, "verify - session NOT found!");
            verify = false;
        }
        else {
            if (this.nccpCryptoFactoryCryptoSession.kchKeyId == null) {
                Log.w(WidevineDrmManager.TAG, "verify - kch is null!");
                return false;
            }
            try {
                final boolean b = verify = mediaDrmCryptoSession.verify(this.nccpCryptoFactoryCryptoSession.kchKeyId, array, array2);
                if (Log.isLoggable(WidevineDrmManager.TAG, 3)) {
                    Log.d(WidevineDrmManager.TAG, "Messaage is verified: " + b);
                    return b;
                }
            }
            catch (Throwable t) {
                Log.e(WidevineDrmManager.TAG, "Failed to verify message ", t);
                this.mediaDrmFailure(false);
                return false;
            }
        }
        return verify;
    }
    
    private static class CryptoSession
    {
        public byte[] kceKeyId;
        public byte[] kchKeyId;
        public byte[] pendingSessionId;
        public byte[] sessionId;
        
        public byte[] activatePendingSessionId() {
            byte[] sessionId = null;
            synchronized (this) {
                if (this.pendingSessionId == null) {
                    Log.e(WidevineDrmManager.TAG, "Pending session does NOT exist! Do nothing!");
                }
                else {
                    Log.d(WidevineDrmManager.TAG, "Pending session does exist! Move pending to current session id and return old!");
                    sessionId = this.sessionId;
                    this.sessionId = this.pendingSessionId;
                    this.pendingSessionId = null;
                }
                return sessionId;
            }
        }
        
        public void reset() {
            synchronized (this) {
                this.pendingSessionId = null;
                this.sessionId = null;
                this.kceKeyId = null;
                this.kchKeyId = null;
            }
        }
    }
    
    public interface NccpProvisiongCallback
    {
        void done(final byte[] p0);
        
        void error();
    }
    
    public interface WidewineProvisiongCallback
    {
        void done(final byte[] p0);
    }
}