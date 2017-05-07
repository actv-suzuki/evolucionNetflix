// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.logging;

public class UserData
{
    public String accountCountry;
    public String accountOwnerToken;
    public String currentProfileGuid;
    public String currentProfileToken;
    public String deviceCategory;
    public String esn;
    public String geoLocationCountry;
    public String languages;
    public String netflixId;
    public String secureNetflixId;
    
    @Override
    public String toString() {
        return "UserData [accountOwnerToken=" + this.accountOwnerToken + ", currentProfileToken=" + this.currentProfileToken + ", currentProfileGuid=" + this.currentProfileGuid + ", netflixId=" + this.netflixId + ", secureNetflixId=" + this.secureNetflixId + ", esn=" + this.esn + ", deviceCategory=" + this.deviceCategory + ", accountCountry=" + this.accountCountry + ", geoLocationCountry=" + this.geoLocationCountry + ", languages=" + this.languages + "]";
    }
}
