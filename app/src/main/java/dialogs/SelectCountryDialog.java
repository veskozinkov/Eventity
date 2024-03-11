package dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.wave.MultiWaveHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import constants.Constants;
import helper_classes.Country;
import helper_classes.CustomToast;
import helper_classes.DeviceCharacteristics;
import helper_classes.KeyboardUtils;
import helper_classes.ManageTitlePosition;
import helper_classes.adapters.CountryListAdapter;
import helper_classes.comparators.CountryNameComparator;
import helper_classes.scale_layout.ScaledLayoutVariables;
import helper_classes.time_zones.TimeZoneAbb;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.R;
import vz.apps.dailyevents.SignUpActivity;

public class SelectCountryDialog extends DialogFragment {

    private static final String TAG = "SelectTimeZoneDialog";

    private TextView title;
    private EditText search;
    private ListView countries;
    private ProgressBar progressBar;
    private CountryListAdapter adapter;
    private DisplayMetrics metrics;
    private Window dialogWindow;
    private MultiWaveHeader waveHeader;
    private FloatingActionButton back;
    private View whiteView;
    private boolean toastShowing = false;

    private boolean dialogAnimationFlag = true;
    private int listViewState = 1;
    private int listViewPosition = 0;
    private int listViewY = 0;
    private String listViewSearch = "";

    private TextWatcher filter;
    private KeyboardUtils.SoftKeyboardToggleListener keyboardListener;

    public interface CountrySelect {
        void onCountrySelected(int image, String information, String timeZone, String iso);
    }

