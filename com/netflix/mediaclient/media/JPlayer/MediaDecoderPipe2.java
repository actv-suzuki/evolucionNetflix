// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.media.JPlayer;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCodec$CryptoInfo;
import android.os.Message;
import android.os.Looper;
import com.netflix.mediaclient.util.AndroidUtils;
import com.netflix.mediaclient.Log;
import android.media.MediaCrypto;
import android.view.Surface;
import android.media.MediaFormat;
import android.media.MediaCodec$BufferInfo;
import android.os.HandlerThread;
import android.os.Handler;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import android.media.MediaCodec;

public abstract class MediaDecoderPipe2 extends MediaDecoderBase
{
    private static final long INPUTBUFFER_TO = -1L;
    private static final int MSG_DECODER_FLUSH = 2;
    private static final int MSG_DECODER_GET_FRAME = 1;
    private static final int MSG_DECODER_INITIALIZED = 3;
    private static final int MSG_DECODER_STOP = 4;
    private static final long OUTPUTBUFFER_TO = -1L;
    private static final String TAG = "MediaDecoder2";
    protected static final long TIME_TO_NEXT_RETRY = 20L;
    private InputDataSource mDataSource;
    protected MediaCodec mDecoder;
    private boolean mDecoderPause;
    private boolean mEncrypted;
    private int mInputBufferCnt;
    private ByteBuffer[] mInputBuffers;
    private LinkedList<Integer> mInputBuffersQ;
    private Handler mInputHandler;
    private LocalStateNotifier mInputState;
    private HandlerThread mInputThread;
    private boolean mIsAudio;
    private int mOutputBufferCnt;
    protected MediaCodec$BufferInfo[] mOutputBufferInfo;
    protected ByteBuffer[] mOutputBuffers;
    protected LinkedList<Integer> mOutputBuffersQ;
    private Handler mOutputHandler;
    private LocalStateNotifier mOutputState;
    private HandlerThread mOutputThread;
    private String mTag;
    
    public MediaDecoderPipe2(final InputDataSource mDataSource, final String s, final MediaFormat mediaFormat, final Surface surface, final MediaCrypto mediaCrypto) throws Exception {
        this.mDecoder = null;
        this.mInputState = new LocalStateNotifier();
        this.mOutputState = new LocalStateNotifier();
        final StringBuilder sb = new StringBuilder("MediaDecoder2");
        if (s.startsWith("audio/")) {
            this.mIsAudio = true;
            sb.append("Audio");
            this.mTag = sb.toString();
        }
        else if (s.startsWith("video/")) {
            this.mIsAudio = false;
            sb.append("Video");
            this.mTag = sb.toString();
        }
        else if (Log.isLoggable(this.mTag, 6)) {
            Log.e(this.mTag, s + " is not valid");
        }
        Log.d(this.mTag, "creating ... ");
        this.mDataSource = mDataSource;
        this.createDecoder(s, mediaCrypto);
        if (mediaCrypto != null) {
            this.mEncrypted = true;
        }
        else {
            this.mEncrypted = false;
        }
        this.configureDecoder(mediaFormat, surface, mediaCrypto);
        this.startDecoder();
        this.configureOutputBuffers();
        this.mState = -1;
    }
    
    private boolean configureDecoder(final MediaFormat mediaFormat, final Surface surface, final MediaCrypto mediaCrypto) {
        this.mDecoder.configure(mediaFormat, surface, mediaCrypto, 0);
        if (Log.isLoggable(this.mTag, 3)) {
            Log.d(this.mTag, "configureDecoder " + mediaFormat);
        }
        return true;
    }
    
    private void configureOutputBuffers() {
        try {
            this.mOutputBuffers = this.mDecoder.getOutputBuffers();
            this.mOutputBufferCnt = this.mOutputBuffers.length;
            if (Log.isLoggable(this.mTag, 3)) {
                Log.d(this.mTag, "has " + this.mOutputBufferCnt + " output buffers");
            }
            this.mOutputBufferInfo = new MediaCodec$BufferInfo[this.mOutputBufferCnt];
        }
        catch (Exception ex) {
            Log.e(this.mTag, "get un-known exception while getOutputBuffers()");
            this.mOutputBufferCnt = 0;
        }
    }
    
    private boolean createDecoder(final String s, final MediaCrypto mediaCrypto) {
        if (!this.mIsAudio) {
            final boolean b = mediaCrypto != null && mediaCrypto.requiresSecureDecoderComponent(s);
            if (AndroidUtils.getAndroidVersion() > 18) {
                this.createVideoDecoderForK(s, b);
            }
            else {
                this.createVideoDecoderPreK(s, b);
            }
        }
        if (this.mDecoder == null) {
            this.mDecoder = MediaCodec.createDecoderByType(s);
            if (Log.isLoggable(this.mTag, 3)) {
                Log.d(this.mTag, "createDecoder " + s);
            }
        }
        return true;
    }
    
