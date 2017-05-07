// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.service.webclient.model.branches;

import com.netflix.mediaclient.servicemgr.VideoType;

public class Video
{
    public static boolean isSocialVideoType(final VideoType videoType) {
        return VideoType.SOCIAL_FRIEND.equals(videoType) || VideoType.SOCIAL_GROUP.equals(videoType) || VideoType.SOCIAL_POPULAR.equals(videoType);
    }
    
    public static class Bookmark
    {
        private int bookmarkPosition;
        private long lastModified;
        
        public int getBookmarkPosition() {
            return this.bookmarkPosition;
        }
        
        public long getLastModified() {
            return this.lastModified;
        }
        
        public void setBookmarkPosition(final int bookmarkPosition) {
            this.bookmarkPosition = bookmarkPosition;
        }
        
        public void setLastModified(final long lastModified) {
            this.lastModified = lastModified;
        }
        
        @Override
        public String toString() {
            return "Bookmark [bookmarkPosition=" + this.bookmarkPosition + ", lastModified=" + this.lastModified + "]";
        }
    }
    
    public static class BookmarkStill
    {
        public String stillUrl;
    }
    
    public static class Detail
    {
        public String actors;
        public String baseUrl;
        public String bifUrl;
        public String certification;
        public String directors;
        public int endtime;
        public int episodeCount;
        public String genres;
        public String horzDispUrl;
        public boolean isAutoPlayEnabled;
        public boolean isHdAvailable;
        public boolean isNextPlayableEpisode;
        public String mdxHorzUrl;
        public String mdxVertUrl;
        public float predictedRating;
        public String quality;
        public String restUrl;
        public int runtime;
        public int seasonCount;
        public String storyImgUrl;
        public String synopsis;
        public String synopsisNarrative;
        public String tvCardUrl;
        public int year;
        
        @Override
        public String toString() {
            return "Detail [year=" + this.year + ", synopsis=" + this.synopsis + ", synopsisNarrative=" + this.synopsisNarrative + ", quality=" + this.quality + ", directors=" + this.directors + ", actors=" + this.actors + ", genres=" + this.genres + ", certification=" + this.certification + ", predictedRating=" + this.predictedRating + ", horzDispUrl=" + this.horzDispUrl + ", restUrl=" + this.restUrl + ", bifUrl=" + this.bifUrl + ", baseUrl=" + this.baseUrl + ", tvCardUrl=" + this.tvCardUrl + ", mdxHorzUrl=" + this.mdxHorzUrl + ", mdxVertUrl=" + this.mdxVertUrl + ", storyImgUrl=" + this.storyImgUrl + ", episodeCount=" + this.episodeCount + ", seasonCount=" + this.seasonCount + ", isHdAvailable=" + this.isHdAvailable + ", isAutoPlayEnabled=" + this.isAutoPlayEnabled + ", isNextPlayableEpisode=" + this.isNextPlayableEpisode + ", runtime=" + this.runtime + ", endtime=" + this.endtime + "]";
        }
    }
    
    public static class InQueue
    {
        public boolean inQueue;
        
        @Override
        public String toString() {
            return "InQueue [inQueue=" + this.inQueue + "]";
        }
    }
    
    public static class Rating
    {
        public float userRating;
        
        @Override
        public String toString() {
            return "Rating [userRating=" + this.userRating + "]";
        }
    }
    
    public static class SearchTitle
    {
        public String certification;
        public int releaseYear;
        public String title;
        
        @Override
        public String toString() {
            return this.title;
        }
    }
    
    public static class Summary implements Video
    {
        protected String boxartUrl;
        protected VideoType enumType;
        public VideoType errorType;
        public String horzDispUrl;
        protected String id;
        protected boolean isEpisode;
        protected String title;
        public String tvCardUrl;
        protected String type;
        public int videoYear;
        
        @Override
        public String getBoxshotURL() {
            return this.boxartUrl;
        }
        
        @Override
        public VideoType getErrorType() {
            return this.errorType;
        }
        
        @Override
        public String getHorzDispUrl() {
            return this.horzDispUrl;
        }
        
        @Override
        public String getId() {
            return this.id;
        }
        
        @Override
        public String getTitle() {
            return this.title;
        }
        
        @Override
        public String getTvCardUrl() {
            return this.tvCardUrl;
        }
        
        @Override
        public VideoType getType() {
            if (this.enumType == null) {
                this.enumType = VideoType.create(this.type);
            }
            return this.enumType;
        }
        
        public boolean isEpisode() {
            return this.isEpisode;
        }
        
        public void setErrorType(final VideoType errorType) {
            this.errorType = errorType;
        }
        
        @Override
        public String toString() {
            return "Summary [id=" + this.id + ", type=" + this.type + ", title=" + this.title + ", isEpisode=" + this.isEpisode + "]";
        }
    }
}
