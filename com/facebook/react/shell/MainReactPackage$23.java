// 
// Decompiled by Procyon v0.5.30
// 

package com.facebook.react.shell;

import com.facebook.react.modules.websocket.WebSocketModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.NativeModule;
import javax.inject.Provider;

class MainReactPackage$23 implements Provider<NativeModule>
{
    final /* synthetic */ MainReactPackage this$0;
    final /* synthetic */ ReactApplicationContext val$context;
    
    MainReactPackage$23(final MainReactPackage this$0, final ReactApplicationContext val$context) {
        this.this$0 = this$0;
        this.val$context = val$context;
    }
    
    public NativeModule get() {
        return new WebSocketModule(this.val$context);
    }
}
