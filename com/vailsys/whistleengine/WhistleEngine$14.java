// 
// Decompiled by Procyon v0.5.30
// 

package com.vailsys.whistleengine;

import android.os.Build$VERSION;
import android.os.IBinder;
import android.content.Intent;
import java.util.Iterator;
import java.util.Map;
import android.util.Log;
import android.app.Notification;
import android.os.Handler;
import android.content.Context;
import android.app.Service;

class WhistleEngine$14 implements Runnable
{
    final /* synthetic */ WhistleEngine this$0;
    final /* synthetic */ int val$line;
    
    WhistleEngine$14(final WhistleEngine this$0, final int val$line) {
        this.this$0 = this$0;
        this.val$line = val$line;
    }
    
    @Override
    public void run() {
        if (this.this$0.eventListener != null) {
            this.this$0.eventListener.networkFailure(this.val$line);
        }
    }
}