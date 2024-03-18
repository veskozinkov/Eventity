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
import androidx.constraintlayout.widget.Guideline;
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
import dialogs.AddChangeInfoDialog;
import dialogs.ConfirmationDialog;
import helper_classes.CustomToast;
import helper_classes.CustomTypefaceSpan;
import helper_classes.Information;
import helper_classes.ManageTitlePosition;
import helper_classes.Utility;
import helper_classes.adapters.InformationListAdapter;
import helper_classes.comparators.TimeComparator;
import helper_classes.notifications.DatabaseNotification;
import helper_classes.notifications.NotificationHelper;
import helper_classes.scale_layout.ScaledLayoutVariables;
import io.paperdb.Paper;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;

public class DayFragment extends Fragment {

    private static final String TAG = "DayFragment";

    private Guideline guideline5;
    private TextView title;
    private TextView subtitle;
    private ListView day;
    private FloatingActionButton add;
    private FloatingActionButton accountSettings;
    private FloatingActionButton back;
    private String selectedWeek;
    private String selectedDay;
    private String selectedDay_localString;
    private ArrayList<Information> information;
    private InformationListAdapter adapter;
    private int saveChangeIndex;
    private int deleteIndex;
    private int currentDay;
    private int selectedDayInt;
    private String selectedDayDate;
    private String Week;
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

