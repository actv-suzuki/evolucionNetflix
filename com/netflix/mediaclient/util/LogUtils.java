// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.util;

import android.support.v4.content.LocalBroadcastManager;
import android.content.Intent;
import android.content.Context;
import com.netflix.mediaclient.servicemgr.model.trackable.Trackable;
import java.util.Collections;
import com.netflix.mediaclient.servicemgr.UiLocation;
import com.netflix.mediaclient.servicemgr.model.LoMoType;
import com.netflix.mediaclient.servicemgr.model.VideoType;
import com.netflix.mediaclient.servicemgr.model.Video;
import com.netflix.mediaclient.servicemgr.model.BasicLoMo;
import com.netflix.mediaclient.servicemgr.ServiceManager;
import com.netflix.mediaclient.Log;

public final class LogUtils
{
    private static final int CLIENT_CODE_STACK_INDEX;
    private static final String TAG = "nf_log";
    
    static {
        int n = 0;
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int length = stackTrace.length;
        int n2 = 0;
        int client_CODE_STACK_INDEX;
        while (true) {
            client_CODE_STACK_INDEX = n;
            if (n2 >= length) {
                break;
            }
            final StackTraceElement stackTraceElement = stackTrace[n2];
            ++n;
            if (stackTraceElement.getClassName().equals(LogUtils.class.getName())) {
                client_CODE_STACK_INDEX = n;
                break;
            }
            ++n2;
        }
        CLIENT_CODE_STACK_INDEX = client_CODE_STACK_INDEX;
    }
    
    public static String getCurrMethodName() {
        return Thread.currentThread().getStackTrace()[LogUtils.CLIENT_CODE_STACK_INDEX].getMethodName();
    }
    
    public static void logCurrentThreadName(final String s, final String s2) {
        if (Log.isLoggable(s, 2)) {
            Log.v(s, "Current thread name: " + Thread.currentThread().getName() + ", msg: " + s2);
        }
    }
    
    public static void reportPresentationTracking(final ServiceManager serviceManager, final BasicLoMo basicLoMo, final Video video, final int n) {
        if (serviceManager == null || !serviceManager.isReady()) {
            Log.w("nf_presentation", "Manager not ready - can't report presentation tracking");
            return;
        }
        if (!VideoType.isPresentationTrackingType(video.getType())) {
            Log.v("nf_presentation", "Video is not presentation-trackable");
            return;
        }
        UiLocation uiLocation;
        if (basicLoMo.getType() == LoMoType.FLAT_GENRE) {
            uiLocation = UiLocation.GENRE_LOLOMO;
        }
        else {
            uiLocation = UiLocation.HOME_LOLOMO;
        }
        if (Log.isLoggable("nf_presentation", 2)) {
            Log.v("nf_presentation", String.format("%s, %s, offset %d, id: %s", basicLoMo.getTitle(), uiLocation, n, video.getId()));
        }
        serviceManager.getClientLogging().getPresentationTracking().reportPresentation(basicLoMo, Collections.singletonList(video.getId()), n, uiLocation);
    }
    
    public static void reportSignUpOnDevice(final Context context) {
        final Intent intent = new Intent("com.netflix.mediaclient.intent.action.ONSIGNUP");
        intent.addCategory("com.netflix.mediaclient.intent.category.LOGGING");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    
    protected static void validateArgument(final Object o, final String s) {
        if (o == null) {
            Log.e("nf_log", s);
            throw new IllegalArgumentException(s);
        }
    }
}
