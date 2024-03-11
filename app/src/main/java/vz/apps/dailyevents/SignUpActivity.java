package vz.apps.dailyevents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.functions.FirebaseFunctions;
import com.scwang.wave.MultiWaveHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import application_class.ProjectM;
import constants.Constants;
import dialogs.SelectCountryDialog;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.NetworkStateReceiver;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.EmailValidator;
import helper_classes.validators.NameValidator;
import helper_classes.validators.PasswordValidator;
import helper_classes.validators.TimeZoneISOValidator;

public class SignUpActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener, SelectCountryDialog.CountrySelect {

    private static final String TAG = "SignUpActivity";

    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextView existingAccount;
    private TextView title;
    private Button proceed;
    private FloatingActionButton back;
    private ConstraintLayout selectCountry;
    private ProgressBar progressBar;
    private String selectedTimeZone;
    private String selectedTimeZoneISO;
    private MultiWaveHeader waveHeader;
    private ConstraintLayout splashScreen;
    private boolean keyboardOpened;
    private boolean buttonClicked;
    private boolean toastShowing;
    private boolean selectCountryDialogToastShowing;

    private TextWatcher firstName_TextWatcher;
    private TextWatcher lastName_TextWatcher;
    private TextWatcher emailTextWatcher;
    private TextWatcher passwordTextWatcher;

    private NetworkStateReceiver networkStateReceiver;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        hideSystemUI();

        firstName = findViewById(R.id.firstName_TIL);
        lastName = findViewById(R.id.lastName_TIL);
        email = findViewById(R.id.email_TIL);
        password = findViewById(R.id.password_TIL);
        existingAccount = findViewById(R.id.existingAccount_TextView);
        title = findViewById(R.id.title_TextView);
        proceed = findViewById(R.id.proceed_Button);
        back = findViewById(R.id.back_FAB);
        selectCountry = findViewById(R.id.selectCountry_Layout);
        progressBar = findViewById(R.id.progressBar);
        selectedTimeZone = null;
        selectedTimeZoneISO = null;
        waveHeader = findViewById(R.id.waveHeader);
        splashScreen = findViewById(R.id.splashScreen_ConstraintLayout);
        keyboardOpened = false;
        networkStateReceiver = new NetworkStateReceiver();
        buttonClicked = false;
        toastShowing = false;
        selectCountryDialogToastShowing = false;

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
        ManageTitlePosition.manageActivitySingleTitle(this, waveHeader, title);
        scaleButtons();

        scaleEditTexts();
        ((ProjectM) getApplicationContext()).setSignUpActivityLastOpened(true);

        String callingActivity = getIntent().getStringExtra(Constants.ACTIVITY_KEY);
        if (callingActivity != null && callingActivity.equals(SignInActivity.class.getSimpleName())) splashScreen.setVisibility(View.GONE);

        back.setOnClickListener(v -> {
            if (!buttonClicked) finish();
        });

