// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.media;

import android.os.Parcel;

class MediaBrowserServiceCompat$MediaBrowserServiceImplApi23$1 extends MediaBrowserServiceCompat$Result<MediaBrowserCompat$MediaItem>
{
    final /* synthetic */ MediaBrowserServiceCompat$MediaBrowserServiceImplApi23 this$1;
    final /* synthetic */ MediaBrowserServiceCompatApi21$ResultWrapper val$resultWrapper;
    
    MediaBrowserServiceCompat$MediaBrowserServiceImplApi23$1(final MediaBrowserServiceCompat$MediaBrowserServiceImplApi23 this$1, final Object o, final MediaBrowserServiceCompatApi21$ResultWrapper val$resultWrapper) {
        this.this$1 = this$1;
        this.val$resultWrapper = val$resultWrapper;
        super(o);
    }
    
    @Override
    public void detach() {
        this.val$resultWrapper.detach();
    }
    
    @Override
    void onResultSent(final MediaBrowserCompat$MediaItem mediaBrowserCompat$MediaItem, final int n) {
        if (mediaBrowserCompat$MediaItem == null) {
            this.val$resultWrapper.sendResult(null);
            return;
        }
        final Parcel obtain = Parcel.obtain();
        mediaBrowserCompat$MediaItem.writeToParcel(obtain, 0);
        this.val$resultWrapper.sendResult(obtain);
    }
}
