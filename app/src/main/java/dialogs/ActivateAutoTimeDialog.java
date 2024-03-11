package dialogs;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.scwang.wave.MultiWaveHeader;

import java.util.Objects;

import constants.Constants;
import helper_classes.DeviceCharacteristics;
import helper_classes.ManageTitlePosition;
import helper_classes.scale_layout.ScaledLayoutVariables;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.MainActivity;
import vz.apps.dailyevents.R;
import vz.apps.dailyevents.SignInActivity;

public class ActivateAutoTimeDialog extends DialogFragment {

    private TextView title;
    private Button turnOn;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private MultiWaveHeader waveHeader;
    private boolean dialogAnimationFlag = true;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activate_auto_time_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        turnOn = view.findViewById(R.id.turnOn_Button);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        waveHeader = view.findViewById(R.id.waveHeader);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.AATD_TITLE_TEXT_SIZE);
        scaleButtons();
        hideSystemUI();

        turnOn.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)));

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

        if (requireActivity() instanceof MainActivity) ((MainActivity) requireActivity()).setButtonClicked(false);
        else {
            if (requireActivity() instanceof SignInActivity) ((SignInActivity) requireActivity()).setButtonClicked(false);
            else {
                if (requireActivity() instanceof AccountSettingsActivity) ((AccountSettingsActivity) requireActivity()).setButtonClicked(false);
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

    private void scaleButtons() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) turnOn.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.BUTTONS_HEIGHT;
        turnOn.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.BUTTONS_TEXT_SIZE);
        turnOn.setLayoutParams(layoutParams);
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.AATD_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.aatd_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.aatd_h_division_number, typedValue, true);

        return typedValue.getFloat();
    }

    private float getWidthDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.dialogs_w_division_number, typedValue, true);

        return typedValue.getFloat();
    }
}
