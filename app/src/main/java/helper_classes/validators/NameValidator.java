package helper_classes.validators;

import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

import constants.Constants;
import vz.apps.dailyevents.R;

public class NameValidator {

    public static boolean validateName(TextInputLayout name, String Name, Context context) {
        if (Name.isEmpty()) {
            name.setError(context.getString(R.string.empty_field_error_message));
            return false;
        } else {
            if (Name.length() > Constants.MAX_NAME_LENGTH) {
                name.setError(context.getString(R.string.max_name_length_error_message, Constants.MAX_NAME_LENGTH));
                return false;
            } else {
                name.setError(null);
                return true;
            }
        }
    }
}
