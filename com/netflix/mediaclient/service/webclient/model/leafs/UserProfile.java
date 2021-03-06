// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.webclient.model.leafs;

import com.google.gson.annotations.SerializedName;
import com.netflix.mediaclient.servicemgr.interface_.user.ProfileType;
import java.util.List;
import java.util.Iterator;
import org.json.JSONException;
import com.netflix.mediaclient.Log;
import android.text.TextUtils;
import com.netflix.mediaclient.util.JsonUtils;
import org.json.JSONObject;
import com.netflix.mediaclient.util.StringUtils;
import java.util.ArrayList;

public class UserProfile implements com.netflix.mediaclient.servicemgr.interface_.user.UserProfile
{
    private static final String FIELD_AUTOPLAY_ON = "autoPlayOn";
    private static final String FIELD_AVATAR_URL = "avatarUrl";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_EPERIENCE = "experienceType";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_GEO_COUNTRY = "geoCountry";
    private static final String FIELD_IQ_ENABLED = "isIqEnabled";
    private static final String FIELD_IS_PRIMARY = "isPrimaryProfile";
    private static final String FIELD_LANGUAGES = "languages";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_MATURITY_LEVEL = "maturityLevel";
    private static final String FIELD_PROFILE_GUID = "profileId";
    private static final String FIELD_PROFILE_NAME = "profileName";
    private static final String FIELD_PROFILE_TOKEN = "userId";
    private static final String FIELD_REQ_COUNTRY = "reqCountry";
    private static final String TAG = "UserProfile";
    public UserProfile$Operation operation;
    public SubtitlePreference subtitlePreference;
    public UserProfile$Summary summary;
    
    public UserProfile() {
    }
    
    public UserProfile(final String s) {
        int i = 0;
        final SubtitlePreference subtitlePreference = null;
        this.summary = new UserProfile$Summary(this);
        this.summary.languages = new ArrayList<UserProfile$Language>();
        this.operation = new UserProfile$Operation(this);
        JSONObject jsonObject;
        try {
            if (StringUtils.isEmpty(s)) {
                jsonObject = new JSONObject();
            }
            else {
                jsonObject = new JSONObject(s);
            }
            this.summary.profileToken = JsonUtils.getString(jsonObject, "userId", (String)null);
            this.summary.profileGuid = JsonUtils.getString(jsonObject, "profileId", (String)null);
            this.summary.profileName = JsonUtils.getString(jsonObject, "profileName", (String)null);
            this.summary.firstName = JsonUtils.getString(jsonObject, "firstName", (String)null);
            this.summary.lastName = JsonUtils.getString(jsonObject, "lastName", (String)null);
            this.summary.email = JsonUtils.getString(jsonObject, "email", (String)null);
            this.summary.maturityLevel = JsonUtils.getInt(jsonObject, "maturityLevel", -1);
            this.summary.isIqEnabled = JsonUtils.getBoolean(jsonObject, "isIqEnabled", false);
            this.summary.isPrimaryProfile = JsonUtils.getBoolean(jsonObject, "isPrimaryProfile", false);
            this.summary.isAutoPlayEnabled = JsonUtils.getBoolean(jsonObject, "autoPlayOn", false);
            this.summary.experienceType = JsonUtils.getString(jsonObject, "experienceType", (String)null);
            this.summary.avatarUrl = JsonUtils.getString(jsonObject, "avatarUrl", (String)null);
            this.summary.geoCountry = JsonUtils.getString(jsonObject, "geoCountry", (String)null);
            this.summary.reqCountry = JsonUtils.getString(jsonObject, "reqCountry", (String)null);
            final String string = JsonUtils.getString(jsonObject, "languages", (String)null);
            if (StringUtils.isNotEmpty(string)) {
                for (String[] split = TextUtils.split(string, ","); i < split.length; ++i) {
                    this.summary.languages.add(new UserProfile$Language(this, split[i]));
                }
            }
        }
        catch (JSONException ex) {
            Log.d("UserProfile", "failed to create json string=" + s + " exception =" + ex);
            return;
        }
        final String string2 = JsonUtils.getString(jsonObject, "subtitleOverride", (String)null);
        SubtitlePreference subtitlePreference2;
        if (StringUtils.isEmpty(string2)) {
            subtitlePreference2 = subtitlePreference;
        }
        else {
            subtitlePreference2 = new SubtitlePreference(string2);
        }
        this.subtitlePreference = subtitlePreference2;
    }
    
