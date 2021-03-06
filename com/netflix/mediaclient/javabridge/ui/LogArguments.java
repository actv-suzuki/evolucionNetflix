// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.javabridge.ui;

import org.json.JSONObject;

public final class LogArguments
{
    public static final String LOG_LEVEL = "logLevel";
    public static final String MSG = "msg";
    public static final String TAGS = "tags";
    public static final String TRACEAREA = "traceArea";
    public static final String TYPE = "type";
    public LogArguments$LogLevel logLevel;
    public String msg;
    public String[] tags;
    public String traceArea;
    public String type;
    
    public LogArguments(final LogArguments$LogLevel logLevel, final String msg, final String type, final String[] tags) {
        this.logLevel = logLevel;
        this.msg = msg;
        this.type = type;
        this.tags = tags;
    }
    
    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("logLevel", (Object)this.logLevel.getLevelInString());
        jsonObject.put("msg", (Object)this.msg);
        jsonObject.put("traceArea", (Object)this.traceArea);
        jsonObject.put("type", (Object)this.type);
        jsonObject.put("tags", (Object)this.tags);
        return jsonObject;
    }
}
