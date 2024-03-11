package dialogs;

import android.content.res.ColorStateList;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.scwang.wave.MultiWaveHeader;

import java.util.Objects;

import application_class.ProjectM;
import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.ConfirmationWordValidator;
import helper_classes.validators.PasswordValidator;
import helper_classes.validators.SundayDateTimeValidator;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;

public class DeleteAccountDialog extends DialogFragment {

    private TextView title;
    private TextInputLayout confirmation;
    private TextInputLayout password;
    private Button cancel;
    private Button delete;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;

    private TextWatcher confirmationTextWatcher;
    private TextWatcher passwordTextWatcher;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_account_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        confirmation = view.findViewById(R.id.confirmation_TIL);
        password = view.findViewById(R.id.password_TIL);
        cancel = view.findViewById(R.id.cancel_Button);
        delete = view.findViewById(R.id.delete_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        progressBar = view.findViewById(R.id.progressBar);
        waveHeader = view.findViewById(R.id.waveHeader);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        scaleButtons();
        scaleEditTexts();
        setTitle();
        hideSystemUI();

        cancel.setOnClickListener(v -> {
            if (Constants.auth.getCurrentUser() != null) {
                Constants.auth.getCurrentUser().reload();
            }

            getDialog().dismiss();
        });

        delete.setOnClickListener(v -> {
            String confirmationWord = Objects.requireNonNull(confirmation.getEditText()).getText().toString().toLowerCase().trim();
            String Password = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

            if (ConfirmationWordValidator.validateConfirmWord(confirmation, confirmationWord, getActivity()) & PasswordValidator.isEmpty(password, Password, getActivity())) {
                if (SundayDateTimeValidator.validateDateAndTime()) {
                    cancel.setVisibility(View.INVISIBLE);
                    delete.setVisibility(View.INVISIBLE);

                    Objects.requireNonNull(confirmation.getEditText()).setEnabled(false);
                    Objects.requireNonNull(password.getEditText()).setEnabled(false);

                    progressBar.setVisibility(View.VISIBLE);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) deleteUserAccount(Password);
                        });
                    }
                } else {
                    confirmation.setError(getString(R.string.try_again_in_a_few_minutes));
                    password.setError(getString(R.string.try_again_in_a_few_minutes));
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
        ((AccountSettingsActivity) requireActivity()).setButtonClicked(false);

        Objects.requireNonNull(confirmation.getEditText()).removeTextChangedListener(confirmationTextWatcher);
        Objects.requireNonNull(password.getEditText()).removeTextChangedListener(passwordTextWatcher);
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

    private void setTitle() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double density = displayMetrics.density;
        int pxWidth = DeviceCharacteristics.getWidthPx(requireActivity());

        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw600dp) {
            if (Constants.DEFAULT_LANG.equals(Constants.EN_LANG)) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);
            } else {
                if (Constants.DEFAULT_LANG.equals(Constants.BG_LANG)) {
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE - ScaledLayoutVariables.BG_DIALOG_TITLE_TEXT_SIZE_SUB_NUM);
                }
            }
        } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);
        }
    }

    private void textWatcherSetup() {
        confirmationTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmation.setError(null);
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

        Objects.requireNonNull(confirmation.getEditText()).addTextChangedListener(confirmationTextWatcher);
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(passwordTextWatcher);
    }

    private void deleteUserAccount(String Password) {
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(Constants.auth.getCurrentUser()).getEmail()), Password);

        Constants.auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AccountSettingsActivity.deletingAccount = true;

                Objects.requireNonNull(Constants.auth.getCurrentUser()).delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        CustomToast.showSuccess(requireActivity(), getString(R.string.account_deleted_successfully), Toast.LENGTH_SHORT);
                        MainActivity.deletedAccount = true;

                        ((AccountSettingsActivity) requireActivity()).getSplashScreen_().setVisibility(View.VISIBLE);
                        ((MainActivity) ((ProjectM) requireActivity().getApplicationContext()).getMainActivityContext()).setSplashScreenVisibility(View.VISIBLE);

                        requireActivity().finish();
                        requireActivity().overridePendingTransition(0, 0);
                    } else {
                        CustomToast.showError(requireActivity(), getString(R.string.account_not_deleted_successfully), Toast.LENGTH_LONG);
                    }

                    Objects.requireNonNull(getDialog()).dismiss();
                });
            } else {
                password.setError(getString(R.string.wrong_password));

                cancel.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);

                Objects.requireNonNull(confirmation.getEditText()).setEnabled(true);
                Objects.requireNonNull(password.getEditText()).setEnabled(true);

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.DAD_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.dad_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private void scaleButtons() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) delete.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        delete.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        delete.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) cancel.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        cancel.setLayoutParams(layoutParams);
    }

    private void scaleEditTexts() {
        Objects.requireNonNull(confirmation.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        Objects.requireNonNull(password.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.dad_h_division_number, typedValue, true);

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

    public Button getDelete() {
        return delete;
    }
}
