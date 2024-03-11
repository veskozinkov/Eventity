package helper_classes.notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import vz.apps.dailyevents.R;

import constants.Constants;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/notification_sound");

        RemoteViews customNotificationNormalLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification_normal);
        customNotificationNormalLayout.setTextViewText(R.id.title_TextView, intent.getStringExtra(Constants.NOTIFICATION_TITLE));
        customNotificationNormalLayout.setTextViewText(R.id.content_TextView, intent.getStringExtra(Constants.NOTIFICATION_CONTENT));

        RemoteViews customNotificationExpandedLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification_expanded);
        customNotificationExpandedLayout.setTextViewText(R.id.title_TextView, intent.getStringExtra(Constants.NOTIFICATION_TITLE));
        customNotificationExpandedLayout.setTextViewText(R.id.content_TextView, intent.getStringExtra(Constants.NOTIFICATION_CONTENT));

        Notification notification = new NotificationCompat.Builder(context, Constants.EVENT_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLights(ContextCompat.getColor(context, R.color.colorPrimary), Constants.NOTIFICATION_LIGHTS_ON, Constants.NOTIFICATION_LIGHTS_OFF)
                .setVibrate(Constants.NOTIFICATION_VIBRATION_PATTERN)
                .setSound(sound)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(customNotificationNormalLayout)
                .setCustomBigContentView(customNotificationExpandedLayout)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(intent.getIntExtra(Constants.NOTIFICATION_ID, 0), notification);
    }
}
