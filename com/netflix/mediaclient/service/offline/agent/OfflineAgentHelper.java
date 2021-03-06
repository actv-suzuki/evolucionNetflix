// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.offline.agent;

import com.netflix.mediaclient.javabridge.ui.Nrdp;
import com.netflix.mediaclient.javabridge.ui.LogArguments;
import com.netflix.mediaclient.service.logging.logblob.LogBlobType;
import com.netflix.mediaclient.javabridge.ui.LogArguments$LogLevel;
import android.os.Environment;
import java.io.File;
import android.os.Build$VERSION;
import com.netflix.mediaclient.service.NrdController;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.service.offline.registry.OfflineRegistry;
import com.netflix.mediaclient.service.user.UserAgent;
import java.util.ArrayList;
import com.netflix.mediaclient.servicemgr.interface_.offline.DownloadState;
import java.util.concurrent.TimeUnit;
import com.netflix.mediaclient.util.PreferenceUtils;
import android.content.Context;
import java.util.Iterator;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.service.offline.download.OfflinePlayable;
import java.util.List;
import java.util.Map;

class OfflineAgentHelper
{
    private static final long DISK_FREE_SPACE_SAFETY_MARGIN = 50000000L;
    public static final int MIN_HR_BEFORE_NEXT_LICENSE_SYNC = 24;
    private static final String TAG = "nf_offlineAgent";
    
    static void applyGeoPlayabilityFlags(final Map<String, Boolean> map, final List<OfflinePlayable> list) {
        if (map != null && map.size() > 0) {
            for (final OfflinePlayable offlinePlayable : list) {
                final Boolean b = map.get(offlinePlayable.getPlayableId());
                if (b != null) {
                    if (Log.isLoggable()) {
                        Log.i("nf_offlineAgent", "handleGeoPlayabilityUpdated playableId=" + offlinePlayable.getPlayableId() + " geoWatchable=" + b);
                    }
                    offlinePlayable.getOfflineViewablePersistentData().setGeoBlocked(!b);
                }
            }
        }
    }
    
    static boolean enoughTimePassedSinceLastLicenseSync(final Context context) {
        return System.currentTimeMillis() - PreferenceUtils.getLongPref(context, "pref_offline_license_sync_time", 0L) > TimeUnit.HOURS.toMillis(24L);
    }
    
    static boolean ensureEnoughDiskSpaceForNewRequest(final long n, final List<OfflinePlayable> list) {
        final Iterator<OfflinePlayable> iterator = list.iterator();
        long n2 = 50000000L;
        while (iterator.hasNext()) {
            final OfflinePlayable offlinePlayable = iterator.next();
            if (offlinePlayable.getDownloadState() != DownloadState.Complete) {
                n2 += offlinePlayable.getTotalEstimatedSpace() - offlinePlayable.getCurrentEstimatedSpace();
            }
        }
        if (n2 > n) {
            Log.e("nf_offlineAgent", "ensureEnoughDiskSpaceForNewRequest freeSpaceNeeded=" + n2 + " freeSpace=" + n);
            return false;
        }
        return true;
    }
    
    static OfflinePlayable findNextCreatingStatePlayable(final List<OfflinePlayable> list) {
        for (final OfflinePlayable offlinePlayable : list) {
            if (offlinePlayable.getDownloadState() == DownloadState.Creating) {
                return offlinePlayable;
            }
        }
        return null;
    }
    
    static List<String> getCompletedVideoIds(final List<OfflinePlayable> list) {
        final ArrayList<String> list2 = new ArrayList<String>();
        for (final OfflinePlayable offlinePlayable : list) {
            if (offlinePlayable.getDownloadState() == DownloadState.Complete) {
                list2.add(offlinePlayable.getPlayableId());
            }
        }
        return list2;
    }
    
    static long getLastMaintenanceJobStartTime(final Context context) {
        return PreferenceUtils.getLongPref(context, "pref_offline_maintenance_job_start_time", -1L);
    }
    