        existingAccount.setOnClickListener(v -> {
            if (!buttonClicked) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.putExtra(Constants.ACTIVITY_KEY, getClass().getSimpleName());

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        proceed.setOnClickListener(v -> {
            if (!buttonClicked) {
                String FirstName = Objects.requireNonNull(firstName.getEditText()).getText().toString().trim();
                String LastName = Objects.requireNonNull(lastName.getEditText()).getText().toString().trim();
                String Email = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
                String Password = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

                if (NameValidator.validateName(firstName, FirstName, getApplicationContext())
                        & NameValidator.validateName(lastName, LastName, getApplicationContext())
                        & EmailValidator.validateEmail(email, Email, getApplicationContext())
                        & PasswordValidator.validatePassword(password, Password, getApplicationContext())
                        & TimeZoneISOValidator.validate(selectedTimeZone, selectedTimeZoneISO, SignUpActivity.this)) {
                    registerUser(FirstName, LastName, Email, Password);
                }
            }
        });

        selectCountry.setOnClickListener(view -> {
            if (!buttonClicked) {
                buttonClicked = true;
                selectCountryDialog();
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
            if ((double) Math.round(DeviceCharacteristics.getWidthPx(SignUpActivity.this) / getResources().getDisplayMetrics().density * 100) / 100 >= Constants.sw360dp) {
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
                CustomToast.showWarning(SignUpActivity.this, getString(R.string.sw_less_than_360dp_warning_message), Toast.LENGTH_LONG);
                CustomToast.showInfo(SignUpActivity.this, getString(R.string.sw_less_than_360dp_info_message), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        textWatcherSetup();
        KeyboardUtils.addKeyboardToggleListener(this, keyboardListener);

        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        if ((double) Math.round(DeviceCharacteristics.getWidthPx(this) / getResources().getDisplayMetrics().density * 100) / 100 < Constants.sw360dp) {
            if (splashScreen.getVisibility() == View.GONE) splashScreen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Objects.requireNonNull(firstName.getEditText()).removeTextChangedListener(firstName_TextWatcher);
        Objects.requireNonNull(lastName.getEditText()).removeTextChangedListener(lastName_TextWatcher);
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

    private void registerUser(final String firstName, final String lastName, final String email, String password) {
        proceed.setVisibility(View.INVISIBLE);

        disableTextView(existingAccount, R.color.colorTextViewDisabled);
        disableFABButton(back);

        Objects.requireNonNull(this.firstName.getEditText()).setEnabled(false);
        Objects.requireNonNull(this.lastName.getEditText()).setEnabled(false);
        Objects.requireNonNull(this.email.getEditText()).setEnabled(false);
        Objects.requireNonNull(this.password.getEditText()).setEnabled(false);

        selectCountry.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Constants.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CustomToast.showSuccess(this, getString(R.string.account_created_successfully), Toast.LENGTH_SHORT);

                createUserRef(firstName, lastName).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "registerUser: task1 was successful");
                    } else {
                        Log.e(TAG, "registerUser: task1 failed");
                    }
                });

                Objects.requireNonNull(Constants.auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        CustomToast.showInfo(this, getString(R.string.confirmation_email_sent_to) + " " + Constants.auth.getCurrentUser().getEmail(), Toast.LENGTH_LONG);
                    } else {
                        CustomToast.showError(this, getString(R.string.confirmation_email_sent_to_error) + " " + Constants.auth.getCurrentUser().getEmail() + ". " + getString(R.string.try_again_later), Toast.LENGTH_LONG);
                    }
                });

                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.putExtra(Constants.ACTIVITY_KEY, getClass().getSimpleName());

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthUserCollisionException e) {
                    SignUpActivity.this.email.setError(getString(R.string.email_address_in_use));

                    proceed.setVisibility(View.VISIBLE);

                    enableTextView(existingAccount, R.color.colorTextBlack);
                    enableFABButton(back);

                    this.firstName.getEditText().setEnabled(true);
                    this.lastName.getEditText().setEnabled(true);
                    this.email.getEditText().setEnabled(true);
                    this.password.getEditText().setEnabled(true);

                    selectCountry.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    CustomToast.showError(this, getString(R.string.account_not_created_successfully), Toast.LENGTH_LONG);

                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    intent.putExtra(Constants.ACTIVITY_KEY, getClass().getSimpleName());

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });
    }

    private Task<Object> createUserRef(String firstName, String lastName) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.TZ, selectedTimeZone);
        data.put(Constants.ISO, selectedTimeZoneISO);
        data.put(Constants.FIRST_NAME, firstName);
        data.put(Constants.LAST_NAME, lastName);

        return FirebaseFunctions.getInstance("europe-west1")
                .getHttpsCallable("create_user_ref")
                .call(data)
                .continueWith(task -> Objects.requireNonNull(task.getResult()).getData());
    }

    private void selectCountryDialog() {
        SelectCountryDialog dialog = new SelectCountryDialog();
        dialog.show(getSupportFragmentManager(), Constants.SELECT_COUNTRY_DIALOG_TAG);
    }

    private void textWatcherSetup() {
        firstName_TextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        lastName_TextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

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

        Objects.requireNonNull(firstName.getEditText()).addTextChangedListener(firstName_TextWatcher);
        Objects.requireNonNull(lastName.getEditText()).addTextChangedListener(lastName_TextWatcher);
        Objects.requireNonNull(email.getEditText()).addTextChangedListener(emailTextWatcher);
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(passwordTextWatcher);
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
        Objects.requireNonNull(firstName.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        Objects.requireNonNull(lastName.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
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

    public void setButtonClicked(boolean buttonClicked) { this.buttonClicked = buttonClicked; }

    public boolean isToastShowing() { return toastShowing; }

    public void setToastShowing(boolean toastShowing) { this.toastShowing = toastShowing; }

    public boolean getSelectCountryDialogToastShowing() { return selectCountryDialogToastShowing; }

    public void setSelectCountryDialogToastShowing(boolean selectCountryDialogToastShowing) { this.selectCountryDialogToastShowing = selectCountryDialogToastShowing; }

    @Override
    public void networkAvailable() {
        enableButton(proceed);
    }

    @Override
    public void networkUnavailable() {
        disableButton(proceed);
    }

    @Override
    public void onCountrySelected(int image, String information, String timeZone, String iso) {
        ImageView flag = selectCountry.findViewById(R.id.flag_ImageView);
        TextView country = selectCountry.findViewById(R.id.country_TextView);

        selectedTimeZone = timeZone;
        selectedTimeZoneISO = iso;

        flag.setImageResource(image);
        country.setText(information);

        if (information.equals(getString(R.string.select_country))) {
            country.setTextColor(ContextCompat.getColor(this, R.color.colorHint));
        } else {
            country.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlack));
        }
    }
}
