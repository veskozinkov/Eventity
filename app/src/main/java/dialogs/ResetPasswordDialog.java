package dialogs;

import android.content.DialogInterface;
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
import com.scwang.wave.MultiWaveHeader;

import java.util.Objects;

import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.EmailValidator;
import vz.apps.dailyevents.R;
import vz.apps.dailyevents.SignInActivity;

public class ResetPasswordDialog extends DialogFragment {

    private TextView title;
    private TextInputLayout email;
    private Button cancel;
    private Button send;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;

    private TextWatcher emailTextWatcher;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.password_reset_dialog, container, false);

        email = view.findViewById(R.id.email_TIL);
        cancel = view.findViewById(R.id.cancel_Button);
        send = view.findViewById(R.id.send_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        progressBar = view.findViewById(R.id.progressBar);
        waveHeader = view.findViewById(R.id.waveHeader);
        title = view.findViewById(R.id.title_TextView);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        scaleButtons();
        Objects.requireNonNull(email.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        setTitle();
        hideSystemUI();

        ((SignInActivity) requireActivity()).removeKeyboardListener();

        cancel.setOnClickListener(v -> getDialog().dismiss());

        send.setOnClickListener(v -> {
            String Email = Objects.requireNonNull(email.getEditText()).getText().toString().trim();

            if (EmailValidator.validateEmail(email, Email, getActivity())) {
                sendPasswordResetEmail(Email);
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
        ((SignInActivity) requireActivity()).setButtonClicked(false);

        Objects.requireNonNull(email.getEditText()).removeTextChangedListener(emailTextWatcher);
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((SignInActivity) requireActivity()).addKeyboardListener();
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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double density = displayMetrics.density;
        int pxWidth = DeviceCharacteristics.getWidthPx(requireActivity());

        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw600dp) {
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

    private void sendPasswordResetEmail(String Email) {
        cancel.setVisibility(View.INVISIBLE);
        send.setVisibility(View.INVISIBLE);

        Objects.requireNonNull(email.getEditText()).setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        Constants.auth.sendPasswordResetEmail(Email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CustomToast.showSuccess(requireActivity(), getString(R.string.email_sent_successfully), Toast.LENGTH_SHORT);
            } else {
                CustomToast.showError(requireActivity(), getString(R.string.email_not_sent_successfully), Toast.LENGTH_LONG);
            }

            Objects.requireNonNull(getDialog()).dismiss();
        });
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.PRD_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.prd_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private void scaleButtons() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) send.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        send.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        send.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) cancel.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        cancel.setLayoutParams(layoutParams);
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.prd_h_division_number, typedValue, true);

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

    public Button getSend() {
        return send;
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

        Objects.requireNonNull(email.getEditText()).addTextChangedListener(emailTextWatcher);
    }
}
