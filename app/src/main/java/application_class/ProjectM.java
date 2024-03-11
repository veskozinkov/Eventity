package application_class;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import constants.Constants;
import helper_classes.ID;
import helper_classes.Information;
import helper_classes.notifications.NotificationHelper;
import io.paperdb.Paper;
import vz.apps.dailyevents.MainActivity;

public class ProjectM extends Application {

    private static final String TAG = "ProjectM";

    private String ref_date;
    private String week0Flag;
    private String week1Flag;
    private String week2Flag;
    private HandlerThread handlerThread;
    private Handler handler;
    private Context mainActivityContext;
    private boolean signUpActivityLastOpened;

    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(getApplicationContext());
        Constants.auth.useAppLanguage();

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        mainActivityContext = null;
        signUpActivityLastOpened = false;

        boolean firstStart = preferences.getBoolean("first_start", true);

        if (firstStart) {
            Paper.book(Constants.DEVICE_ID).write(Constants.ID, ID.generateDeviceID(Constants.DEVICE_ID_LENGTH));
            Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).write(Constants.CONNECTION, 0);
            Paper.book(Constants.LOCAL_VARIABLES_MANAGED).write(Constants.MANAGED, 1);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_start", false);
            editor.apply();
        }

        Paper.book(Constants.ALL_EVENTS_DELETED).write(Constants.DELETED, 0);

        NotificationHelper.createNotificationChannel(getApplicationContext());
        manageLocalVariables();
    }

    public void manageLocalVariables() {
        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

        if (Constants.auth.getCurrentUser() != null && Constants.auth.getCurrentUser().isEmailVerified() && connectionLostWhileSigningIn == 0) {
            TimeZone.setDefault(TimeZone.getTimeZone((String) Paper.book(Constants.COUNTRY).read(Constants.TZ)));

            int autoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);

            if (autoTime == 1) {
                getLocalVariables();
                updateLocalVariables();

                NotificationHelper.cancelAllNotifications(this, Constants.auth.getCurrentUser().getUid());
                NotificationHelper.scheduleAllNotifications(this, Constants.auth.getCurrentUser().getUid());

                setupHandlerAndRunnable();

                Paper.book(Constants.LOCAL_VARIABLES_MANAGED).write(Constants.MANAGED, 1);
            } else { if (autoTime == 0) Paper.book(Constants.LOCAL_VARIABLES_MANAGED).write(Constants.MANAGED, 0); }
        }
    }

    private void updateLocalVariables() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date date = new Date();

        try {
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(ref_date)));
        } catch (ParseException e) {
            Log.e(TAG, "updateLocalVariables: ParseException: " + e.getMessage());
        }

        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        int count = 0;

        while (date.compareTo(calendar.getTime()) > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            count++;
        }

        int count1 = count;

        if (count > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            Paper.book(Constants.REF_DATE).write(Constants.DATE, dateFormat.format(calendar.getTime()));

            if (count < 4) {
                for (int i = 0; i < count; i++) {
                    if (week0Flag.equals(Constants.LAST_WEEK)) {
                        updateLocalWeekFlags("", Constants.LAST_WEEK, Constants.THIS_WEEK, Constants.NEXT_WEEK);
                        destroyWeekBook(Constants.WEEK0);
                    } else {
                        if (week1Flag.equals(Constants.LAST_WEEK)) {
                            updateLocalWeekFlags(Constants.NEXT_WEEK, "", Constants.LAST_WEEK, Constants.THIS_WEEK);
                            destroyWeekBook(Constants.WEEK1);
                        } else {
                            if (week2Flag.equals(Constants.LAST_WEEK)) {
                                updateLocalWeekFlags(Constants.THIS_WEEK, Constants.NEXT_WEEK, "", Constants.LAST_WEEK);
                                destroyWeekBook(Constants.WEEK2);
                            } else {
                                updateLocalWeekFlags(Constants.LAST_WEEK, Constants.THIS_WEEK, Constants.NEXT_WEEK, "");
                                destroyWeekBook(Constants.WEEK3);
                            }
                        }
                    }

                    getLocalVariables();
                }
            } else {
                count = count % 4;

                destroyWeekBook(Constants.WEEK0);
                destroyWeekBook(Constants.WEEK1);
                destroyWeekBook(Constants.WEEK2);
                destroyWeekBook(Constants.WEEK3);

                for (int i = 0; i < count; i++) {
                    if (week0Flag.equals(Constants.LAST_WEEK)) {
                        updateLocalWeekFlags("", Constants.LAST_WEEK, Constants.THIS_WEEK, Constants.NEXT_WEEK);
                    } else {
                        if (week1Flag.equals(Constants.LAST_WEEK)) {
                            updateLocalWeekFlags(Constants.NEXT_WEEK, "", Constants.LAST_WEEK, Constants.THIS_WEEK);
                        } else {
                            if (week2Flag.equals(Constants.LAST_WEEK)) {
                                updateLocalWeekFlags(Constants.THIS_WEEK, Constants.NEXT_WEEK, "", Constants.LAST_WEEK);
                            } else {
                                updateLocalWeekFlags(Constants.LAST_WEEK, Constants.THIS_WEEK, Constants.NEXT_WEEK, "");
                            }
                        }
                    }

                    getLocalVariables();
                }
            }
        }

        if (count1 > 0) {
            List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).getAllKeys();

            for (int i = 0; i < allBookKeys.size(); i++) {
                long value = Objects.requireNonNull(Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).read(allBookKeys.get(i)));
                value -= count1;

                if (value > 0) {
                    Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).write(allBookKeys.get(i), value);
                } else {
                    Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).delete(allBookKeys.get(i));

                    if (value < -2) {
                        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).delete(allBookKeys.get(i));
                    } else {
                        if (value == 0) {
                            if (week0Flag.equals(Constants.NEXT_WEEK)) {
                                updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK0);
                            } else {
                                if (week1Flag.equals(Constants.NEXT_WEEK)) {
                                    updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK1);
                                } else {
                                    if (week2Flag.equals(Constants.NEXT_WEEK)) {
                                        updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK2);
                                    } else {
                                        updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK3);
                                    }
                                }
                            }
                        } else {
                            if (value == -1) {
                                if (week0Flag.equals(Constants.THIS_WEEK)) {
                                    updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK0);
                                } else {
                                    if (week1Flag.equals(Constants.THIS_WEEK)) {
                                        updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK1);
                                    } else {
                                        if (week2Flag.equals(Constants.THIS_WEEK)) {
                                            updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK2);
                                        } else {
                                            updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK3);
                                        }
                                    }
                                }
                            } else {
                                if (week0Flag.equals(Constants.LAST_WEEK)) {
                                    updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK0);
                                } else {
                                    if (week1Flag.equals(Constants.LAST_WEEK)) {
                                        updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK1);
                                    } else {
                                        if (week2Flag.equals(Constants.LAST_WEEK)) {
                                            updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK2);
                                        } else {
                                            updateLocalOtherAppointments(allBookKeys.get(i), Constants.WEEK3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).getAllKeys();

            if (allBookKeys.size() == 0) {
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).destroy();
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).destroy();
            }
        }
    }

    public void getLocalVariables() {
        ref_date = Paper.book(Constants.REF_DATE).read(Constants.DATE);
        week0Flag = Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK0);
        week1Flag = Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK1);
        week2Flag = Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK2);
    }

    private void updateLocalWeekFlags(String week0, String week1, String week2, String week3) {
        Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK0, week0);
        Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK1, week1);
        Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK2, week2);
        Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK3, week3);
    }

    private void updateLocalOtherAppointments(String key, String week) {
        Information person = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).read(key);
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).delete(key);
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Objects.requireNonNull(person).dayFromDate(this).getWeekDay_ENG()).write(key + ".**", new Information(0, person.getDetails(), person.getDate(), person.getTime(), person.getEv_notif()));

        if (Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(key + "." + Constants.ID1) != null) {
            Paper.book(week + "." + person.dayFromDate(this).getWeekDay_ENG() + "." + Constants.NOTIFICATIONS).write(key + ".**." + Constants.ID1, Objects.requireNonNull(Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(key + "." + Constants.ID1)));
            Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).delete(key + "." + Constants.ID1);
        }

        if (Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(key + "." + Constants.ID2) != null) {
            Paper.book(week + "." + person.dayFromDate(this).getWeekDay_ENG() + "." + Constants.NOTIFICATIONS).write(key + ".**." + Constants.ID2, Objects.requireNonNull(Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(key + "." + Constants.ID2)));
            Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).delete(key + "." + Constants.ID2);
        }
    }

    private void destroyWeekBook(String week) {
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.MONDAY).destroy();
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.TUESDAY).destroy();
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.WEDNESDAY).destroy();
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.THURSDAY).destroy();
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.FRIDAY).destroy();
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.SATURDAY).destroy();
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.SUNDAY).destroy();

        Paper.book(week + "." + Constants.MONDAY + "." + Constants.NOTIFICATIONS).destroy();
        Paper.book(week + "." + Constants.TUESDAY + "." + Constants.NOTIFICATIONS).destroy();
        Paper.book(week + "." + Constants.WEDNESDAY + "." + Constants.NOTIFICATIONS).destroy();
        Paper.book(week + "." + Constants.THURSDAY + "." + Constants.NOTIFICATIONS).destroy();
        Paper.book(week + "." + Constants.FRIDAY + "." + Constants.NOTIFICATIONS).destroy();
        Paper.book(week + "." + Constants.SATURDAY + "." + Constants.NOTIFICATIONS).destroy();
        Paper.book(week + "." + Constants.SUNDAY + "." + Constants.NOTIFICATIONS).destroy();
    }

    public void setupHandlerAndRunnable() {
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());
        runnable.run();
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
            dateFormat.setLenient(false);

            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setLenient(false);

            Date date = new Date();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 58);
            calendar.set(Calendar.MILLISECOND, 999);

            if (Constants.auth.getCurrentUser() != null) {
                if (date.compareTo(calendar.getTime()) > 0) {
                    new Handler().postDelayed(() -> {
                        updateLocalVariables();

                        if (mainActivityContext != null) {
                            ((MainActivity) mainActivityContext).runOnUiThread(() -> ((MainActivity) mainActivityContext).updateFragmentInfoOnDateChange());
                        }
                    }, Constants.UPDATE_FRAGMENT_INFO_DELAY);
                }

                handler.postDelayed(runnable, Constants.UPDATE_FRAGMENT_INFO_DELAY);
            } else {
                handler.removeCallbacks(runnable);
                handlerThread.quitSafely();
            }
        }
    };

    public void setMainActivityContext(Context mainActivityContext) { this.mainActivityContext = mainActivityContext; }

    public Context getMainActivityContext() { return mainActivityContext; }

    public void setSignUpActivityLastOpened(boolean signUpActivityLastOpened) { this.signUpActivityLastOpened = signUpActivityLastOpened; }

    public boolean getSignUpActivityLastOpened() { return signUpActivityLastOpened; }
}
