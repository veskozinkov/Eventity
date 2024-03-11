package helper_classes.validators;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.TimeZone;

import constants.Constants;
import helper_classes.CustomToast;
import vz.apps.dailyevents.R;
import vz.apps.dailyevents.SignUpActivity;

public class TimeZoneISOValidator {

    public static boolean validate(String timeZone, String iso, Context context) {
        if (timeZone == null || iso == null) {
            if (!((SignUpActivity) context).isToastShowing()) {
                ((SignUpActivity) context).setToastShowing(true);

                CustomToast.showInfo(context, context.getString(R.string.select_country_info_message), Toast.LENGTH_SHORT);
                new Handler().postDelayed(() -> ((SignUpActivity) context).setToastShowing(false), Constants.TOAST_SHORT_DURATION);
            }

            return false;
        } else {
            String defaultTimeZone = TimeZone.getDefault().getID();
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));

            if (!SundayDateTimeValidator.validateDateAndTime()) {
                if (!((SignUpActivity) context).isToastShowing()) {
                    ((SignUpActivity) context).setToastShowing(true);

                    CustomToast.showWarning(context, context.getString(R.string.try_again_in_a_few_minutes), Toast.LENGTH_LONG);
                    new Handler().postDelayed(() -> ((SignUpActivity) context).setToastShowing(false), Constants.TOAST_LONG_DURATION);
                }

                TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
                return false;
            } else {
                TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
                return true;
            }
        }
    }
}
