package dialogs;

import android.app.NotificationManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.wave.MultiWaveHeader;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import io.paperdb.Paper;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.R;

public class OtherSettingsDialog extends DialogFragment {

    private static final String TAG = "OtherSettingsDialog";

    private TextView title;
    private TextView infoStringChangeLimit;
    private TextView listChangeLimit;
    private FloatingActionButton back;
    private Spinner timeFormat;
    private Spinner dateFormat;
    private Spinner notifications;
    private Spinner notificationsSchedule;
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar4;
    private ProgressBar progressBar5;
    private ProgressBar progressBar6;
    private Button increase1;
    private Button increase2;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;
    private boolean buttonClicked = false;
    private int spinnerFlag = 4;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_settings_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        infoStringChangeLimit = view.findViewById(R.id.infoStringChangeLimit_TextView);
        listChangeLimit = view.findViewById(R.id.listChangeLimit_TextView);
        back = view.findViewById(R.id.back_FAB);
        timeFormat = view.findViewById(R.id.timeFormat_Spinner);
        dateFormat = view.findViewById(R.id.dateFormat_Spinner);
        notifications = view.findViewById(R.id.notifications_Spinner);
        notificationsSchedule = view.findViewById(R.id.notificationsSchedule_Spinner);
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar3 = view.findViewById(R.id.progressBar3);
        progressBar4 = view.findViewById(R.id.progressBar4);
        progressBar5 = view.findViewById(R.id.progressBar5);
        progressBar6 = view.findViewById(R.id.progressBar6);
        increase1 = view.findViewById(R.id.increase_Button1);
        increase2 = view.findViewById(R.id.increase_Button2);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        waveHeader = view.findViewById(R.id.waveHeader);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);
        hideSystemUI();
        setupSpinnersAndButtons();

        displayCharacterAndEventLimits();

        back.setOnClickListener(v -> {
            if (!buttonClicked) {
                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload();
                }

                getDialog().dismiss();
            }
        });

        timeFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerFlag == 0) {
                    timeFormat.setEnabled(false);
                    timeFormat.setBackgroundResource(R.drawable.spinner_round_border);
                    timeFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                    progressBar1.setVisibility(View.VISIBLE);

                    disableFABButton(back);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null)
                                saveSpinnerSelection(position, Constants.TIME_F);
                        });
                    }
                } else {
                    spinnerFlag--;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerFlag == 0) {
                    dateFormat.setEnabled(false);
                    dateFormat.setBackgroundResource(R.drawable.spinner_round_border);
                    dateFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                    progressBar2.setVisibility(View.VISIBLE);

                    disableFABButton(back);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null)
                                saveSpinnerSelection(position, Constants.DATE_F);
                        });
                    }
                } else {
                    spinnerFlag--;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        notifications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerFlag == 0) {
                    notifications.setEnabled(false);
                    notifications.setBackgroundResource(R.drawable.spinner_round_border);
                    notifications.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                    progressBar3.setVisibility(View.VISIBLE);

                    disableFABButton(back);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null)
                                saveSpinnerSelection(position, Constants.NOTIF);
                        });
                    }
                } else {
                    spinnerFlag--;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        notificationsSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerFlag == 0) {
                    notificationsSchedule.setEnabled(false);
                    notificationsSchedule.setBackgroundResource(R.drawable.spinner_round_border);
                    notificationsSchedule.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                    progressBar4.setVisibility(View.VISIBLE);

                    disableFABButton(back);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null)
                                saveSpinnerSelection(position, Constants.NOTIF_SCHED);
                        });
                    }
                } else {
                    spinnerFlag--;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        increase1.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                timeFormat.setEnabled(false);
                dateFormat.setEnabled(false);
                notifications.setEnabled(false);
                notificationsSchedule.setEnabled(false);

                disableFABButton(back);
                increase1.setVisibility(View.INVISIBLE);
                progressBar5.setVisibility(View.VISIBLE);

                increaseDecreaseCharactersLimit();
            }
        });

        increase2.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                timeFormat.setEnabled(false);
                dateFormat.setEnabled(false);
                notifications.setEnabled(false);
                notificationsSchedule.setEnabled(false);

                disableFABButton(back);
                increase2.setVisibility(View.INVISIBLE);
                progressBar6.setVisibility(View.VISIBLE);

                increaseDecreaseEventsLimit();
            }
        });

        dialogWindow.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) hideSystemUI();
        });

        setCancelable(false);
        dialogWindow.setBackgroundDrawable(generateBackgroundDrawable());
        dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        dialogWindow.setWindowAnimations(R.style.DialogAnimation1);
        dialogWindow.setGravity(Gravity.TOP);
        view.setClipToOutline(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!dialogAnimationFlag) {
            dialogWindow.setWindowAnimations(R.style.DialogAnimation2);
        }

        if (DeviceCharacteristics.hasNotch(requireActivity())) {
            dialogWindow.setLayout((int) (metrics.widthPixels / getWidthDivisionNumber()), (int) (metrics.heightPixels / getHeightDivisionNumber()) + DeviceCharacteristics.getStatusBarHeight(requireActivity()));
        } else {
            dialogWindow.setLayout((int) (metrics.widthPixels / getWidthDivisionNumber()), (int) (metrics.heightPixels / getHeightDivisionNumber()));
        }

        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    public void onStop() {
        super.onStop();

        dialogAnimationFlag = false;
        ((AccountSettingsActivity) requireActivity()).setButtonClicked(false);
    }

    private void hideSystemUI() {
        dialogWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setupSpinnersAndButtons() {
        ArrayAdapter<CharSequence> timeFormatAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.time_formats, R.layout.spinner_item);
        timeFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFormat.setAdapter(timeFormatAdapter);
        timeFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);

        ArrayAdapter<CharSequence> dateFormatAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.date_formats, R.layout.spinner_item);
        dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateFormat.setAdapter(dateFormatAdapter);
        dateFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);

        ArrayAdapter<CharSequence> notificationsAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.notifications_settings, R.layout.spinner_item);
        notificationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notifications.setAdapter(notificationsAdapter);
        notifications.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);

        ArrayAdapter<CharSequence> notificationsScheduleAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.notifications_schedule, R.layout.spinner_item);
        notificationsScheduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationsSchedule.setAdapter(notificationsScheduleAdapter);
        notificationsSchedule.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);

        timeFormat.setSelection(BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F))).intValueExact());
        dateFormat.setSelection(BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F))).intValueExact());
        notifications.setSelection(BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.NOTIF))).intValueExact());
        notificationsSchedule.setSelection(BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.NOTIF_SCHED))).intValueExact());

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) progressBar1.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT;
        progressBar1.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) progressBar2.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT;
        progressBar2.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) progressBar3.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT;
        progressBar3.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) progressBar4.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.OSD_PROGRESS_BAR_HEIGHT;
        progressBar4.setLayoutParams(layoutParams);

        back.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        back.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);

        layoutParams = (ConstraintLayout.LayoutParams) increase1.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.OSD_BUTTONS_HEIGHT;
        increase1.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.OSD_BUTTONS_TEXT_SIZE);
        increase1.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) increase2.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.OSD_BUTTONS_HEIGHT;
        increase2.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.OSD_BUTTONS_TEXT_SIZE);
        increase2.setLayoutParams(layoutParams);
    }

    private void saveSpinnerSelection(long position, String format) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.DATABASE_URL);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int code = connection.getResponseCode();

                    requireActivity().runOnUiThread(() -> {
                        if(code == HttpURLConnection.HTTP_OK) {
                            Constants.usersRef.child(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid()).child(Constants.OTHER_SETTINGS).child(format).setValue(position)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Paper.book(Constants.OTHER_SETTINGS).write(format, position);
                                        } else {
                                            CustomToast.showError(requireActivity(), getString(R.string.saving_settings_error), Toast.LENGTH_LONG);
                                        }

                                        switch (format) {
                                            case Constants.TIME_F:
                                                timeFormat.setEnabled(true);
                                                timeFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                                timeFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                                progressBar1.setVisibility(View.GONE);
                                                break;

                                            case Constants.DATE_F:
                                                dateFormat.setEnabled(true);
                                                dateFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                                dateFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                                progressBar2.setVisibility(View.GONE);
                                                break;

                                            case Constants.NOTIF:
                                                notifications.setEnabled(true);
                                                notifications.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                                notifications.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                                progressBar3.setVisibility(View.GONE);

                                                if (position == 0) {
                                                    if (!NotificationManagerCompat.from(requireActivity()).areNotificationsEnabled()) {
                                                        CustomToast.showWarning(requireActivity(), getString(R.string.notifications_disabled), Toast.LENGTH_LONG);
                                                    }

                                                    if (requireActivity().getSystemService(NotificationManager.class).getNotificationChannel(Constants.EVENT_NOTIFICATION_CHANNEL_ID).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                                                        CustomToast.showWarning(requireActivity(), getString(R.string.notification_channel_disabled), Toast.LENGTH_LONG);
                                                    }
                                                }

                                                break;

                                            case Constants.NOTIF_SCHED:
                                                notificationsSchedule.setEnabled(true);
                                                notificationsSchedule.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                                notificationsSchedule.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                                progressBar4.setVisibility(View.GONE);
                                                break;
                                        }

                                        if (progressBar1.getVisibility() == View.GONE && progressBar2.getVisibility() == View.GONE && progressBar3.getVisibility() == View.GONE && progressBar4.getVisibility() == View.GONE) enableFABButton(back);
                                    });
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                            switch (format) {
                                case Constants.TIME_F:
                                    timeFormat.setEnabled(true);
                                    timeFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                    timeFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                    progressBar1.setVisibility(View.GONE);
                                    break;

                                case Constants.DATE_F:
                                    dateFormat.setEnabled(true);
                                    dateFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                    dateFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                    progressBar2.setVisibility(View.GONE);
                                    break;

                                case Constants.NOTIF:
                                    notifications.setEnabled(true);
                                    notifications.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                    notifications.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                    progressBar3.setVisibility(View.GONE);
                                    break;

                                case Constants.NOTIF_SCHED:
                                    notificationsSchedule.setEnabled(true);
                                    notificationsSchedule.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                    notificationsSchedule.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                    progressBar4.setVisibility(View.GONE);
                                    break;
                            }

                            if (progressBar1.getVisibility() == View.GONE && progressBar2.getVisibility() == View.GONE && progressBar3.getVisibility() == View.GONE && progressBar4.getVisibility() == View.GONE) enableFABButton(back);
                        }

                        connection.disconnect();
                    });
                } catch (IOException e) {
                    Log.e(TAG, "saveSpinnerSelection: IOException: " + e.getMessage());

                    requireActivity().runOnUiThread(() -> {
                        CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        switch (format) {
                            case Constants.TIME_F:
                                timeFormat.setEnabled(true);
                                timeFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                timeFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                progressBar1.setVisibility(View.GONE);
                                break;

                            case Constants.DATE_F:
                                dateFormat.setEnabled(true);
                                dateFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                dateFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                progressBar2.setVisibility(View.GONE);
                                break;

                            case Constants.NOTIF:
                                notifications.setEnabled(true);
                                notifications.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                notifications.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                progressBar3.setVisibility(View.GONE);
                                break;

                            case Constants.NOTIF_SCHED:
                                notificationsSchedule.setEnabled(true);
                                notificationsSchedule.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                                notificationsSchedule.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                                progressBar4.setVisibility(View.GONE);
                                break;
                        }

                        if (progressBar1.getVisibility() == View.GONE && progressBar2.getVisibility() == View.GONE && progressBar3.getVisibility() == View.GONE && progressBar4.getVisibility() == View.GONE) enableFABButton(back);
                    });
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "saveSpinnerSelection: MalformedURLException: " + e.getMessage());

                requireActivity().runOnUiThread(() -> {
                    CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                    switch (format) {
                        case Constants.TIME_F:
                            timeFormat.setEnabled(true);
                            timeFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                            timeFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                            progressBar1.setVisibility(View.GONE);
                            break;

                        case Constants.DATE_F:
                            dateFormat.setEnabled(true);
                            dateFormat.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                            dateFormat.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                            progressBar2.setVisibility(View.GONE);
                            break;

                        case Constants.NOTIF:
                            notifications.setEnabled(true);
                            notifications.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                            notifications.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                            progressBar3.setVisibility(View.GONE);
                            break;

                        case Constants.NOTIF_SCHED:
                            notificationsSchedule.setEnabled(true);
                            notificationsSchedule.setBackgroundResource(R.drawable.spinner_round_border_with_arrow);
                            notificationsSchedule.setPadding(ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD, ScaledLayoutVariables.OSD_SPINNER_PAD);
                            progressBar4.setVisibility(View.GONE);
                            break;
                    }

                    if (progressBar1.getVisibility() == View.GONE && progressBar2.getVisibility() == View.GONE && progressBar3.getVisibility() == View.GONE && progressBar4.getVisibility() == View.GONE) enableFABButton(back);
                });
            }
        });

        thread.start();
    }

    private void increaseDecreaseCharactersLimit() {
        long value = ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.CHAR_INC)) - 1) * (-1);

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.DATABASE_URL);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int code = connection.getResponseCode();

                    requireActivity().runOnUiThread(() -> {
                        if (code == HttpURLConnection.HTTP_OK) {
                            Constants.usersRef.child(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid()).child(Constants.OTHER_SETTINGS).child(Constants.CHAR_INC).setValue(value)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            String text = "";

                                            if (value == 1) {
                                                increase1.setText(getString(R.string.decrease));
                                                text = getString(R.string.info_character_limit, Constants.MAX_INCREASED_INFO_LENGTH);
                                            }
                                            else {
                                                if (value == 0) {
                                                    increase1.setText(getString(R.string.increase));
                                                    text = getString(R.string.info_character_limit, Constants.MAX_INFO_LENGTH);
                                                }
                                            }

                                            Paper.book(Constants.OTHER_SETTINGS).write(Constants.CHAR_INC, value);

                                            SpannableString spannableString = new SpannableString(text);
                                            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorGrey)), text.indexOf(":") + 1, text.length(), 0);
                                            infoStringChangeLimit.setText(spannableString);
                                        } else {
                                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);
                                        }

                                        buttonClicked = false;

                                        timeFormat.setEnabled(true);
                                        dateFormat.setEnabled(true);
                                        notifications.setEnabled(true);
                                        notificationsSchedule.setEnabled(true);

                                        enableFABButton(back);
                                        increase1.setVisibility(View.VISIBLE);
                                        progressBar5.setVisibility(View.GONE);
                                    });
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                            buttonClicked = false;

                            timeFormat.setEnabled(true);
                            dateFormat.setEnabled(true);
                            notifications.setEnabled(true);
                            notificationsSchedule.setEnabled(true);

                            enableFABButton(back);
                            increase1.setVisibility(View.VISIBLE);
                            progressBar5.setVisibility(View.GONE);
                        }

                        connection.disconnect();
                    });
                } catch (IOException e) {
                    Log.e(TAG, "increaseDecreaseCharactersLimit: IOException: " + e.getMessage());

                    requireActivity().runOnUiThread(() -> {
                        CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        buttonClicked = false;

                        timeFormat.setEnabled(true);
                        dateFormat.setEnabled(true);
                        notifications.setEnabled(true);
                        notificationsSchedule.setEnabled(true);

                        enableFABButton(back);
                        increase1.setVisibility(View.VISIBLE);
                        progressBar5.setVisibility(View.GONE);
                    });
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "increaseDecreaseCharactersLimit: MalformedURLException: " + e.getMessage());

                requireActivity().runOnUiThread(() -> {
                    CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                    buttonClicked = false;

                    timeFormat.setEnabled(true);
                    dateFormat.setEnabled(true);
                    notifications.setEnabled(true);
                    notificationsSchedule.setEnabled(true);

                    enableFABButton(back);
                    increase1.setVisibility(View.VISIBLE);
                    progressBar5.setVisibility(View.GONE);
                });
            }
        });

        thread.start();
    }

    private void increaseDecreaseEventsLimit() {
        long value = ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.EVENTS_INC)) - 1) * (-1);

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.DATABASE_URL);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int code = connection.getResponseCode();

                    requireActivity().runOnUiThread(() -> {
                        if (code == HttpURLConnection.HTTP_OK) {
                            Constants.usersRef.child(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid()).child(Constants.OTHER_SETTINGS).child(Constants.EVENTS_INC).setValue(value)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            String text = "";

                                            if (value == 1) {
                                                increase2.setText(getString(R.string.decrease));
                                                text = getString(R.string.events_per_day_limit, Constants.MAX_INCREASED_EVENTS_PER_DAY, Constants.MAX_INCREASED_OTHER_EVENTS);
                                            } else {
                                                if (value == 0) {
                                                    increase2.setText(getString(R.string.increase));
                                                    text = getString(R.string.events_per_day_limit, Constants.MAX_EVENTS_PER_DAY, Constants.MAX_OTHER_EVENTS);
                                                }
                                            }

                                            Paper.book(Constants.OTHER_SETTINGS).write(Constants.EVENTS_INC, value);

                                            SpannableString spannableString = new SpannableString(text);
                                            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorGrey)), text.indexOf(":") + 1, text.indexOf("/", text.indexOf("/") + 1), 0);
                                            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorGrey)), text.indexOf("/", text.indexOf("/") + 1) + 1, text.length(), 0);
                                            listChangeLimit.setText(spannableString);
                                        } else {
                                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);
                                        }

                                        buttonClicked = false;

                                        timeFormat.setEnabled(true);
                                        dateFormat.setEnabled(true);
                                        notifications.setEnabled(true);
                                        notificationsSchedule.setEnabled(true);

                                        enableFABButton(back);
                                        increase2.setVisibility(View.VISIBLE);
                                        progressBar6.setVisibility(View.GONE);
                                    });
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                            buttonClicked = false;

                            timeFormat.setEnabled(true);
                            dateFormat.setEnabled(true);
                            notifications.setEnabled(true);
                            notificationsSchedule.setEnabled(true);

                            enableFABButton(back);
                            increase2.setVisibility(View.VISIBLE);
                            progressBar6.setVisibility(View.GONE);
                        }

                        connection.disconnect();
                    });
                } catch (IOException e) {
                    Log.e(TAG, "increaseDecreaseEventsLimit: IOException: " + e.getMessage());

                    requireActivity().runOnUiThread(() -> {
                        CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        buttonClicked = false;

                        timeFormat.setEnabled(true);
                        dateFormat.setEnabled(true);
                        notifications.setEnabled(true);
                        notificationsSchedule.setEnabled(true);

                        enableFABButton(back);
                        increase2.setVisibility(View.VISIBLE);
                        progressBar6.setVisibility(View.GONE);
                    });
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "increaseDecreaseEventsLimit: MalformedURLException: " + e.getMessage());

                requireActivity().runOnUiThread(() -> {
                    CustomToast.showError(requireActivity(), getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                    buttonClicked = false;

                    timeFormat.setEnabled(true);
                    dateFormat.setEnabled(true);
                    notifications.setEnabled(true);
                    notificationsSchedule.setEnabled(true);

                    enableFABButton(back);
                    increase2.setVisibility(View.VISIBLE);
                    progressBar6.setVisibility(View.GONE);
                });
            }
        });

        thread.start();
    }

    private void displayCharacterAndEventLimits() {
        int infoLimit = Constants.MAX_INFO_LENGTH;

        if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.CHAR_INC)) == 1) {
            increase1.setText(getString(R.string.decrease));
            infoLimit = Constants.MAX_INCREASED_INFO_LENGTH;
        }

        String text = getString(R.string.info_character_limit, infoLimit);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorGrey)), text.indexOf(":") + 1, text.length(), 0);
        infoStringChangeLimit.setText(spannableString);

        int listLimit1 = Constants.MAX_EVENTS_PER_DAY;
        int listLimit2 = Constants.MAX_OTHER_EVENTS;

        if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.EVENTS_INC)) == 1) {
            increase2.setText(getString(R.string.decrease));
            listLimit1 = Constants.MAX_INCREASED_EVENTS_PER_DAY;
            listLimit2 = Constants.MAX_INCREASED_OTHER_EVENTS;
        }

        text = getString(R.string.events_per_day_limit, listLimit1, listLimit2);
        spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorGrey)), text.indexOf(":") + 1, text.indexOf("/", text.indexOf("/") + 1), 0);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorGrey)), text.indexOf("/", text.indexOf("/") + 1) + 1, text.length(), 0);
        listChangeLimit.setText(spannableString);
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int pxBackgroundTopPadding = ScaledLayoutVariables.OSD_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) pxBackgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, pxBackgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.osd_guideline1, getHeightDivisionNumber(), pxBackgroundTopPadding);

        return background;
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.osd_h_division_number, typedValue, true);

        return typedValue.getFloat();
    }

    private float getWidthDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.dialogs_w_division_number, typedValue, true);

        return typedValue.getFloat();
    }

    public void disableButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDisabled)));
    }

    public void enableButton(Button button) {
        button.setEnabled(true);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary)));
    }

    public void disableFABButton(FloatingActionButton button) {
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDisabled)));
    }

    public void enableFABButton(FloatingActionButton button) {
        button.setEnabled(true);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary)));
    }

    public void disableSpinner(Spinner spinner) { spinner.setEnabled(false); }

    public void enableSpinner(Spinner spinner) { spinner.setEnabled(true); }

    public Spinner getTimeFormat() { return timeFormat; }

    public Spinner getDateFormat() { return dateFormat; }

    public Spinner getNotifications() { return notifications; }

    public Spinner getNotificationsSchedule() { return notificationsSchedule; }

    public Button getIncrease1() { return increase1; }

    public Button getIncrease2() { return increase2; }
}
