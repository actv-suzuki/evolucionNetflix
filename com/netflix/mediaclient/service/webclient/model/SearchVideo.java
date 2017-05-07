// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.webclient.model;

import com.netflix.mediaclient.servicemgr.VideoType;
import com.netflix.mediaclient.service.webclient.model.branches.Video;

public class SearchVideo extends Video implements com.netflix.mediaclient.servicemgr.SearchVideo
{
    public SearchTitle searchTitle;
    public Summary summary;
    
    @Override
    public String getBoxshotURL() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getBoxshotURL();
    }
    
    @Override
    public String getCertification() {
        if (this.searchTitle == null) {
            return null;
        }
        return this.searchTitle.certification;
    }
    
    @Override
    public VideoType getErrorType() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getErrorType();
    }
    
    @Override
    public String getHorzDispUrl() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getHorzDispUrl();
    }
    
    @Override
    public String getId() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getId();
    }
    
    @Override
    public String getTitle() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getTitle();
    }
    
    @Override
    public String getTvCardUrl() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getTvCardUrl();
    }
    
    @Override
    public VideoType getType() {
        if (this.summary == null) {
            return null;
        }
        return this.summary.getType();
    }
    
    @Override
    public int getYear() {
        Integer value;
        if (this.searchTitle == null) {
            value = null;
        }
        else {
            value = this.searchTitle.releaseYear;
        }
        return value;
    }
    
    @Override
    public String toString() {
        return "SearchVideo [getTitle()=" + this.getTitle() + ", getType()=" + this.getType() + "]";
    }
}
