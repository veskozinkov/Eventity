package vz.apps.dailyevents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import application_class.ProjectM;
import constants.Constants;
import dialogs.ActivateAutoTimeDialog;
import dialogs.AddChangeInfoDialog;
import dialogs.AddChangeInfoDialog2;
import dialogs.ConfirmationDialog;
import fragments.DayFragment;
import fragments.OtherEventsFragment;
import fragments.WeekFragment;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.NetworkStateReceiver;
import helper_classes.notifications.NotificationHelper;
import helper_classes.scale_layout.ScaleLayout;
import helper_classes.scale_layout.ScaledLayoutVariables;
import io.paperdb.Paper;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener, FirebaseAuth.AuthStateListener {

    private static final String TAG = "MainActivity";

    public static boolean deletedAccount;
    public static boolean twoSignedInDevices;
    private boolean network;
    private boolean backFlag;
    private boolean toastShowing1;
    private boolean toastShowing2;
    private String uid;
    private SmoothBottomBar bottomNavigation;
    private NetworkStateReceiver networkStateReceiver;
    private String selectedWeek = "";
    private ValueEventListener deviceIDListener;
    private View blockBottomNavigation;
    private ConstraintLayout splashScreen;
    private boolean reloadingUser;
    private boolean buttonClicked;
    private boolean shouldRefreshDayFragment;
    private boolean shouldRefreshOtherEventsFragment;
    private boolean fragmentTransitionByBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MainTheme);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        networkStateReceiver = new NetworkStateReceiver();
        deletedAccount = false;
        twoSignedInDevices = false;
        backFlag = false;
        toastShowing1 = false;
        toastShowing2 = false;
        uid = null;
        blockBottomNavigation = findViewById(R.id.blockBottomNavigation);
        splashScreen = findViewById(R.id.splashScreen_ConstraintLayout);
        reloadingUser = false;
        buttonClicked = false;
        shouldRefreshDayFragment = false;
        shouldRefreshOtherEventsFragment = false;
        fragmentTransitionByBottomNavigation = true;

        String callingActivity = getIntent().getStringExtra(Constants.ACTIVITY_KEY);
        if (callingActivity != null && callingActivity.equals(SignInActivity.class.getSimpleName())) splashScreen.setVisibility(View.GONE);
        else { ScaleLayout.scaleVariables(this); }

        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));
        if (connectionLostWhileSigningIn == 1) {
            if (!toastShowing1) {
                toastShowing1 = true;

                CustomToast.showError(this, getString(R.string.connection_lost_while_signing_in_error_message2), Toast.LENGTH_LONG);
                CustomToast.showInfo(this, getString(R.string.connection_lost_while_signing_in_info_message1), Toast.LENGTH_LONG);
                CustomToast.showInfo(this, getString(R.string.connection_lost_while_signing_in_info_message2), Toast.LENGTH_LONG);
                CustomToast.showInfo(this, getString(R.string.connection_lost_while_signing_in_info_message3), Toast.LENGTH_LONG);

                new Handler().postDelayed(() -> toastShowing1 = false, Constants.TOAST_LONG_DURATION * 4);
            }
        }

        deviceIDListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deviceID = Paper.book(Constants.DEVICE_ID).read(Constants.ID);
                int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals(deviceID)) {
                    twoSignedInDevices = true;
                    Constants.auth.signOut();

                    if (connectionLostWhileSigningIn == 1) {
                        Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).write(Constants.CONNECTION, 0);
                    } else {
                        CustomToast.showInfo(MainActivity.this, getString(R.string.account_active_on_another_device), Toast.LENGTH_LONG);
                        CustomToast.showWarning(MainActivity.this, getString(R.string.change_password_warning_message), Toast.LENGTH_LONG);
                    }
                } else {
                    if (connectionLostWhileSigningIn == 1) {
                        Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).write(Constants.CONNECTION, 0);
                        Constants.auth.signOut();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
            }
        };

        ((ProjectM) getApplicationContext()).setMainActivityContext(this);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                hideSystemUI();
            }
        });

        if (Constants.GRADIENT_ANGLE == 0 || Constants.GRADIENT_ANGLE == 45 || Constants.GRADIENT_ANGLE == 315) bottomNavigation.setBackgroundResource(R.drawable.red_black_gradient_0);
        else { bottomNavigation.setBackgroundResource(R.drawable.red_black_gradient_180); }

        selectedWeek = Constants.THIS_WEEK;
        createWeekFragment(selectedWeek);
        bottomNavigation.setItemAnimDuration(0);
        bottomNavigation.setItemIconSize(ScaledLayoutVariables.BOTTOM_NAV_ICON_SIZE);

        bottomNavigation.setOnItemSelectedListener(i -> {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            if (Constants.auth.getCurrentUser() != null && !reloadingUser) {
                reloadingUser = true;
                Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> reloadingUser = false);
            }

            if (i == 0) {
                fragmentTransitionByBottomNavigation = true;

                selectedWeek = Constants.LAST_WEEK;
                createWeekFragment(selectedWeek);
            } else {
                if (i == 1) {
                    fragmentTransitionByBottomNavigation = true;

                    selectedWeek = Constants.THIS_WEEK;
                    createWeekFragment(selectedWeek);
                } else {
                    if (i == 2) {
                        fragmentTransitionByBottomNavigation = true;

                        selectedWeek = Constants.NEXT_WEEK;
                        createWeekFragment(selectedWeek);
                    } else {
                        if (i == 3 && Constants.auth.getCurrentUser() != null) {
                            int autoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);

                            if (autoTime == 1) {
                                if ((int) Objects.requireNonNull(Paper.book(Constants.LOCAL_VARIABLES_MANAGED).read(Constants.MANAGED)) == 0) ((ProjectM) getApplicationContext()).manageLocalVariables();
                                createOtherEventsFragment();
                            } else {
                                if (autoTime == 0) {
                                    if (selectedWeek.equals(Constants.LAST_WEEK)) bottomNavigation.setItemActiveIndex(0);
                                    else {
                                        if (selectedWeek.equals(Constants.THIS_WEEK)) bottomNavigation.setItemActiveIndex(1);
                                        else {
                                            if (selectedWeek.equals(Constants.NEXT_WEEK)) bottomNavigation.setItemActiveIndex(2);
                                        }
                                    }

                                    activateAutoTimeDialog();
                                }
                            }
                        }
                    }
                }
            }

            return true;
        });

        findViewById(android.R.id.content).post(() -> bottomNavigation.setItemAnimDuration(Constants.BOTTOM_NAV_ANIM_DURATION));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Constants.auth.addAuthStateListener(this);

        if (Constants.auth.getCurrentUser() != null && Constants.auth.getCurrentUser().isEmailVerified()) {
            uid = Constants.auth.getCurrentUser().getUid();

            if (shouldRefreshDayFragment || shouldRefreshOtherEventsFragment) {
                shouldRefreshDayFragment = false;
                shouldRefreshOtherEventsFragment = false;

                updateFragmentInfoOnDateChange();
            }

            Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.DEVICE_ID).addValueEventListener(deviceIDListener);
            Constants.auth.getCurrentUser().reload();

            if ((double) Math.round(DeviceCharacteristics.getWidthPx(this) / getResources().getDisplayMetrics().density * 100) / 100 < Constants.sw360dp) {
                if (splashScreen.getVisibility() == View.GONE) splashScreen.setVisibility(View.VISIBLE);
            }
        }

        int autoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);

        if (autoTime == 1) {
            DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
            if (dialogFragment != null) dialogFragment.dismiss();
        }

        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();

        Constants.auth.removeAuthStateListener(this);

        if (uid != null && !uid.isEmpty()) {
            Constants.usersRef.child(uid).child(Constants.DEVICE_ID).removeEventListener(deviceIDListener);
        }

        networkStateReceiver.removeListener(this);
        unregisterReceiver(networkStateReceiver);

        if (Constants.auth.getCurrentUser() != null && Constants.auth.getCurrentUser().isEmailVerified()) {
            if ((double) Math.round(DeviceCharacteristics.getWidthPx(this) / getResources().getDisplayMetrics().density * 100) / 100 < Constants.sw360dp) finishAndRemoveTask();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void destroyWeekBook(String week) {
        Paper.book(uid + "." + week + "." + Constants.MONDAY).destroy();
        Paper.book(uid + "." + week + "." + Constants.TUESDAY).destroy();
        Paper.book(uid + "." + week + "." + Constants.WEDNESDAY).destroy();
        Paper.book(uid + "." + week + "." + Constants.THURSDAY).destroy();
        Paper.book(uid + "." + week + "." + Constants.FRIDAY).destroy();
        Paper.book(uid + "." + week + "." + Constants.SATURDAY).destroy();
        Paper.book(uid + "." + week + "." + Constants.SUNDAY).destroy();
    }

    private void destroyAllBooks() {
        destroyWeekBook(Constants.WEEK0);
        destroyWeekBook(Constants.WEEK1);
        destroyWeekBook(Constants.WEEK2);
        destroyWeekBook(Constants.WEEK3);

        Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).destroy();
        Paper.book(uid + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).destroy();
        Paper.book(Constants.REF_DATE).destroy();
        Paper.book(Constants.WEEK_FLAGS).destroy();
        Paper.book(Constants.COUNTRY).destroy();
        Paper.book(Constants.OTHER_SETTINGS).destroy();
    }

    private void createWeekFragment(String selectedWeek) {
        WeekFragment weekFragment = new WeekFragment();
        Bundle bundle = new Bundle();

        bundle.putString(Constants.BUNDLE_KEY1, selectedWeek);
        weekFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, weekFragment).commit();
    }

    private void createOtherEventsFragment() {
        OtherEventsFragment otherEventsFragment = new OtherEventsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, otherEventsFragment).commit();
    }

    private void activateAutoTimeDialog() {
        ActivateAutoTimeDialog dialog = new ActivateAutoTimeDialog();
        dialog.show(getSupportFragmentManager(), Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
    }

    private void networkEnabledSetup(Fragment fragment, DialogFragment dialogFragment1, DialogFragment dialogFragment2, DialogFragment dialogFragment3) {
        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

        if (connectionLostWhileSigningIn == 0) {
            network = true;

            if (fragment instanceof DayFragment) {
                if (!selectedWeek.equals(Constants.LAST_WEEK) && !(selectedWeek.equals(Constants.THIS_WEEK) && ((DayFragment) fragment).getCurrentDay() > ((DayFragment) fragment).getSelectedDayInt())) {
                    ((DayFragment) fragment).enableFABButton(((DayFragment) fragment).getAdd());
                }
            } else {
                if (fragment instanceof OtherEventsFragment) {
                    ((OtherEventsFragment) fragment).enableFABButton(((OtherEventsFragment) fragment).getAdd());
                }
            }

            if (dialogFragment1 != null) {
                ((AddChangeInfoDialog) dialogFragment1).enableButton(((AddChangeInfoDialog) dialogFragment1).getOk());
            } else {
                if (dialogFragment2 != null) {
                    ((AddChangeInfoDialog2) dialogFragment2).enableButton(((AddChangeInfoDialog2) dialogFragment2).getOk());
                } else {
                    if (dialogFragment3 != null) {
                        ((ConfirmationDialog) dialogFragment3).enableButton(((ConfirmationDialog) dialogFragment3).getYes());
                    }
                }
            }
        } else {
            networkDisabledSetup(fragment, dialogFragment1, dialogFragment2, dialogFragment3);
        }
    }

    private void networkDisabledSetup(Fragment fragment, DialogFragment dialogFragment1, DialogFragment dialogFragment2, DialogFragment dialogFragment3) {
        int connectionLostWhileSigningIn = Objects.requireNonNull(Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).read(Constants.CONNECTION));

        network = false;

        if (fragment instanceof DayFragment) {
            ((DayFragment) fragment).disableFABButton(((DayFragment) fragment).getAdd());

            if (connectionLostWhileSigningIn == 1) ((DayFragment) fragment).disableAccountSettings();
        } else {
            if (fragment instanceof OtherEventsFragment) {
                ((OtherEventsFragment) fragment).disableFABButton(((OtherEventsFragment) fragment).getAdd());

                if (connectionLostWhileSigningIn == 1) ((OtherEventsFragment) fragment).disableAccountSettings();
            } else {
                if (fragment instanceof WeekFragment) {
                    if (connectionLostWhileSigningIn == 1) ((WeekFragment) fragment).disableAccountSettings();
                }
            }
        }

        if (dialogFragment1 != null) {
            ((AddChangeInfoDialog) dialogFragment1).disableButton(((AddChangeInfoDialog) dialogFragment1).getOk());
        } else {
            if (dialogFragment2 != null) {
                ((AddChangeInfoDialog2) dialogFragment2).disableButton(((AddChangeInfoDialog2) dialogFragment2).getOk());
            } else {
                if (dialogFragment3 != null) {
                    ((ConfirmationDialog) dialogFragment3).disableButton(((ConfirmationDialog) dialogFragment3).getYes());
                }
            }
        }
    }

    private void dismissAllDialogs() {
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
        DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
        DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
        DialogFragment dialogFragment4 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

        if (dialogFragment1 != null) dialogFragment1.dismiss();
        else {
            if (dialogFragment2 != null) dialogFragment2.dismiss();
            else {
                if (dialogFragment3 != null) dialogFragment3.dismiss();
                else {
                    if (dialogFragment4 != null) dialogFragment4.dismiss();
                }
            }
        }
    }

    public void updateFragmentInfoOnDateChange() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (fragment instanceof WeekFragment) {
            ((WeekFragment) fragment).displayNumberOfSavedEventsPerDay(selectedWeek);
        } else {
            if (fragment instanceof DayFragment) {
                refreshDayFragment(fragment);
            } else {
                if (fragment instanceof OtherEventsFragment) {
                    refreshOtherEventsFragment();
                }
            }
        }
    }

    private void refreshDayFragment(Fragment fragment) {
        try {
            DayFragment dayFragment = new DayFragment();
            Bundle bundle = new Bundle();

            DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
            DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

            bundle.putString(Constants.BUNDLE_KEY1, ((DayFragment) fragment).getSelectedWeek());
            bundle.putString(Constants.BUNDLE_KEY2, ((DayFragment) fragment).getSelectedDay());
            dayFragment.setArguments(bundle);

            if (dialogFragment1 != null) dialogFragment1.dismiss();
            else {
                if (dialogFragment2 != null) dialogFragment2.dismiss();
            }

            closeContextMenu();

            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, dayFragment).addToBackStack(null).commit();
        } catch (IllegalStateException e) {
            shouldRefreshDayFragment = true;
        }
    }

    private void refreshOtherEventsFragment() {
        try {
            DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
            DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

            if (dialogFragment1 != null) dialogFragment1.dismiss();
            else {
                if (dialogFragment2 != null) dialogFragment2.dismiss();
            }

            closeContextMenu();

            OtherEventsFragment otherEventsFragment = new OtherEventsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, otherEventsFragment).commit();
        } catch (IllegalStateException e) {
            shouldRefreshOtherEventsFragment = true;
        }
    }

    public boolean getNetwork() { return network; }

    public boolean isToastShowing1() { return toastShowing1; }

    public void setToastShowing1(boolean toastShowing1) { this.toastShowing1 = toastShowing1; }

    public boolean isToastShowing2() { return toastShowing2; }

    public void setToastShowing2(boolean toastShowing2) { this.toastShowing2 = toastShowing2; }

    public void setBackFlag(boolean backFlag) { this.backFlag = backFlag; }

    public String getUID() { return uid; }

    public void setSplashScreenVisibility(int visibility) { splashScreen.setVisibility(visibility); }

    public boolean getButtonClicked() { return buttonClicked; }

    public void setButtonClicked(boolean buttonClicked) { this.buttonClicked = buttonClicked; }

    public View getBlockBottomNavigation() { return blockBottomNavigation; }

    public boolean getFragmentTransitionByBottomNavigation() { return fragmentTransitionByBottomNavigation; }

    public void setFragmentTransitionByBottomNavigation(boolean fragmentTransitionByBottomNavigation) { this.fragmentTransitionByBottomNavigation = fragmentTransitionByBottomNavigation; }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (Constants.auth.getCurrentUser() == null) {
            if (uid != null && !uid.isEmpty()) {
                NotificationHelper.cancelAllNotifications(this, uid);
                destroyAllBooks();

                Constants.usersRef.child(uid).child(Constants.DEVICE_ID).removeEventListener(deviceIDListener);

                if (!deletedAccount && !twoSignedInDevices) {
                    Constants.usersRef.child(uid).child(Constants.DEVICE_ID).setValue("");
                }
            }

            if (!backFlag) {
                ((ProjectM) getApplicationContext()).setMainActivityContext(null);

                dismissAllDialogs();
                if (splashScreen.getVisibility() == View.GONE) splashScreen.setVisibility(View.VISIBLE);

                if (((ProjectM) getApplicationContext()).getSignUpActivityLastOpened()) startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                else { startActivity(new Intent(MainActivity.this, SignInActivity.class)); }

                overridePendingTransition(0, 0);
                finish();
            }
        } else {
            if (!Constants.auth.getCurrentUser().isEmailVerified()) {
                Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.DEVICE_ID).removeEventListener(deviceIDListener);

                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                overridePendingTransition(0, 0);
                finish();
            } else {
                if ((double) Math.round(DeviceCharacteristics.getWidthPx(MainActivity.this) / getResources().getDisplayMetrics().density * 100) / 100 >= Constants.sw360dp) {
                    if (splashScreen.getVisibility() == View.VISIBLE) {
                        splashScreen.animate()
                                .alpha(0.0f)
                                .setDuration(Constants.SPLASH_SCREEN_ANIM_DURATION)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);

                                        splashScreen.setVisibility(View.GONE);
                                        splashScreen.setAlpha(1.0f);
                                    }
                                });
                    }
                } else {
                    CustomToast.showWarning(MainActivity.this, getString(R.string.sw_less_than_360dp_warning_message), Toast.LENGTH_LONG);
                    CustomToast.showInfo(MainActivity.this, getString(R.string.sw_less_than_360dp_info_message), Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public void networkAvailable() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
        DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
        DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

        if (Constants.auth.getCurrentUser() != null) {
            networkEnabledSetup(fragment, dialogFragment1, dialogFragment2, dialogFragment3);
        }
    }

    @Override
    public void networkUnavailable() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG_TAG);
        DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ADD_CHANGE_INFO_DIALOG2_TAG);
        DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);

        if (Constants.auth.getCurrentUser() != null) {
            networkDisabledSetup(fragment, dialogFragment1, dialogFragment2, dialogFragment3);
        }
    }
}
