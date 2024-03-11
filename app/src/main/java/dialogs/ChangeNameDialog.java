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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.scwang.wave.MultiWaveHeader;

import java.util.Objects;

import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.NameValidator;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.R;

public class ChangeNameDialog extends DialogFragment {

    private static final String TAG = "ChangeNameDialog";

    private TextView title;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private Button cancel;
    private Button ok;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;

    private TextWatcher firstName_TextWatcher;
    private TextWatcher lastName_TextWatcher;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    public interface NameUpdate {
        void successfullyUpdatedName();
    }

    private NameUpdate nameUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_name_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        firstName = view.findViewById(R.id.firstName_TIL);
        lastName = view.findViewById(R.id.lastName_TIL);
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
            String FirstName = Objects.requireNonNull(firstName.getEditText()).getText().toString().trim();
            String LastName = Objects.requireNonNull(lastName.getEditText()).getText().toString().trim();

            if (NameValidator.validateName(firstName, FirstName, getActivity()) & NameValidator.validateName(lastName, LastName, getActivity())) {
                String name = FirstName + " " + LastName;

                if ((name).equals(Objects.requireNonNull(Constants.auth.getCurrentUser()).getDisplayName())) {
                    Objects.requireNonNull(getDialog()).dismiss();
                } else {
                    cancel.setVisibility(View.INVISIBLE);
                    ok.setVisibility(View.INVISIBLE);

                    Objects.requireNonNull(firstName.getEditText()).setEnabled(false);
                    Objects.requireNonNull(lastName.getEditText()).setEnabled(false);

                    progressBar.setVisibility(View.VISIBLE);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) updateName(FirstName, LastName);
                        });
                    }
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

        Objects.requireNonNull(firstName.getEditText()).removeTextChangedListener(firstName_TextWatcher);
        Objects.requireNonNull(lastName.getEditText()).removeTextChangedListener(lastName_TextWatcher);
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            nameUpdate = (NameUpdate) getActivity();
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

    private void updateName(String FirstName, String LastName) {
        final String name = FirstName + " " + LastName;

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        Objects.requireNonNull(Constants.auth.getCurrentUser()).updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CustomToast.showSuccess(requireActivity(), getString(R.string.name_changed_successfully), Toast.LENGTH_SHORT);
                nameUpdate.successfullyUpdatedName();
            } else {
                CustomToast.showError(requireActivity(), getString(R.string.name_not_changed_successfully), Toast.LENGTH_LONG);
            }

            Objects.requireNonNull(getDialog()).dismiss();
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
        Objects.requireNonNull(firstName.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        Objects.requireNonNull(lastName.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
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

        Objects.requireNonNull(firstName.getEditText()).addTextChangedListener(firstName_TextWatcher);
        Objects.requireNonNull(lastName.getEditText()).addTextChangedListener(lastName_TextWatcher);
    }
}