    @Override
    public String getAvatarUrl() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.avatarUrl;
    }
    
    @Override
    public String getEmail() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.email;
    }
    
    public String getEperienceType() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.experienceType;
    }
    
    @Override
    public String getFirstName() {
        if (this.summary == null) {
            return null;
        }
        return StringUtils.decodeHtmlEntities(this.summary.firstName);
    }
    
    @Override
    public String getGeoCountry() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.geoCountry;
    }
    
    @Override
    public String[] getLanguages() {
        if (this.summary == null) {
            return null;
        }
        final String[] array = new String[this.summary.languages.size()];
        final Iterator<UserProfile$Language> iterator = this.summary.languages.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            array[n] = iterator.next().code;
            ++n;
        }
        return array;
    }
    
    @Override
    public String getLanguagesInCsv() {
        if (this.summary == null) {
            return null;
        }
        return TextUtils.join((CharSequence)",", (Iterable)this.getLanguagesList());
    }
    
    @Override
    public List<String> getLanguagesList() {
        if (this.summary == null) {
            return null;
        }
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<UserProfile$Language> iterator = this.summary.languages.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().code);
        }
        return list;
    }
    
    @Override
    public String getLastName() {
        if (this.summary == null) {
            return null;
        }
        return StringUtils.decodeHtmlEntities(this.summary.lastName);
    }
    
    @Override
    public int getMaturityLevel() {
        if (this.summary == null) {
            return -1;
        }
        return this.summary.maturityLevel;
    }
    
    @Override
    public String getProfileGuid() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.profileGuid;
    }
    
    @Override
    public String getProfileName() {
        if (this.summary == null) {
            return null;
        }
        return StringUtils.decodeHtmlEntities(this.summary.profileName);
    }
    
    @Override
    public String getProfileToken() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.profileToken;
    }
    
    @Override
    public ProfileType getProfileType() {
        if (this.summary != null) {
            if (this.summary.enumType == null) {
                this.summary.enumType = ProfileType.create(this.summary.experienceType);
            }
            return this.summary.enumType;
        }
        return ProfileType.STANDARD;
    }
    
    @Override
    public String getReqCountry() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.reqCountry;
    }
    
    public SubtitlePreference getSubtitlePreference() {
        return this.subtitlePreference;
    }
    
    @Override
    public boolean isAutoPlayEnabled() {
        return this.summary != null && this.summary.isAutoPlayEnabled;
    }
    
    @Override
    public boolean isIQEnabled() {
        return this.summary != null && this.summary.isIqEnabled;
    }
    
    @Override
    public boolean isKidsProfile() {
        return this.getProfileType() == ProfileType.JFK;
    }
    
    @Override
    public boolean isPrimaryProfile() {
        return this.summary != null && this.summary.isPrimaryProfile;
    }
    
    @Override
    public String toString() {
        final JSONObject jsonObject = new JSONObject();
        while (true) {
            try {
                jsonObject.put("userId", (Object)this.getProfileToken());
                jsonObject.put("profileId", (Object)this.getProfileGuid());
                jsonObject.put("profileName", (Object)this.getProfileName());
                jsonObject.put("firstName", (Object)this.getFirstName());
                jsonObject.put("lastName", (Object)this.getLastName());
                jsonObject.put("email", (Object)this.getEmail());
                jsonObject.put("maturityLevel", this.getMaturityLevel());
                jsonObject.put("isIqEnabled", this.isIQEnabled());
                jsonObject.put("isPrimaryProfile", this.isPrimaryProfile());
                jsonObject.put("autoPlayOn", this.isAutoPlayEnabled());
                jsonObject.put("experienceType", (Object)this.getEperienceType());
                jsonObject.put("avatarUrl", (Object)this.getAvatarUrl());
                jsonObject.put("languages", (Object)this.getLanguagesInCsv());
                jsonObject.put("geoCountry", (Object)this.getGeoCountry());
                jsonObject.put("reqCountry", (Object)this.getReqCountry());
                if (this.subtitlePreference != null) {
                    jsonObject.put("subtitleOverride", (Object)this.subtitlePreference.toString());
                }
                Log.d("UserProfile", "user string=" + jsonObject.toString());
                return jsonObject.toString();
            }
            catch (JSONException ex) {
                Log.d("UserProfile", "failed in json string " + ex);
                continue;
            }
            break;
        }
    }
}
