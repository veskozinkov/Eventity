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

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;
import com.scwang.wave.MultiWaveHeader;

import java.util.Objects;

import constants.Constants;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.notifications.NotificationHelper;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.PasswordValidator;
import helper_classes.validators.SundayDateTimeValidator;
import io.paperdb.Paper;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.R;

public class DeleteAllEventsDialog extends DialogFragment {

    private TextView title;
    private TextInputLayout password;
    private Button cancel;
    private Button delete;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;

    private TextWatcher emailTextWatcher;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_all_events_dialog, container, false);

        password = view.findViewById(R.id.password_TIL);
        cancel = view.findViewById(R.id.cancel_Button);
        delete = view.findViewById(R.id.delete_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        progressBar = view.findViewById(R.id.progressBar);
        waveHeader = view.findViewById(R.id.waveHeader);
        title = view.findViewById(R.id.title_TextView);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        scaleButtons();
        Objects.requireNonNull(password.getEditText()).setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        setTitle();
        hideSystemUI();

        cancel.setOnClickListener(v -> getDialog().dismiss());

        delete.setOnClickListener(v -> {
            String Password = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

            if (PasswordValidator.isEmpty(password, Password, requireActivity())) {
                if (SundayDateTimeValidator.validateDateAndTime()) {
                    cancel.setVisibility(View.INVISIBLE);
                    delete.setVisibility(View.INVISIBLE);

                    Objects.requireNonNull(password.getEditText()).setEnabled(false);

                    progressBar.setVisibility(View.VISIBLE);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) deleteAllSavedEvents(Password);
                        });
                    }
                } else { password.setError(getString(R.string.try_again_in_a_few_minutes)); }
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

        Objects.requireNonNull(password.getEditText()).removeTextChangedListener(emailTextWatcher);
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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double density = displayMetrics.density;
        int pxWidth = DeviceCharacteristics.getWidthPx(requireActivity());

        if ((double) Math.round(pxWidth / density * 100) / 100 >= Constants.sw360dp && (double) Math.round(pxWidth / density * 100) / 100 < Constants.sw600dp) {
            if (Constants.DEFAULT_LANG.equals(Constants.EN_LANG)) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DAED_TITLE_TEXT_SIZE);
            } else {
                if (Constants.DEFAULT_LANG.equals(Constants.BG_LANG)) {
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DAED_TITLE_TEXT_SIZE - ScaledLayoutVariables.BG_DIALOG_TITLE_TEXT_SIZE_SUB_NUM);
                }
            }
        } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DAED_TITLE_TEXT_SIZE);
        }
    }

    private void deleteAllSavedEvents(String Password) {
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(Constants.auth.getCurrentUser()).getEmail()), Password);

        Constants.auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteAllUserEvents().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        NotificationHelper.cancelAllNotifications(requireActivity(), Constants.auth.getCurrentUser().getUid());

                        destroyWeekBook(Constants.WEEK0);
                        destroyWeekBook(Constants.WEEK1);
                        destroyWeekBook(Constants.WEEK2);
                        destroyWeekBook(Constants.WEEK3);
                        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.INFO).destroy();
                        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + Constants.OTHER_EVENTS + "." + Constants.FLAGS).destroy();

                        Paper.book(Constants.ALL_EVENTS_DELETED).write(Constants.DELETED, 1);
                        CustomToast.showSuccess(requireActivity(), getString(R.string.all_saved_events_deleted_successfully), Toast.LENGTH_LONG);
                    } else {
                        CustomToast.showError(requireActivity(), getString(R.string.all_saved_events_not_deleted_successfully), Toast.LENGTH_LONG);
                        Constants.auth.signOut();
                    }

                    dismiss();
                });
            } else {
                password.setError(getString(R.string.wrong_password));

                cancel.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                Objects.requireNonNull(password.getEditText()).setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Task<Object> deleteAllUserEvents() {
        return FirebaseFunctions.getInstance("europe-west1")
                .getHttpsCallable("delete_all_user_events")
                .call()
                .continueWith(task -> Objects.requireNonNull(task.getResult()).getData());
    }

    private void destroyWeekBook(String week) {
        Paper.book(Objects.requireNonNull(Constants.auth.getCurrentUser()).getUid() + "." + week + "." + Constants.MONDAY).destroy();
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Constants.TUESDAY).destroy();
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Constants.WEDNESDAY).destroy();
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Constants.THURSDAY).destroy();
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Constants.FRIDAY).destroy();
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Constants.SATURDAY).destroy();
        Paper.book(Constants.auth.getCurrentUser().getUid() + "." + week + "." + Constants.SUNDAY).destroy();
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.DAED_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.daed_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

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

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.daed_h_division_number, typedValue, true);

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

    private void textWatcherSetup() {
        emailTextWatcher = new TextWatcher() {
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

        Objects.requireNonNull(password.getEditText()).addTextChangedListener(emailTextWatcher);
    }
}
