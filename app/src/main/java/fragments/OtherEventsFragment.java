package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.wave.MultiWaveHeader;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import dialogs.ActivateAutoTimeDialog;
import dialogs.AddChangeInfoDialog2;
import dialogs.ConfirmationDialog;
import helper_classes.CustomToast;
import helper_classes.CustomTypefaceSpan;
import helper_classes.Information;
import helper_classes.ManageTitlePosition;
import helper_classes.Utility;
import helper_classes.adapters.InformationListAdapter;
import helper_classes.comparators.ChainedComparator;
import helper_classes.comparators.DateComparator;
import helper_classes.comparators.TimeComparator;
import helper_classes.notifications.DatabaseNotification;
import helper_classes.notifications.NotificationHelper;
import helper_classes.scale_layout.ScaledLayoutVariables;
import io.paperdb.Paper;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;

public class OtherEventsFragment extends Fragment {

    private static final String TAG = "OtherApptsFragment";

    private ConstraintLayout constraintLayout;
    private TextView title;
    private TextView subtitle;
    private ListView day;
    private FloatingActionButton add;
    private FloatingActionButton accountSettings;
    private FloatingActionButton back;
    private ArrayList<Information> information;
    private InformationListAdapter adapter;
    private int saveChangeIndex;
    private int deleteIndex;
    private MultiWaveHeader waveHeader;
    private boolean reloadingUser;
    private long initialDateFormatIndex;
    private long initialTimeFormatIndex;
    private String initialTimeZone;
    private boolean toastShowing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.day_fragment, container, false);

        constraintLayout = view.findViewById(R.id.constraintLayout);
        title = view.findViewById(R.id.title_TextView);
        subtitle = view.findViewById(R.id.subtitle_TextView);
        day = view.findViewById(R.id.day_ListView);
        add = view.findViewById(R.id.add_FAB);
        accountSettings = view.findViewById(R.id.accountSettings_FAB);
        back = view.findViewById(R.id.back_FAB);
        information = new ArrayList<>();
        adapter = new InformationListAdapter(requireActivity(), R.layout.information_list_adapter_view, information, false);
        waveHeader = view.findViewById(R.id.waveHeader);
        reloadingUser = false;
        initialDateFormatIndex = Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F));
        initialTimeFormatIndex = Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F));
        initialTimeZone = Paper.book(Constants.COUNTRY).read(Constants.TZ);
        toastShowing = false;

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        setTitle();
        scaleFABs();

        if (!((MainActivity) requireActivity()).getNetwork()) disableFABButton(add);

        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));
        if (connectionLostWhileSigningIn == 1) disableAccountSettings();

        day.setScrollingCacheEnabled(false);
        day.setAdapter(adapter);
        registerForContextMenu(day);

        add.setOnClickListener(v -> {
            if (!((MainActivity) requireActivity()).getButtonClicked()) {
                ((MainActivity) requireActivity()).setButtonClicked(true);
                ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.VISIBLE);

                if (Constants.auth.getCurrentUser() != null) {
                    int listLimit = Constants.MAX_OTHER_EVENTS;
                    boolean firstErrorMessage = true;

                    if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.EVENTS_INC)) == 1) {
                        listLimit = Constants.MAX_INCREASED_OTHER_EVENTS;
                        firstErrorMessage = false;
                    }

                    if (information.size() < listLimit) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) {
                                saveChangeIndex = -1;
                                addChangeInfoDialog2();
                            }
                        });
                    } else {
                        ((MainActivity) requireActivity()).setButtonClicked(false);
                        ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.GONE);

                        if (!toastShowing) {
                            toastShowing = true;

                            if (firstErrorMessage) {
                                CustomToast.showInfo(requireActivity(), getString(R.string.other_events_limit_info_message1, listLimit), Toast.LENGTH_LONG);
                                new Handler().postDelayed(() -> toastShowing = false, Constants.TOAST_LONG_DURATION);
                            } else {
                                CustomToast.showInfo(requireActivity(), getString(R.string.other_events_limit_info_message2, listLimit), Toast.LENGTH_SHORT);
                                new Handler().postDelayed(() -> toastShowing = false, Constants.TOAST_SHORT_DURATION);
                            }
                        }
                    }
                }
            }
        });

        accountSettings.setOnClickListener(v -> {
            if (!((MainActivity) requireActivity()).getButtonClicked()) {
                if (connectionLostWhileSigningIn == 0) {
                    Intent intent = new Intent(requireActivity(), AccountSettingsActivity.class);
                    intent.putExtra("uid", ((MainActivity) requireActivity()).getUID());

                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    if (!((MainActivity) requireActivity()).isToastShowing1()) {
                        Activity activity = requireActivity();
                        ((MainActivity) requireActivity()).setToastShowing1(true);

                        CustomToast.showError(requireActivity(), getString(R.string.connection_lost_while_signing_in_error_message1), Toast.LENGTH_LONG);
                        CustomToast.showInfo(requireActivity(), getString(R.string.connection_lost_while_signing_in_info_message1), Toast.LENGTH_LONG);
                        CustomToast.showInfo(requireActivity(), getString(R.string.connection_lost_while_signing_in_info_message2), Toast.LENGTH_LONG);
                        CustomToast.showInfo(requireActivity(), getString(R.string.connection_lost_while_signing_in_info_message3), Toast.LENGTH_LONG);

                        new Handler().postDelayed(() -> ((MainActivity) activity).setToastShowing1(false), Constants.TOAST_LONG_DURATION * 4);
                    }
                }
            }
        });

        back.setOnClickListener(v -> {
            if (!((MainActivity) requireActivity()).getButtonClicked()) {
                ((MainActivity) requireActivity()).setBackFlag(true);

                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload();
                }

                requireActivity().finish();
            }
        });

        getParentFragmentManager().setFragmentResultListener(Constants.ADD_CHANGE_INFO_DIALOG2_TAG, this, (requestKey, result) -> {
            String details = result.getString(Constants.BUNDLE_KEY3);
            String finalTime = result.getString(Constants.BUNDLE_KEY4);
            String selectedDate = result.getString(Constants.BUNDLE_KEY5);
            DatabaseNotification databaseNotification = Utility.getGsonParser().fromJson(result.getString(Constants.BUNDLE_KEY6), DatabaseNotification.class);

            sendDetailsTimeDateNotif(details, finalTime, selectedDate, databaseNotification);
        });

        getParentFragmentManager().setFragmentResultListener(Constants.CONFIRMATION_DIALOG_TAG, this, (requestKey, result) -> yesButtonClicked());

        displayInformation();
        scaleListView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int autoTime = Settings.Global.getInt(requireActivity().getContentResolver(), Settings.Global.AUTO_TIME, 0);

        if (autoTime == 1) {
            if (initialDateFormatIndex != (long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F)) || initialTimeFormatIndex != (long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F)) || !initialTimeZone.equals(Paper.book(Constants.COUNTRY).read(Constants.TZ)) || (int) Objects.requireNonNull(Paper.book(Constants.ALL_EVENTS_DELETED).read(Constants.DELETED)) == 1) {
                initialDateFormatIndex = Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F));
                initialTimeFormatIndex = Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F));
                initialTimeZone = Paper.book(Constants.COUNTRY).read(Constants.TZ);
                Paper.book(Constants.ALL_EVENTS_DELETED).write(Constants.DELETED, 0);

                OtherEventsFragment otherEventsFragment = new OtherEventsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, otherEventsFragment).commit();
            }
        } else {
            if (autoTime == 0) {
                DialogFragment dialogFragment1 = (DialogFragment) getParentFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
                DialogFragment dialogFragment2 = (DialogFragment) getParentFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

                if (dialogFragment1 != null) dialogFragment1.dismiss();
                else { if (dialogFragment2 != null) dialogFragment2.dismiss(); }

                DialogFragment dialogFragment3 = (DialogFragment) getParentFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
                if (dialogFragment3 == null) activateAutoTimeDialog();
            }
        }
    }

    public void displayInformation() {
        if (Constants.auth.getCurrentUser() != null) {
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<Information> peopleCopy = new ArrayList<>();
            List<String> allBookKeys = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).getAllKeys();

            information.clear();

            if (allBookKeys.size() == 0) {
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).destroy();
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).destroy();
            }

            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).read(allBookKeys.get(i));

                information.add(new Information(Objects.requireNonNull(info).getDetails(), formatDate(info.getDate()), info.dayFromDate(getActivity()).getWeekDay_localString().substring(0, 3), formatTime(info.getTime()), info.getEv_notif()));
                peopleCopy.add(new Information(info.getDetails(), info.getDate(), null, info.getTime(), info.getEv_notif()));
                keys.add(allBookKeys.get(i));
            }

            information.sort(new ChainedComparator(new DateComparator(requireActivity(), true), new TimeComparator(true)));
            updateIndexes(keys, peopleCopy);

            adapter.notifyDataSetChanged();
        }
    }

    private void setTitle() {
        title.setText(R.string.fragment_title4);
        subtitle.setVisibility(View.GONE);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(R.id.title_TextView, ConstraintSet.BOTTOM, R.id.guideline2, ConstraintSet.TOP);
        constraintSet.applyTo(constraintLayout);

        ManageTitlePosition.manageActivitySingleTitle(requireActivity(), waveHeader, title);
    }

    private void scaleListView() {
        day.setDividerHeight(ScaledLayoutVariables.DAY_FRAGMENT_LIST_DIV_HEIGHT);
        day.setPadding(0, ScaledLayoutVariables.DAY_FRAGMENT_LIST_TOP_PAD, 0, ScaledLayoutVariables.DAY_FRAGMENT_LIST_BOT_PAD);
    }

    private void addChangeInfoDialog2() {
        if (saveChangeIndex == -1) {
            AddChangeInfoDialog2 dialog = new AddChangeInfoDialog2();
            dialog.show(getParentFragmentManager(), Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
        }

        if (saveChangeIndex >= 0) {
            AddChangeInfoDialog2 dialog = new AddChangeInfoDialog2(information.get(saveChangeIndex).getDetails(), reverseFormatTime(information.get(saveChangeIndex).getTime()), reverseFormatDate(information.get(saveChangeIndex).getDate()), information.get(saveChangeIndex).getEv_notif());
            dialog.show(getParentFragmentManager(), Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
        }
    }

    private void confirmationDialog() {
        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.show(getParentFragmentManager(), Constants.CONFIRMATION_DIALOG_TAG);
    }

    private void activateAutoTimeDialog() {
        ((MainActivity) requireActivity()).setButtonClicked(true);

        ActivateAutoTimeDialog dialog = new ActivateAutoTimeDialog();
        dialog.show(getParentFragmentManager(), Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
    }

    private void saveInformation(String details, String time, String date, DatabaseNotification databaseNotification) {
        if (isDateTimeAvailable(date, time)) {
            Information info = new Information(0, details, date, time, databaseNotification);
            String pushKey = Constants.usersRef.child(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid()).child(Constants.OTHER_EVENTS).child(Constants.INFO).push().getKey();

            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).write(Objects.requireNonNull(pushKey), info);
            displayInformation();

            long count = getWeekDifference(date);
            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).write(pushKey, count);

            NotificationHelper.scheduleNotification(requireActivity(), databaseNotification, null, null, info.getDate(), info.getTime(), pushKey, info.getDetails());
            Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.FLAGS).child(Objects.requireNonNull(pushKey)).setValue(count);
        } else {
            CustomToast.showError(requireActivity(), getString(R.string.date_and_time_already_exist_error), Toast.LENGTH_LONG);
        }
    }

    private void changeInformation(String name, String time, String date, DatabaseNotification databaseNotification) {
        if (isDateTimeAvailable(date, time)) {
            List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).getAllKeys();

            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info1 = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).read(allBookKeys.get(i));

                if (Objects.requireNonNull(info1).getIndex() == saveChangeIndex) {
                    Information info2 = new Information(info1.getIndex(), name, date, time, databaseNotification);

                    Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).write(allBookKeys.get(i), info2);
                    displayInformation();

                    long count = getWeekDifference(date);
                    Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).write(allBookKeys.get(i), count);

                    if (!info2.getDate().equals(info1.getDate()) || !info2.getTime().equals(info1.getTime()) || !info2.getEv_notif().equals(info1.getEv_notif())) {
                        NotificationHelper.cancelNotification(requireActivity(), null, null, allBookKeys.get(i));
                        NotificationHelper.scheduleNotification(requireActivity(), info2.getEv_notif(), null, null, info2.getDate(), info2.getTime(), allBookKeys.get(i), info2.getDetails());
                    }

                    Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.FLAGS).child(allBookKeys.get(i)).setValue(count);
                    break;
                }
            }
        } else {
            CustomToast.showError(requireActivity(), getString(R.string.date_and_time_already_exist_error), Toast.LENGTH_LONG);
        }
    }

    private void deleteInformation(final int index) {
        List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).getAllKeys();

        for (int i = 0; i < allBookKeys.size(); i++) {
            Information info = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).read(allBookKeys.get(i));

            if (Objects.requireNonNull(info).getIndex() == index) {
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).delete(allBookKeys.get(i));
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).delete(allBookKeys.get(i));

                NotificationHelper.cancelNotification(requireActivity(), null, null, allBookKeys.get(i));
                displayInformation();

                Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.INFO).child(allBookKeys.get(i)).removeValue();
                Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.FLAGS).child(allBookKeys.get(i)).removeValue();

                break;
            }
        }
    }

    private void updateIndexes(ArrayList<String> keys, ArrayList<Information> peopleCopy) {
        TreeMap<String, Information> sortedMap = new TreeMap<>();

        for (int i = 0; i < peopleCopy.size(); i++) {
            sortedMap.put(keys.get(i), peopleCopy.get(i));
        }

        peopleCopy.sort(new ChainedComparator(new DateComparator(requireActivity(), false), new TimeComparator(false)));

        for (int i = 0; i < peopleCopy.size(); i++) {
            peopleCopy.get(i).setIndex(i);
        }

        for (Map.Entry<String, Information> entry : sortedMap.entrySet()) {
            Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).write(entry.getKey(), entry.getValue());
            Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.INFO).child(entry.getKey()).setValue(entry.getValue());
        }
    }

    private boolean isDateTimeAvailable(String date, String time) {
        List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).getAllKeys();

        for (int i = 0; i < allBookKeys.size(); i++) {
            Information info = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).read(allBookKeys.get(i));

            if (Objects.requireNonNull(info).getDate().equals(date) && info.getTime().equals(time)) {
                if (saveChangeIndex == -1) return false;

                if (saveChangeIndex >= 0) {
                    return info.getIndex() == saveChangeIndex;
                }
            }
        }

        return true;
    }

    private long getWeekDifference(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date Date = new Date();
        String ref_date = Objects.requireNonNull(Paper.book(Constants.REF_DATE).read(Constants.DATE));

        try {
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(ref_date)));
            Date = Objects.requireNonNull(dateFormat.parse(date));
        } catch (ParseException e) {
            Log.e(TAG, "saveInformation: ParseException: " + e.getMessage());
        }

        calendar.add(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long count = 0;

        while (Date.compareTo(calendar.getTime()) > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            count++;
        }

        return count;
    }

    private String formatDate(String dateToFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat1.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date date = new Date();

        try {
            date = dateFormat1.parse(dateToFormat);
        } catch (ParseException e) {
            Log.e(TAG, "formatDate: ParseException: " + e.getMessage());
        }

        calendar.setTime(Objects.requireNonNull(date));

        String[] dateFormatOptions = getEnglishStringArray(R.array.date_formats);
        int dateFormatIndex = BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F))).intValueExact();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat(dateFormatOptions[dateFormatIndex].replace("mm", "MM"), Locale.US);
        dateFormat2.setLenient(false);

        return dateFormat2.format(calendar.getTime());
    }

    private String reverseFormatDate(String dateToFormat) {
        String[] dateFormatOptions = getEnglishStringArray(R.array.date_formats);
        int dateFormatIndex = BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F))).intValueExact();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat(dateFormatOptions[dateFormatIndex].replace("mm", "MM"), Locale.US);
        dateFormat1.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date date = new Date();

        try {
            date = dateFormat1.parse(dateToFormat);
        } catch (ParseException e) {
            Log.e(TAG, "formatDate: ParseException: " + e.getMessage());
        }

        calendar.setTime(Objects.requireNonNull(date));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat2.setLenient(false);

        return dateFormat2.format(calendar.getTime());
    }

    private String formatTime(String timeToFormat) {
        if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F)) == 1) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat24 = new SimpleDateFormat(Constants.TIME_FORMAT_24, Locale.US);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat12 = new SimpleDateFormat(Constants.TIME_FORMAT_12, Locale.US);

            Date date = new Date();

            try {
                date = timeFormat24.parse(timeToFormat);
            } catch (ParseException e) {
                Log.e(TAG, "formatTime: ParseException: " + e.getMessage());
            }

            return timeFormat12.format(Objects.requireNonNull(date));
        } else {
            return timeToFormat;
        }
    }

    private String reverseFormatTime(String timeToFormat) {
        if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F)) == 1) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat24 = new SimpleDateFormat(Constants.TIME_FORMAT_24, Locale.US);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat12 = new SimpleDateFormat(Constants.TIME_FORMAT_12, Locale.US);

            Date date = new Date();

            try {
                date = timeFormat12.parse(timeToFormat);
            } catch (ParseException e) {
                Log.e(TAG, "formatTime: ParseException: " + e.getMessage());
            }

            return timeFormat24.format(Objects.requireNonNull(date));
        } else {
            return timeToFormat;
        }
    }

    private String[] getEnglishStringArray(int id) {
        Configuration configuration = getEnglishConfiguration();
        return requireContext().createConfigurationContext(configuration).getResources().getStringArray(id);
    }

    private Configuration getEnglishConfiguration() {
        Configuration configuration = new Configuration(requireContext().getResources().getConfiguration());
        configuration.setLocale(new Locale("en"));
        return configuration;
    }

    private void scaleFABs() {
        back.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        add.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        accountSettings.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);

        back.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
        add.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
        accountSettings.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
    }

    public void disableFABButton(FloatingActionButton button) {
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDisabled)));
    }

    public void enableFABButton(FloatingActionButton button) {
        button.setEnabled(true);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary)));
    }

    public void disableAccountSettings() {
        accountSettings.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDisabled)));
        accountSettings.setRippleColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
    }

    public FloatingActionButton getAdd() {
        return add;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (Constants.auth.getCurrentUser() != null && !reloadingUser) {
            reloadingUser = true;
            Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> reloadingUser = false);
        }

        requireActivity().getMenuInflater().inflate(R.menu.listview_info_changer, menu);

        for (int i = 0; i < menu.size(); i++) {
            Typeface typeface = Typeface.create(requireActivity().getString(R.string.app_font), Typeface.BOLD_ITALIC);
            SpannableString newTitle = new SpannableString(menu.getItem(i).getTitle());
            newTitle.setSpan(new CustomTypefaceSpan(typeface), 0, newTitle.length(), 0);
            newTitle.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.context_menu_item_text_size), false), 0, newTitle.length(), 0);
            menu.getItem(i).setTitle(newTitle);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (!((MainActivity) requireActivity()).getButtonClicked()) {
            ((MainActivity) requireActivity()).setButtonClicked(true);
            ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.VISIBLE);

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            if (item.getItemId() == R.id.changeInfo_Item) {
                saveChangeIndex = info.position;
                addChangeInfoDialog2();
                return true;
            } else {
                if (item.getItemId() == R.id.deleteInfo_Item) {
                    deleteIndex = info.position;
                    confirmationDialog();
                    return true;
                }
            }
        }

        return super.onContextItemSelected(item);
    }

    private void sendDetailsTimeDateNotif(String details, String time, String date, DatabaseNotification databaseNotification) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.DATABASE_URL);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int code = connection.getResponseCode();

                    requireActivity().runOnUiThread(() -> {
                        if (code == HttpURLConnection.HTTP_OK) {
                            if (saveChangeIndex == -1) {
                                saveInformation(details, time, date, databaseNotification);
                            }

                            if (saveChangeIndex >= 0) {
                                changeInformation(details, time, date, databaseNotification);
                            }
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);
                        }

                        connection.disconnect();

                        DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                } catch (IOException e) {
                    Log.e(TAG, "sendInfoTimeDate: IOException: " + e.getMessage());

                    requireActivity().runOnUiThread(() -> {
                        CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "sendInfoTimeDate: MalformedURLException: " + e.getMessage());

                requireActivity().runOnUiThread(() -> {
                    CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                    DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
                    Objects.requireNonNull(dialogFragment).dismiss();
                });
            }
        });

        thread.start();
    }

    private void yesButtonClicked() {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.DATABASE_URL);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int code = connection.getResponseCode();

                    requireActivity().runOnUiThread(() -> {
                        if (code == HttpURLConnection.HTTP_OK) {
                            deleteInformation(deleteIndex);
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);
                        }

                        connection.disconnect();

                        DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                } catch (IOException e) {
                    Log.e(TAG, "yesButtonClicked: IOException: " + e.getMessage());

                    requireActivity().runOnUiThread(() -> {
                        CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "yesButtonClicked: MalformedURLException: " + e.getMessage());

                requireActivity().runOnUiThread(() -> {
                    CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                    DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
                    Objects.requireNonNull(dialogFragment).dismiss();
                });
            }
        });

        thread.start();
    }
}
