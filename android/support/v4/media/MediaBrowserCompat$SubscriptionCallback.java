// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.media;

import android.os.Bundle;
import java.util.List;
import android.os.Binder;
import android.support.v4.os.BuildCompat;
import android.os.Build$VERSION;
import android.os.IBinder;
import java.lang.ref.WeakReference;

public abstract class MediaBrowserCompat$SubscriptionCallback
{
    private final Object mSubscriptionCallbackObj;
    WeakReference<MediaBrowserCompat$Subscription> mSubscriptionRef;
    private final IBinder mToken;
    
    public MediaBrowserCompat$SubscriptionCallback() {
        if (Build$VERSION.SDK_INT >= 24 || BuildCompat.isAtLeastN()) {
            this.mSubscriptionCallbackObj = MediaBrowserCompatApi24.createSubscriptionCallback(new MediaBrowserCompat$SubscriptionCallback$StubApi24(this));
            this.mToken = null;
            return;
        }
        if (Build$VERSION.SDK_INT >= 21) {
            this.mSubscriptionCallbackObj = MediaBrowserCompatApi21.createSubscriptionCallback(new MediaBrowserCompat$SubscriptionCallback$StubApi21(this));
            this.mToken = (IBinder)new Binder();
            return;
        }
        this.mSubscriptionCallbackObj = null;
        this.mToken = (IBinder)new Binder();
    }
    
    private void setSubscription(final MediaBrowserCompat$Subscription mediaBrowserCompat$Subscription) {
        this.mSubscriptionRef = new WeakReference<MediaBrowserCompat$Subscription>(mediaBrowserCompat$Subscription);
    }
    
    public void onChildrenLoaded(final String s, final List<MediaBrowserCompat$MediaItem> list) {
    }
    
    public void onChildrenLoaded(final String s, final List<MediaBrowserCompat$MediaItem> list, final Bundle bundle) {
    }
    
    public void onError(final String s) {
    }
    
    public void onError(final String s, final Bundle bundle) {
    }
}