    private void createInputThread() {
        final StringBuilder append = new StringBuilder().append("Inputthread");
        String s;
        if (this.mIsAudio) {
            s = "Audio";
        }
        else {
            s = "Video";
        }
        (this.mInputThread = new HandlerThread(append.append(s).toString(), -2)).start();
        this.mInputHandler = new Handler(this.mInputThread.getLooper()) {
            long frameReceived = 0L;
            
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        Log.d(MediaDecoderPipe2.this.mTag, "outputthread handler had unknown message");
                        break;
                    }
                    case 1: {
                        int dequeueInputBuffer = -1;
                    Label_0121:
                        while (true) {
                            if (!MediaDecoderPipe2.this.mInputBuffersQ.isEmpty() || MediaDecoderPipe2.this.mDecoderPause) {
                                break Label_0121;
                            }
                            while (true) {
                                while (true) {
                                    try {
                                        dequeueInputBuffer = MediaDecoderPipe2.this.mDecoder.dequeueInputBuffer(-1L);
                                        if (dequeueInputBuffer >= 0 && dequeueInputBuffer < MediaDecoderPipe2.this.mInputBufferCnt) {
                                            MediaDecoderPipe2.this.mInputBuffersQ.add(dequeueInputBuffer);
                                            if (MediaDecoderPipe2.this.mDecoderPause) {
                                                Log.d(MediaDecoderPipe2.this.mTag, "inputthread pause");
                                                return;
                                            }
                                            break;
                                        }
                                    }
                                    catch (Exception ex) {
                                        Log.d(MediaDecoderPipe2.this.mTag, "get un-documented exception as a result of dequeueInputBuffer() " + ex.getMessage());
                                        continue;
                                    }
                                    break;
                                }
                                Log.d(MediaDecoderPipe2.this.mTag, "get invlaid buffer index " + dequeueInputBuffer + " as a result of dequeueInputBuffer()");
                                continue Label_0121;
                            }
                            break;
                        }
                        if (MediaDecoderPipe2.this.mInputBuffersQ.isEmpty()) {
                            MediaDecoderPipe2.this.mInputHandler.removeMessages(1);
                            MediaDecoderPipe2.this.mInputHandler.sendEmptyMessageDelayed(1, 20L);
                            return;
                        }
                        final int intValue = MediaDecoderPipe2.this.mInputBuffersQ.peekFirst();
                        final InputDataSource.BufferMeta onRequestData = MediaDecoderPipe2.this.mDataSource.onRequestData(MediaDecoderPipe2.this.mInputBuffers[intValue]);
                        if (onRequestData.size <= 0 && onRequestData.flags == 0) {
                            MediaDecoderPipe2.this.mInputHandler.removeMessages(1);
                            MediaDecoderPipe2.this.mInputHandler.sendEmptyMessageDelayed(1, 20L);
                            return;
                        }
                        if (this.frameReceived <= 0L && Log.isLoggable(MediaDecoderPipe2.this.mTag, 3)) {
                            Log.d(MediaDecoderPipe2.this.mTag, "QueueInput " + intValue + " from " + onRequestData.offset + " size= " + onRequestData.size + " @" + onRequestData.timestamp + " ms" + " flags " + onRequestData.flags);
                        }
                        if (MediaDecoderPipe2.this.mRefClock != null && onRequestData.timestamp < MediaDecoderPipe2.this.mRefClock.get() && Log.isLoggable(MediaDecoderPipe2.this.mTag, 3)) {
                            Log.d(MediaDecoderPipe2.this.mTag, "STAT:DEC input late " + this.frameReceived + " at " + MediaDecoderPipe2.this.mRefClock.get() + " by " + (onRequestData.timestamp - MediaDecoderPipe2.this.mRefClock.get()) + " ms");
                        }
                        if ((onRequestData.flags & 0x4) != 0x0) {
                            Log.d(MediaDecoderPipe2.this.mTag, "got decoder input BUFFER_FLAG_END_OF_STREAM");
                        }
                        while (true) {
                            while (true) {
                                try {
                                    if (MediaDecoderPipe2.this.mEncrypted) {
                                        final MediaCodec$CryptoInfo mediaCodec$CryptoInfo = new MediaCodec$CryptoInfo();
                                        mediaCodec$CryptoInfo.mode = 1;
                                        if (onRequestData.nByteEncrypted.length == 0) {
                                            final byte[] array = new byte[16];
                                            for (int i = 0; i < array.length; ++i) {
                                                array[i] = 0;
                                            }
                                            mediaCodec$CryptoInfo.iv = array;
                                            mediaCodec$CryptoInfo.key = array;
                                            mediaCodec$CryptoInfo.numBytesOfClearData = new int[] { onRequestData.size };
                                            mediaCodec$CryptoInfo.numBytesOfEncryptedData = new int[] { 0 };
                                            mediaCodec$CryptoInfo.numSubSamples = 1;
                                        }
                                        else {
                                            mediaCodec$CryptoInfo.iv = onRequestData.iv;
                                            mediaCodec$CryptoInfo.key = onRequestData.key;
                                            mediaCodec$CryptoInfo.numBytesOfClearData = onRequestData.nByteInClear;
                                            mediaCodec$CryptoInfo.numBytesOfEncryptedData = onRequestData.nByteEncrypted;
                                            mediaCodec$CryptoInfo.numSubSamples = onRequestData.nSubsample;
                                        }
                                        MediaDecoderPipe2.this.mDecoder.queueSecureInputBuffer(intValue, onRequestData.offset, mediaCodec$CryptoInfo, onRequestData.timestamp * 1000L, onRequestData.flags);
                                        MediaDecoderPipe2.this.mInputBuffersQ.removeFirst();
                                        ++this.frameReceived;
                                        MediaDecoderPipe2.this.mInputHandler.removeMessages(1);
                                        MediaDecoderPipe2.this.mInputHandler.sendEmptyMessage(1);
                                        return;
                                    }
                                }
                                catch (Exception ex2) {
                                    Log.d(MediaDecoderPipe2.this.mTag, "get un-documented exception as a result of queueInputBuffer() " + ex2);
                                    return;
                                }
                                MediaDecoderPipe2.this.mDecoder.queueInputBuffer(intValue, onRequestData.offset, onRequestData.size, onRequestData.timestamp * 1000L, onRequestData.flags);
                                continue;
                            }
                        }
                        break;
                    }
                    case 2: {
                        MediaDecoderPipe2.this.mInputBuffersQ.clear();
                        synchronized (MediaDecoderPipe2.this.mInputState) {
                            MediaDecoderPipe2.this.mInputState.notify();
                            // monitorexit(MediaDecoderPipe2.access$800(this.this$0))
                            Log.d(MediaDecoderPipe2.this.mTag, "flush input done");
                        }
                    }
                    case 3: {
                        Log.d(MediaDecoderPipe2.this.mTag, "input is initialized");
                        if (MediaDecoderPipe2.this.mEventListener != null) {
                            MediaDecoderPipe2.this.mEventListener.onDecoderReady(MediaDecoderPipe2.this.mIsAudio);
                            return;
                        }
                        break;
                    }
                    case 4: {
                        Log.d(MediaDecoderPipe2.this.mTag, "input is stopped");
                    }
                }
            }
        };
    }
    
    private void createOutputThread() {
        final StringBuilder append = new StringBuilder().append("Outputthread");
        String s;
        if (this.mIsAudio) {
            s = "Audio";
        }
        else {
            s = "Video";
        }
        (this.mOutputThread = new HandlerThread(append.append(s).toString(), -2)).start();
        this.mOutputHandler = new Handler(this.mOutputThread.getLooper()) {
            long frameDecoded = 0L;
            final /* synthetic */ MediaDecoderPipe2 this$0;
            
            public void handleMessage(Message access$1300) {
                switch (((Message)access$1300).what) {
                    default: {
                        Log.d(MediaDecoderPipe2.this.mTag, "outputthread handler had unknown message");
                    }
                    case 1: {
                        if (MediaDecoderPipe2.this.mDecoderPause) {
                            Log.d(MediaDecoderPipe2.this.mTag, "outputthread pause");
                            return;
                        }
                        while (true) {
                            final MediaCodec$BufferInfo mediaCodec$BufferInfo = new MediaCodec$BufferInfo();
                        Label_0104:
                            while (true) {
                                int dequeueOutputBuffer;
                                try {
                                    dequeueOutputBuffer = MediaDecoderPipe2.this.mDecoder.dequeueOutputBuffer(mediaCodec$BufferInfo, -1L);
                                    if (dequeueOutputBuffer == -1) {
                                        MediaDecoderPipe2.this.mOutputHandler.removeMessages(1);
                                        MediaDecoderPipe2.this.mOutputHandler.sendEmptyMessage(1);
                                        return;
                                    }
                                }
                                catch (Exception ex) {
                                    Log.d(MediaDecoderPipe2.this.mTag, "get un-documented exception as a result of dequeueOutputBuffer() " + ex.getMessage());
                                    return;
                                }
                                if (dequeueOutputBuffer == -3) {
                                    Log.d(MediaDecoderPipe2.this.mTag, "OUTPUT_BUFFERS_CHANGED");
                                    MediaDecoderPipe2.this.configureOutputBuffers();
                                    continue Label_0104;
                                }
                                if (dequeueOutputBuffer == -2) {
                                    final MediaFormat outputFormat = MediaDecoderPipe2.this.mDecoder.getOutputFormat();
                                    if (Log.isLoggable(MediaDecoderPipe2.this.mTag, 3)) {
                                        Log.d(MediaDecoderPipe2.this.mTag, "OUTPUT_FORMAT_CHANGED " + outputFormat);
                                        continue Label_0104;
                                    }
                                    continue Label_0104;
                                }
                                else {
                                    if (dequeueOutputBuffer < 0 || dequeueOutputBuffer >= MediaDecoderPipe2.this.mOutputBufferCnt) {
                                        Log.e(MediaDecoderPipe2.this.mTag, dequeueOutputBuffer + " is not valid");
                                        continue Label_0104;
                                    }
                                Label_0587_Outer:
                                    while (true) {
                                        while (true) {
                                        Label_0641:
                                            while (true) {
                                                synchronized (MediaDecoderPipe2.this.mOutputBuffersQ) {
                                                    MediaDecoderPipe2.this.mOutputBuffersQ.add(dequeueOutputBuffer);
                                                    MediaDecoderPipe2.this.mOutputBufferInfo[dequeueOutputBuffer] = mediaCodec$BufferInfo;
                                                    // monitorexit(this.this$0.mOutputBuffersQ)
                                                    if ((mediaCodec$BufferInfo.flags & 0x4) != 0x0) {
                                                        Log.d(MediaDecoderPipe2.this.mTag, "got decoder output BUFFER_FLAG_END_OF_STREAM");
                                                    }
                                                    if (this.frameDecoded <= 0L && Log.isLoggable(MediaDecoderPipe2.this.mTag, 3)) {
                                                        Log.d(MediaDecoderPipe2.this.mTag, "DequeueOutputBuffer " + dequeueOutputBuffer + " size= " + mediaCodec$BufferInfo.size + " @" + mediaCodec$BufferInfo.presentationTimeUs / 1000L + " ms");
                                                    }
                                                    if (MediaDecoderPipe2.this.mRefClock != null && mediaCodec$BufferInfo.presentationTimeUs / 1000L <= MediaDecoderPipe2.this.mRefClock.get() && Log.isLoggable(MediaDecoderPipe2.this.mTag, 3)) {
                                                        Log.d(MediaDecoderPipe2.this.mTag, "STAT:DEC output late " + this.frameDecoded + " at " + MediaDecoderPipe2.this.mRefClock.get() + " by " + (mediaCodec$BufferInfo.presentationTimeUs / 1000L - MediaDecoderPipe2.this.mRefClock.get()) + " ms");
                                                    }
                                                    ++this.frameDecoded;
                                                    if (MediaDecoderPipe2.this.mIsAudio) {
                                                        dequeueOutputBuffer = MediaDecoderPipe2.this.mOutputBufferCnt - 1;
                                                        if (dequeueOutputBuffer > 0) {
                                                            break Label_0641;
                                                        }
                                                        final int n = 1;
                                                        if (this.frameDecoded == n && MediaDecoderPipe2.this.mEventListener != null) {
                                                            MediaDecoderPipe2.this.mEventListener.onDecoderStarted(MediaDecoderPipe2.this.mIsAudio);
                                                            continue Label_0104;
                                                        }
                                                        continue Label_0104;
                                                    }
                                                }
                                                dequeueOutputBuffer = 1;
                                                continue Label_0587_Outer;
                                            }
                                            int n;
                                            if ((n = dequeueOutputBuffer) >= 4) {
                                                n = 4;
                                                continue;
                                            }
                                            continue;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case 2: {
                        synchronized (MediaDecoderPipe2.this.mOutputState) {
                            MediaDecoderPipe2.this.mOutputState.notify();
                            // monitorexit(MediaDecoderPipe2.access$1300(this.this$0))
                            this.frameDecoded = 0L;
                            Log.d(MediaDecoderPipe2.this.mTag, "flush output done");
                        }
                    }
                    case 3: {
                        Log.d(MediaDecoderPipe2.this.mTag, "output is initialized");
                    }
                    case 4: {
                        Log.d(MediaDecoderPipe2.this.mTag, "output stopping...");
                        while (true) {
                            try {
                                MediaDecoderPipe2.this.mDecoder.stop();
                                access$1300 = (Exception)MediaDecoderPipe2.this.mOutputState;
                                // monitorenter(access$1300)
                                final Handler handler = this;
                                final MediaDecoderPipe2 mediaDecoderPipe2 = handler.this$0;
                                final LocalStateNotifier localStateNotifier = mediaDecoderPipe2.mOutputState;
                                localStateNotifier.notify();
                                final Exception ex2 = access$1300;
                                // monitorexit(ex2)
                                final Handler handler2 = this;
                                final MediaDecoderPipe2 mediaDecoderPipe3 = handler2.this$0;
                                final String s = mediaDecoderPipe3.mTag;
                                final String s2 = "output is stopped";
                                Log.d(s, s2);
                                return;
                            }
                            catch (Exception access$1300) {
                                Log.d(MediaDecoderPipe2.this.mTag, "get un-documented exception as a result of stop() " + access$1300.getMessage());
                                continue;
                            }
                            break;
                        }
                        try {
                            final Handler handler = this;
                            final MediaDecoderPipe2 mediaDecoderPipe2 = handler.this$0;
                            final LocalStateNotifier localStateNotifier = mediaDecoderPipe2.mOutputState;
                            localStateNotifier.notify();
                            final Exception ex2 = access$1300;
                            // monitorexit(ex2)
                            final Handler handler2 = this;
                            final MediaDecoderPipe2 mediaDecoderPipe3 = handler2.this$0;
                            final String s = mediaDecoderPipe3.mTag;
                            final String s2 = "output is stopped";
                            Log.d(s, s2);
                            return;
                        }
                        finally {
                        }
                        // monitorexit(access$1300)
                        break;
                    }
                }
            }
        };
    }
    
    private void createVideoDecoderForK(final String s, final boolean b) {
        final String adaptivePlaybackDecoderName = AdaptiveMediaDecoderHelper.getAdaptivePlaybackDecoderName(s);
        if (adaptivePlaybackDecoderName == null) {
            Log.e(this.mTag, "createVideoDecoderForK " + s + "has no adaptive decoder");
            return;
        }
        String string = adaptivePlaybackDecoderName;
        if (b) {
            string = adaptivePlaybackDecoderName + ".secure";
        }
        if (Log.isLoggable(this.mTag, 3)) {
            Log.d(this.mTag, "createVideoDecoderForK " + s + ", name " + string);
        }
        try {
            this.mDecoder = MediaCodec.createByCodecName(string);
        }
        catch (Exception ex) {
            Log.d(this.mTag, "createVideoDecoderForK " + s + ", name " + string + " failed");
            this.mDecoder = null;
        }
    }
    
    private void createVideoDecoderPreK(final String p0, final boolean p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: iload_2        
        //     1: ifeq            168
        //     4: aload_0        
        //     5: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //     8: ldc_w           "try OMX.qcom.video.decoder.avc.smoothstreaming.secure"
        //    11: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    14: pop            
        //    15: aload_0        
        //    16: ldc_w           "OMX.qcom.video.decoder.avc.smoothstreaming.secure"
        //    19: invokestatic    android/media/MediaCodec.createByCodecName:(Ljava/lang/String;)Landroid/media/MediaCodec;
        //    22: putfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //    25: aload_0        
        //    26: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //    29: ifnonnull       95
        //    32: aload_0        
        //    33: aload_1        
        //    34: invokespecial   com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.getSecureDecoderNameForMime:(Ljava/lang/String;)Ljava/lang/String;
        //    37: astore_3       
        //    38: aload_0        
        //    39: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //    42: iconst_3       
        //    43: invokestatic    com/netflix/mediaclient/Log.isLoggable:(Ljava/lang/String;I)Z
        //    46: ifeq            87
        //    49: aload_0        
        //    50: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //    53: new             Ljava/lang/StringBuilder;
        //    56: dup            
        //    57: invokespecial   java/lang/StringBuilder.<init>:()V
        //    60: ldc_w           "createSecureDecoder "
        //    63: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    66: aload_1        
        //    67: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    70: ldc_w           ", name "
        //    73: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    76: aload_3        
        //    77: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    80: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    83: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    86: pop            
        //    87: aload_0        
        //    88: aload_3        
        //    89: invokestatic    android/media/MediaCodec.createByCodecName:(Ljava/lang/String;)Landroid/media/MediaCodec;
        //    92: putfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //    95: return         
        //    96: astore_3       
        //    97: aload_0        
        //    98: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   101: ldc_w           "createSecureDecoder OMX.qcom.video.decoder.avc.smoothstreaming.secure failed"
        //   104: invokestatic    com/netflix/mediaclient/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   107: pop            
        //   108: aload_0        
        //   109: aconst_null    
        //   110: putfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //   113: goto            25
        //   116: astore          4
        //   118: aload_0        
        //   119: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   122: new             Ljava/lang/StringBuilder;
        //   125: dup            
        //   126: invokespecial   java/lang/StringBuilder.<init>:()V
        //   129: ldc_w           "createSecureDecoder "
        //   132: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   135: aload_1        
        //   136: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   139: ldc_w           ", name "
        //   142: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   145: aload_3        
        //   146: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   149: ldc_w           " failed"
        //   152: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   155: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   158: invokestatic    com/netflix/mediaclient/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   161: pop            
        //   162: aload_0        
        //   163: aconst_null    
        //   164: putfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //   167: return         
        //   168: aload_0        
        //   169: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   172: ldc_w           "try OMX.qcom.video.decoder.avc.smoothstreaming"
        //   175: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   178: pop            
        //   179: aload_0        
        //   180: ldc_w           "OMX.qcom.video.decoder.avc.smoothstreaming"
        //   183: invokestatic    android/media/MediaCodec.createByCodecName:(Ljava/lang/String;)Landroid/media/MediaCodec;
        //   186: putfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //   189: return         
        //   190: astore_1       
        //   191: aload_0        
        //   192: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   195: ldc_w           "createSecureDecoder OMX.qcom.video.decoder.avc.smoothstreaming failed"
        //   198: invokestatic    com/netflix/mediaclient/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   201: pop            
        //   202: aload_0        
        //   203: aconst_null    
        //   204: putfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //   207: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  4      25     96     116    Ljava/lang/Exception;
        //  87     95     116    168    Ljava/lang/Exception;
        //  168    189    190    208    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0087:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2592)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String getSecureDecoderNameForMime(final String s) {
        for (int codecCount = MediaCodecList.getCodecCount(), i = 0; i < codecCount; ++i) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                final String[] supportedTypes = codecInfo.getSupportedTypes();
                for (int j = 0; j < supportedTypes.length; ++j) {
                    if (supportedTypes[j].equalsIgnoreCase(s)) {
                        return codecInfo.getName() + ".secure";
                    }
                }
            }
        }
        return null;
    }
    
    private boolean startDecoder() {
        this.mDecoder.start();
        this.mInputBuffers = this.mDecoder.getInputBuffers();
        this.mInputBufferCnt = this.mInputBuffers.length;
        if (Log.isLoggable(this.mTag, 3)) {
            Log.d(this.mTag, "has " + this.mInputBufferCnt + " input buffers");
        }
        this.mInputBuffersQ = new LinkedList<Integer>();
        this.mOutputBuffersQ = new LinkedList<Integer>();
        this.createInputThread();
        this.createOutputThread();
        this.mInputHandler.sendEmptyMessageDelayed(3, 20L);
        return true;
    }
    
    abstract void createRenderer();
    
    @Override
    public void flush() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0        
        //     1: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //     4: invokevirtual   android/media/MediaCodec.flush:()V
        //     7: aload_0        
        //     8: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //    11: ldc_w           "flushinput"
        //    14: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    17: pop            
        //    18: aload_0        
        //    19: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputHandler:Landroid/os/Handler;
        //    22: ifnull          58
        //    25: aload_0        
        //    26: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputState:Lcom/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2$LocalStateNotifier;
        //    29: astore_1       
        //    30: aload_1        
        //    31: monitorenter   
        //    32: aload_0        
        //    33: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputHandler:Landroid/os/Handler;
        //    36: iconst_1       
        //    37: invokevirtual   android/os/Handler.removeMessages:(I)V
        //    40: aload_0        
        //    41: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputHandler:Landroid/os/Handler;
        //    44: iconst_2       
        //    45: invokevirtual   android/os/Handler.sendEmptyMessage:(I)Z
        //    48: pop            
        //    49: aload_0        
        //    50: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputState:Lcom/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2$LocalStateNotifier;
        //    53: invokevirtual   java/lang/Object.wait:()V
        //    56: aload_1        
        //    57: monitorexit    
        //    58: aload_0        
        //    59: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //    62: ldc_w           "flushoutput"
        //    65: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    68: pop            
        //    69: aload_0        
        //    70: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    73: ifnull          109
        //    76: aload_0        
        //    77: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputState:Lcom/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2$LocalStateNotifier;
        //    80: astore_1       
        //    81: aload_1        
        //    82: monitorenter   
        //    83: aload_0        
        //    84: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    87: iconst_1       
        //    88: invokevirtual   android/os/Handler.removeMessages:(I)V
        //    91: aload_0        
        //    92: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    95: iconst_2       
        //    96: invokevirtual   android/os/Handler.sendEmptyMessage:(I)Z
        //    99: pop            
        //   100: aload_0        
        //   101: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputState:Lcom/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2$LocalStateNotifier;
        //   104: invokevirtual   java/lang/Object.wait:()V
        //   107: aload_1        
        //   108: monitorexit    
        //   109: aload_0        
        //   110: invokevirtual   com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.flushRenderer:()V
        //   113: return         
        //   114: astore_1       
        //   115: aload_0        
        //   116: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   119: new             Ljava/lang/StringBuilder;
        //   122: dup            
        //   123: invokespecial   java/lang/StringBuilder.<init>:()V
        //   126: ldc_w           "get un-documented exception as a result of flush() "
        //   129: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   132: aload_1        
        //   133: invokevirtual   java/lang/Exception.getMessage:()Ljava/lang/String;
        //   136: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   139: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   142: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   145: pop            
        //   146: goto            7
        //   149: astore_2       
        //   150: ldc             "MediaDecoder2"
        //   152: ldc_w           "flushinput interrupted"
        //   155: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   158: pop            
        //   159: goto            56
        //   162: astore_2       
        //   163: aload_1        
        //   164: monitorexit    
        //   165: aload_2        
        //   166: athrow         
        //   167: astore_2       
        //   168: ldc             "MediaDecoder2"
        //   170: ldc_w           "flushoutput interrupted"
        //   173: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   176: pop            
        //   177: goto            107
        //   180: astore_2       
        //   181: aload_1        
        //   182: monitorexit    
        //   183: aload_2        
        //   184: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  0      7      114    149    Ljava/lang/Exception;
        //  32     49     162    167    Any
        //  49     56     149    162    Ljava/lang/InterruptedException;
        //  49     56     162    167    Any
        //  56     58     162    167    Any
        //  83     100    180    185    Any
        //  100    107    167    180    Ljava/lang/InterruptedException;
        //  100    107    180    185    Any
        //  107    109    180    185    Any
        //  150    159    162    167    Any
        //  163    165    162    167    Any
        //  168    177    180    185    Any
        //  181    183    180    185    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 97, Size: 97
        //     at java.util.ArrayList.rangeCheck(ArrayList.java:653)
        //     at java.util.ArrayList.get(ArrayList.java:429)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3303)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    abstract void flushRenderer();
    
    void hexprint(final byte[] array) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            sb.append(String.format("%02x  ", array[i]));
        }
        Log.d(this.mTag, sb.toString());
    }
    
    void hexprint(final int[] array) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            sb.append(String.format("%04x  ", array[i]));
        }
        Log.d(this.mTag, sb.toString());
    }
    
    @Override
    public void pause() {
        Log.d(this.mTag, "pause()");
        this.mDecoderPause = true;
        this.pauseRenderer();
    }
    
    abstract void pauseRenderer();
    
    @Override
    public void restart() {
        Log.d(this.mTag, "restart()");
        this.mDecoderPause = false;
        this.mInputHandler.sendEmptyMessage(1);
        this.mOutputHandler.sendEmptyMessage(1);
    }
    
    @Override
    public void start() {
        Log.d(this.mTag, "start()");
        this.mDecoderPause = false;
        this.mInputHandler.sendEmptyMessage(1);
        this.mOutputHandler.sendEmptyMessage(1);
        this.createRenderer();
        this.startRenderer();
    }
    
    abstract void startRenderer();
    
    @Override
    public void stop() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0        
        //     1: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //     4: ldc_w           "stop()"
        //     7: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    10: pop            
        //    11: aload_0        
        //    12: invokevirtual   com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.stopRenderer:()V
        //    15: aload_0        
        //    16: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputHandler:Landroid/os/Handler;
        //    19: ifnull          38
        //    22: aload_0        
        //    23: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputHandler:Landroid/os/Handler;
        //    26: iconst_1       
        //    27: invokevirtual   android/os/Handler.removeMessages:(I)V
        //    30: aload_0        
        //    31: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputHandler:Landroid/os/Handler;
        //    34: iconst_2       
        //    35: invokevirtual   android/os/Handler.removeMessages:(I)V
        //    38: aload_0        
        //    39: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputThread:Landroid/os/HandlerThread;
        //    42: ifnull          53
        //    45: aload_0        
        //    46: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputThread:Landroid/os/HandlerThread;
        //    49: invokevirtual   android/os/HandlerThread.quit:()Z
        //    52: pop            
        //    53: aload_0        
        //    54: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //    57: ldc_w           "input thread stopped"
        //    60: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //    63: pop            
        //    64: aload_0        
        //    65: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    68: ifnull          112
        //    71: aload_0        
        //    72: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    75: iconst_1       
        //    76: invokevirtual   android/os/Handler.removeMessages:(I)V
        //    79: aload_0        
        //    80: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    83: iconst_2       
        //    84: invokevirtual   android/os/Handler.removeMessages:(I)V
        //    87: aload_0        
        //    88: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputHandler:Landroid/os/Handler;
        //    91: iconst_4       
        //    92: invokevirtual   android/os/Handler.sendEmptyMessage:(I)Z
        //    95: pop            
        //    96: aload_0        
        //    97: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputState:Lcom/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2$LocalStateNotifier;
        //   100: astore_1       
        //   101: aload_1        
        //   102: monitorenter   
        //   103: aload_0        
        //   104: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputState:Lcom/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2$LocalStateNotifier;
        //   107: invokevirtual   java/lang/Object.wait:()V
        //   110: aload_1        
        //   111: monitorexit    
        //   112: aload_0        
        //   113: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputThread:Landroid/os/HandlerThread;
        //   116: ifnull          127
        //   119: aload_0        
        //   120: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputThread:Landroid/os/HandlerThread;
        //   123: invokevirtual   android/os/HandlerThread.quit:()Z
        //   126: pop            
        //   127: aload_0        
        //   128: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   131: ldc_w           "output thread stopped"
        //   134: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   137: pop            
        //   138: aload_0        
        //   139: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputThread:Landroid/os/HandlerThread;
        //   142: ifnull          152
        //   145: aload_0        
        //   146: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mInputThread:Landroid/os/HandlerThread;
        //   149: invokevirtual   android/os/HandlerThread.join:()V
        //   152: aload_0        
        //   153: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputThread:Landroid/os/HandlerThread;
        //   156: ifnull          166
        //   159: aload_0        
        //   160: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mOutputThread:Landroid/os/HandlerThread;
        //   163: invokevirtual   android/os/HandlerThread.join:()V
        //   166: aload_0        
        //   167: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   170: ldc_w           "release()"
        //   173: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   176: pop            
        //   177: aload_0        
        //   178: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mDecoder:Landroid/media/MediaCodec;
        //   181: invokevirtual   android/media/MediaCodec.release:()V
        //   184: return         
        //   185: astore_2       
        //   186: ldc             "MediaDecoder2"
        //   188: ldc_w           "stop output interrupted"
        //   191: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   194: pop            
        //   195: goto            110
        //   198: astore_2       
        //   199: aload_1        
        //   200: monitorexit    
        //   201: aload_2        
        //   202: athrow         
        //   203: astore_1       
        //   204: aload_0        
        //   205: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   208: ldc_w           "input/output thread is interrupted"
        //   211: invokestatic    com/netflix/mediaclient/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   214: pop            
        //   215: goto            166
        //   218: astore_1       
        //   219: aload_0        
        //   220: getfield        com/netflix/mediaclient/media/JPlayer/MediaDecoderPipe2.mTag:Ljava/lang/String;
        //   223: ldc_w           "get un-documented exception as a result of releas()"
        //   226: invokestatic    com/netflix/mediaclient/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
        //   229: pop            
        //   230: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  103    110    185    198    Ljava/lang/InterruptedException;
        //  103    110    198    203    Any
        //  110    112    198    203    Any
        //  138    152    203    218    Ljava/lang/InterruptedException;
        //  152    166    203    218    Ljava/lang/InterruptedException;
        //  177    184    218    231    Ljava/lang/Exception;
        //  186    195    198    203    Any
        //  199    201    198    203    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 114, Size: 114
        //     at java.util.ArrayList.rangeCheck(ArrayList.java:653)
        //     at java.util.ArrayList.get(ArrayList.java:429)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3303)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    abstract void stopRenderer();
    
    @Override
    public void unpause() {
        Log.d(this.mTag, "unpause()");
        this.mDecoderPause = false;
        this.mInputHandler.sendEmptyMessage(1);
        this.mOutputHandler.sendEmptyMessage(1);
        this.unpauseRenderer();
    }
    
    abstract void unpauseRenderer();
    
    protected class LocalStateNotifier
    {
        private static final int STATE_FLUSHED = 5;
        private static final int STATE_FLUSHING = 4;
        private static final int STATE_PAUSED = 1;
        private static final int STATE_PAUSING = 2;
        private static final int STATE_PLAYING = 3;
        private int mState;
        
        protected LocalStateNotifier() {
            this.mState = 1;
        }
        
        boolean isFlushed() {
            synchronized (this) {
                return this.mState == 5;
            }
        }
        
        boolean isFlushing() {
            synchronized (this) {
                return this.mState == 4;
            }
        }
        
        boolean isPaused() {
            boolean b = true;
            synchronized (this) {
                if (this.mState != 1) {
                    b = false;
                }
                return b;
            }
        }
        
        boolean isPausing() {
            synchronized (this) {
                return this.mState == 2;
            }
        }
        
        boolean isPlaying() {
            synchronized (this) {
                return this.mState == 3;
            }
        }
        
        void onFlushed() {
            synchronized (this) {
                this.mState = 5;
            }
        }
        
        void onFlushing() {
            synchronized (this) {
                this.mState = 4;
            }
        }
        
        void onPaused() {
            synchronized (this) {
                this.mState = 1;
            }
        }
        
        void onPausing() {
            synchronized (this) {
                this.mState = 2;
            }
        }
        
        void onPlaying() {
            synchronized (this) {
                this.mState = 3;
            }
        }
    }
}