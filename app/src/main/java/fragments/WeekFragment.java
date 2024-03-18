package fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.wave.MultiWaveHeader;

import java.util.List;
import java.util.Objects;

import application_class.ProjectM;
import constants.Constants;
import dialogs.ActivateAutoTimeDialog;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import io.paperdb.Paper;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;

public class WeekFragment extends Fragment implements View.OnClickListener {

    private ScrollView weekScroll;
    private CardView mondayCardView;
    private CardView tuesdayCardView;
    private CardView wednesdayCardView;
    private CardView thursdayCardView;
    private CardView fridayCardView;
    private CardView saturdayCardView;
    private CardView sundayCardView;
    private TextView mondayTextView;
    private TextView tuesdayTextView;
    private TextView wednesdayTextView;
    private TextView thursdayTextView;
    private TextView fridayTextView;
    private TextView saturdayTextView;
    private TextView sundayTextView;
    private TextView mondaySavedEventsTextView;
    private TextView tuesdaySavedEventsTextView;
    private TextView wednesdaySavedEventsTextView;
    private TextView thursdaySavedEventsTextView;
    private TextView fridaySavedEventsTextView;
    private TextView saturdaySavedEventsTextView;
    private TextView sundaySavedEventsTextView;
    private TextView week;
    private FloatingActionButton back;
    private FloatingActionButton accountSettings;
    private MultiWaveHeader waveHeader;
    private final boolean[] scrollItemVisible = {false, false, false, false, false, false, false};

