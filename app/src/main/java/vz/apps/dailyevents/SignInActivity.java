package vz.apps.dailyevents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.scwang.wave.MultiWaveHeader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import application_class.ProjectM;
import constants.Constants;
import dialogs.ActivateAutoTimeDialog;
import dialogs.ResetPasswordDialog;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.Information;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.NetworkStateReceiver;
import helper_classes.notifications.NotificationHelper;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.time_zones.TimeZoneFormat;
import helper_classes.validators.EmailValidator;
import helper_classes.validators.PasswordValidator;
import helper_classes.validators.SundayDateTimeValidator;
import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private static final String TAG = "LoginActivity";

    private TextInputLayout email;
    private TextInputLayout password;
    private TextView newAccount;
    private TextView forgottenPassword;
    private TextView verifyEmail;
    private TextView title;
    private Button proceed;
    private FloatingActionButton back;
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private MultiWaveHeader waveHeader;
    private ConstraintLayout splashScreen;
    private boolean signingIn;
    private boolean keyboardOpened;
    private boolean buttonClicked;

    private TextWatcher emailTextWatcher;
    private TextWatcher passwordTextWatcher;

    private NetworkStateReceiver networkStateReceiver;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        hideSystemUI();

        email = findViewById(R.id.email_TIL);
        password = findViewById(R.id.password_TIL);
        newAccount = findViewById(R.id.newAccount_TextView);
        forgottenPassword = findViewById(R.id.forgottenPassword_TextView);
        verifyEmail = findViewById(R.id.verifyEmail_TextView);
        title = findViewById(R.id.title_TextView);
        proceed = findViewById(R.id.proceed_Button);
        back = findViewById(R.id.back_FAB);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        waveHeader = findViewById(R.id.waveHeader);
        splashScreen = findViewById(R.id.splashScreen_ConstraintLayout);
        signingIn = false;
        keyboardOpened = false;
        networkStateReceiver = new NetworkStateReceiver();
        buttonClicked = false;

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
        ManageTitlePosition.manageActivitySingleTitle(this, waveHeader, title);
        scaleButtons();

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) progressBar2.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.SIA_SMALL_PROGRESS_BAR_HEIGHT;
        progressBar2.setLayoutParams(layoutParams);

        scaleEditTexts();
        ((ProjectM) getApplicationContext()).setSignUpActivityLastOpened(false);

        String callingActivity = getIntent().getStringExtra(Constants.ACTIVITY_KEY);
        if (callingActivity != null && callingActivity.equals(SignUpActivity.class.getSimpleName())) splashScreen.setVisibility(View.GONE);

        back.setOnClickListener(v -> {
            if (!buttonClicked) finish();
        });

        newAccount.setOnClickListener(v -> {
            if (!buttonClicked) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.putExtra(Constants.ACTIVITY_KEY, getClass().getSimpleName());

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        forgottenPassword.setOnClickListener(v -> {
            if (!buttonClicked) {
                buttonClicked = true;
                resetPasswordDialog();
            }
        });

        verifyEmail.setOnClickListener(v -> {
            if (!buttonClicked) {
                verifyEmail.setVisibility(View.GONE);
                progressBar2.setVisibility(View.VISIBLE);

                Objects.requireNonNull(Constants.auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task -> {
                    progressBar2.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        CustomToast.showInfo(this, getString(R.string.confirmation_email_sent_to) + " " + Constants.auth.getCurrentUser().getEmail(), Toast.LENGTH_LONG);
                    } else {
                        CustomToast.showError(this, getString(R.string.confirmation_email_sent_to_error) + " " + Constants.auth.getCurrentUser().getEmail() + ". " + getString(R.string.try_again_later), Toast.LENGTH_LONG);
                    }
                });
            }
        });

        proceed.setOnClickListener(v -> {
            if (!buttonClicked) {
                String Email = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
                String Password = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

                if (EmailValidator.validateEmail(email, Email, getApplicationContext()) & PasswordValidator.isEmpty(password, Password, getApplicationContext())) {
                    verifyEmail.setVisibility(View.GONE);
                    proceed.setVisibility(View.INVISIBLE);

                    disableTextView(forgottenPassword, R.color.colorTextViewDisabled);
                    disableTextView(newAccount, R.color.colorTextViewDisabled);
                    disableFABButton(back);

                    email.getEditText().setEnabled(false);
                    password.getEditText().setEnabled(false);

                    progressBar1.setVisibility(View.VISIBLE);

                    Thread thread = new Thread(() -> {
                        try {
                            URL url = new URL(Constants.DATABASE_URL);

                            try {
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                int code = connection.getResponseCode();

                                runOnUiThread(() -> {
                                    if (code == HttpURLConnection.HTTP_OK) {
                                        signInUser(Email, Password);
                                    } else {
                                        CustomToast.showError(this, getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                                        verifyEmail.setVisibility(View.GONE);
                                        proceed.setVisibility(View.VISIBLE);

                                        enableTextView(forgottenPassword, R.color.colorTextBlack);
                                        enableTextView(newAccount, R.color.colorTextBlack);
                                        enableFABButton(back);

                                        email.getEditText().setEnabled(true);
                                        password.getEditText().setEnabled(true);

                                        progressBar1.setVisibility(View.GONE);
                                    }

                                    connection.disconnect();
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "onCreate: IOException: " + e.getMessage());

                                runOnUiThread(() -> {
                                    CustomToast.showError(this, getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                                    verifyEmail.setVisibility(View.GONE);
                                    proceed.setVisibility(View.VISIBLE);

                                    enableTextView(forgottenPassword, R.color.colorTextBlack);
                                    enableTextView(newAccount, R.color.colorTextBlack);
                                    enableFABButton(back);

                                    Objects.requireNonNull(email.getEditText()).setEnabled(true);
                                    Objects.requireNonNull(password.getEditText()).setEnabled(true);

                                    progressBar1.setVisibility(View.GONE);
                                });
                            }
                        } catch (MalformedURLException e) {
                            Log.e(TAG, "onCreate: MalformedURLException: " + e.getMessage());

                            runOnUiThread(() -> {
                                CustomToast.showError(this, getString(R.string.problem_with_server), Toast.LENGTH_SHORT);

                                verifyEmail.setVisibility(View.GONE);
                                proceed.setVisibility(View.VISIBLE);

                                enableTextView(forgottenPassword, R.color.colorTextBlack);
                                enableTextView(newAccount, R.color.colorTextBlack);
                                enableFABButton(back);

                                Objects.requireNonNull(email.getEditText()).setEnabled(true);
                                Objects.requireNonNull(password.getEditText()).setEnabled(true);

                                progressBar1.setVisibility(View.GONE);
                            });
                        }
                    });

                    thread.start();
                }
            }
        });

        keyboardListener = isVisible -> {
            if (isVisible) {
                keyboardOpened = true;
                showSystemUI();
            } else {
                keyboardOpened = false;
                hideSystemUI();
            }
        };

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                if (!keyboardOpened) hideSystemUI();
            }
        });

        findViewById(android.R.id.content).post(() -> {
            if ((double) Math.round(DeviceCharacteristics.getWidthPx(SignInActivity.this) / getResources().getDisplayMetrics().density * 100) / 100 >= Constants.sw360dp) {
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
                CustomToast.showWarning(SignInActivity.this, getString(R.string.sw_less_than_360dp_warning_message), Toast.LENGTH_LONG);
                CustomToast.showInfo(SignInActivity.this, getString(R.string.sw_less_than_360dp_info_message), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        textWatcherSetup();
        KeyboardUtils.addKeyboardToggleListener(this, keyboardListener);

        int autoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);

        if (autoTime == 1) {
            DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
            if (dialogFragment != null) dialogFragment.dismiss();
        }

        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        if ((double) Math.round(DeviceCharacteristics.getWidthPx(this) / getResources().getDisplayMetrics().density * 100) / 100 < Constants.sw360dp) {
            if (splashScreen.getVisibility() == View.GONE) splashScreen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Objects.requireNonNull(email.getEditText()).removeTextChangedListener(emailTextWatcher);
        Objects.requireNonNull(password.getEditText()).removeTextChangedListener(passwordTextWatcher);
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);

        networkStateReceiver.removeListener(this);
        unregisterReceiver(networkStateReceiver);

        if ((double) Math.round(DeviceCharacteristics.getWidthPx(this) / getResources().getDisplayMetrics().density * 100) / 100 < Constants.sw360dp) finishAndRemoveTask();
    }

    @Override
    public void onBackPressed() {
        if (back.isEnabled() && !buttonClicked) super.onBackPressed();
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

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void signInUser(String Email, String Password) {
        int autoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);

        if (autoTime == 1) {
            Constants.auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(Constants.auth.getCurrentUser()).getDisplayName() != null && !Objects.requireNonNull(Constants.auth.getCurrentUser().getDisplayName()).isEmpty()) {
                        if (Constants.auth.getCurrentUser().isEmailVerified()) {
                            signingIn = true;

                            Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.COUNTRY).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String defaultTimeZone = TimeZone.getDefault().getID();
                                    final String timeZone = snapshot.child(Constants.TZ).getValue(String.class);

                                    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));

                                    if (SundayDateTimeValidator.validateDateAndTime()) {
                                        String deviceID = Paper.book(Constants.DEVICE_ID).read(Constants.ID);
                                        String iso = snapshot.child(Constants.ISO).getValue(String.class);

                                        Paper.book(Constants.COUNTRY).write(Constants.TZ, Objects.requireNonNull(timeZone));
                                        Paper.book(Constants.COUNTRY).write(Constants.ISO, Objects.requireNonNull(iso));

                                        Constants.usersRef.child(Constants.auth.getCurrentUser().getUid()).child(Constants.DEVICE_ID).setValue(deviceID);

                                        syncInformation(timeZone);
                                    } else {
                                        email.setError(getString(R.string.try_again_in_a_few_minutes));
                                        password.setError(getString(R.string.try_again_in_a_few_minutes));

                                        Constants.auth.signOut();
                                        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));

                                        verifyEmail.setVisibility(View.GONE);
                                        proceed.setVisibility(View.VISIBLE);
                                        enableTextView(forgottenPassword, R.color.colorTextBlack);
                                        enableTextView(newAccount, R.color.colorTextBlack);
                                        enableFABButton(back);
                                        Objects.requireNonNull(email.getEditText()).setEnabled(true);
                                        Objects.requireNonNull(password.getEditText()).setEnabled(true);
                                        progressBar1.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
                                }
                            });
                        } else {
                            email.setError(getString(R.string.email_address_not_confirmed));

                            verifyEmail.setVisibility(View.VISIBLE);
                            proceed.setVisibility(View.VISIBLE);

                            enableTextView(forgottenPassword, R.color.colorTextBlack);
                            enableTextView(newAccount, R.color.colorTextBlack);
                            enableFABButton(back);

                            Objects.requireNonNull(email.getEditText()).setEnabled(true);
                            Objects.requireNonNull(password.getEditText()).setEnabled(true);

                            progressBar1.setVisibility(View.GONE);
                        }
                    } else {
                        adminDeleteUser(Constants.auth.getCurrentUser().getUid()).addOnCompleteListener(task1 -> {
                            verifyEmail.setVisibility(View.GONE);
                            proceed.setVisibility(View.VISIBLE);

                            enableTextView(forgottenPassword, R.color.colorTextBlack);
                            enableTextView(newAccount, R.color.colorTextBlack);
                            enableFABButton(back);

                            Objects.requireNonNull(email.getEditText()).setEnabled(true);
                            Objects.requireNonNull(password.getEditText()).setEnabled(true);

                            progressBar1.setVisibility(View.GONE);
                        });

                        Constants.auth.signOut();

                        CustomToast.showError(this, getString(R.string.connection_lost_while_signing_up_error), Toast.LENGTH_LONG);
                        CustomToast.showInfo(this, getString(R.string.connection_lost_while_signing_up_info_message1), Toast.LENGTH_LONG);
                        CustomToast.showInfo(this, getString(R.string.connection_lost_while_signing_up_info_message2), Toast.LENGTH_LONG);
                    }
                } else {
                    email.setError(getString(R.string.wrong_email_address_or_password));
                    password.setError(getString(R.string.wrong_email_address_or_password));

                    verifyEmail.setVisibility(View.GONE);
                    proceed.setVisibility(View.VISIBLE);

                    enableTextView(forgottenPassword, R.color.colorTextBlack);
                    enableTextView(newAccount, R.color.colorTextBlack);
                    enableFABButton(back);

                    Objects.requireNonNull(email.getEditText()).setEnabled(true);
                    Objects.requireNonNull(password.getEditText()).setEnabled(true);

                    progressBar1.setVisibility(View.GONE);
                }
            });
        } else {
            if (autoTime == 0) {
                activateAutoTimeDialog();

                verifyEmail.setVisibility(View.GONE);
                proceed.setVisibility(View.VISIBLE);

                enableTextView(forgottenPassword, R.color.colorTextBlack);
                enableTextView(newAccount, R.color.colorTextBlack);
                enableFABButton(back);

                Objects.requireNonNull(email.getEditText()).setEnabled(true);
                Objects.requireNonNull(password.getEditText()).setEnabled(true);

                progressBar1.setVisibility(View.GONE);
            }
        }
    }

    private void syncInformation(final String time_zone) {
        Constants.tzs_info.child(TimeZoneFormat.format(time_zone)).child(Constants.PROPS).child(Constants.UPDATED).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final long updated = Objects.requireNonNull(snapshot.getValue(Long.class));

                Constants.dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ref_date = snapshot.getValue(String.class);

                        if (updated == 1) {
                            Paper.book(Constants.REF_DATE).write(Constants.DATE, Objects.requireNonNull(ref_date));
                        } else {
                            if (updated == 0) {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
                                dateFormat.setLenient(false);

                                Calendar calendar = Calendar.getInstance(Locale.US);
                                calendar.setLenient(false);

                                try {
                                    calendar.setTime(Objects.requireNonNull(dateFormat.parse(Objects.requireNonNull(ref_date))));
                                } catch (ParseException e) {
                                    Log.e(TAG, "updateLocalVariables: ParseException: " + e.getMessage());
                                }

                                calendar.add(Calendar.DAY_OF_MONTH, -7);
                                Paper.book(Constants.REF_DATE).write(Constants.DATE, dateFormat.format(calendar.getTime()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
                    }
                });

                Constants.weekFlagsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (updated == 1) {
                            Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK0, Objects.requireNonNull(snapshot.child(Constants.WEEK0).getValue(String.class)));
                            Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK1, Objects.requireNonNull(snapshot.child(Constants.WEEK1).getValue(String.class)));
                            Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK2, Objects.requireNonNull(snapshot.child(Constants.WEEK2).getValue(String.class)));
                            Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK3, Objects.requireNonNull(snapshot.child(Constants.WEEK3).getValue(String.class)));
                        } else {
                            if (updated == 0) {
                                Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK0, Objects.requireNonNull(snapshot.child(Constants.WEEK1).getValue(String.class)));
                                Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK1, Objects.requireNonNull(snapshot.child(Constants.WEEK2).getValue(String.class)));
                                Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK2, Objects.requireNonNull(snapshot.child(Constants.WEEK3).getValue(String.class)));
                                Paper.book(Constants.WEEK_FLAGS).write(Constants.WEEK3, Objects.requireNonNull(snapshot.child(Constants.WEEK0).getValue(String.class)));
                            }
                        }

                        Constants.usersRef.child(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        sync(snapshot);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: DatabaseError: " + error.getMessage());
            }
        });
    }

    private void sync(DataSnapshot snapshot) {
        if (Constants.auth.getCurrentUser() != null) {
            long time_format = Objects.requireNonNull(snapshot.child(Constants.OTHER_SETTINGS).child(Constants.TIME_F).getValue(Long.class));
            long date_format = Objects.requireNonNull(snapshot.child(Constants.OTHER_SETTINGS).child(Constants.DATE_F).getValue(Long.class));
            long notifications = Objects.requireNonNull(snapshot.child(Constants.OTHER_SETTINGS).child(Constants.NOTIF).getValue(Long.class));
            long notifications_schedule = Objects.requireNonNull(snapshot.child(Constants.OTHER_SETTINGS).child(Constants.NOTIF_SCHED).getValue(Long.class));
            long characters_increased = Objects.requireNonNull(snapshot.child(Constants.OTHER_SETTINGS).child(Constants.CHAR_INC).getValue(Long.class));
            long events_increased = Objects.requireNonNull(snapshot.child(Constants.OTHER_SETTINGS).child(Constants.EVENTS_INC).getValue(Long.class));

            Paper.book(Constants.OTHER_SETTINGS).write(Constants.TIME_F, time_format);
            Paper.book(Constants.OTHER_SETTINGS).write(Constants.DATE_F, date_format);
            Paper.book(Constants.OTHER_SETTINGS).write(Constants.NOTIF, notifications);
            Paper.book(Constants.OTHER_SETTINGS).write(Constants.NOTIF_SCHED, notifications_schedule);
            Paper.book(Constants.OTHER_SETTINGS).write(Constants.CHAR_INC, characters_increased);
            Paper.book(Constants.OTHER_SETTINGS).write(Constants.EVENTS_INC, events_increased);

            if (snapshot.getChildrenCount() > 3) {
                if (snapshot.hasChild(Constants.WEEK0)) {
                    for (DataSnapshot ds1 : snapshot.child(Constants.WEEK0).getChildren()) {
                        for (DataSnapshot ds2 : ds1.getChildren()) {
                            Information person = ds2.getValue(Information.class);
                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.WEEK0 + "." + ds1.getKey()).write(Objects.requireNonNull(ds2.getKey()), Objects.requireNonNull(person));
                        }
                    }
                }

                if (snapshot.hasChild(Constants.WEEK1)) {
                    for (DataSnapshot ds1 : snapshot.child(Constants.WEEK1).getChildren()) {
                        for (DataSnapshot ds2 : ds1.getChildren()) {
                            Information person = ds2.getValue(Information.class);
                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.WEEK1 + "." + ds1.getKey()).write(Objects.requireNonNull(ds2.getKey()), Objects.requireNonNull(person));
                        }
                    }
                }

                if (snapshot.hasChild(Constants.WEEK2)) {
                    for (DataSnapshot ds1 : snapshot.child(Constants.WEEK2).getChildren()) {
                        for (DataSnapshot ds2 : ds1.getChildren()) {
                            Information person = ds2.getValue(Information.class);
                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.WEEK2 + "." + ds1.getKey()).write(Objects.requireNonNull(ds2.getKey()), Objects.requireNonNull(person));
                        }
                    }
                }

                if (snapshot.hasChild(Constants.WEEK3)) {
                    for (DataSnapshot ds1 : snapshot.child(Constants.WEEK3).getChildren()) {
                        for (DataSnapshot ds2 : ds1.getChildren()) {
                            Information person = ds2.getValue(Information.class);
                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.WEEK3 + "." + ds1.getKey()).write(Objects.requireNonNull(ds2.getKey()), Objects.requireNonNull(person));
                        }
                    }
                }

                if (snapshot.hasChild(Constants.OTHER_EVENTS)) {
                    ArrayList<String> keys = new ArrayList<>();
                    ArrayList<String> weeks = new ArrayList<>();

                    String week0Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK0));
                    String week1Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK1));
                    String week2Flag = Objects.requireNonNull(Paper.book(Constants.WEEK_FLAGS).read(Constants.WEEK2));

                    for (DataSnapshot ds : snapshot.child(Constants.OTHER_EVENTS).child(Constants.FLAGS).getChildren()) {
                        long flag = Objects.requireNonNull(ds.getValue(Long.class));

                        if (flag > 0) {
                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).write(Objects.requireNonNull(ds.getKey()), flag);
                        } else {
                            if (flag >= -2) {
                                keys.add(ds.getKey());

                                if (flag == 0) {
                                    if (week0Flag.equals(Constants.NEXT_WEEK)) {
                                        weeks.add(Constants.WEEK0);
                                    } else {
                                        if (week1Flag.equals(Constants.NEXT_WEEK)) {
                                            weeks.add(Constants.WEEK1);
                                        } else {
                                            if (week2Flag.equals(Constants.NEXT_WEEK)) {
                                                weeks.add(Constants.WEEK2);
                                            } else {
                                                weeks.add(Constants.WEEK3);
                                            }
                                        }
                                    }
                                } else {
                                    if (flag == -1) {
                                        if (week0Flag.equals(Constants.THIS_WEEK)) {
                                            weeks.add(Constants.WEEK0);
                                        } else {
                                            if (week1Flag.equals(Constants.THIS_WEEK)) {
                                                weeks.add(Constants.WEEK1);
                                            } else {
                                                if (week2Flag.equals(Constants.THIS_WEEK)) {
                                                    weeks.add(Constants.WEEK2);
                                                } else {
                                                    weeks.add(Constants.WEEK3);
                                                }
                                            }
                                        }
                                    } else {
                                        if (week0Flag.equals(Constants.LAST_WEEK)) {
                                            weeks.add(Constants.WEEK0);
                                        } else {
                                            if (week1Flag.equals(Constants.LAST_WEEK)) {
                                                weeks.add(Constants.WEEK1);
                                            } else {
                                                if (week2Flag.equals(Constants.LAST_WEEK)) {
                                                    weeks.add(Constants.WEEK2);
                                                } else {
                                                    weeks.add(Constants.WEEK3);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (DataSnapshot ds : snapshot.child(Constants.OTHER_EVENTS).child(Constants.INFO).getChildren()) {
                        if (keys.contains(ds.getKey())) {
                            Information person = ds.getValue(Information.class);
                            int index = keys.indexOf(ds.getKey());

                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + weeks.get(index) + "." + Objects.requireNonNull(person).dayFromDate(this).getWeekDay_ENG()).write(keys.get(index) + ".**", person);
                        } else {
                            Information person = ds.getValue(Information.class);
                            Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).write(Objects.requireNonNull(ds.getKey()), Objects.requireNonNull(person));
                        }
                    }
                }
            }

            ((ProjectM) getApplicationContext()).getLocalVariables();
            ((ProjectM) getApplicationContext()).setupHandlerAndRunnable();

            NotificationHelper.scheduleAllNotifications(this, Constants.auth.getCurrentUser().getUid());

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.putExtra(Constants.ACTIVITY_KEY, getClass().getSimpleName());

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    private void textWatcherSetup() {
        emailTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        Objects.requireNonNull(email.getEditText()).addTextChangedListener(emailTextWatcher);
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(passwordTextWatcher);
    }

    private Task<Object> adminDeleteUser(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.UID, uid);

        return FirebaseFunctions.getInstance("europe-west1")
                .getHttpsCallable("admin_delete_user")
                .call(data)
                .continueWith(task -> Objects.requireNonNull(task.getResult()).getData());
    }

    private void scaleButtons() {
        back.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        back.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) proceed.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        proceed.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        proceed.setLayoutParams(layoutParams);
    }

    private void scaleEditTexts() {
        Objects.requireNonNull(email.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        Objects.requireNonNull(password.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
    }

    public void removeKeyboardListener() {
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);
    }

    public void addKeyboardListener() {
        KeyboardUtils.addKeyboardToggleListener(this, keyboardListener);
    }

    private void disableButton(Button button) {
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

    private void disableTextView(TextView textView, int color) {
        textView.setEnabled(false);
        textView.setTextColor(ContextCompat.getColor(this, color));
    }

    private void enableTextView(TextView textView, int color) {
        textView.setEnabled(true);
        textView.setTextColor(ContextCompat.getColor(this, color));
    }

    private void networkEnabledSetup(DialogFragment dialogFragment1) {
        enableButton(proceed);
        enableTextView(verifyEmail, R.color.colorError);
        enableTextView(forgottenPassword, R.color.colorTextBlack);

        Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).write(Constants.CONNECTION, 0);

        if (dialogFragment1 != null) {
            ((ResetPasswordDialog) dialogFragment1).enableButton(((ResetPasswordDialog) dialogFragment1).getSend());
        }
    }

    private void networkDisabledSetup(DialogFragment dialogFragment1) {
        disableButton(proceed);
        disableTextView(verifyEmail, R.color.colorErrorDisabled);
        disableTextView(forgottenPassword, R.color.colorTextViewDisabled);

        if (signingIn) Paper.book(Constants.CONNECTION_LOST_WHILE_SIGNING_IN).write(Constants.CONNECTION, 1);

        if (dialogFragment1 != null) {
            ((ResetPasswordDialog) dialogFragment1).disableButton(((ResetPasswordDialog) dialogFragment1).getSend());
        }
    }

    private void resetPasswordDialog() {
        ResetPasswordDialog dialog = new ResetPasswordDialog();
        dialog.show(getSupportFragmentManager(), Constants.RESET_PASSWORD_DIALOG_TAG);
    }

    private void activateAutoTimeDialog() {
        buttonClicked = true;

        ActivateAutoTimeDialog dialog = new ActivateAutoTimeDialog();
        dialog.show(getSupportFragmentManager(), Constants.ACTIVATE_AUTO_TIME_DIALOG_TAG);
    }

    public void setButtonClicked(boolean buttonClicked) { this.buttonClicked = buttonClicked; }

    @Override
    public void networkAvailable() {
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.RESET_PASSWORD_DIALOG_TAG);
        networkEnabledSetup(dialogFragment1);
    }

    @Override
    public void networkUnavailable() {
        DialogFragment dialogFragment1 = (DialogFragment) getSupportFragmentManager().findFragmentByTag(Constants.RESET_PASSWORD_DIALOG_TAG);
        networkDisabledSetup(dialogFragment1);
    }
}