    static OfflinePlayable getOfflineViewableByPlayableId(final String s, final List<OfflinePlayable> list) {
        if (s == null) {
            return null;
        }
        for (final OfflinePlayable offlinePlayable : list) {
            if (s.equals(offlinePlayable.getPlayableId())) {
                return offlinePlayable;
            }
        }
        return null;
    }
    
    static int getZeroPlayableLicenseSyncCount(final Context context) {
        return PreferenceUtils.getIntPref(context, "pref_offline_license_sync_count_zero", 0);
    }
    
    static boolean hasAnyItemInCreatingOrCreateFailed(final List<OfflinePlayable> list) {
        for (final OfflinePlayable offlinePlayable : list) {
            if (offlinePlayable.getDownloadState() == DownloadState.Creating || offlinePlayable.getDownloadState() == DownloadState.CreateFailed) {
                return true;
            }
        }
        return false;
    }
    
    static boolean hasPrimaryProfileGuidChanged(final UserAgent userAgent, final OfflineRegistry offlineRegistry) {
        final String primaryProfileGuid = userAgent.getPrimaryProfileGuid();
        final String primaryProfileGuid2 = offlineRegistry.getPrimaryProfileGuid();
        if (Log.isLoggable()) {
            Log.i("nf_offlineAgent", "newPrimaryProfileGuid=" + primaryProfileGuid + " primaryProfileGuidInRegistry=" + primaryProfileGuid2);
        }
        if (StringUtils.isNotEmpty(primaryProfileGuid) && StringUtils.isNotEmpty(primaryProfileGuid2)) {
            if (!primaryProfileGuid.equals(primaryProfileGuid2)) {
                Log.e("nf_offlineAgent", "primaryProfileGuid don't match... going to delete all content");
                return true;
            }
            if (Log.isLoggable()) {
                Log.i("nf_offlineAgent", "primaryProfileGuid match");
            }
        }
        return false;
    }
    
    static void sendOfflineDlRequestStorageInfoLogblob(final NrdController nrdController, final String s, final long n, final String s2, final String s3) {
        if (nrdController != null) {
            final Nrdp nrdp = nrdController.getNrdp();
            if (nrdp != null) {
                boolean externalStorageRemovable = false;
                if (Build$VERSION.SDK_INT >= 21) {
                    externalStorageRemovable = Environment.isExternalStorageRemovable(new File(s3));
                }
                nrdp.getLog().log(new LogArguments(LogArguments$LogLevel.INFO, "DlRequestStorageInfo playableId=" + s + " freeSpace=" + n + " oxId=" + s2 + " isRemovable=" + externalStorageRemovable, LogBlobType.OFFLINE_LOGBLOB_TYPE.getValue(), null));
            }
        }
    }
    
    static void sendOfflineNotAvailableLogblob(final NrdController nrdController, final OfflineUnavailableReason offlineUnavailableReason) {
        if (nrdController != null) {
            final Nrdp nrdp = nrdController.getNrdp();
            if (nrdp != null) {
                final LogArguments logArguments = new LogArguments(LogArguments$LogLevel.INFO, "offline feature n/a, code=" + offlineUnavailableReason.getCodeForLogblob(), LogBlobType.OFFLINE_LOGBLOB_TYPE.getValue(), null);
                Log.i("nf_offlineAgent", "sending offline not available logblob=%s", offlineUnavailableReason.toString());
                nrdp.getLog().log(logArguments);
            }
        }
    }
    
    static void setLastLicenseSyncTimeToNow(final Context context) {
        PreferenceUtils.putLongPref(context, "pref_offline_license_sync_time", System.currentTimeMillis());
    }
    
    static void setLastMaintenanceJobStartTime(final Context context, final long n) {
        PreferenceUtils.putLongPref(context, "pref_offline_maintenance_job_start_time", n);
    }
    
    static void setZeroPlayableLicenseSyncCount(final Context context, final int n) {
        PreferenceUtils.putIntPref(context, "pref_offline_license_sync_count_zero", n);
    }
}
