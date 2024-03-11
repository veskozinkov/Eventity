package helper_classes.validators;

import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import constants.Constants;
import io.paperdb.Paper;
import vz.apps.dailyevents.R;

public class AddChangeInfoValidator {

    public static boolean validateInfo(TextInputLayout information, String Information, Context context) {
        if (Information.isEmpty()) {
            information.setError(context.getString(R.string.empty_field_error_message));
            return false;
        } else {
            int infoLimit = Constants.MAX_INFO_LENGTH;
            if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.CHAR_INC)) == 1) infoLimit = Constants.MAX_INCREASED_INFO_LENGTH;

            if (Information.length() > infoLimit) {
                information.setError(context.getString(R.string.max_info_length_error_message, infoLimit));
                return false;
            } else {
                if (!SundayDateTimeValidator.validateDateAndTime()) {
                    information.setError(context.getString(R.string.try_again_in_a_few_minutes));
                    return false;
                } else {
                    information.setError(null);
                    return true;
                }
            }
        }
    }
}
