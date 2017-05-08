// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.media;

import com.netflix.mediaclient.servicemgr.interface_.Playable;
import com.netflix.mediaclient.servicemgr.interface_.BasicVideo;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.netflix.mediaclient.servicemgr.interface_.CWVideo;
import java.util.Map;
import java.util.HashMap;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.servicemgr.interface_.PlaybackBookmark;
import com.netflix.mediaclient.android.app.BackgroundTask;
import java.util.Iterator;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.service.webclient.model.leafs.UserProfile;
import java.util.List;
import android.content.Context;
import java.io.File;

public class BookmarkStore
{
    private static final int MAX_BOOKMARKS_PER_PROFILE = 100;
    private static final String TAG = "nf_BookmarkStore";
    private static BookmarkStore sBookmarkStore;
    private BookmarkStore$BookmarkData mBookmarkData;
    private File mBookmarkStoreFile;
    private Context mContext;
    
    public BookmarkStore() {
        this.mBookmarkData = new BookmarkStore$BookmarkData(this, null);
    }
    
    public static BookmarkStore getInstance() {
        Label_0028: {
            if (BookmarkStore.sBookmarkStore != null) {
                break Label_0028;
            }
            synchronized (BookmarkStore.class) {
                if (BookmarkStore.sBookmarkStore == null) {
                    BookmarkStore.sBookmarkStore = new BookmarkStore();
                }
                return BookmarkStore.sBookmarkStore;
            }
        }
    }
    
