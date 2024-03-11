package dialogs;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.scwang.wave.MultiWaveHeader;
import com.shawnlin.numberpicker.NumberPicker;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.Utility;
import helper_classes.notifications.DatabaseNotification;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.AddChangeInfoValidator;
import io.paperdb.Paper;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;

public class AddChangeInfoDialog extends DialogFragment {

    private static final String TAG = "AddInformationDialog";

    private TextView title;
    private TextInputLayout information;
    private NumberPicker hour;
    private NumberPicker minutes;
    private NumberPicker am_pm;
    private TextView colon;
    private Button cancel;
    private Button ok;
    private DisplayMetrics metrics;
    private String Information = "";
    private String Time = "";
    private int Hour = -1;
    private int Minutes = -1;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private FloatingActionButton notification;
    private FloatingActionButton notificationSchedule;
    private DatabaseNotification databaseNotification = new DatabaseNotification(true, 0, 0);
    private boolean initialNotificationActive = true;
    private int initialFirstNotificationIndex = 0;
    private int initialSecondNotificationIndex = 0;
    private boolean dialogAnimationFlag = true;
    private boolean buttonClicked = false;
    private int inflatedLayout;

    private TextWatcher infoTextWatcher;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    public AddChangeInfoDialog() {
    }

    public AddChangeInfoDialog(String information, String time, DatabaseNotification databaseNotification) {
        Information = information;
        Time = time;
        this.databaseNotification = new DatabaseNotification(databaseNotification.isActive(), databaseNotification.getIndex1(), databaseNotification.getIndex2());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_change_info_dialog_24_hour, container, false);

