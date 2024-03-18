package helper_classes.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import constants.Constants;
import helper_classes.ID;
import helper_classes.Information;
import io.paperdb.Paper;
import vz.apps.dailyevents.R;

public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    public static void createNotificationChannel(Context context) {
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/notification_sound");
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        NotificationChannel notificationChannel = new NotificationChannel(Constants.EVENT_NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(context.getColor(R.color.colorPrimary));
        notificationChannel.setVibrationPattern(Constants.NOTIFICATION_VIBRATION_PATTERN);
        notificationChannel.setSound(sound, attributes);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationChannel.setDescription(context.getString(R.string.notification_channel_description));

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void scheduleNotification(Context context, DatabaseNotification databaseNotification, String week, String weekDay, String date, String time, String eventID, String notificationContent) {
        String eventID1 = eventID + "." + Constants.ID1;
        String eventID2 = eventID + "." + Constants.ID2;

        if (databaseNotification.isActive()) {
            if (databaseNotification.getIndex2() == 0) {
                switch (databaseNotification.getIndex1()) {
                    case 0:
                        schedule(context, week, weekDay, date, time, 0, 0, eventID1, context.getString(R.string.on_time_notification_title), notificationContent);
                        break;

                    case 1:
                        schedule(context, week, weekDay, date, time, 0, -5, eventID1, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                        break;

                    case 2:
                        schedule(context, week, weekDay, date, time, 0, -10, eventID1, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                        break;

                    case 3:
                        schedule(context, week, weekDay, date, time, 0, -15, eventID1, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                        break;

                    case 4:
                        schedule(context, week, weekDay, date, time, 0, -30, eventID1, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                        break;

                    case 5:
                        schedule(context, week, weekDay, date, time, -1, 0, eventID1, context.getString(R.string.early_1_hr_notification_title), notificationContent);
                        break;

                    case 6:
                        schedule(context, week, weekDay, date, time, -2, 0, eventID1, context.getString(R.string.early_2_hrs_notification_title), notificationContent);
                        break;

                    case 7:
                        schedule(context, week, weekDay, date, time, -3, 0, eventID1, context.getString(R.string.early_3_hrs_notification_title), notificationContent);
                        break;

                    case 8:
                        schedule(context, week, weekDay, date, time, -6, 0, eventID1, context.getString(R.string.early_6_hrs_notification_title), notificationContent);
                        break;

                    case 9:
                        schedule(context, week, weekDay, date, time, -12, 0, eventID1, context.getString(R.string.early_12_hrs_notification_title), notificationContent);
                        break;

                    case 10:
                        schedule(context, week, weekDay, date, time, -24, 0, eventID1, context.getString(R.string.early_24_hrs_notification_title), notificationContent);
                        break;
                }
            } else {
                switch (databaseNotification.getIndex1()) {
                    case 1:
                        schedule(context, week, weekDay, date, time, 0, -5, eventID1, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                        schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                        break;

                    case 2:
                        schedule(context, week, weekDay, date, time, 0, -10, eventID1, context.getString(R.string.early_10_mins_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 3:
                        schedule(context, week, weekDay, date, time, 0, -15, eventID1, context.getString(R.string.early_15_mins_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 4:
                        schedule(context, week, weekDay, date, time, 0, -30, eventID1, context.getString(R.string.early_30_mins_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 5:
                        schedule(context, week, weekDay, date, time, -1, 0, eventID1, context.getString(R.string.early_1_hr_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;

                            case 5:
                                schedule(context, week, weekDay, date, time, 0, -30, eventID2, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 6:
                        schedule(context, week, weekDay, date, time, -2, 0, eventID1, context.getString(R.string.early_2_hrs_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;

                            case 5:
                                schedule(context, week, weekDay, date, time, 0, -30, eventID2, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                                break;

                            case 6:
                                schedule(context, week, weekDay, date, time, -1, 0, eventID2, context.getString(R.string.early_1_hr_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 7:
                        schedule(context, week, weekDay, date, time, -3, 0, eventID1, context.getString(R.string.early_3_hrs_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;

                            case 5:
                                schedule(context, week, weekDay, date, time, 0, -30, eventID2, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                                break;

                            case 6:
                                schedule(context, week, weekDay, date, time, -1, 0, eventID2, context.getString(R.string.early_1_hr_notification_title), notificationContent);
                                break;

                            case 7:
                                schedule(context, week, weekDay, date, time, -2, 0, eventID2, context.getString(R.string.early_2_hrs_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 8:
                        schedule(context, week, weekDay, date, time, -6, 0, eventID1, context.getString(R.string.early_6_hrs_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;

                            case 5:
                                schedule(context, week, weekDay, date, time, 0, -30, eventID2, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                                break;

                            case 6:
                                schedule(context, week, weekDay, date, time, -1, 0, eventID2, context.getString(R.string.early_1_hr_notification_title), notificationContent);
                                break;

                            case 7:
                                schedule(context, week, weekDay, date, time, -2, 0, eventID2, context.getString(R.string.early_2_hrs_notification_title), notificationContent);
                                break;

                            case 8:
                                schedule(context, week, weekDay, date, time, -3, 0, eventID2, context.getString(R.string.early_3_hrs_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 9:
                        schedule(context, week, weekDay, date, time, -12, 0, eventID1, context.getString(R.string.early_12_hrs_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;

                            case 5:
                                schedule(context, week, weekDay, date, time, 0, -30, eventID2, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                                break;

                            case 6:
                                schedule(context, week, weekDay, date, time, -1, 0, eventID2, context.getString(R.string.early_1_hr_notification_title), notificationContent);
                                break;

                            case 7:
                                schedule(context, week, weekDay, date, time, -2, 0, eventID2, context.getString(R.string.early_2_hrs_notification_title), notificationContent);
                                break;

                            case 8:
                                schedule(context, week, weekDay, date, time, -3, 0, eventID2, context.getString(R.string.early_3_hrs_notification_title), notificationContent);
                                break;

                            case 9:
                                schedule(context, week, weekDay, date, time, -6, 0, eventID2, context.getString(R.string.early_6_hrs_notification_title), notificationContent);
                                break;
                        }

                        break;

                    case 10:
                        schedule(context, week, weekDay, date, time, -24, 0, eventID1, context.getString(R.string.early_24_hrs_notification_title), notificationContent);

                        switch (databaseNotification.getIndex2()) {
                            case 1:
                                schedule(context, week, weekDay, date, time, 0, 0, eventID2, context.getString(R.string.on_time_notification_title), notificationContent);
                                break;

                            case 2:
                                schedule(context, week, weekDay, date, time, 0, -5, eventID2, context.getString(R.string.early_5_mins_notification_title), notificationContent);
                                break;

                            case 3:
                                schedule(context, week, weekDay, date, time, 0, -10, eventID2, context.getString(R.string.early_10_mins_notification_title), notificationContent);
                                break;

                            case 4:
                                schedule(context, week, weekDay, date, time, 0, -15, eventID2, context.getString(R.string.early_15_mins_notification_title), notificationContent);
                                break;

                            case 5:
                                schedule(context, week, weekDay, date, time, 0, -30, eventID2, context.getString(R.string.early_30_mins_notification_title), notificationContent);
                                break;

                            case 6:
                                schedule(context, week, weekDay, date, time, -1, 0, eventID2, context.getString(R.string.early_1_hr_notification_title), notificationContent);
                                break;

                            case 7:
                                schedule(context, week, weekDay, date, time, -2, 0, eventID2, context.getString(R.string.early_2_hrs_notification_title), notificationContent);
                                break;

                            case 8:
                                schedule(context, week, weekDay, date, time, -3, 0, eventID2, context.getString(R.string.early_3_hrs_notification_title), notificationContent);
                                break;

                            case 9:
                                schedule(context, week, weekDay, date, time, -6, 0, eventID2, context.getString(R.string.early_6_hrs_notification_title), notificationContent);
                                break;

                            case 10:
                                schedule(context, week, weekDay, date, time, -12, 0, eventID2, context.getString(R.string.early_12_hrs_notification_title), notificationContent);
                                break;
                        }

                        break;
                }
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private static void schedule(Context context, String week, String weekDay, String date, String time, int hoursToRemove, int minutesToRemove, String eventID, String notificationTitle, String notificationContent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT + " " + Constants.TIME_FORMAT_24, Locale.US);
        dateFormat.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        try {
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(date + " " + time)));
        } catch (ParseException e) {
            Log.e(TAG, "scheduleNotification: ParseException: " + e.getMessage());
        }

        calendar.add(Calendar.HOUR_OF_DAY, hoursToRemove);
        calendar.add(Calendar.MINUTE, minutesToRemove);

        if (week != null && weekDay != null) {
            Date currentDate = new Date();

            if (currentDate.compareTo(calendar.getTime()) <= 0) {
                int notificationID = ID.generateNotificationID();
                Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).write(eventID, notificationID);

                intent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
                intent.putExtra(Constants.NOTIFICATION_CONTENT, notificationContent);
                intent.putExtra(Constants.NOTIFICATION_ID, notificationID);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            int notificationID = ID.generateNotificationID();
            Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).write(eventID, notificationID);

            intent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
            intent.putExtra(Constants.NOTIFICATION_CONTENT, notificationContent);
            intent.putExtra(Constants.NOTIFICATION_ID, notificationID);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelNotification(Context context, String week, String weekDay, String eventID) {
        if (week != null && weekDay != null) {
            if (Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID1) != null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Objects.requireNonNull(Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID1)), intent, PendingIntent.FLAG_IMMUTABLE);

                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }

            if (Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID2) != null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Objects.requireNonNull(Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID2)), intent, PendingIntent.FLAG_IMMUTABLE);

                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }

            Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).delete(eventID + "." + Constants.ID1);
            Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).delete(eventID + "." + Constants.ID2);

            if (Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).getAllKeys().isEmpty()) Paper.book(week + "." + weekDay + "." + Constants.NOTIFICATIONS).destroy();
        } else {
            if (Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID1) != null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Objects.requireNonNull(Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID1)), intent, PendingIntent.FLAG_IMMUTABLE);

                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }

            if (Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID2) != null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Objects.requireNonNull(Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(eventID + "." + Constants.ID2)), intent, PendingIntent.FLAG_IMMUTABLE);

                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }

            Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).delete(eventID + "." + Constants.ID1);
            Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).delete(eventID + "." + Constants.ID2);

            if (Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).getAllKeys().isEmpty()) Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).destroy();
        }
    }

    public static void scheduleAllNotifications(Context context, String uid) {
        TreeMap<String, Information> week0Events = getAllWeekEvents(Constants.WEEK0, uid);
        TreeMap<String, Information> week1Events = getAllWeekEvents(Constants.WEEK1, uid);
        TreeMap<String, Information> week2Events = getAllWeekEvents(Constants.WEEK2, uid);
        TreeMap<String, Information> week3Events = getAllWeekEvents(Constants.WEEK3, uid);

        List<String> allBookKeys = Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).getAllKeys();
        TreeMap<String, Information> otherEvents = new TreeMap<>();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).read(allBookKeys.get(i));
                otherEvents.put(allBookKeys.get(i), info);
            }
        } else {
            Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).destroy();
        }

        for (Map.Entry<String, Information> event : week0Events.entrySet()) {
            String weekDay = event.getKey().substring(0, event.getKey().indexOf("."));
            String eventID = event.getKey().substring(event.getKey().indexOf(".") + 1);
            Information info = event.getValue();

            scheduleNotification(context, info.getEv_notif(), Constants.WEEK0, weekDay, getDate(Constants.WEEK0, weekDay), info.getTime(), eventID, info.getDetails());
        }

        for (Map.Entry<String, Information> event : week1Events.entrySet()) {
            String weekDay = event.getKey().substring(0, event.getKey().indexOf("."));
            String eventID = event.getKey().substring(event.getKey().indexOf(".") + 1);
            Information info = event.getValue();

            scheduleNotification(context, info.getEv_notif(), Constants.WEEK1, weekDay, getDate(Constants.WEEK1, weekDay), info.getTime(), eventID, info.getDetails());
        }

        for (Map.Entry<String, Information> event : week2Events.entrySet()) {
            String weekDay = event.getKey().substring(0, event.getKey().indexOf("."));
            String eventID = event.getKey().substring(event.getKey().indexOf(".") + 1);
            Information info = event.getValue();

            scheduleNotification(context, info.getEv_notif(), Constants.WEEK2, weekDay, getDate(Constants.WEEK2, weekDay), info.getTime(), eventID, info.getDetails());
        }

        for (Map.Entry<String, Information> event : week3Events.entrySet()) {
            String weekDay = event.getKey().substring(0, event.getKey().indexOf("."));
            String eventID = event.getKey().substring(event.getKey().indexOf(".") + 1);
            Information info = event.getValue();

            scheduleNotification(context, info.getEv_notif(), Constants.WEEK3, weekDay, getDate(Constants.WEEK3, weekDay), info.getTime(), eventID, info.getDetails());
        }

        for (Map.Entry<String, Information> event : otherEvents.entrySet()) {
            Information info = event.getValue();
            scheduleNotification(context, info.getEv_notif(), null, null, info.getDate(), info.getTime(), event.getKey(), info.getDetails());
        }
    }

    public static void cancelAllNotifications(Context context, String uid) {
        ArrayList<String> week0EventsKeys = getAllWeekEventsKeys(Constants.WEEK0, uid);
        ArrayList<String> week1EventsKeys = getAllWeekEventsKeys(Constants.WEEK1, uid);
        ArrayList<String> week2EventsKeys = getAllWeekEventsKeys(Constants.WEEK2, uid);
        ArrayList<String> week3EventsKeys = getAllWeekEventsKeys(Constants.WEEK3, uid);

        List<String> otherEventsKeys = Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).getAllKeys();
        if (otherEventsKeys.isEmpty()) Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).destroy();

        for (int i = 0; i < week0EventsKeys.size(); i++) {
            String weekDay = week0EventsKeys.get(i).substring(0, week0EventsKeys.get(i).indexOf("."));
            String eventID = week0EventsKeys.get(i).substring(week0EventsKeys.get(i).indexOf(".") + 1);

            cancelNotification(context, Constants.WEEK0, weekDay, eventID);
        }

        for (int i = 0; i < week1EventsKeys.size(); i++) {
            String weekDay = week1EventsKeys.get(i).substring(0, week1EventsKeys.get(i).indexOf("."));
            String eventID = week1EventsKeys.get(i).substring(week1EventsKeys.get(i).indexOf(".") + 1);

            cancelNotification(context, Constants.WEEK1, weekDay, eventID);
        }

        for (int i = 0; i < week2EventsKeys.size(); i++) {
            String weekDay = week2EventsKeys.get(i).substring(0, week2EventsKeys.get(i).indexOf("."));
            String eventID = week2EventsKeys.get(i).substring(week2EventsKeys.get(i).indexOf(".") + 1);

            cancelNotification(context, Constants.WEEK2, weekDay, eventID);
        }

        for (int i = 0; i < week3EventsKeys.size(); i++) {
            String weekDay = week3EventsKeys.get(i).substring(0, week3EventsKeys.get(i).indexOf("."));
            String eventID = week3EventsKeys.get(i).substring(week3EventsKeys.get(i).indexOf(".") + 1);

            cancelNotification(context, Constants.WEEK3, weekDay, eventID);
        }

        for (int i = 0; i < otherEventsKeys.size(); i++) {
            cancelNotification(context, null, null, otherEventsKeys.get(i));
        }
    }

    private static TreeMap<String, Information> getAllWeekEvents(String week, String uid) {
        TreeMap<String, Information> events = new TreeMap<>();
        List<String> allBookKeys = Paper.book(uid + "." + week + "." + Constants.MONDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.MONDAY).read(allBookKeys.get(i));
                events.put(Constants.MONDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.MONDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.TUESDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.TUESDAY).read(allBookKeys.get(i));
                events.put(Constants.TUESDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.TUESDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.WEDNESDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.WEDNESDAY).read(allBookKeys.get(i));
                events.put(Constants.WEDNESDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.WEDNESDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.THURSDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.THURSDAY).read(allBookKeys.get(i));
                events.put(Constants.THURSDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.THURSDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.FRIDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.FRIDAY).read(allBookKeys.get(i));
                events.put(Constants.FRIDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.FRIDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.SATURDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.SATURDAY).read(allBookKeys.get(i));
                events.put(Constants.SATURDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.SATURDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.SUNDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(uid + "." + week + "." + Constants.SUNDAY).read(allBookKeys.get(i));
                events.put(Constants.SUNDAY + "." + allBookKeys.get(i), info);
            }
        } else { Paper.book(uid + "." + week + "." + Constants.SUNDAY).destroy(); }

        return events;
    }

    private static ArrayList<String> getAllWeekEventsKeys(String week, String uid) {
        ArrayList<String> eventsKeys = new ArrayList<>();
        List<String> allBookKeys = Paper.book(uid + "." + week + "." + Constants.MONDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.MONDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.MONDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.TUESDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.TUESDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.TUESDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.WEDNESDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.WEDNESDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.WEDNESDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.THURSDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.THURSDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.THURSDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.FRIDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.FRIDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.FRIDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.SATURDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.SATURDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.SATURDAY).destroy(); }

        allBookKeys = Paper.book(uid + "." + week + "." + Constants.SUNDAY).getAllKeys();

        if (!allBookKeys.isEmpty()) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                eventsKeys.add(Constants.SUNDAY + "." + allBookKeys.get(i));
            }
        } else { Paper.book(uid + "." + week + "." + Constants.SUNDAY).destroy(); }

        return eventsKeys;
    }

    private static String getDate(String week, String weekDay) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat1.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date date = new Date();
        String ref_date = Objects.requireNonNull(Paper.book(Constants.REF_DATE).read(Constants.DATE));

        try {
            date = dateFormat1.parse(ref_date);
        } catch (ParseException e) {
            Log.e(TAG, "getDate: ParseException: " + e.getMessage());
        }

        calendar.setTime(Objects.requireNonNull(date));
        String weekDayDate = "";

        switch ((String) Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(week))) {
            case Constants.LAST_WEEK:
                switch (weekDay) {
                    case Constants.MONDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -7);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.TUESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -6);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.WEDNESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -5);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.THURSDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -4);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.FRIDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -3);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.SATURDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -2);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.SUNDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;
                }
                break;

            case Constants.THIS_WEEK:
                switch (weekDay) {
                    case Constants.MONDAY:
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.TUESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.WEDNESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 2);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.THURSDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 3);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.FRIDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 4);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.SATURDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 5);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.SUNDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 6);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;
                }
                break;

            case Constants.NEXT_WEEK:
                switch (weekDay) {
                    case Constants.MONDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.TUESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 8);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.WEDNESDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 9);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.THURSDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 10);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.FRIDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 11);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.SATURDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 12);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;

                    case Constants.SUNDAY:
                        calendar.add(Calendar.DAY_OF_MONTH, 13);
                        weekDayDate = dateFormat1.format(calendar.getTime());
                        break;
                }
                break;
        }

        return weekDayDate;
    }
}
