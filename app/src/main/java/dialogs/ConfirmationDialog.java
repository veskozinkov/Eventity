package dialogs;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.Fragment;

import com.scwang.wave.MultiWaveHeader;

import java.util.Objects;

import constants.Constants;
import fragments.DayFragment;
import fragments.OtherEventsFragment;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.validators.SundayDateTimeValidator;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;

public class ConfirmationDialog extends DialogFragment {

    private TextView title;
    private Button no;
    private Button yes;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private ProgressBar progressBar;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;
    private boolean buttonClicked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmation_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        no = view.findViewById(R.id.no_Button);
        yes = view.findViewById(R.id.yes_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        progressBar = view.findViewById(R.id.progressBar);
        waveHeader = view.findViewById(R.id.waveHeader);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.DIALOG_TITLE_TEXT_SIZE);
        scaleButtons();
        hideSystemUI();

        if (!(requireActivity() instanceof AccountSettingsActivity) && !((MainActivity) requireActivity()).getNetwork()) {
            disableButton(yes);
        }

        no.setOnClickListener(v -> {
            if (Constants.auth.getCurrentUser() != null) {
                Constants.auth.getCurrentUser().reload();
            }

            getDialog().dismiss();
        });

        yes.setOnClickListener(v -> {
            Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

            if (fragment instanceof DayFragment || fragment instanceof OtherEventsFragment) {
                if (SundayDateTimeValidator.validateDateAndTime()) {
                    no.setVisibility(View.INVISIBLE);
                    yes.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    if (Constants.auth.getCurrentUser() != null) {
                        Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                            if (Constants.auth.getCurrentUser() != null) getParentFragmentManager().setFragmentResult(Constants.CONFIRMATION_DIALOG_TAG, new Bundle());
                        });
                    }
                } else {
                    if (!((MainActivity) requireActivity()).isToastShowing2()) {
                        Activity activity = requireActivity();
                        ((MainActivity) requireActivity()).setToastShowing2(true);

                        CustomToast.showWarning(requireActivity(), getString(R.string.try_again_in_a_few_minutes), Toast.LENGTH_LONG);
                        new Handler().postDelayed(() -> ((MainActivity) activity).setToastShowing2(false), Constants.TOAST_LONG_DURATION);
                    }

                    Objects.requireNonNull(getDialog()).dismiss();
                }
            } else {
                if (!buttonClicked) {
                    buttonClicked = true;

                    getParentFragmentManager().setFragmentResult(Constants.CONFIRMATION_DIALOG_TAG, new Bundle());
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            }
        });

        dialogWindow.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) hideSystemUI();
        });

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

        if (requireActivity() instanceof AccountSettingsActivity) ((AccountSettingsActivity) requireActivity()).setButtonClicked(false);
        else {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).setButtonClicked(false);
                ((MainActivity) requireActivity()).getBlockBottomNavigation().setVisibility(View.GONE);
            }
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

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.CD_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.cd_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private void scaleButtons() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) yes.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        yes.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        yes.setLayoutParams(layoutParams);

        layoutParams = (ConstraintLayout.LayoutParams) no.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        no.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        no.setLayoutParams(layoutParams);
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.cd_h_division_number, typedValue, true);

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

    public Button getYes() {
        return yes;
    }
}