    private String selectedWeek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.week_fragment, container, false);

        weekScroll = view.findViewById(R.id.week_ScrollView);
        if (((MainActivity) requireActivity()).getFragmentTransitionByBottomNavigation()) {
            weekScroll.setVisibility(View.INVISIBLE);
            ((MainActivity) requireActivity()).setFragmentTransitionByBottomNavigation(false);
        }

        mondayCardView = view.findViewById(R.id.monday_CardView);
        tuesdayCardView = view.findViewById(R.id.tuesday_CardView);
        wednesdayCardView = view.findViewById(R.id.wednesday_CardView);
        thursdayCardView = view.findViewById(R.id.thursday_CardView);
        fridayCardView = view.findViewById(R.id.friday_CardView);
        saturdayCardView = view.findViewById(R.id.saturday_CardView);
        sundayCardView = view.findViewById(R.id.sunday_CardView);
        mondayTextView = view.findViewById(R.id.monday_TextView);
        tuesdayTextView = view.findViewById(R.id.tuesday_TextView);
        wednesdayTextView = view.findViewById(R.id.wednesday_TextView);
        thursdayTextView = view.findViewById(R.id.thursday_TextView);
        fridayTextView = view.findViewById(R.id.friday_TextView);
        saturdayTextView = view.findViewById(R.id.saturday_TextView);
        sundayTextView = view.findViewById(R.id.sunday_TextView);
        mondaySavedEventsTextView = view.findViewById(R.id.mondaySavedEvents_TextView);
        tuesdaySavedEventsTextView = view.findViewById(R.id.tuesdaySavedEvents_TextView);
        wednesdaySavedEventsTextView = view.findViewById(R.id.wednesdaySavedEvents_TextView);
        thursdaySavedEventsTextView = view.findViewById(R.id.thursdaySavedEvents_TextView);
        fridaySavedEventsTextView = view.findViewById(R.id.fridaySavedEvents_TextView);
        saturdaySavedEventsTextView = view.findViewById(R.id.saturdaySavedEvents_TextView);
        sundaySavedEventsTextView = view.findViewById(R.id.sundaySavedEvents_TextView);
        week = view.findViewById(R.id.week_TextView);
        back = view.findViewById(R.id.back_FAB);
        accountSettings = view.findViewById(R.id.accountSettings_FAB);
        waveHeader = view.findViewById(R.id.waveHeader);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        selectedWeek = requireArguments().getString(Constants.BUNDLE_KEY1);

        setTitle();
        manageScrollView();
        scaleFABs();

        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));
        if (connectionLostWhileSigningIn == 1) disableAccountSettings();

        accountSettings.setOnClickListener(v -> {
            if (Constants.auth.getCurrentUser() != null) {
                int connectionLostWhileSigningIn2 = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

                if (connectionLostWhileSigningIn2 == 0) {
                    int autoTime = Settings.Global.getInt(requireActivity().getContentResolver(), Settings.Global.AUTO_TIME, 0);

                    if (autoTime == 1) {
                        if ((int) Objects.requireNonNull(Paper.book(Constants.LOCAL_VARIABLES_MANAGED).read(Constants.MANAGED)) == 0) ((ProjectM) requireActivity().getApplicationContext()).manageLocalVariables();

                        Intent intent = new Intent(requireActivity(), AccountSettingsActivity.class);
                        intent.putExtra("uid", ((MainActivity) requireActivity()).getUID());

                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        if (autoTime == 0) {
                            activateAutoTimeDialog();
                        }
                    }
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
            ((MainActivity) requireActivity()).setBackFlag(true);

            if (Constants.auth.getCurrentUser() != null) {
                Constants.auth.getCurrentUser().reload();
            }

            requireActivity().finish();
        });

        weekScroll.post(() -> {
            playScrollAnimation();
            weekScroll.setVisibility(View.VISIBLE);
        });
        weekScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> playScrollAnimation());

        getParentFragmentManager().setFragmentResultListener(Constants.DAY_FRAGMENT, this, (requestKey, result) -> {
            if (Constants.auth.getCurrentUser() != null) displayNumberOfSavedEventsPerDay(selectedWeek);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Constants.auth.getCurrentUser() != null) displayNumberOfSavedEventsPerDay(selectedWeek);
    }

    private void setTitle() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double density = displayMetrics.density;
        int pxWidth = DeviceCharacteristics.getWidthPx(requireActivity());

        switch (selectedWeek) {
            case Constants.LAST_WEEK:
                week.setText(R.string.fragment_title1);

                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp) {
                    if (Constants.DEFAULT_LANG.equals(Constants.EN_LANG)) {
                        week.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
                    } else {
                        if (Constants.DEFAULT_LANG.equals(Constants.BG_LANG)) {
                            week.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE - ScaledLayoutVariables.BG_TITLE_TEXT_SIZE_SUB_NUM);
                        }
                    }
                }

                break;

            case Constants.THIS_WEEK:
                week.setText(R.string.fragment_title2);
                week.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
                break;

            case Constants.NEXT_WEEK:
                week.setText(R.string.fragment_title3);

                if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp) {
                    if (Constants.DEFAULT_LANG.equals(Constants.EN_LANG)) {
                        week.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
                    } else {
                        if (Constants.DEFAULT_LANG.equals(Constants.BG_LANG)) {
                            week.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE - ScaledLayoutVariables.BG_TITLE_TEXT_SIZE_SUB_NUM);
                        }
                    }
                }

                break;
        }

        ManageTitlePosition.manageActivitySingleTitle(requireActivity(), waveHeader, week);
    }

    private void manageScrollView() {
        mondayCardView.setOnClickListener(this);
        tuesdayCardView.setOnClickListener(this);
        wednesdayCardView.setOnClickListener(this);
        thursdayCardView.setOnClickListener(this);
        fridayCardView.setOnClickListener(this);
        saturdayCardView.setOnClickListener(this);
        sundayCardView.setOnClickListener(this);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mondayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_MON_TOP_MAR, 0, 0);
        mondayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        mondayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        mondayCardView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) tuesdayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR, 0, 0);
        tuesdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        tuesdayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        tuesdayCardView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) wednesdayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR, 0, 0);
        wednesdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        wednesdayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        wednesdayCardView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) thursdayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR, 0, 0);
        thursdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        thursdayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        thursdayCardView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) fridayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR, 0, 0);
        fridayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        fridayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        fridayCardView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) saturdayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR, 0, 0);
        saturdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        saturdayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        saturdayCardView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) sundayCardView.getLayoutParams();
        layoutParams.setMargins(0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TOP_MAR, 0, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_SUN_BOT_MAR);
        sundayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.WEEK_FRAGMENT_SCROLL_TEXT_SIZE);
        sundayTextView.setPadding(0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_TOP_PAD, 0, ScaledLayoutVariables.WEEK_FRAGMENT_CARD_BOT_PAD);
        sundayCardView.setLayoutParams(layoutParams);

        if (Constants.GRADIENT_ANGLE == 0 || Constants.GRADIENT_ANGLE == 45 || Constants.GRADIENT_ANGLE == 315) {
            mondayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
            tuesdayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
            wednesdayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
            thursdayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
            fridayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
            saturdayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
            sundayTextView.setBackgroundResource(R.drawable.red_black_gradient_0);
        } else {
            mondayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
            tuesdayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
            wednesdayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
            thursdayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
            fridayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
            saturdayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
            sundayTextView.setBackgroundResource(R.drawable.red_black_gradient_180);
        }
    }

    private void createDayFragment(String selectedWeek, String selectedDay) {
        DayFragment dayFragment = new DayFragment();
        Bundle bundle = new Bundle();

        bundle.putString(Constants.BUNDLE_KEY1, selectedWeek);
        bundle.putString(Constants.BUNDLE_KEY2, selectedDay);
        dayFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().add(R.id.fragmentContainer, dayFragment).addToBackStack(null).commit();
    }

    public void displayNumberOfSavedEventsPerDay(String selectedWeek) {
        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

        if (connectionLostWhileSigningIn == 0) {
            String week = "";

            String week0Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK0));
            String week1Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK1));
            String week2Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK2));
            String week3Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK3));

            if (week0Flag.equals(selectedWeek)) week = Constants.WEEK0;
            else {
                if (week1Flag.equals(selectedWeek)) week = Constants.WEEK1;
                else {
                    if (week2Flag.equals(selectedWeek)) week = Constants.WEEK2;
                    else {
                        if (week3Flag.equals(selectedWeek)) week = Constants.WEEK3;
                    }
                }
            }

            List<String> allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.MONDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) mondaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { mondaySavedEventsTextView.setText(""); }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.TUESDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) tuesdaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { tuesdaySavedEventsTextView.setText(""); }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.WEDNESDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) wednesdaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { wednesdaySavedEventsTextView.setText(""); }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.THURSDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) thursdaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { thursdaySavedEventsTextView.setText(""); }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.FRIDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) fridaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { fridaySavedEventsTextView.setText(""); }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.SATURDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) saturdaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { saturdaySavedEventsTextView.setText(""); }

            allBookKeys = Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.SUNDAY).getAllKeys();
            if (!allBookKeys.isEmpty()) sundaySavedEventsTextView.setText(String.valueOf(allBookKeys.size()));
            else { sundaySavedEventsTextView.setText(""); }
        }
    }

    private void playScrollAnimation() {
        Rect scrollBounds = new Rect();
        weekScroll.getHitRect(scrollBounds);

        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_in_left);
        manageScrollAnimation(scrollBounds, animation);
    }

    private void manageScrollAnimation(Rect scrollBounds, Animation animation) {
        if (mondayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[0]) {
                scrollItemVisible[0] = true;
                mondayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[0] = false;
        }

        if (tuesdayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[1]) {
                scrollItemVisible[1] = true;
                tuesdayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[1] = false;
        }

        if (wednesdayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[2]) {
                scrollItemVisible[2] = true;
                wednesdayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[2] = false;
        }

        if (thursdayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[3]) {
                scrollItemVisible[3] = true;
                thursdayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[3] = false;
        }

        if (fridayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[4]) {
                scrollItemVisible[4] = true;
                fridayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[4] = false;
        }

        if (saturdayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[5]) {
                scrollItemVisible[5] = true;
                saturdayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[5] = false;
        }

        if (sundayCardView.getLocalVisibleRect(scrollBounds)) {
            if (!scrollItemVisible[6]) {
                scrollItemVisible[6] = true;
                sundayCardView.startAnimation(animation);
            }
        } else {
            scrollItemVisible[6] = false;
        }
    }

    private void activateAutoTimeDialog() {
        ActivateAutoTimeDialog dialog = new ActivateAutoTimeDialog();
        dialog.show(getParentFragmentManager(), Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
    }

    private void scaleFABs() {
        back.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        accountSettings.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);

        back.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
        accountSettings.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
    }

    public void disableAccountSettings() {
        accountSettings.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDisabled)));
        accountSettings.setRippleColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
    }

    @Override
    public void onClick(View v) {
        if (Constants.auth.getCurrentUser() != null) {
            Constants.auth.getCurrentUser().reload();

            int autoTime = Settings.Global.getInt(requireActivity().getContentResolver(), Settings.Global.AUTO_TIME, 0);

            if (autoTime == 1) {
                if ((int) Objects.requireNonNull(Paper.book(Constants.LOCAL_VARIABLES_MANAGED).read(Constants.MANAGED)) == 0) ((ProjectM) requireActivity().getApplicationContext()).manageLocalVariables();

                if (v.getId() == R.id.monday_CardView) {
                    createDayFragment(selectedWeek, Constants.MONDAY);
                } else {
                    if (v.getId() == R.id.tuesday_CardView) {
                        createDayFragment(selectedWeek, Constants.TUESDAY);
                    } else {
                        if (v.getId() == R.id.wednesday_CardView) {
                            createDayFragment(selectedWeek, Constants.WEDNESDAY);
                        } else {
                            if (v.getId() == R.id.thursday_CardView) {
                                createDayFragment(selectedWeek, Constants.THURSDAY);
                            } else {
                                if (v.getId() == R.id.friday_CardView) {
                                    createDayFragment(selectedWeek, Constants.FRIDAY);
                                } else {
                                    if (v.getId() == R.id.saturday_CardView) {
                                        createDayFragment(selectedWeek, Constants.SATURDAY);
                                    } else {
                                        if (v.getId() == R.id.sunday_CardView) {
                                            createDayFragment(selectedWeek, Constants.SUNDAY);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (autoTime == 0) {
                    activateAutoTimeDialog();
                }
            }
        }
    }
}
