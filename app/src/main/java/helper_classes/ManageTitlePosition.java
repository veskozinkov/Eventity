package helper_classes;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.scwang.wave.MultiWaveHeader;

import constants.Constants;
import vz.apps.dailyevents.R;

public class ManageTitlePosition {

    public static void manageActivitySingleTitle(Context context, MultiWaveHeader waveHeader, TextView title) {
        int pxStatusBarHeight = DeviceCharacteristics.getStatusBarHeight(context);
        int pxTitleHeight = getTextViewHeight(title, DeviceCharacteristics.getWidthPx(context), 1);
        float pxWaveHeaderOccupiedSpace = DeviceCharacteristics.getHeightPx(context) * getFraction(context, R.fraction.activity_title_guideline);
        float pxTitleFreeSpace = pxWaveHeaderOccupiedSpace - pxStatusBarHeight - (waveHeader.getWaveHeight() * 2) + (pxTitleHeight * Constants.ACTIVITY_SINGLE_TITLE_PER);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) title.getLayoutParams();

        if (DeviceCharacteristics.hasNotch(context)) {
            if (pxTitleFreeSpace >= pxTitleHeight) {
                layoutParams.verticalBias = (pxStatusBarHeight - (pxTitleHeight * Constants.ACTIVITY_SINGLE_TITLE_PER) + (pxTitleFreeSpace / 2) - (pxTitleHeight / 2f)) / (pxWaveHeaderOccupiedSpace - pxTitleHeight);
            } else {
                pxTitleFreeSpace = pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2) - (pxTitleHeight * Constants.ACTIVITY_SINGLE_TITLE_PER);
                layoutParams.verticalBias = ((pxTitleFreeSpace / 2) - (pxTitleHeight / 2f)) / (pxWaveHeaderOccupiedSpace - pxTitleHeight);
            }
        } else {
            pxTitleFreeSpace = pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2) - (pxTitleHeight * Constants.ACTIVITY_SINGLE_TITLE_PER);
            layoutParams.verticalBias = ((pxTitleFreeSpace / 2) - (pxTitleHeight / 2f)) / (pxWaveHeaderOccupiedSpace - pxTitleHeight);
        }

        title.setLayoutParams(layoutParams);
    }

    public static void manageFragmentMultiTitle(Context context, MultiWaveHeader waveHeader, TextView title, TextView subtitle, Guideline titleSeparationGuideline) {
        int pxStatusBarHeight = DeviceCharacteristics.getStatusBarHeight(context);
        int pxTitleHeight = getTextViewHeight(title, DeviceCharacteristics.getWidthPx(context), 1);
        int pxSubtitleHeight = getTextViewHeight(subtitle, DeviceCharacteristics.getWidthPx(context), 1);
        float pxWaveHeaderOccupiedSpace = DeviceCharacteristics.getHeightPx(context) * getFraction(context, R.fraction.activity_title_guideline);
        float pxTitleFreeSpace = pxWaveHeaderOccupiedSpace - pxStatusBarHeight - (waveHeader.getWaveHeight() * 2) + (pxTitleHeight * Constants.FRAGMENT_MULTI_TITLE_PER);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) titleSeparationGuideline.getLayoutParams();

        if (DeviceCharacteristics.hasNotch(context)) {
            if (pxTitleFreeSpace >= pxTitleHeight + pxSubtitleHeight) {
                layoutParams.guidePercent = ((pxStatusBarHeight - (pxTitleHeight * Constants.FRAGMENT_MULTI_TITLE_PER) + (pxTitleFreeSpace / 2)) + (pxTitleHeight - ((pxTitleHeight + pxSubtitleHeight) / 2f))) / (DeviceCharacteristics.getHeightPx(context) * getFraction(context, R.fraction.ma_guideline1));
            } else {
                pxTitleFreeSpace = pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2) - (pxTitleHeight * Constants.FRAGMENT_MULTI_TITLE_PER);
                layoutParams.guidePercent = ((pxTitleFreeSpace / 2) + (pxTitleHeight - ((pxTitleHeight + pxSubtitleHeight) / 2f))) / (DeviceCharacteristics.getHeightPx(context) * getFraction(context, R.fraction.ma_guideline1));
            }
        } else {
            pxTitleFreeSpace = pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2) - (pxTitleHeight * Constants.FRAGMENT_MULTI_TITLE_PER);
            layoutParams.guidePercent = ((pxTitleFreeSpace / 2) + (pxTitleHeight - ((pxTitleHeight + pxSubtitleHeight) / 2f))) / (DeviceCharacteristics.getHeightPx(context) * getFraction(context, R.fraction.ma_guideline1));
        }

        titleSeparationGuideline.setLayoutParams(layoutParams);
    }

    public static void manageDialogTitle(Context context, MultiWaveHeader waveHeader, TextView title, int guidelineID, float heightDivisionNumber, int backgroundTopPadding) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxTitleHeight = getTextViewHeight(title, DeviceCharacteristics.getWidthPx(context), getFraction(context, R.fraction.dialogs_w_division_number));
        int titleNumberOfLines = getTextViewNumberOfLines(title, DeviceCharacteristics.getWidthPx(context), getFraction(context, R.fraction.dialogs_w_division_number));
        float pxWaveHeaderOccupiedSpace;

        if (DeviceCharacteristics.hasNotch(context)) {
            int pxStatusBarHeight = DeviceCharacteristics.getStatusBarHeight(context);
            pxWaveHeaderOccupiedSpace = (((int) (displayMetrics.heightPixels / heightDivisionNumber) + pxStatusBarHeight) - backgroundTopPadding) * getFraction(context, guidelineID);
        } else {
            pxWaveHeaderOccupiedSpace = ((int) (displayMetrics.heightPixels / heightDivisionNumber) - backgroundTopPadding) * getFraction(context, guidelineID);
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) title.getLayoutParams();

        if (titleNumberOfLines == 1) {
            layoutParams.verticalBias = (((pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2) - (pxTitleHeight * Constants.DIALOG_SINGLE_TITLE_PER)) / 2) - (pxTitleHeight / 2f)) / (pxWaveHeaderOccupiedSpace - pxTitleHeight);
        } else {
            if (titleNumberOfLines >= 2) {
                layoutParams.verticalBias = (((pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2) - (pxTitleHeight * Constants.DIALOG_MULTI_TITLE_PER)) / 2) - (pxTitleHeight / 2f)) / (pxWaveHeaderOccupiedSpace - pxTitleHeight);
            } else {
                if (titleNumberOfLines == 0) {
                    layoutParams.verticalBias = (((pxWaveHeaderOccupiedSpace - (waveHeader.getWaveHeight() * 2)) / 2) - (pxTitleHeight / 2f)) / (pxWaveHeaderOccupiedSpace - pxTitleHeight);
                }
            }
        }

        title.setLayoutParams(layoutParams);
    }

    private static float getFraction(Context context, int id) {
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(id, typedValue, true);

        return typedValue.getFloat();
    }

    private static int getTextViewHeight(TextView textView, int deviceWidth, float divisionNumber) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (deviceWidth / divisionNumber), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);

        return textView.getMeasuredHeight();
    }

    private static int getTextViewNumberOfLines(TextView textView, int deviceWidth, float divisionNumber) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (deviceWidth / divisionNumber), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);

        return textView.getLineCount();
    }
}
