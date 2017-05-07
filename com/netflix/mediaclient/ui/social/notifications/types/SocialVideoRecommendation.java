// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.social.notifications.types;

import android.widget.Button;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.netflix.mediaclient.ui.social.notifications.SocialNotificationViewHolder;
import com.netflix.mediaclient.util.StringUtils;
import com.netflix.mediaclient.ui.player.PlayerActivity;
import com.netflix.mediaclient.protocol.nflx.SendThanksToSocialNotificationActionHandler;
import android.app.PendingIntent;
import com.netflix.mediaclient.ui.common.PlayContext;
import com.netflix.mediaclient.ui.details.DetailsActivity;
import com.netflix.mediaclient.ui.common.PlayContextImp;
import android.content.Context;
import com.netflix.mediaclient.service.pushnotification.MessageData;
import com.netflix.mediaclient.service.webclient.model.leafs.social.SocialNotificationsListSummary;
import com.netflix.mediaclient.service.webclient.model.leafs.social.SocialNotificationSummary;
import android.support.v4.app.NotificationCompat;

public class SocialVideoRecommendation extends SocialNotification
{
    @Override
    protected void addNotificationActions(final NotificationCompat.Builder builder, final SocialNotificationSummary socialNotificationSummary, final SocialNotificationsListSummary socialNotificationsListSummary, final MessageData messageData, final Context context) {
        super.addNotificationActions(builder, socialNotificationSummary, socialNotificationsListSummary, messageData, context);
        final int n = (int)System.currentTimeMillis();
        builder.addAction(new NotificationCompat.Action(2130837654, context.getString(2131493414), PendingIntent.getBroadcast(context.getApplicationContext(), n, DetailsActivity.getIntentForBroadcastReceiver(socialNotificationSummary.getVideoSummary().getType(), socialNotificationSummary.getVideoSummary().getId(), socialNotificationSummary.getVideoSummary().getTitle(), new PlayContextImp(socialNotificationsListSummary.getRequestId(), socialNotificationsListSummary.getMDPTrackId(), 0, 0), null, messageData), 134217728)));
        builder.addAction(new NotificationCompat.Action(2130837699, context.getString(2131493415), PendingIntent.getBroadcast(context.getApplicationContext(), n, SendThanksToSocialNotificationActionHandler.getSayThanksIntent(context, socialNotificationSummary.getId(), socialNotificationSummary.getStoryId(), true, messageData), 134217728)));
        builder.addAction(new NotificationCompat.Action(2130837665, context.getString(2131493416), PendingIntent.getBroadcast(context.getApplicationContext(), n, PlayerActivity.createColdStartIntent(socialNotificationSummary.getVideoSummary().getId(), socialNotificationSummary.getVideoSummary().getType(), new PlayContextImp(socialNotificationsListSummary.getRequestId(), socialNotificationsListSummary.getPlayerTrackId(), 0, 0), messageData), 134217728)));
    }
    
    @Override
    protected void addNotificationText(final NotificationCompat.Builder builder, final NotificationCompat.BigPictureStyle bigPictureStyle, final SocialNotificationSummary socialNotificationSummary, final Context context) {
        String s;
        if (StringUtils.isEmpty(socialNotificationSummary.getMessageString())) {
            s = context.getResources().getString(2131493397);
        }
        else {
            s = "\"" + socialNotificationSummary.getMessageString() + "\"";
        }
        bigPictureStyle.setSummaryText(s);
        builder.setContentText(s);
    }
    
    @Override
    public TextView getAddToMyListButton(final SocialNotificationViewHolder socialNotificationViewHolder) {
        return (TextView)socialNotificationViewHolder.getRightButton();
    }
    
    @Override
    public SocialNotificationSummary.NotificationTypes getNotificationType() {
        return SocialNotificationSummary.NotificationTypes.VIDEO_RECOMMENDATION;
    }
    
    @Override
    public View getSayThanksButton(final SocialNotificationViewHolder socialNotificationViewHolder) {
        return (View)socialNotificationViewHolder.getLeftButton();
    }
    
    @Override
    public void initView(final View view, final SocialNotificationViewHolder socialNotificationViewHolder, final SocialNotificationSummary socialNotificationSummary, final Context context) {
        boolean enabled = true;
        super.initView(view, socialNotificationViewHolder, socialNotificationSummary, context);
        socialNotificationViewHolder.getMiddleTextView().setText((CharSequence)Html.fromHtml(context.getResources().getString(2131493401, new Object[] { socialNotificationSummary.getVideoSummary().getTitle() })));
        socialNotificationViewHolder.getLeftButton().setVisibility(0);
        final Button leftButton = socialNotificationViewHolder.getLeftButton();
        if (socialNotificationSummary.getWasThanked()) {
            enabled = false;
        }
        leftButton.setEnabled(enabled);
        final Button leftButton2 = socialNotificationViewHolder.getLeftButton();
        int text;
        if (socialNotificationSummary.getWasThanked()) {
            text = 2131493407;
        }
        else {
            text = 2131493406;
        }
        leftButton2.setText(text);
        socialNotificationViewHolder.getRightButton().setVisibility(0);
    }
}
