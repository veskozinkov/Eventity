package helper_classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import constants.Constants;
import helper_classes.notifications.DatabaseNotification;
import vz.apps.dailyevents.R;

@Keep
public class Information {

    private static final String TAG = "Information";

    private long index;
    private String details;
    private String date;
    private String weekDay;
    private String time;
    private DatabaseNotification ev_notif;

    public Information(String details, String date, String weekDay, String time, DatabaseNotification ev_notif) {
        this.details = details;
        this.date = date;
        this.weekDay = weekDay;
        this.time = time;
        this.ev_notif = ev_notif;
    }

    public Information(long index, String details, String date, String time, DatabaseNotification ev_notif) {
        this.index = index;
        this.details = details;
        this.date = date;
        this.time = time;
        this.ev_notif = ev_notif;
    }

    public Information() { }

    public long getIndex() { return index; }

    public void setIndex(long index) { this.index = index; }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DatabaseNotification getEv_notif() { return ev_notif; }

    public void setEv_notif(DatabaseNotification ev_notif) { this.ev_notif = ev_notif; }

    public WeekDays dayFromDate(Context context) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date date = new Date();
        String weekDay_localString = "";
        String weekDay_ENG = "";

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat.setLenient(false);

        try {
            date = dateFormat.parse(this.date);
        } catch (ParseException e) {
            Log.e(TAG, "dayFromDate: ParseException: " + e.getMessage());
        }

        calendar.setTime(Objects.requireNonNull(date));

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                weekDay_localString = context.getString(R.string.monday);
                weekDay_ENG = Constants.MONDAY;
                break;

            case Calendar.TUESDAY:
                weekDay_localString = context.getString(R.string.tuesday);
                weekDay_ENG = Constants.TUESDAY;
                break;

            case Calendar.WEDNESDAY:
                weekDay_localString = context.getString(R.string.wednesday);
                weekDay_ENG = Constants.WEDNESDAY;
                break;

            case Calendar.THURSDAY:
                weekDay_localString = context.getString(R.string.thursday);
                weekDay_ENG = Constants.THURSDAY;
                break;

            case Calendar.FRIDAY:
                weekDay_localString = context.getString(R.string.friday);
                weekDay_ENG = Constants.FRIDAY;
                break;

            case Calendar.SATURDAY:
                weekDay_localString = context.getString(R.string.saturday);
                weekDay_ENG = Constants.SATURDAY;
                break;

            case Calendar.SUNDAY:
                weekDay_localString = context.getString(R.string.sunday);
                weekDay_ENG = Constants.SUNDAY;
                break;
        }

        return new WeekDays(weekDay_localString, weekDay_ENG);
    }
}
