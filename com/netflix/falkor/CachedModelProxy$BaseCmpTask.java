// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.falkor;

import com.netflix.mediaclient.util.DataUtil$StringPair;
import java.util.List;
import com.google.gson.JsonObject;

abstract class CachedModelProxy$BaseCmpTask implements CachedModelProxy$CmpTaskDetails
{
    @Override
    public void customHandleResponse(final JsonObject jsonObject) {
    }
    
    @Override
    public List<DataUtil$StringPair> getOptionalRequestParams() {
        return null;
    }
    
    @Override
    public boolean shouldCollapseMissingPql() {
        return false;
    }
    
    @Override
    public boolean shouldCustomHandleResponse() {
        return false;
    }
    
    @Override
    public boolean shouldSkipCache() {
        return false;
    }
    
    @Override
    public boolean shouldUseAuthorization() {
        return true;
    }
    
    @Override
    public boolean shouldUseCacheOnly() {
        return false;
    }
    
    @Override
    public boolean shouldUseCallMethod() {
        return false;
    }
}
