package vz.apps.dailyevents;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.scwang.wave.MultiWaveHeader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import application_class.ProjectM;
import constants.Constants;
import dialogs.ActivateAutoTimeDialog;
import dialogs.ChangeEmailDialog;
import dialogs.ChangeNameDialog;
import dialogs.ChangePasswordDialog;
import dialogs.ConfirmationDialog;
import dialogs.DeleteAccountDialog;
import dialogs.DeleteAllEventsDialog;
import dialogs.OtherSettingsDialog;
import dialogs.SelectCountryDialog;
import helper_classes.Country;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.ManageTitlePosition;
import helper_classes.NetworkStateReceiver;
import helper_classes.notifications.NotificationHelper;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.time_zones.TimeZoneFormat;
import io.paperdb.Paper;

public class AccountSettingsActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NetworkStateReceiver.NetworkStateReceiverListener, View.OnClickListener, ChangeNameDialog.NameUpdate, ChangeEmailDialog.EmailUpdate, ChangePasswordDialog.PasswordUpdate, SelectCountryDialog.CountrySelect {

    private static final String TAG = "AccountSettingsActivity";

    public static boolean deletingAccount;
    private HorizontalScrollView nameHScrollView;
    private HorizontalScrollView emailHScrollView;
    private HorizontalScrollView passwordHScrollView;
    private TextView name;
    private TextView email;
    private TextView password;
    private TextView title;
    private Button changeName;
    private Button changeEmail;
    private Button changePassword;
    private Button signout;
    private Button deleteAcc;
    private FloatingActionButton back;
    private FloatingActionButton otherSettings;
    private FloatingActionButton deleteAllEvents;
    private ConstraintLayout selectCountry;
    private String uid;
    private boolean network;
    private String selectedTimeZone;
    private String oldTimeZone;
    private String selectedTimeZoneISO;
    private MultiWaveHeader waveHeader;
    private ConstraintLayout splashScreen;
    private boolean buttonClicked;
    private boolean toastShowing;
    private boolean selectCountryDialogToastShowing;

    private NetworkStateReceiver networkStateReceiver;
    private ValueEventListener deviceIDListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        hideSystemUI();

        nameHScrollView = findViewById(R.id.name_HScrollView);
        emailHScrollView = findViewById(R.id.email_HScrollView);
        passwordHScrollView = findViewById(R.id.password_HScrollView);
        name = findViewById(R.id.Name_TextView);
        email = findViewById(R.id.Email_TextView);
        password = findViewById(R.id.Password_TextView);
        title = findViewById(R.id.title_TextView);
        back = findViewById(R.id.back_FAB);
        otherSettings = findViewById(R.id.otherSettings_FAB);
        deleteAllEvents = findViewById(R.id.deleteAllEvents_FAB);
        signout = findViewById(R.id.signout_Button);
        deleteAcc = findViewById(R.id.deleteAcc_Button);
        changeName = findViewById(R.id.changeName_Button);
        changeEmail = findViewById(R.id.changeEmail_Button);
        changePassword = findViewById(R.id.changePassword_Button);
        selectCountry = findViewById(R.id.selectCountry_Layout);
        uid = getIntent().getStringExtra("uid");
        deletingAccount = false;
        selectedTimeZone = null;
        oldTimeZone = null;
        selectedTimeZoneISO = null;
        waveHeader = findViewById(R.id.waveHeader);
        splashScreen = findViewById(R.id.splashScreen_ConstraintLayout);
        buttonClicked = false;
        toastShowing = false;
        selectCountryDialogToastShowing = false;

        networkStateReceiver = new NetworkStateReceiver();

        deviceIDListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deviceID = Paper.book(Constants.DEVICE_ID).read(Constants.ID);

                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals(deviceID)) {
                    Constants.auth.signOut();
                    MainActivity.twoSignedInDevices = true;

                    CustomToast.showInfo(AccountSettingsActivity.this, getString(R.string.account_active_on_another_device), Toast.LENGTH_LONG);
                    CustomToast.showWarning(AccountSettingsActivity.this, getString(R.string.change_password_warning_message), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
            }
        };

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
        ManageTitlePosition.manageActivitySingleTitle(this, waveHeader, title);
        scaleButtons();

        displayAccountDetails();

        changeName.setOnClickListener(this);
        changeEmail.setOnClickListener(this);
        changePassword.setOnClickListener(this);

        signout.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) confirmationDialog();
                    });
                }
            }
        });

        deleteAcc.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) deleteAccountDialog();
                    });
                }
            }
        });

        deleteAllEvents.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) deleteAllEventsDialog();
                    });
                }
            }
        });

        otherSettings.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;

                if (Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) otherSettingsDialog();
                    });
                }
            }
        });

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        selectCountry.setOnClickListener(v -> {
            if (!buttonClicked) {
                if (network) {
                    buttonClicked = true;

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) selectCountryDialog();
                        });
                    }
                } else {
                    if (!toastShowing) {
                        toastShowing = true;

                        CustomToast.showError(this, getString(R.string.no_internet), Toast.LENGTH_SHORT);
                        new Handler().postDelayed(() -> toastShowing = false, Constants.TOAST_SHORT_DURATION);
                    }
                }
            }
        });

        getSupportFragmentManager().setFragmentResultListener(Constants.CONFIRMATION_DIALOG_TAG, this, (requestKey, result) -> yesButtonClicked());

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                hideSystemUI();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Constants.auth.addAuthStateListener(this);
        Constants.usersRef.child(uid).child(Constants.DEVICE_ID).addValueEventListener(deviceIDListener);

        if (Constants.auth.getCurrentUser() != null) {
            Constants.auth.getCurrentUser().reload();

            if ((double) Math.round(DeviceCharacteristics.getWidthPx(this) / getResources().getDisplayMetrics().density * 100) / 100 < Constants.sw360dp) finish();
        }

        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        int autoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);

        if (autoTime == 1) {
            DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
            if (dialogFragment != null) dialogFragment.dismiss();
        } else {
            if (autoTime == 0) {
                DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.OTHER_SETTINGS_DIALOG_TAG);
                DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_NAME_DIALOG_TAG);
                DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_EMAIL_DIALOG_TAG);
                DialogFragment dialogFragment4 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_PASSWORD_DIALOG_TAG);
                DialogFragment dialogFragment5 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                DialogFragment dialogFragment6 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
                DialogFragment dialogFragment7 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ACCOUNT_DIALOG_TAG);
                DialogFragment dialogFragment8 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ALL_EVENTS_DIALOG_TAG);

                if (dialogFragment1 != null) dialogFragment1.dismiss();
                else {
                    if (dialogFragment2 != null) dialogFragment2.dismiss();
                    else {
                        if (dialogFragment3 != null) dialogFragment3.dismiss();
                        else {
                            if (dialogFragment4 != null) dialogFragment4.dismiss();
                            else {
                                if (dialogFragment5 != null) dialogFragment5.dismiss();
                                else {
                                    if (dialogFragment6 != null) dialogFragment6.dismiss();
                                    else {
                                        if (dialogFragment7 != null) dialogFragment7.dismiss();
                                        else {
                                            if (dialogFragment8 != null) dialogFragment8.dismiss();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DialogFragment dialogFragment9 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
                if (dialogFragment9 == null) activateAutoTimeDialog();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Constants.auth.removeAuthStateListener(this);
        Constants.usersRef.child(uid).child(Constants.DEVICE_ID).removeEventListener(deviceIDListener);

        networkStateReceiver.removeListener(this);
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Constants.auth.getCurrentUser() != null) {
            Constants.auth.getCurrentUser().reload();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private void displayAccountDetails() {
        name.setText(Objects.requireNonNull(Constants.auth.getCurrentUser()).getDisplayName());
        email.setText(Constants.auth.getCurrentUser().getEmail());
        password.setText(new String(new char[new Random().nextInt(Constants.MAX_PASSWORD_LABEL_LENGTH - (Constants.MIN_PASSWORD_LENGTH - 1)) + Constants.MIN_PASSWORD_LENGTH]).replace("\0", "*"));

        name.setWidth(getDisplayInfoTextViewWidth(name));
        email.setWidth(getDisplayInfoTextViewWidth(email));
        password.setWidth(getDisplayInfoTextViewWidth(password));

        nameHScrollView.post(() -> {
            if (nameHScrollView.canScrollHorizontally(-1) || nameHScrollView.canScrollHorizontally(1)) {
                name.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                name.setWidth(name.getWidth() + (int) getResources().getDimension(R.dimen.display_info_text_view_additional_width));
            } else {
                name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        });

        emailHScrollView.post(() -> {
            if (emailHScrollView.canScrollHorizontally(-1) || emailHScrollView.canScrollHorizontally(1)) {
                email.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                email.setWidth(email.getWidth() + (int) getResources().getDimension(R.dimen.display_info_text_view_additional_width));
            } else {
                email.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        });

        passwordHScrollView.post(() -> {
            if (passwordHScrollView.canScrollHorizontally(-1) || passwordHScrollView.canScrollHorizontally(1)) {
                password.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                password.setWidth(password.getWidth() + (int) getResources().getDimension(R.dimen.display_info_text_view_additional_width));
            } else {
                password.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        });

        ImageView flag = selectCountry.findViewById(R.id.flag_ImageView);
        TextView country = selectCountry.findViewById(R.id.country_TextView);

        int image = getResources().getIdentifier(Paper.book(Constants.COUNTRY).read(Constants.ISO), "drawable", getPackageName());
        ArrayList<Country> countriesListCopy = new ArrayList<>(Constants.getCountryList(this));

        for (int i = 0; i < countriesListCopy.size(); i++) {
            if (countriesListCopy.get(i).getFlag() == image) {
                flag.setImageResource(image);

                if (countriesListCopy.get(i).getTimeZonesAbbs().size() > 1) {
                    String information = countriesListCopy.get(i).getName() + ", ";
                    String time_zone = Paper.book(Constants.COUNTRY).read(Constants.TZ);

                    for (int j = 0; j < countriesListCopy.get(i).getTimeZonesAbbs().size(); j++) {
                        if (countriesListCopy.get(i).getTimeZonesAbbs().get(j).getTimeZone().equals(time_zone)) {
                            information += countriesListCopy.get(i).getTimeZonesAbbs().get(j).getAbbreviation();
                            country.setText(information);
                            break;
                        }
                    }
                } else {
                    country.setText(countriesListCopy.get(i).getName());
                }

                break;
            }
        }
    }

    private void scaleButtons() {
        back.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        otherSettings.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        deleteAllEvents.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);

        back.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
        otherSettings.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
        deleteAllEvents.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) changeName.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.SMALL_BUTTONS_HEIGHT;
        changeName.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.SMALL_BUTTONS_TEXT_SIZE);
        changeName.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) changeEmail.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.SMALL_BUTTONS_HEIGHT;
        changeEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.SMALL_BUTTONS_TEXT_SIZE);
        changeEmail.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) changePassword.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.SMALL_BUTTONS_HEIGHT;
        changePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.SMALL_BUTTONS_TEXT_SIZE);
        changePassword.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) signout.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        signout.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        signout.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) deleteAcc.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        deleteAcc.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        deleteAcc.setLayoutParams(layoutParams);
    }

    private int getDisplayInfoTextViewWidth(TextView textView) {
        Paint textPaint = textView.getPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.display_info_text_size));
        textPaint.setTypeface(Typeface.create(getString(R.string.app_font), Typeface.BOLD_ITALIC));

        return Math.round(textPaint.measureText(textView.getText().toString()));
    }

    private void otherSettingsDialog() {
        OtherSettingsDialog dialog = new OtherSettingsDialog();
        dialog.show(getSupportFragmentManager(), Constants.OTHER_SETTINGS_DIALOG_TAG);
    }

    private void deleteAllEventsDialog() {
        DeleteAllEventsDialog dialog = new DeleteAllEventsDialog();
        dialog.show(getSupportFragmentManager(), Constants.DELETE_ALL_EVENTS_DIALOG_TAG);
    }

    private void changeNameDialog() {
        ChangeNameDialog dialog = new ChangeNameDialog();
        dialog.show(getSupportFragmentManager(), Constants.CHANGE_NAME_DIALOG_TAG);
    }

    private void changeEmailDialog() {
        ChangeEmailDialog dialog = new ChangeEmailDialog();
        dialog.show(getSupportFragmentManager(), Constants.CHANGE_EMAIL_DIALOG_TAG);
    }

    private void changePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog();
        dialog.show(getSupportFragmentManager(), Constants.CHANGE_PASSWORD_DIALOG_TAG);
    }

    private void deleteAccountDialog() {
        DeleteAccountDialog dialog = new DeleteAccountDialog();
        dialog.show(getSupportFragmentManager(), Constants.DELETE_ACCOUNT_DIALOG_TAG);
    }

    private void confirmationDialog() {
        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.show(getSupportFragmentManager(), Constants.CONFIRMATION_DIALOG_TAG);
    }

    private void selectCountryDialog() {
        SelectCountryDialog dialog = new SelectCountryDialog();
        dialog.show(getSupportFragmentManager(), Constants.SELECT_COUNTRY_DIALOG_TAG);
    }

    private void activateAutoTimeDialog() {
        buttonClicked = true;

        ActivateAutoTimeDialog dialog = new ActivateAutoTimeDialog();
        dialog.show(getSupportFragmentManager(), Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
    }

    public void disableButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDisabled)));
    }

    private void enableButton(Button button) {
        button.setEnabled(true);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    public void disableFABButton(FloatingActionButton button) {
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDisabled)));
    }

    public void enableFABButton(FloatingActionButton button) {
        button.setEnabled(true);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    private void networkEnabledSetup(DialogFragment dialogFragment1, DialogFragment dialogFragment2, DialogFragment dialogFragment3, DialogFragment dialogFragment4, DialogFragment dialogFragment5, DialogFragment dialogFragment6, DialogFragment dialogFragment7) {
        network = true;

        enableFABButton(otherSettings);
        enableFABButton(deleteAllEvents);
        enableButton(changeName);
        enableButton(changeEmail);
        enableButton(changePassword);
        enableButton(signout);
        enableButton(deleteAcc);

        if (dialogFragment1 != null) {
            ((ChangeNameDialog) dialogFragment1).enableButton(((ChangeNameDialog) dialogFragment1).getOk());
        } else {
            if (dialogFragment2 != null) {
                ((ChangeEmailDialog) dialogFragment2).enableButton(((ChangeEmailDialog) dialogFragment2).getOk());
            } else {
                if (dialogFragment3 != null) {
                    ((ChangePasswordDialog) dialogFragment3).enableButton(((ChangePasswordDialog) dialogFragment3).getOk());
                } else {
                    if (dialogFragment4 != null) {
                        ((ConfirmationDialog) dialogFragment4).enableButton(((ConfirmationDialog) dialogFragment4).getYes());
                    } else {
                        if (dialogFragment5 != null) {
                            ((DeleteAccountDialog) dialogFragment5).enableButton(((DeleteAccountDialog) dialogFragment5).getDelete());
                        } else {
                            if (dialogFragment6 != null) {
                                ((OtherSettingsDialog) dialogFragment6).enableSpinner(((OtherSettingsDialog) dialogFragment6).getTimeFormat());
                                ((OtherSettingsDialog) dialogFragment6).enableSpinner(((OtherSettingsDialog) dialogFragment6).getDateFormat());
                                ((OtherSettingsDialog) dialogFragment6).enableSpinner(((OtherSettingsDialog) dialogFragment6).getNotifications());
                                ((OtherSettingsDialog) dialogFragment6).enableSpinner(((OtherSettingsDialog) dialogFragment6).getNotificationsSchedule());

                                ((OtherSettingsDialog) dialogFragment6).enableButton(((OtherSettingsDialog) dialogFragment6).getIncrease1());
                                ((OtherSettingsDialog) dialogFragment6).enableButton(((OtherSettingsDialog) dialogFragment6).getIncrease2());
                            } else {
                                if (dialogFragment7 != null) {
                                    ((DeleteAllEventsDialog) dialogFragment7).enableButton(((DeleteAllEventsDialog) dialogFragment7).getDelete());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void networkDisabledSetup(DialogFragment dialogFragment1, DialogFragment dialogFragment2, DialogFragment dialogFragment3, DialogFragment dialogFragment4, DialogFragment dialogFragment5, DialogFragment dialogFragment6, DialogFragment dialogFragment7) {
        network = false;

        disableFABButton(otherSettings);
        disableFABButton(deleteAllEvents);
        disableButton(changeName);
        disableButton(changeEmail);
        disableButton(changePassword);
        disableButton(signout);
        disableButton(deleteAcc);

        if (dialogFragment1 != null) {
            ((ChangeNameDialog) dialogFragment1).disableButton(((ChangeNameDialog) dialogFragment1).getOk());
        } else {
            if (dialogFragment2 != null) {
                ((ChangeEmailDialog) dialogFragment2).disableButton(((ChangeEmailDialog) dialogFragment2).getOk());
            } else {
                if (dialogFragment3 != null) {
                    ((ChangePasswordDialog) dialogFragment3).disableButton(((ChangePasswordDialog) dialogFragment3).getOk());
                } else {
                    if (dialogFragment4 != null) {
                        ((ConfirmationDialog) dialogFragment4).disableButton(((ConfirmationDialog) dialogFragment4).getYes());
                    } else {
                        if (dialogFragment5 != null) {
                            ((DeleteAccountDialog) dialogFragment5).disableButton(((DeleteAccountDialog) dialogFragment5).getDelete());
                        } else {
                            if (dialogFragment6 != null) {
                                ((OtherSettingsDialog) dialogFragment6).disableSpinner(((OtherSettingsDialog) dialogFragment6).getTimeFormat());
                                ((OtherSettingsDialog) dialogFragment6).disableSpinner(((OtherSettingsDialog) dialogFragment6).getDateFormat());
                                ((OtherSettingsDialog) dialogFragment6).disableSpinner(((OtherSettingsDialog) dialogFragment6).getNotifications());
                                ((OtherSettingsDialog) dialogFragment6).disableSpinner(((OtherSettingsDialog) dialogFragment6).getNotificationsSchedule());

                                ((OtherSettingsDialog) dialogFragment6).disableButton(((OtherSettingsDialog) dialogFragment6).getIncrease1());
                                ((OtherSettingsDialog) dialogFragment6).disableButton(((OtherSettingsDialog) dialogFragment6).getIncrease2());
                            } else {
                                if (dialogFragment7 != null) {
                                    ((DeleteAllEventsDialog) dialogFragment7).disableButton(((DeleteAllEventsDialog) dialogFragment7).getDelete());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void dismissAllDialogs() {
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
        DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_EMAIL_DIALOG_TAG);
        DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_NAME_DIALOG_TAG);
        DialogFragment dialogFragment4 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_PASSWORD_DIALOG_TAG);
        DialogFragment dialogFragment5 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
        DialogFragment dialogFragment6 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ACCOUNT_DIALOG_TAG);
        DialogFragment dialogFragment7 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ALL_EVENTS_DIALOG_TAG);
        DialogFragment dialogFragment8 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.OTHER_SETTINGS_DIALOG_TAG);
        DialogFragment dialogFragment9 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);

        if (dialogFragment1 != null) dialogFragment1.dismiss();
        else {
            if (dialogFragment2 != null) dialogFragment2.dismiss();
            else {
                if (dialogFragment3 != null) dialogFragment3.dismiss();
                else {
                    if (dialogFragment4 != null) dialogFragment4.dismiss();
                    else {
                        if (dialogFragment5 != null) dialogFragment5.dismiss();
                        else {
                            if (dialogFragment6 != null) dialogFragment6.dismiss();
                            else {
                                if (dialogFragment7 != null) dialogFragment7.dismiss();
                                else {
                                    if (dialogFragment8 != null) dialogFragment8.dismiss();
                                    else {
                                        if (dialogFragment9 != null) dialogFragment9.dismiss();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Task<Object> changeUserTimeZone() {
        Map<String, Object> data = new HashMap<>();
        data.put("new_tz", selectedTimeZone);
        data.put("old_tz", oldTimeZone);
        data.put(Constants.ISO, selectedTimeZoneISO);

        return FirebaseFunctions.getInstance("europe-west1")
                .getHttpsCallable("change_user_tz")
                .call(data)
                .continueWith(task -> Objects.requireNonNull(task.getResult()).getData());
    }

    private void countrySelected(int image, String information, String timeZone, String OldTimeZone, String iso) {
        Constants.tzs_info.child(TimeZoneFormat.format(OldTimeZone)).child(Constants.PROPS).child(Constants.UPDATED).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long updated = Objects.requireNonNull(snapshot.getValue(Long.class));

                if (updated == 1) {
                    Constants.tzs_info.child(TimeZoneFormat.format(timeZone)).child(Constants.PROPS).child(Constants.UPDATED).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long updated = Objects.requireNonNull(snapshot.getValue(Long.class));

                            if (updated == 1) {
                                selectedTimeZone = timeZone;
                                oldTimeZone = OldTimeZone;
                                selectedTimeZoneISO = iso;

                                changeUserTimeZone().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        ImageView flag = selectCountry.findViewById(R.id.flag_ImageView);
                                        TextView country = selectCountry.findViewById(R.id.country_TextView);

                                        Paper.book(Constants.COUNTRY).write(Constants.TZ, timeZone);
                                        Paper.book(Constants.COUNTRY).write(Constants.ISO, iso);

                                        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));

                                        NotificationHelper.cancelAllNotifications(AccountSettingsActivity.this, Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid());
                                        NotificationHelper.scheduleAllNotifications(AccountSettingsActivity.this, Constants.auth.getCurrentUser().getUid());

                                        flag.setImageResource(image);
                                        country.setText(information);

                                        CustomToast.showSuccess(AccountSettingsActivity.this, getString(R.string.country_changed_successfully), Toast.LENGTH_SHORT);
                                    } else {
                                        CustomToast.showError(AccountSettingsActivity.this, getString(R.string.country_not_changed_successfully), Toast.LENGTH_LONG);
                                    }

                                    DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                                    Objects.requireNonNull(dialogFragment).dismiss();
                                });
                            } else {
                                if (updated == 0) {
                                    CustomToast.showInfo(AccountSettingsActivity.this, getString(R.string.chosen_country_will_soon_be_updated) + " " + getString(R.string.try_again_later), Toast.LENGTH_LONG);

                                    DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                                    Objects.requireNonNull(dialogFragment).dismiss();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());

                            DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                            Objects.requireNonNull(dialogFragment).dismiss();
                        }
                    });
                } else {
                    if (updated == 0) {
                        CustomToast.showInfo(AccountSettingsActivity.this, getString(R.string.info_will_soon_be_updated) + " " + getString(R.string.try_again_later), Toast.LENGTH_LONG);

                        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());

                DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                Objects.requireNonNull(dialogFragment).dismiss();
            }
        });
    }

    public boolean getNetwork() { return network; }

    public ConstraintLayout getSplashScreen_() { return splashScreen; }

    public void setButtonClicked(boolean buttonClicked) { this.buttonClicked = buttonClicked; }

    public boolean getSelectCountryDialogToastShowing() { return selectCountryDialogToastShowing; }

    public void setSelectCountryDialogToastShowing(boolean selectCountryDialogToastShowing) { this.selectCountryDialogToastShowing = selectCountryDialogToastShowing; }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (Constants.auth.getCurrentUser() == null) {
            Constants.usersRef.child(uid).child(Constants.DEVICE_ID).removeEventListener(deviceIDListener);

            if (!deletingAccount) {
                dismissAllDialogs();

                splashScreen.setVisibility(View.VISIBLE);
                ((MainActivity) ((ProjectM) getApplicationContext()).getMainActivityContext()).setSplashScreenVisibility(View.VISIBLE);

                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    private void yesButtonClicked() {
        Constants.auth.signOut();
    }

    @Override
    public void successfullyUpdatedName() {
        displayAccountDetails();
    }

    @Override
    public void successfullyUpdatedEmail() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_EMAIL_DIALOG_TAG);
        Objects.requireNonNull(dialogFragment).dismiss();

        Objects.requireNonNull(Constants.auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CustomToast.showInfo(this, getString(R.string.confirmation_email_sent_to) + " " + Constants.auth.getCurrentUser().getEmail(), Toast.LENGTH_LONG);
            } else {
                CustomToast.showError(this, getString(R.string.confirmation_email_sent_to_error) + " " + Constants.auth.getCurrentUser().getEmail() + ". " + getString(R.string.try_again_later), Toast.LENGTH_LONG);
            }

            Constants.auth.signOut();
        });
    }

    @Override
    public void successfullyUpdatedPassword() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_PASSWORD_DIALOG_TAG);
        Objects.requireNonNull(dialogFragment).dismiss();

        Constants.auth.signOut();
    }

    @Override
    public void networkAvailable() {
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_NAME_DIALOG_TAG);
        DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_EMAIL_DIALOG_TAG);
        DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_PASSWORD_DIALOG_TAG);
        DialogFragment dialogFragment4 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
        DialogFragment dialogFragment5 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ACCOUNT_DIALOG_TAG);
        DialogFragment dialogFragment6 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.OTHER_SETTINGS_DIALOG_TAG);
        DialogFragment dialogFragment7 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ALL_EVENTS_DIALOG_TAG);

        networkEnabledSetup(dialogFragment1, dialogFragment2, dialogFragment3, dialogFragment4, dialogFragment5, dialogFragment6, dialogFragment7);
    }

    @Override
    public void networkUnavailable() {
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_NAME_DIALOG_TAG);
        DialogFragment dialogFragment2 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_EMAIL_DIALOG_TAG);
        DialogFragment dialogFragment3 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CHANGE_PASSWORD_DIALOG_TAG);
        DialogFragment dialogFragment4 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.CONFIRMATION_DIALOG_TAG);
        DialogFragment dialogFragment5 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ACCOUNT_DIALOG_TAG);
        DialogFragment dialogFragment6 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.OTHER_SETTINGS_DIALOG_TAG);
        DialogFragment dialogFragment7 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.DELETE_ALL_EVENTS_DIALOG_TAG);

        networkDisabledSetup(dialogFragment1, dialogFragment2, dialogFragment3, dialogFragment4, dialogFragment5, dialogFragment6, dialogFragment7);
    }

    @Override
    public void onCountrySelected(int image, String information, String timeZone, String iso) {
        if (!Objects.requireNonNull(Paper.book(Constants.COUNTRY).read(Constants.ISO)).equals(iso) || !Objects.requireNonNull(Paper.book(Constants.COUNTRY).read(Constants.TZ)).equals(timeZone)) {
            String OldTimeZone = Paper.book(Constants.COUNTRY).read(Constants.TZ);

            Thread thread = new Thread(() -> {
                try {
                    URL url = new URL(Constants.DATABASE_URL);

                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        int code = connection.getResponseCode();

                        runOnUiThread(() -> {
                            if(code == HttpURLConnection.HTTP_OK) {
                                countrySelected(image, information, timeZone, OldTimeZone, iso);
                            } else {
                                CustomToast.showError(this, getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                                DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                                Objects.requireNonNull(dialogFragment).dismiss();
                            }

                            connection.disconnect();
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "onCountrySelected: IOException: " + e.getMessage());

                        runOnUiThread(() -> {
                            CustomToast.showError(this, getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                            DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                            Objects.requireNonNull(dialogFragment).dismiss();
                        });
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "onCountrySelected: MalformedURLException: " + e.getMessage());

                    runOnUiThread(() -> {
                        CustomToast.showError(this, getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
                        Objects.requireNonNull(dialogFragment).dismiss();
                    });
                }
            });

            thread.start();
        } else {
            DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.SELECT_COUNTRY_DIALOG_TAG);
            Objects.requireNonNull(dialogFragment).dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (!buttonClicked) {
            buttonClicked = true;

            if (Constants.auth.getCurrentUser() != null) {
                Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                    if (Constants.auth.getCurrentUser() != null) {
                        if (v.getId() == R.id.changeName_Button) {
                            changeNameDialog();
                        } else {
                            if (v.getId() == R.id.changeEmail_Button) {
                                changeEmailDialog();
                            } else {
                                if (v.getId() == R.id.changePassword_Button) {
                                    changePasswordDialog();
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
