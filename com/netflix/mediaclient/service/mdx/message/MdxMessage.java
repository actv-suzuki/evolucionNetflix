// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.mdx.message;

import org.json.JSONObject;

public abstract class MdxMessage
{
    protected static final String PROPERTY_xid = "xid";
    protected static final String TAG = "nf_mdx";
    public static final String TYPE_AUDIO_SUBTITLES_CHANGED = "AUDIO_SUBTITLES_CHANGED";
    public static final String TYPE_AUDIO_SUBTITLES_SETTINGS = "AUDIO_SUBTITLES_SETTINGS";
    public static final String TYPE_DIALOG_CANCEL = "DIALOG_CANCEL";
    public static final String TYPE_DIALOG_RESPONSE = "DIALOG_RESPONSE";
    public static final String TYPE_DIALOG_SHOW = "DIALOG_SHOW";
    public static final String TYPE_GET_AUDIO_SUBTITLES = "GET_AUDIO_SUBTITLES";
    public static final String TYPE_HANDSHAKE = "HANDSHAKE";
    public static final String TYPE_HANDSHAKE_ACCEPTED = "HANDSHAKE_ACCEPTED";
    public static final String TYPE_MESSAGE_IGNORED = "MESSAGE_IGNORED";
    public static final String TYPE_META_DATA_CHANGED = "META_DATA_CHANGED";
    public static final String TYPE_PLAYER_CAPABILITIES = "PLAYER_CAPABILITIES";
    public static final String TYPE_PLAYER_CURRENT_STATE = "PLAYER_CURRENT_STATE";
    public static final String TYPE_PLAYER_GET_CAPABILITIES = "PLAYER_GET_CAPABILITIES";
    public static final String TYPE_PLAYER_GET_CURRENT_STATE = "PLAYER_GET_CURRENT_STATE";
    public static final String TYPE_PLAYER_PAUSE = "PLAYER_PAUSE";
    public static final String TYPE_PLAYER_PLAY = "PLAYER_PLAY";
    public static final String TYPE_PLAYER_RESUME = "PLAYER_RESUME";
    public static final String TYPE_PLAYER_SET_AUTO_ADVANCE = "PLAYER_SET_AUTO_ADVANCE";
    public static final String TYPE_PLAYER_SET_CURRENT_TIME = "PLAYER_SET_CURRENT_TIME";
    public static final String TYPE_PLAYER_SET_VOLUME = "PLAYER_SET_VOLUME";
    public static final String TYPE_PLAYER_SKIP = "PLAYER_SKIP";
    public static final String TYPE_PLAYER_STATE_CHANGED = "PLAYER_STATE_CHANGED";
    public static final String TYPE_PLAYER_STOP = "PLAYER_STOP";
    public static final String TYPE_SET_AUDIO_SUBTITLES = "SET_AUDIO_SUBTITLES";
    protected JSONObject mJson;
    private String mName;
    
    protected MdxMessage(final String mName) {
        this.mJson = new JSONObject();
        this.mName = mName;
    }
    
    public String messageName() {
        return this.mName;
    }
    
    public JSONObject messageObject() {
        return this.mJson;
    }
}