        inflatedLayout = BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F))).intValueExact();

        if (inflatedLayout == 1) {
            view = inflater.inflate(R.layout.add_change_info_dialog_12_hour, container, false);
            am_pm = view.findViewById(R.id.am_pm_NumberPicker);
        }

        title = view.findViewById(R.id.title_TextView);
        information = view.findViewById(R.id.info_TIL);
        hour = view.findViewById(R.id.hour_NumberPicker);
        minutes = view.findViewById(R.id.minutes_NumberPicker);
        colon = view.findViewById(R.id.colon_TextView);
        cancel = view.findViewById(R.id.cancel_Button);
        ok = view.findViewById(R.id.ok_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        progressBar = view.findViewById(R.id.progressBar);
        waveHeader = view.findViewById(R.id.waveHeader);
        notification = view.findViewById(R.id.notification_FAB);
        notificationSchedule = view.findViewById(R.id.notificationSchedule_FAB);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);
        scaleButtons();
        Objects.requireNonNull(information.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        hideSystemUI();

        if (!((MainActivity) requireActivity()).getNetwork()) {
            disableButton(ok);
        }

        if (!Information.isEmpty() && !Time.isEmpty()) {
            Objects.requireNonNull(information.getEditText()).setText(Information);
            Hour = Integer.parseInt(Time.substring(0, 2));
            Minutes = Integer.parseInt(Time.substring(3));

            if (!databaseNotification.isActive()) {
                notification.setImageResource(R.drawable.ic_notification_off);
                initialNotificationActive = false;
                disableFABButton(notificationSchedule);
            } else {
                if (!NotificationManagerCompat.from(requireActivity()).areNotificationsEnabled()) {
                    CustomToast.showWarning(requireActivity(), getString(R.string.notifications_disabled), Toast.LENGTH_LONG);
                }

                if (requireActivity().getSystemService(NotificationManager.class).getNotificationChannel(Constants.EVENT_NOTIFICATION_CHANNEL_ID).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    CustomToast.showWarning(requireActivity(), getString(R.string.notification_channel_disabled), Toast.LENGTH_LONG);
                }
            }
        } else {
            managePopupMenuClick(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.NOTIF_SCHED)), null);

            if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.NOTIF)) == 1) {
                notification.setImageResource(R.drawable.ic_notification_off);
                databaseNotification.setActive(false);
                initialNotificationActive = false;
                disableFABButton(notificationSchedule);
            } else {
                if (!NotificationManagerCompat.from(requireActivity()).areNotificationsEnabled()) {
                    CustomToast.showWarning(requireActivity(), getString(R.string.notifications_disabled), Toast.LENGTH_LONG);
                }

                if (requireActivity().getSystemService(NotificationManager.class).getNotificationChannel(Constants.EVENT_NOTIFICATION_CHANNEL_ID).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    CustomToast.showWarning(requireActivity(), getString(R.string.notification_channel_disabled), Toast.LENGTH_LONG);
                }
            }
        }

        initialFirstNotificationIndex = databaseNotification.getIndex1();
        initialSecondNotificationIndex = databaseNotification.getIndex2();

        numberPickerSetup();

        notification.setOnClickListener(v -> {
            if (databaseNotification.isActive()) {
                notification.setImageResource(R.drawable.ic_notification_off);
                databaseNotification.setActive(false);
                disableFABButton(notificationSchedule);
            } else {
                notification.setImageResource(R.drawable.ic_notification_active);
                databaseNotification.setActive(true);
                enableFABButton(notificationSchedule);

                if (!NotificationManagerCompat.from(requireActivity()).areNotificationsEnabled()) {
                    CustomToast.showWarning(requireActivity(), getString(R.string.notifications_disabled), Toast.LENGTH_LONG);
                }

                if (requireActivity().getSystemService(NotificationManager.class).getNotificationChannel(Constants.EVENT_NOTIFICATION_CHANNEL_ID).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    CustomToast.showWarning(requireActivity(), getString(R.string.notification_channel_disabled), Toast.LENGTH_LONG);
                }
            }
        });

        notificationSchedule.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                dialogWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

                PopupMenu notificationMenu;
                Context wrapper = new ContextThemeWrapper(requireActivity(), R.style.PopupMenuStyle);

                notificationMenu = new PopupMenu(wrapper, v, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
                notificationMenu.inflate(R.menu.notification_changer);

                SpannableString spannableString1 = new SpannableString(notificationMenu.getMenu().getItem(databaseNotification.getIndex1()).getTitle());
                spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorPrimary)), 0, spannableString1.length(), 0);
                notificationMenu.getMenu().getItem(databaseNotification.getIndex1()).setTitle(spannableString1);

                if (notificationMenu.getMenu().getItem(databaseNotification.getIndex1()).getSubMenu() != null) {
                    spannableString1 = new SpannableString(notificationMenu.getMenu().getItem(databaseNotification.getIndex1()).getSubMenu().getItem(databaseNotification.getIndex2()).getTitle());
                    spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorPrimary)), 0, spannableString1.length(), 0);
                    notificationMenu.getMenu().getItem(databaseNotification.getIndex1()).getSubMenu().getItem(databaseNotification.getIndex2()).setTitle(spannableString1);
                }

                notificationMenu.show();

                notificationMenu.setOnMenuItemClickListener(item -> {
                    boolean value = managePopupMenuClick(-1, item);

                    if (notificationMenu.getMenu().findItem(item.getItemId()).getSubMenu() != null) {
                        SpannableString spannableString2 = new SpannableString(getString(R.string.second_notification));
                        spannableString2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorPrimary)), 0, spannableString2.length(), 0);

                        if (Constants.DEFAULT_LANG.equals(Constants.EN_LANG)) {
                            if (item.getItemId() == R.id.early5mins_Item) {
                                spannableString2.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.early5mins_title_text_size), false), 0, spannableString2.length(), 0);
                            } else {
                                if (item.getItemId() == R.id.early10mins_Item) {
                                    spannableString2.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.early10mins_title_text_size), false), 0, spannableString2.length(), 0);
                                } else {
                                    spannableString2.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.popup_menu_title_text_size), false), 0, spannableString2.length(), 0);
                                }
                            }
                        } else {
                            if (Constants.DEFAULT_LANG.equals(Constants.BG_LANG)) {
                                spannableString2.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.popup_menu_title_text_size), false), 0, spannableString2.length(), 0);
                            }
                        }

                        notificationMenu.getMenu().findItem(item.getItemId()).getSubMenu().setHeaderTitle(spannableString2);
                    }

                    return value;
                });

                notificationMenu.setOnDismissListener(menu -> {
                    buttonClicked = false;
                    hideSystemUI();
                });
            }
        });

        cancel.setOnClickListener(v -> {
            if (Constants.auth.getCurrentUser() != null) {
                Constants.auth.getCurrentUser().reload();
            }

            getDialog().dismiss();
        });

        ok.setOnClickListener(v -> {
            String details = Objects.requireNonNull(information.getEditText()).getText().toString().trim();
            @SuppressLint("DefaultLocale")
            String time = String.format("%02d", hour.getValue()) + ":" + String.format("%02d", minutes.getValue());

            if (inflatedLayout == 1) {
                if (am_pm.getValue() % 2 == 0) time += " AM";
                else { time += " PM"; }

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat timeFormat24 = new SimpleDateFormat(Constants.TIME_FORMAT_24, Locale.US);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat timeFormat12 = new SimpleDateFormat(Constants.TIME_FORMAT_12, Locale.US);

                Date date = new Date();

                try {
                    date = timeFormat12.parse(time);
                } catch (ParseException e) {
                    Log.e(TAG, "onCreateView: ParseException: " + e.getMessage());
                }

                time = timeFormat24.format(Objects.requireNonNull(date));
            }

            if (AddChangeInfoValidator.validateInfo(information, details, requireActivity())) {
                if (!details.equals(AddChangeInfoDialog.this.Information) || !time.equals(Time) || initialNotificationActive != databaseNotification.isActive() || ((initialFirstNotificationIndex != databaseNotification.getIndex1() || initialSecondNotificationIndex != databaseNotification.getIndex2()) && databaseNotification.isActive())) {
                    cancel.setVisibility(View.INVISIBLE);
                    ok.setVisibility(View.INVISIBLE);
                    information.getEditText().setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    disableFABButton(notification);
                    disableFABButton(notificationSchedule);

                    if (Constants.auth.getCurrentUser() != null) {
                        String finalTime = time;

                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) {
                                Bundle bundle = new Bundle();

                                bundle.putString(Constants.BUNDLE_KEY3, details);
                                bundle.putString(Constants.BUNDLE_KEY4, finalTime);
                                bundle.putString(Constants.BUNDLE_KEY6, Utility.getGsonParser().toJson(databaseNotification));

                                getParentFragmentManager().setFragmentResult(Constants.ADD_CHANGE_INFO_DIALOG_TAG, bundle);
                            }
                        });
                    }
                } else {
                    getDialog().dismiss();
                }
            }
        });

        keyboardListener = isVisible -> {
            if (isVisible) showSystemUI();
            else { hideSystemUI(); }
        };

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

        textWatcherSetup();
        KeyboardUtils.addKeyboardToggleListener(getActivity(), keyboardListener);

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

        ((MainActivity) requireActivity()).setButtonClicked(false);
        ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.GONE);

        Objects.requireNonNull(information.getEditText()).removeTextChangedListener(infoTextWatcher);
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);
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

    private void showSystemUI() {
        dialogWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private boolean managePopupMenuClick(long defaultSelection, MenuItem item) {
        if (defaultSelection == 0 || (item != null && item.getItemId() == R.id.onTime_Item)) {
            databaseNotification.setIndex1(0);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 1 || (item != null && item.getItemId() == R.id.early5mins_notification_off)) {
            databaseNotification.setIndex1(1);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 2 || (item != null && item.getItemId() == R.id.early5mins_onTime_Item)) {
            databaseNotification.setIndex1(1);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 3 || (item != null && item.getItemId() == R.id.early10mins_notification_off)) {
            databaseNotification.setIndex1(2);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 4 || (item != null && item.getItemId() == R.id.early10mins_onTime_Item)) {
            databaseNotification.setIndex1(2);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 5 || (item != null && item.getItemId() == R.id.early10mins_early5mins_Item)) {
            databaseNotification.setIndex1(2);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 6 || (item != null && item.getItemId() == R.id.early15mins_notification_off)) {
            databaseNotification.setIndex1(3);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 7 || (item != null && item.getItemId() == R.id.early15mins_onTime_Item)) {
            databaseNotification.setIndex1(3);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 8 || (item != null && item.getItemId() == R.id.early15mins_early5mins_Item)) {
            databaseNotification.setIndex1(3);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 9 || (item != null && item.getItemId() == R.id.early15mins_early10mins_Item)) {
            databaseNotification.setIndex1(3);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 10 || (item != null && item.getItemId() == R.id.early30mins_notification_off)) {
            databaseNotification.setIndex1(4);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 11 || (item != null && item.getItemId() == R.id.early30mins_onTime_Item)) {
            databaseNotification.setIndex1(4);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 12 || (item != null && item.getItemId() == R.id.early30mins_early5mins_Item)) {
            databaseNotification.setIndex1(4);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 13 || (item != null && item.getItemId() == R.id.early30mins_early10mins_Item)) {
            databaseNotification.setIndex1(4);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 14 || (item != null && item.getItemId() == R.id.early30mins_early15mins_Item)) {
            databaseNotification.setIndex1(4);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 15 || (item != null && item.getItemId() == R.id.early1hr_notification_off)) {
            databaseNotification.setIndex1(5);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 16 || (item != null && item.getItemId() == R.id.early1hr_onTime_Item)) {
            databaseNotification.setIndex1(5);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 17 || (item != null && item.getItemId() == R.id.early1hr_early5mins_Item)) {
            databaseNotification.setIndex1(5);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 18 || (item != null && item.getItemId() == R.id.early1hr_early10mins_Item)) {
            databaseNotification.setIndex1(5);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 19 || (item != null && item.getItemId() == R.id.early1hr_early15mins_Item)) {
            databaseNotification.setIndex1(5);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 20 || (item != null && item.getItemId() == R.id.early1hr_early30mins_Item)) {
            databaseNotification.setIndex1(5);
            databaseNotification.setIndex2(5);
            return true;
        }

        if (defaultSelection == 21 || (item != null && item.getItemId() == R.id.early2hrs_notification_off)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 22 || (item != null && item.getItemId() == R.id.early2hrs_onTime_Item)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 23 || (item != null && item.getItemId() == R.id.early2hrs_early5mins_Item)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 24 || (item != null && item.getItemId() == R.id.early2hrs_early10mins_Item)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 25 || (item != null && item.getItemId() == R.id.early2hrs_early15mins_Item)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 26 || (item != null && item.getItemId() == R.id.early2hrs_early30mins_Item)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(5);
            return true;
        }

        if (defaultSelection == 27 || (item != null && item.getItemId() == R.id.early2hrs_early1hr_Item)) {
            databaseNotification.setIndex1(6);
            databaseNotification.setIndex2(6);
            return true;
        }

        if (defaultSelection == 28 || (item != null && item.getItemId() == R.id.early3hrs_notification_off)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 29 || (item != null && item.getItemId() == R.id.early3hrs_onTime_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 30 || (item != null && item.getItemId() == R.id.early3hrs_early5mins_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 31 || (item != null && item.getItemId() == R.id.early3hrs_early10mins_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 32 || (item != null && item.getItemId() == R.id.early3hrs_early15mins_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 33 || (item != null && item.getItemId() == R.id.early3hrs_early30mins_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(5);
            return true;
        }

        if (defaultSelection == 34 || (item != null && item.getItemId() == R.id.early3hrs_early1hr_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(6);
            return true;
        }

        if (defaultSelection == 35 || (item != null && item.getItemId() == R.id.early3hrs_early2hrs_Item)) {
            databaseNotification.setIndex1(7);
            databaseNotification.setIndex2(7);
            return true;
        }

        if (defaultSelection == 36 || (item != null && item.getItemId() == R.id.early6hrs_notification_off)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 37 || (item != null && item.getItemId() == R.id.early6hrs_onTime_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 38 || (item != null && item.getItemId() == R.id.early6hrs_early5mins_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 39 || (item != null && item.getItemId() == R.id.early6hrs_early10mins_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 40 || (item != null && item.getItemId() == R.id.early6hrs_early15mins_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 41 || (item != null && item.getItemId() == R.id.early6hrs_early30mins_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(5);
            return true;
        }

        if (defaultSelection == 42 || (item != null && item.getItemId() == R.id.early6hrs_early1hr_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(6);
            return true;
        }

        if (defaultSelection == 43 || (item != null && item.getItemId() == R.id.early6hrs_early2hrs_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(7);
            return true;
        }

        if (defaultSelection == 44 || (item != null && item.getItemId() == R.id.early6hrs_early3hrs_Item)) {
            databaseNotification.setIndex1(8);
            databaseNotification.setIndex2(8);
            return true;
        }

        if (defaultSelection == 45 || (item != null && item.getItemId() == R.id.early12hrs_notification_off)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 46 || (item != null && item.getItemId() == R.id.early12hrs_onTime_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 47 || (item != null && item.getItemId() == R.id.early12hrs_early5mins_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 48 || (item != null && item.getItemId() == R.id.early12hrs_early10mins_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 49 || (item != null && item.getItemId() == R.id.early12hrs_early15mins_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 50 || (item != null && item.getItemId() == R.id.early12hrs_early30mins_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(5);
            return true;
        }

        if (defaultSelection == 51 || (item != null && item.getItemId() == R.id.early12hrs_early1hr_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(6);
            return true;
        }

        if (defaultSelection == 52 || (item != null && item.getItemId() == R.id.early12hrs_early2hrs_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(7);
            return true;
        }

        if (defaultSelection == 53 || (item != null && item.getItemId() == R.id.early12hrs_early3hrs_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(8);
            return true;
        }

        if (defaultSelection == 54 || (item != null && item.getItemId() == R.id.early12hrs_early6hrs_Item)) {
            databaseNotification.setIndex1(9);
            databaseNotification.setIndex2(9);
            return true;
        }

        if (defaultSelection == 55 || (item != null && item.getItemId() == R.id.early24hrs_notification_off)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(0);
            return true;
        }

        if (defaultSelection == 56 || (item != null && item.getItemId() == R.id.early24hrs_onTime_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(1);
            return true;
        }

        if (defaultSelection == 57 || (item != null && item.getItemId() == R.id.early24hrs_early5mins_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(2);
            return true;
        }

        if (defaultSelection == 58 || (item != null && item.getItemId() == R.id.early24hrs_early10mins_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(3);
            return true;
        }

        if (defaultSelection == 59 || (item != null && item.getItemId() == R.id.early24hrs_early15mins_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(4);
            return true;
        }

        if (defaultSelection == 60 || (item != null && item.getItemId() == R.id.early24hrs_early30mins_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(5);
            return true;
        }

        if (defaultSelection == 61 || (item != null && item.getItemId() == R.id.early24hrs_early1hr_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(6);
            return true;
        }

        if (defaultSelection == 62 || (item != null && item.getItemId() == R.id.early24hrs_early2hrs_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(7);
            return true;
        }

        if (defaultSelection == 63 || (item != null && item.getItemId() == R.id.early24hrs_early3hrs_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(8);
            return true;
        }

        if (defaultSelection == 64 || (item != null && item.getItemId() == R.id.early24hrs_early6hrs_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(9);
            return true;
        }

        if (defaultSelection == 65 || (item != null && item.getItemId() == R.id.early24hrs_early12hrs_Item)) {
            databaseNotification.setIndex1(10);
            databaseNotification.setIndex2(10);
            return true;
        }

        return false;
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.ACID_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.acid_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private void scaleButtons() {
        notification.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        notificationSchedule.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);

        notification.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
        notificationSchedule.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ok.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        ok.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        ok.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) cancel.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        cancel.setLayoutParams(layoutParams);
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.acid_h_division_number, typedValue, true);

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

    public Button getOk() {
        return ok;
    }

    private void textWatcherSetup() {
        infoTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                information.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        Objects.requireNonNull(information.getEditText()).addTextChangedListener(infoTextWatcher);
    }

    @SuppressLint("DefaultLocale")
    private void numberPickerSetup() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) hour.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR, 0, ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR);
        hour.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) minutes.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR, 0, ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR);
        minutes.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) colon.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR, 0, ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR);
        colon.setLayoutParams(layoutParams);

        hour.setTypeface(getString(R.string.app_font), Typeface.BOLD_ITALIC);
        hour.setSelectedTypeface(getString(R.string.app_font), Typeface.BOLD_ITALIC);
        minutes.setTypeface(getString(R.string.app_font), Typeface.BOLD_ITALIC);
        minutes.setSelectedTypeface(getString(R.string.app_font), Typeface.BOLD_ITALIC);

        if (inflatedLayout == 0) {
            hour.setMinValue(0);
            hour.setMaxValue(23);
            hour.setValue(0);
        } else {
            if (inflatedLayout == 1) {
                layoutParams = (ConstraintLayout.LayoutParams) am_pm.getLayoutParams();
                layoutParams.setMargins(0, ScaledLayoutVariables.ACID_NUM_PICKER_TOP_MAR, 0, ScaledLayoutVariables.ACID_NUM_PICKER_BOT_MAR);
                am_pm.setLayoutParams(layoutParams);

                am_pm.setTypeface(getString(R.string.app_font), Typeface.BOLD_ITALIC);
                am_pm.setSelectedTypeface(getString(R.string.app_font), Typeface.BOLD_ITALIC);

                hour.setMinValue(1);
                hour.setMaxValue(12);
                hour.setValue(12);

                am_pm.setMinValue(0);
                am_pm.setMaxValue(3);
                am_pm.setValue(0);
                am_pm.setDisplayedValues(new String[] {Constants.AM, Constants.PM, Constants.AM, Constants.PM});
                am_pm.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            }
        }

        minutes.setMinValue(0);
        minutes.setMaxValue(59);
        minutes.setValue(0);

        hour.setFormatter(value -> String.format("%02d", value));
        minutes.setFormatter(value -> String.format("%02d", value));

        if (Hour != -1 && Minutes != -1) {
            if (inflatedLayout == 0) {
                hour.setValue(Hour);
                minutes.setValue(Minutes);
            } else {
                if (inflatedLayout == 1) {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat timeFormat24 = new SimpleDateFormat(Constants.TIME_FORMAT_24, Locale.US);
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat timeFormat12 = new SimpleDateFormat(Constants.TIME_FORMAT_12, Locale.US);

                    String time = String.format("%02d", Hour) + ":" + String.format("%02d", Minutes);
                    Date date = new Date();

                    try {
                        date = timeFormat24.parse(time);
                    } catch (ParseException e) {
                        Log.e(TAG, "formatTime: ParseException: " + e.getMessage());
                    }

                    time = timeFormat12.format(Objects.requireNonNull(date));

                    hour.setValue(Integer.parseInt(time.substring(0, 2)));
                    minutes.setValue(Integer.parseInt(time.substring(3, 5)));

                    if (time.contains(Constants.AM)) am_pm.setValue(0);
                    else { if (time.contains(Constants.PM)) am_pm.setValue(1); }
                }
            }
        }

        hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }
}
