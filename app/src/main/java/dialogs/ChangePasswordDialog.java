package dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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

import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.PasswordValidator;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.R;

public class ChangePasswordDialog extends DialogFragment {

    private static final String TAG = "ChangeNameDialog";

    private TextView title;
    private TextInputLayout currentPassword;
    private TextInputLayout newPassword;
    private Button cancel;
    private Button ok;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;

    private TextWatcher currentPassword_TextWatcher;
    private TextWatcher newPassword_TextWatcher;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    public interface PasswordUpdate {
        void successfullyUpdatedPassword();
    }

    private PasswordUpdate passwordUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        currentPassword = view.findViewById(R.id.currentPassword_TIL);
        newPassword = view.findViewById(R.id.newPassword_TIL);
        cancel = view.findViewById(R.id.cancel_Button);
        ok = view.findViewById(R.id.ok_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        progressBar = view.findViewById(R.id.progressBar);
        waveHeader = view.findViewById(R.id.waveHeader);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);
        scaleButtons();
        scaleEditTexts();
        hideSystemUI();

        cancel.setOnClickListener(v -> {
            if (Constants.auth.getCurrentUser() != null) {
                Constants.auth.getCurrentUser().reload();
            }

            getDialog().dismiss();
        });

        ok.setOnClickListener(v -> {
            String CurrentPassword = Objects.requireNonNull(currentPassword.getEditText()).getText().toString().trim();
            String NewPassword = Objects.requireNonNull(newPassword.getEditText()).getText().toString().trim();

            if (PasswordValidator.isEmpty(currentPassword, CurrentPassword, getActivity()) & PasswordValidator.validatePassword(newPassword, NewPassword, getActivity())) {
                cancel.setVisibility(View.INVISIBLE);
                ok.setVisibility(View.INVISIBLE);

                Objects.requireNonNull(currentPassword.getEditText()).setEnabled(false);
                Objects.requireNonNull(newPassword.getEditText()).setEnabled(false);

                progressBar.setVisibility(View.VISIBLE);

                if (Constants.auth.getCurrentUser() != null) {
                    Objects.requireNonNull(Constants.auth.getCurrentUser()).reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) updatePassword(CurrentPassword, NewPassword);
                    });
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

        Objects.requireNonNull(currentPassword.getEditText()).removeTextChangedListener(currentPassword_TextWatcher);
        Objects.requireNonNull(newPassword.getEditText()).removeTextChangedListener(newPassword_TextWatcher);
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            passwordUpdate = (PasswordUpdate) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
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

    private void updatePassword(String CurrentPassword, String NewPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(Constants.auth.getCurrentUser()).getEmail()), CurrentPassword);

        Constants.auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!NewPassword.equals(CurrentPassword)) {
                    Constants.auth.getCurrentUser().updatePassword(NewPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            CustomToast.showSuccess(requireActivity(), getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT);
                            passwordUpdate.successfullyUpdatedPassword();
                        } else {
                            CustomToast.showError(requireActivity(), getString(R.string.password_not_changed_successfully), Toast.LENGTH_LONG);
                            Objects.requireNonNull(getDialog()).dismiss();
                        }
                    });
                } else {
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            } else {
                currentPassword.setError(getString(R.string.wrong_password));

                cancel.setVisibility(View.VISIBLE);
                ok.setVisibility(View.VISIBLE);

                Objects.requireNonNull(currentPassword.getEditText()).setEnabled(true);
                Objects.requireNonNull(newPassword.getEditText()).setEnabled(true);

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.CHANGE_DIALOGS_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.change_dialogs_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private void scaleButtons() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ok.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        ok.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        ok.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) cancel.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        cancel.setLayoutParams(layoutParams);
    }

    private void scaleEditTexts() {
        Objects.requireNonNull(currentPassword.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        Objects.requireNonNull(newPassword.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.change_dialogs_h_division_number, typedValue, true);

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

    public Button getOk() {
        return ok;
    }

    private void textWatcherSetup() {
        currentPassword_TextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        newPassword_TextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        Objects.requireNonNull(currentPassword.getEditText()).addTextChangedListener(currentPassword_TextWatcher);
        Objects.requireNonNull(newPassword.getEditText()).addTextChangedListener(newPassword_TextWatcher);
    }
}