    private boolean isProfileStillValid(final String s, final List<UserProfile> list) {
        final Iterator<UserProfile> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (StringUtils.notEmptyAndEquals(iterator.next().getProfileGuid(), s)) {
                return true;
            }
        }
        return false;
    }
    
    private void persistBookmarkData() {
        new BackgroundTask().execute(new BookmarkStore$2(this));
    }
    
    private void setBookmarkNoPersist(final String s, final PlaybackBookmark playbackBookmark) {
        if (Log.isLoggable()) {
            Log.i("nf_BookmarkStore", "setBookmark videoId=" + playbackBookmark.mVideoId + " bookmarkTimeInSeconds=" + playbackBookmark.mBookmarkInSecond);
        }
        if (this.mBookmarkData.mBookmarkMap.get(s) == null) {
            this.mBookmarkData.mBookmarkMap.put(s, new HashMap<String, PlaybackBookmark>());
        }
        final Map<String, PlaybackBookmark> map = this.mBookmarkData.mBookmarkMap.get(s);
        this.trimSizeIfNeeded(map);
        map.put(playbackBookmark.mVideoId, playbackBookmark);
    }
    
    private void trimSizeIfNeeded(final Map<String, PlaybackBookmark> map) {
        Object o = null;
        Object o2 = null;
        long mBookmarkUpdateTimeInUTCMs = 2147483647L;
        if (map.size() > 100) {
            final Iterator<String> iterator = map.keySet().iterator();
            while (true) {
                o = o2;
                if (!iterator.hasNext()) {
                    break;
                }
                final String s = iterator.next();
                if (map.get(s).mBookmarkUpdateTimeInUTCMs >= mBookmarkUpdateTimeInUTCMs) {
                    continue;
                }
                mBookmarkUpdateTimeInUTCMs = map.get(s).mBookmarkUpdateTimeInUTCMs;
                o2 = s;
            }
        }
        if (o != null) {
            map.remove(o);
        }
    }
    
    public PlaybackBookmark getBookmark(final String s, final String s2) {
        while (true) {
            Object mContext = null;
            Label_0113: {
                synchronized (this) {
                    mContext = this.mContext;
                    PlaybackBookmark playbackBookmark;
                    if (mContext == null) {
                        playbackBookmark = null;
                    }
                    else {
                        final Map<String, PlaybackBookmark> map = this.mBookmarkData.mBookmarkMap.get(s);
                        if (map == null) {
                            playbackBookmark = null;
                        }
                        else {
                            mContext = (playbackBookmark = map.get(s2));
                            if (Log.isLoggable()) {
                                if (mContext == null) {
                                    break Label_0113;
                                }
                                Log.i("nf_BookmarkStore", "getBookmark videoId=" + s2 + " bookmarkTimeInSeconds=" + ((PlaybackBookmark)mContext).mBookmarkInSecond);
                                playbackBookmark = (PlaybackBookmark)mContext;
                            }
                        }
                    }
                    return playbackBookmark;
                }
            }
            Log.i("nf_BookmarkStore", "getBookmark no bookmark for videoId=" + s2);
            return (PlaybackBookmark)mContext;
        }
    }
    
    public void init(final Context mContext) {
        this.mContext = mContext;
        this.mBookmarkStoreFile = new File(this.mContext.getFilesDir() + "/bookmarkStore.json");
        new BackgroundTask().execute(new BookmarkStore$1(this));
    }
    
    public void onCWVideosFetched(final List<CWVideo> list, final String s) {
        while (true) {
        Label_0120_Outer:
            while (true) {
            Label_0248:
                while (true) {
                    Label_0242: {
                        Label_0239: {
                            synchronized (this) {
                                Object mContext = this.mContext;
                                if (mContext != null && list != null && s != null) {
                                    final Iterator<CWVideo> iterator = list.iterator();
                                    boolean b = false;
                                    if (iterator.hasNext()) {
                                        mContext = iterator.next();
                                        if (Log.isLoggable()) {
                                            Log.i("nf_BookmarkStore", "-> cwVideo title=" + ((BasicVideo)mContext).getTitle());
                                        }
                                        final PlaybackBookmark bookmark = this.getBookmark(s, ((Playable)mContext).getPlayableId());
                                        int n;
                                        if (bookmark == null) {
                                            Log.i("nf_BookmarkStore", "got a new bookmark");
                                            n = 1;
                                        }
                                        else {
                                            final long seconds = TimeUnit.MILLISECONDS.toSeconds(bookmark.mBookmarkUpdateTimeInUTCMs - ((Playable)mContext).getPlayableBookmarkUpdateTime());
                                            Log.i("nf_BookmarkStore", "bookMarkStoreTimeIsNewBySeconds=" + seconds);
                                            if (seconds >= 0L) {
                                                break Label_0242;
                                            }
                                            n = 1;
                                        }
                                        if (n != 0) {
                                            this.setBookmarkNoPersist(s, new PlaybackBookmark(((Playable)mContext).getPlayableBookmarkPosition(), ((Playable)mContext).getPlayableBookmarkUpdateTime(), ((Playable)mContext).getPlayableId()));
                                            b = true;
                                            break Label_0248;
                                        }
                                        break Label_0239;
                                    }
                                    else if (b) {
                                        this.persistBookmarkData();
                                    }
                                }
                                return;
                            }
                        }
                        break Label_0248;
                    }
                    int n = 0;
                    continue;
                }
                continue Label_0120_Outer;
            }
        }
    }
    
    public void setBookmark(final String s, final PlaybackBookmark playbackBookmark) {
        while (true) {
            Label_0038: {
                synchronized (this) {
                    if (this.mContext != null) {
                        if (s != null && playbackBookmark != null) {
                            break Label_0038;
                        }
                        Log.e("nf_BookmarkStore", "setBookmark not valid data");
                    }
                    return;
                }
            }
            final String s2;
            this.setBookmarkNoPersist(s2, playbackBookmark);
            this.persistBookmarkData();
        }
    }
    
    public void updateValidProfiles(final List<UserProfile> list) {
        // monitorenter(this)
        Label_0174: {
            if (list != null) {
                ArrayList<String> list2;
                try {
                    if (list.size() <= 0 || this.mBookmarkData == null || this.mBookmarkData.mBookmarkMap == null) {
                        break Label_0174;
                    }
                    list2 = new ArrayList<String>();
                    for (final Map.Entry<String, Map<String, PlaybackBookmark>> entry : this.mBookmarkData.mBookmarkMap.entrySet()) {
                        if (!this.isProfileStillValid(entry.getKey(), list)) {
                            list2.add(entry.getKey());
                        }
                    }
                }
                finally {
                }
                // monitorexit(this)
                final Iterator<Object> iterator2 = list2.iterator();
                while (iterator2.hasNext()) {
                    this.mBookmarkData.mBookmarkMap.remove(iterator2.next());
                }
                if (list2.size() > 0) {
                    this.persistBookmarkData();
                }
            }
        }
    }
    // monitorexit(this)
}
