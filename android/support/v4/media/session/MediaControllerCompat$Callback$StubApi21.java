// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.media.session;

import java.util.List;
import android.support.v4.media.MediaMetadataCompat;
import android.os.Bundle;

class MediaControllerCompat$Callback$StubApi21 implements MediaControllerCompatApi21$Callback
{
    final /* synthetic */ MediaControllerCompat$Callback this$0;
    
    MediaControllerCompat$Callback$StubApi21(final MediaControllerCompat$Callback this$0) {
        this.this$0 = this$0;
    }
    
    @Override
    public void onAudioInfoChanged(final int n, final int n2, final int n3, final int n4, final int n5) {
        this.this$0.onAudioInfoChanged(new MediaControllerCompat$PlaybackInfo(n, n2, n3, n4, n5));
    }
    
    @Override
    public void onExtrasChanged(final Bundle bundle) {
        this.this$0.onExtrasChanged(bundle);
    }
    
    @Override
    public void onMetadataChanged(final Object o) {
        this.this$0.onMetadataChanged(MediaMetadataCompat.fromMediaMetadata(o));
    }
    
    @Override
    public void onPlaybackStateChanged(final Object o) {
        this.this$0.onPlaybackStateChanged(PlaybackStateCompat.fromPlaybackState(o));
    }
    
    @Override
    public void onQueueChanged(final List<?> list) {
        this.this$0.onQueueChanged(MediaSessionCompat$QueueItem.fromQueueItemList(list));
    }
    
    @Override
    public void onQueueTitleChanged(final CharSequence charSequence) {
        this.this$0.onQueueTitleChanged(charSequence);
    }
    
    @Override
    public void onSessionDestroyed() {
        this.this$0.onSessionDestroyed();
    }
    
    @Override
    public void onSessionEvent(final String s, final Bundle bundle) {
        this.this$0.onSessionEvent(s, bundle);
    }
}
