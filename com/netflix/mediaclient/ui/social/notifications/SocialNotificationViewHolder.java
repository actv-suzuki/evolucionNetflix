// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.social.notifications;

import android.view.View;
import android.widget.Button;
import com.netflix.mediaclient.android.widget.AdvancedImageView;
import android.widget.TextView;

public class SocialNotificationViewHolder
{
    private final TextView bottomText;
    private final AdvancedImageView friendImage;
    private final Button leftButton;
    private final TextView middleText;
    private final AdvancedImageView movieArtImage;
    private final View playButton;
    private final Button rightButton;
    private final TextView timeStampView;
    private final TextView topText;
    
    public SocialNotificationViewHolder(final AdvancedImageView friendImage, final AdvancedImageView movieArtImage, final TextView timeStampView, final TextView topText, final TextView middleText, final TextView bottomText, final Button leftButton, final Button rightButton, final View playButton) {
        this.friendImage = friendImage;
        this.movieArtImage = movieArtImage;
        this.timeStampView = timeStampView;
        this.topText = topText;
        this.middleText = middleText;
        this.bottomText = bottomText;
        this.leftButton = leftButton;
        this.rightButton = rightButton;
        this.playButton = playButton;
    }
    
    public TextView getBottomTextView() {
        return this.bottomText;
    }
    
    public AdvancedImageView getFriendImage() {
        return this.friendImage;
    }
    
    public Button getLeftButton() {
        return this.leftButton;
    }
    
    public TextView getMiddleTextView() {
        return this.middleText;
    }
    
    public AdvancedImageView getMovieArtImage() {
        return this.movieArtImage;
    }
    
    public View getPlayButton() {
        return this.playButton;
    }
    
    public Button getRightButton() {
        return this.rightButton;
    }
    
    public TextView getTimeStampView() {
        return this.timeStampView;
    }
    
    public TextView getTopTextView() {
        return this.topText;
    }
}