        guideline5 = view.findViewById(R.id.guideline5);
        title = view.findViewById(R.id.title_TextView);
        subtitle = view.findViewById(R.id.subtitle_TextView);
        day = view.findViewById(R.id.day_ListView);
        add = view.findViewById(R.id.add_FAB);
        accountSettings = view.findViewById(R.id.accountSettings_FAB);
        back = view.findViewById(R.id.back_FAB);
        information = new ArrayList<>();
        adapter = new InformationListAdapter(requireActivity(), R.layout.information_list_adapter_view, information, true);
        currentDay = Calendar.getInstance(Locale.US).get(Calendar.DAY_OF_WEEK);
        waveHeader = view.findViewById(R.id.waveHeader);
        reloadingUser = false;
        initialDateFormatIndex = Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F));
        initialTimeFormatIndex = Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F));
        initialTimeZone = Paper.book(Constants.COUNTRY).read(Constants.TZ);
        toastShowing = false;

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        selectedWeek = requireArguments().getString(Constants.BUNDLE_KEY1);
        selectedDay = requireArguments().getString(Constants.BUNDLE_KEY2);

        scaleFABs();
        dayFragmentSetup();

        if (!((MainActivity) requireActivity()).getNetwork()) disableFABButton(add);

        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));
        if (connectionLostWhileSigningIn == 1) disableAccountSettings();

        day.setScrollingCacheEnabled(false);
        day.setAdapter(adapter);

        add.setOnClickListener(v -> {
            if (!((MainActivity) requireActivity()).getButtonClicked()) {
                ((MainActivity) requireActivity()).setButtonClicked(true);
                ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.VISIBLE);

                if (Constants.auth.getCurrentUser() != null) {
                    int listLimit = Constants.MAX_EVENTS_PER_DAY;
                    boolean firstErrorMessage = true;

                    if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.EVENTS_INC)) == 1) {
                        listLimit = Constants.MAX_INCREASED_EVENTS_PER_DAY;
                        firstErrorMessage = false;
                    }

                    if (information.size() < listLimit) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) {
                                saveChangeIndex = -1;
                                addChangeInfoDialog();
                            }
                        });
                    } else {
                        ((MainActivity) requireActivity()).setButtonClicked(false);
                        ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.GONE);

                        if (!toastShowing) {
                            toastShowing = true;

                            if (firstErrorMessage) {
                                CustomToast.showInfo(requireActivity(), getString(R.string.events_per_day_limit_info_message1, listLimit), Toast.LENGTH_LONG);
                                new Handler().postDelayed(() -> toastShowing = false, Constants.TOAST_LONG_DURATION);
                            } else {
                                CustomToast.showInfo(requireActivity(), getString(R.string.events_per_day_limit_info_message2, listLimit), Toast.LENGTH_SHORT);
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
                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload();
                    getParentFragmentManager().setFragmentResult(Constants.DAY_FRAGMENT, new Bundle());
                }

                getParentFragmentManager().popBackStack();
            }
        });

        getParentFragmentManager().setFragmentResultListener(Constants.ADD_CHANGE_INFO_DIALOG_TAG, this, (requestKey, result) -> {
            String details = result.getString(Constants.BUNDLE_KEY3);
            String finalTime = result.getString(Constants.BUNDLE_KEY4);
            DatabaseNotification databaseNotification = Utility.getGsonParser().fromJson(result.getString(Constants.BUNDLE_KEY6), DatabaseNotification.class);

            sendDetailsTimeNotif(details, finalTime, databaseNotification);
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

                DayFragment dayFragment = new DayFragment();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.BUNDLE_KEY1, selectedWeek);
                bundle.putString(Constants.BUNDLE_KEY2, selectedDay);
                dayFragment.setArguments(bundle);

                getParentFragmentManager().popBackStack();
                getParentFragmentManager().beginTransaction().add(R.id.fragmentContainer, dayFragment).addToBackStack(null).commit();
            }
        } else {
            if (autoTime == 0) {
                DialogFragment dialogFragment1 = (DialogFragment) getParentFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
                DialogFragment dialogFragment2 = (DialogFragment) getParentFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

                if (dialogFragment1 != null) dialogFragment1.dismiss();
                else { if (dialogFragment2 != null) dialogFragment2.dismiss(); }

                DialogFragment dialogFragment3 = (DialogFragment) getParentFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
                if (dialogFragment3 == null) activateAutoTimeDialog();
            }
        }
    }

    public void displayInformation() {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Information> peopleCopy = new ArrayList<>();
        List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Week + "." + selectedDay).getAllKeys();

        information.clear();

        if (allBookKeys.isEmpty()) Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Week + "." + selectedDay).destroy();

        for (int i = 0; i < allBookKeys.size(); i++) {
            Information info = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).read(allBookKeys.get(i));

            information.add(new Information(Objects.requireNonNull(info).getDetails(), formatDate(selectedDayDate), selectedDay_localString.substring(0, 3), formatTime(info.getTime()), info.getEv_notif()));
            peopleCopy.add(new Information(info.getDetails(), info.getDate(), null, info.getTime(), info.getEv_notif()));
            keys.add(allBookKeys.get(i));
        }

        information.sort(new TimeComparator(true));
        updateIndexes(keys, peopleCopy);

        adapter.notifyDataSetChanged();
    }

    public void dayFragmentSetup() {
        checkCurrentDay();
        checkSelectedDay();
        setTitle();
        getWeekFlag();
        getDate();

        String subtitle = selectedDay_localString + " (" + formatDate(selectedDayDate) + ")";
        this.subtitle.setText(subtitle);

        if (!selectedWeek.equals(Constants.LAST_WEEK) && !(selectedWeek.equals(Constants.THIS_WEEK) && currentDay > selectedDayInt)) {
            registerForContextMenu(day);
        }
    }

    private void scaleListView() {
        day.setDividerHeight(ScaledLayoutVariables.DAY_FRAGMENT_LIST_DIV_HEIGHT);
        day.setPadding(0, ScaledLayoutVariables.DAY_FRAGMENT_LIST_TOP_PAD, 0, ScaledLayoutVariables.DAY_FRAGMENT_LIST_BOT_PAD);
    }

    private void checkCurrentDay() {
        switch (currentDay) {
            case Calendar.MONDAY:
                currentDay = 1;
                break;

            case Calendar.TUESDAY:
                currentDay = 2;
                break;

            case Calendar.WEDNESDAY:
                currentDay = 3;
                break;

            case Calendar.THURSDAY:
                currentDay = 4;
                break;

            case Calendar.FRIDAY:
                currentDay = 5;
                break;

            case Calendar.SATURDAY:
                currentDay = 6;
                break;

            case Calendar.SUNDAY:
                currentDay = 7;
                break;
        }
    }

    private void checkSelectedDay() {
        switch (selectedDay) {
            case Constants.MONDAY:
                selectedDayInt = 1;
                selectedDay_localString = getString(R.string.monday);
                break;

            case Constants.TUESDAY:
                selectedDayInt = 2;
                selectedDay_localString = getString(R.string.tuesday);
                break;

            case Constants.WEDNESDAY:
                selectedDayInt = 3;
                selectedDay_localString = getString(R.string.wednesday);
                break;

            case Constants.THURSDAY:
                selectedDayInt = 4;
                selectedDay_localString = getString(R.string.thursday);
                break;

            case Constants.FRIDAY:
                selectedDayInt = 5;
                selectedDay_localString = getString(R.string.friday);
                break;

            case Constants.SATURDAY:
                selectedDayInt = 6;
                selectedDay_localString = getString(R.string.saturday);
                break;

            case Constants.SUNDAY:
                selectedDayInt = 7;
                selectedDay_localString = getString(R.string.sunday);
                break;
        }
    }

    private void setTitle() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.SUBTITLE_TEXT_SIZE);

        switch (selectedWeek) {
            case Constants.LAST_WEEK:
                disableFABButton(add);
                break;

            case Constants.THIS_WEEK:
                if (currentDay > selectedDayInt) {
                    disableFABButton(add);
                }

                break;
        }

        ManageTitlePosition.manageFragmentMultiTitle(requireActivity(), waveHeader, title, subtitle, guideline5);
    }

    private void getDate() {
        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

        if (connectionLostWhileSigningIn == 0) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
            dateFormat.setLenient(false);

            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setLenient(false);

            Date date = new Date();
            String ref_date = Objects.requireNonNull(Paper.book(Constants.REF_DATE).read(Constants.DATE));

            try {
                date = dateFormat.parse(ref_date);
            } catch (ParseException e) {
                Log.e(TAG, "getDate: ParseException: " + e.getMessage());
            }

            calendar.setTime(Objects.requireNonNull(date));

            switch (selectedWeek) {
                case Constants.LAST_WEEK:
                    switch (selectedDay) {
                        case Constants.MONDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -7);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.TUESDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -6);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.WEDNESDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -5);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.THURSDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -4);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.FRIDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -3);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.SATURDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -2);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.SUNDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;
                    }
                    break;

                case Constants.THIS_WEEK:
                    switch (selectedDay) {
                        case Constants.MONDAY:
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.TUESDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.WEDNESDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 2);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.THURSDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 3);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.FRIDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 4);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.SATURDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 5);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.SUNDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 6);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;
                    }
                    break;

                case Constants.NEXT_WEEK:
                    switch (selectedDay) {
                        case Constants.MONDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 7);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.TUESDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 8);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.WEDNESDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 9);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.THURSDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 10);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.FRIDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 11);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.SATURDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 12);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;

                        case Constants.SUNDAY:
                            calendar.add(Calendar.DAY_OF_MONTH, 13);
                            selectedDayDate = dateFormat.format(calendar.getTime());
                            break;
                    }
                    break;
            }
        }
    }

    private void getWeekFlag() {
        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

        if (connectionLostWhileSigningIn == 0) {
            String week0Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK0));
            String week1Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK1));
            String week2Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK2));
            String week3Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK3));

            if (week0Flag.equals(selectedWeek)) Week = Constants.WEEK0;
            else {
                if (week1Flag.equals(selectedWeek)) Week = Constants.WEEK1;
                else {
                    if (week2Flag.equals(selectedWeek)) Week = Constants.WEEK2;
                    else {
                        if (week3Flag.equals(selectedWeek)) Week = Constants.WEEK3;
                    }
                }
            }
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

    private void addChangeInfoDialog() {
        if (saveChangeIndex == -1) {
            AddChangeInfoDialog dialog = new AddChangeInfoDialog();
            dialog.show(getParentFragmentManager(), Constants.ADD_CHANGE_INFO_DIALOG_TAG);
        }

        if (saveChangeIndex >= 0) {
            AddChangeInfoDialog dialog = new AddChangeInfoDialog(information.get(saveChangeIndex).getDetails(), reverseFormatTime(information.get(saveChangeIndex).getTime()), information.get(saveChangeIndex).getEv_notif());
            dialog.show(getParentFragmentManager(), Constants.ADD_CHANGE_INFO_DIALOG_TAG);
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

    private void saveInformation(final String details, final String time, DatabaseNotification databaseNotification) {
        if (isTimeAvailable(time)) {
            Information info = new Information(0, details, null, time, databaseNotification);
            String pushKey = Constants.usersRef.child(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid()).child(Week).child(selectedDay).push().getKey();

            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).write(Objects.requireNonNull(pushKey), info);

            NotificationHelper.scheduleNotification(requireActivity(), databaseNotification, Week, selectedDay, selectedDayDate, info.getTime(), pushKey, info.getDetails());
            displayInformation();
        } else {
            CustomToast.showError(requireActivity(), getString(R.string.time_already_exists_error), Toast.LENGTH_LONG);
        }
    }

    private void changeInformation(final String name, final String time, DatabaseNotification databaseNotification) {
        if (isTimeAvailable(time)) {
            List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Week + "." + selectedDay).getAllKeys();

            for (int i = 0; i < allBookKeys.size(); i++) {
                Information info1 = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).read(allBookKeys.get(i));

                if (Objects.requireNonNull(info1).getIndex() == saveChangeIndex) {
                    Information info2;

                    if (allBookKeys.get(i).contains(".**")) {
                        info2 = new Information(info1.getIndex(), name, info1.getDate(), time, databaseNotification);
                    } else {
                        info2 = new Information(info1.getIndex(), name, null, time, databaseNotification);
                    }

                    Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).write(allBookKeys.get(i), info2);

                    if (!info2.getTime().equals(info1.getTime()) || !info2.getEv_notif().equals(info1.getEv_notif())) {
                        NotificationHelper.cancelNotification(requireActivity(), Week, selectedDay, allBookKeys.get(i));
                        NotificationHelper.scheduleNotification(requireActivity(), info2.getEv_notif(), Week, selectedDay, selectedDayDate, info2.getTime(), allBookKeys.get(i), info2.getDetails());
                    }

                    displayInformation();

                    break;
                }
            }
        } else {
            CustomToast.showError(requireActivity(), getString(R.string.time_already_exists_error), Toast.LENGTH_LONG);
        }
    }

    private void deleteInformation(final int index) {
        List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Week + "." + selectedDay).getAllKeys();

        for (int i = 0; i < allBookKeys.size(); i++) {
            Information info = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).read(allBookKeys.get(i));

            if (Objects.requireNonNull(info).getIndex() == index) {
                Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).delete(allBookKeys.get(i));
                NotificationHelper.cancelNotification(requireActivity(), Week, selectedDay, allBookKeys.get(i));

                displayInformation();

                if (allBookKeys.get(i).contains(".**")) {
                    String key = allBookKeys.get(i).substring(0, allBookKeys.get(i).lastIndexOf("."));

                    Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.INFO).child(key).removeValue();
                    Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.FLAGS).child(key).removeValue();
                } else {
                    Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Week).child(selectedDay).child(allBookKeys.get(i)).removeValue();
                }

                break;
            }
        }
    }

    private void updateIndexes(ArrayList<String> keys, ArrayList<Information> peopleCopy) {
        TreeMap<String, Information> sortedMap = new TreeMap<>();

        for (int i = 0; i < peopleCopy.size(); i++) {
            sortedMap.put(keys.get(i), peopleCopy.get(i));
        }

        peopleCopy.sort(new TimeComparator(false));

        for (int i = 0; i < peopleCopy.size(); i++) {
            peopleCopy.get(i).setIndex(i);
        }

        for (Map.Entry<String, Information> entry : sortedMap.entrySet()) {
            Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Week + "." + selectedDay).write(entry.getKey(), entry.getValue());

            if (entry.getKey().contains(".**")) {
                String key = entry.getKey().substring(0, entry.getKey().lastIndexOf("."));
                Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.OTHER_EVENTS).child(Constants.INFO).child(key).setValue(entry.getValue());
            } else {
                Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Week).child(selectedDay).child(entry.getKey()).setValue(entry.getValue());
            }
        }
    }

    private boolean isTimeAvailable(String time) {
        List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + Week + "." + selectedDay).getAllKeys();

        for (int i = 0; i < allBookKeys.size(); i++) {
            Information info = Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Week + "." + selectedDay).read(allBookKeys.get(i));

            if (Objects.requireNonNull(info).getTime().equals(time)) {
                if (saveChangeIndex == -1) return false;

                if (saveChangeIndex >= 0) {
                    return info.getIndex() == saveChangeIndex;
                }
            }
        }

        return true;
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

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(dateFormatOptions[dateFormatIndex].replace("mm", "MM"), Locale.US);
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
        } else { return timeToFormat; }
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
        } else { return timeToFormat; }
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

    public FloatingActionButton getAdd() { return add; }

    public int getCurrentDay() { return currentDay; }

    public int getSelectedDayInt() { return selectedDayInt; }

    public String getSelectedWeek() { return selectedWeek; }

    public String getSelectedDay() { return selectedDay; }

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
                addChangeInfoDialog();
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

    private void sendDetailsTimeNotif(String details, String time, DatabaseNotification databaseNotification) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.DATABASE_URL);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int code = connection.getResponseCode();

                    requireActivity().runOnUiThread(() -> {
                        if(code == HttpURLConnection.HTTP_OK) {
                            if (saveChangeIndex == -1) {
                                saveInformation(details, time, databaseNotification);
                            }

                            if (saveChangeIndex >= 0) {
                                changeInformation(details, time, databaseNotification);
                            }
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);
                        }

                        connection.disconnect();

                        DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                } catch (IOException e) {
                    Log.e(TAG, "sendInfoTime: IOException: " + e.getMessage());

                    requireActivity().runOnUiThread(() -> {
                        CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "sendInfoTime: MalformedURLException: " + e.getMessage());

                requireActivity().runOnUiThread(() -> {
                    CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                    DialogFragment dialogFragment = (DialogFragment) requireActivity().getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
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
                        if(code == HttpURLConnection.HTTP_OK) {
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
