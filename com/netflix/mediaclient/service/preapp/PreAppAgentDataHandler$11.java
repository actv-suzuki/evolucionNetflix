// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.preapp;

import java.util.HashSet;
import com.netflix.mediaclient.service.pservice.PDiskDataRepository$LoadCallback;
import com.netflix.mediaclient.util.AndroidUtils;
import com.netflix.mediaclient.service.pservice.PServiceWidgetAgent;
import com.netflix.mediaclient.service.pservice.PDiskData$ImageType;
import com.netflix.mediaclient.service.resfetcher.LoggingResourceFetcherCallback;
import com.netflix.mediaclient.service.ServiceAgent$BrowseAgentInterface;
import com.netflix.mediaclient.service.browse.BrowseAgentCallback;
import com.netflix.mediaclient.service.pservice.PService;
import android.content.Intent;
import com.netflix.model.branches.FalkorVideo;
import java.util.Map;
import com.netflix.mediaclient.servicemgr.interface_.LoMoType;
import com.netflix.mediaclient.service.pservice.PVideo;
import java.util.Iterator;
import com.netflix.mediaclient.Log;
import java.util.ArrayList;
import com.netflix.mediaclient.android.app.BackgroundTask;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.service.browse.SimpleBrowseAgentCallback;
import com.netflix.mediaclient.servicemgr.interface_.LoMo;
import com.netflix.mediaclient.servicemgr.interface_.Video;
import com.netflix.mediaclient.servicemgr.interface_.CWVideo;
import com.netflix.mediaclient.service.pservice.PDiskData$ListType;
import java.util.Set;
import com.netflix.mediaclient.servicemgr.interface_.Billboard;
import java.util.List;
import com.netflix.mediaclient.service.ServiceAgent;
import android.content.Context;
import com.netflix.mediaclient.service.pservice.PDiskDataRepository;
import com.netflix.mediaclient.service.pservice.PDiskData;
import com.netflix.mediaclient.util.data.DataRepository$DataSavedCallback;

class PreAppAgentDataHandler$11 implements Runnable
{
    final /* synthetic */ PreAppAgentDataHandler this$0;
    final /* synthetic */ DataRepository$DataSavedCallback val$callback;
    final /* synthetic */ PDiskData val$newData;
    
    PreAppAgentDataHandler$11(final PreAppAgentDataHandler this$0, final PDiskData val$newData, final DataRepository$DataSavedCallback val$callback) {
        this.this$0 = this$0;
        this.val$newData = val$newData;
        this.val$callback = val$callback;
    }
    
    @Override
    public void run() {
        PDiskDataRepository.saveData(PreAppAgentDataHandler.mContext, this.val$newData.toJsonString(), this.val$callback);
    }
}