    private CountrySelect countrySelect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_country_dialog, container, false);

        title = view.findViewById(R.id.title_TextView);
        search = view.findViewById(R.id.search_EditText);
        countries = view.findViewById(R.id.country_ListView);
        progressBar = view.findViewById(R.id.progressBar);
        metrics = getResources().getDisplayMetrics();
        dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        waveHeader = view.findViewById(R.id.waveHeader);
        back = view.findViewById(R.id.back_FAB);
        whiteView = view.findViewById(R.id.white_View);

        waveHeader.setGradientAngle(Constants.GRADIENT_ANGLE);
        waveHeader.setWaveHeight(ScaledLayoutVariables.WAVE_HEADER_HEIGHT);

        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScaledLayoutVariables.TITLE_TEXT_SIZE);
        scaleFABs();
        setupCountriesView();
        search.setPadding(ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD, ScaledLayoutVariables.EDIT_TEXT_PAD);
        hideSystemUI();

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) progressBar.getLayoutParams();
        layoutParams.width = ScaledLayoutVariables.SCD_PROGRESS_BAR_SIZE;
        layoutParams.height = ScaledLayoutVariables.SCD_PROGRESS_BAR_SIZE;
        progressBar.setLayoutParams(layoutParams);

        if (requireActivity() instanceof SignUpActivity) {
            ((SignUpActivity) requireActivity()).removeKeyboardListener();
        }

        back.setOnClickListener(v -> {
            if (listViewState == 1) {
                if (requireActivity() instanceof AccountSettingsActivity && Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload();
                }

                getDialog().dismiss();
            }
            else {
                if (listViewState == 2) {
                    whiteView.setVisibility(View.VISIBLE);
                    KeyboardUtils.forceCloseKeyboard(v);
                    setupCountriesView();
                }
            }
        });

        countries.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (adapter.getItem(i).getTimeZonesAbbs() != null) {
                if (requireActivity() instanceof SignUpActivity) {
                    manageOnItemClick(view1, i);
                } else {
                    if (requireActivity() instanceof AccountSettingsActivity) {
                        if (((AccountSettingsActivity) requireActivity()).getNetwork()) {
                            manageOnItemClick(view1, i);
                        } else {
                            if (!toastShowing) {
                                toastShowing = true;

                                CustomToast.showError(requireActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT);
                                new Handler().postDelayed(() -> toastShowing = false, Constants.TOAST_SHORT_DURATION);
                            }
                        }
                    }
                }
            } else {
                countrySelect.onCountrySelected(adapter.getItem(i).getFlag(), adapter.getItem(i).getName(), null, null);
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        keyboardListener = isVisible -> {
            if (isVisible) showSystemUI();
            else { hideSystemUI(); }
        };

        if (requireActivity() instanceof AccountSettingsActivity) {
            if (!((AccountSettingsActivity) requireActivity()).getSelectCountryDialogToastShowing()) {
                Activity activity = requireActivity();
                ((AccountSettingsActivity) requireActivity()).setSelectCountryDialogToastShowing(true);

                CustomToast.showWarning(requireActivity(), getString(R.string.select_country_dialog_warning_message), Toast.LENGTH_LONG);
                new Handler().postDelayed(() -> ((AccountSettingsActivity) activity).setSelectCountryDialogToastShowing(false), Constants.TOAST_LONG_DURATION);
            }
        } else {
            if (requireActivity() instanceof SignUpActivity) {
                if (!((SignUpActivity) requireActivity()).getSelectCountryDialogToastShowing()) {
                    Activity activity = requireActivity();
                    ((SignUpActivity) requireActivity()).setSelectCountryDialogToastShowing(true);

                    CustomToast.showWarning(requireActivity(), getString(R.string.select_country_dialog_warning_message), Toast.LENGTH_LONG);
                    new Handler().postDelayed(() -> ((SignUpActivity) activity).setSelectCountryDialogToastShowing(false), Constants.TOAST_LONG_DURATION);
                }
            }
        }

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

        if (requireActivity() instanceof AccountSettingsActivity) ((AccountSettingsActivity) requireActivity()).setButtonClicked(false);
        else {
            if (requireActivity() instanceof SignUpActivity) ((SignUpActivity) requireActivity()).setButtonClicked(false);
        }

        search.removeTextChangedListener(filter);
        KeyboardUtils.removeKeyboardToggleListener(keyboardListener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            countrySelect = (CountrySelect) requireActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (requireActivity() instanceof SignUpActivity) {
            ((SignUpActivity) requireActivity()).addKeyboardListener();
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

    private void setupCountriesView() {
        ArrayList<Country> countryListCopy = new ArrayList<>(Constants.getCountryList(requireActivity()));
        countryListCopy.sort(new CountryNameComparator(getActivity()));

        adapter = new CountryListAdapter(requireActivity(), R.layout.country_list_adapter_view, countryListCopy);

        countries.setScrollingCacheEnabled(false);
        countries.setAdapter(adapter);

        search.setText(listViewSearch);
        search.clearFocus();
        countries.setSelectionFromTop(listViewPosition, listViewY);

        listViewState = 1;
        listViewPosition = 0;
        listViewY = 0;
        listViewSearch = "";
    }

    private void manageOnItemClick(View view, int i) {
        if (adapter.getItem(i).getTimeZonesAbbs().size() > 1) {
            ArrayList<Country> subItems = new ArrayList<>();

            for (int j = 0; j < adapter.getItem(i).getTimeZonesAbbs().size(); j++) {
                subItems.add(new Country(adapter.getItem(i).getFlag(), adapter.getItem(i).getTimeZonesAbbs().get(j).getAbbreviation(), new ArrayList<>(Collections.singletonList(new TimeZoneAbb(adapter.getItem(i).getTimeZonesAbbs().get(j).getTimeZone(), adapter.getItem(i).getName())))));
            }

            listViewSearch = search.getText().toString();
            search.getText().clear();
            search.clearFocus();

            KeyboardUtils.forceCloseKeyboard(view);
            subItems.sort(new CountryNameComparator(requireActivity()));

            listViewState = 2;
            listViewPosition = countries.getFirstVisiblePosition();
            View v = countries.getChildAt(0);
            listViewY = (v == null) ? 0 : (v.getTop() - countries.getPaddingTop());

            adapter = new CountryListAdapter(requireActivity(), R.layout.country_list_adapter_view, subItems);
            countries.setAdapter(adapter);
        } else {
            countries.setVisibility(View.INVISIBLE);
            disableFABButton(back);
            search.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            if (adapter.getItem(i).getTimeZonesAbbs().get(0).getAbbreviation() == null) {
                if (requireActivity() instanceof AccountSettingsActivity && Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) {
                            countrySelect.onCountrySelected(adapter.getItem(i).getFlag(), adapter.getItem(i).getName(), adapter.getItem(i).getTimeZonesAbbs().get(0).getTimeZone(), getResources().getResourceName(adapter.getItem(i).getFlag()).substring(getResources().getResourceName(adapter.getItem(i).getFlag()).lastIndexOf("/") + 1));
                        }
                    });
                } else {
                    if (requireActivity() instanceof SignUpActivity) {
                        countrySelect.onCountrySelected(adapter.getItem(i).getFlag(), adapter.getItem(i).getName(), adapter.getItem(i).getTimeZonesAbbs().get(0).getTimeZone(), getResources().getResourceName(adapter.getItem(i).getFlag()).substring(getResources().getResourceName(adapter.getItem(i).getFlag()).lastIndexOf("/") + 1));
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }
            } else {
                if (requireActivity() instanceof AccountSettingsActivity && Constants.auth.getCurrentUser() != null) {
                    Constants.auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                        if (Constants.auth.getCurrentUser() != null) {
                            countrySelect.onCountrySelected(adapter.getItem(i).getFlag(), adapter.getItem(i).getTimeZonesAbbs().get(0).getAbbreviation() + ", " + adapter.getItem(i).getName(), adapter.getItem(i).getTimeZonesAbbs().get(0).getTimeZone(), getResources().getResourceName(adapter.getItem(i).getFlag()).substring(getResources().getResourceName(adapter.getItem(i).getFlag()).lastIndexOf("/") + 1));
                        }
                    });
                } else {
                    if (requireActivity() instanceof SignUpActivity) {
                        countrySelect.onCountrySelected(adapter.getItem(i).getFlag(), adapter.getItem(i).getTimeZonesAbbs().get(0).getAbbreviation() + ", " + adapter.getItem(i).getName(), adapter.getItem(i).getTimeZonesAbbs().get(0).getTimeZone(), getResources().getResourceName(adapter.getItem(i).getFlag()).substring(getResources().getResourceName(adapter.getItem(i).getFlag()).lastIndexOf("/") + 1));
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }
            }
        }
    }

    private void textWatcherSetup() {
        filter = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence, count -> whiteView.setVisibility(View.GONE));
                countries.smoothScrollToPosition(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                
            }
        };

        search.addTextChangedListener(filter);
    }

    private ShapeDrawable generateBackgroundDrawable() {
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        int backgroundTopPadding = ScaledLayoutVariables.SCD_BACKGROUND_TOP_PAD;

        if (DeviceCharacteristics.hasNotch(requireActivity())) backgroundTopPadding += DeviceCharacteristics.getStatusBarHeight(requireActivity());

        background.getPaint().setColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
        background.setAlpha(0);
        background.setPadding(0, backgroundTopPadding, 0, 0);

        ManageTitlePosition.manageDialogTitle(requireActivity(), waveHeader, title, R.fraction.scd_guideline1, getHeightDivisionNumber(), backgroundTopPadding);

        return background;
    }

    private void scaleFABs() {
        back.setCustomSize(ScaledLayoutVariables.FAB_CUSTOM_SIZE);
        back.setMaxImageSize(ScaledLayoutVariables.ICON_SIZE);
    }

    private float getHeightDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.scd_h_division_number, typedValue, true);

        return typedValue.getFloat();
    }

    private float getWidthDivisionNumber() {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.fraction.dialogs_w_division_number, typedValue, true);

        return typedValue.getFloat();
    }

    public void disableFABButton(FloatingActionButton button) {
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDisabled)));
    }
}