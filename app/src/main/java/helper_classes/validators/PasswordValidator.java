package helper_classes.validators;

import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

import constants.Constants;
import vz.apps.dailyevents.R;

public class PasswordValidator {

    public static boolean isEmpty(TextInputLayout password, String Password, Context context) {
        if (Password.isEmpty()) {
            password.setError(context.getString(R.string.empty_field_error_message));
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public static boolean validatePassword(TextInputLayout password, String Password, Context context) {
        if (Password.isEmpty()) {
            password.setError(context.getString(R.string.empty_field_error_message));
            return false;
        } else {
            if (Password.length() < Constants.MIN_PASSWORD_LENGTH) {
                password.setError(context.getString(R.string.min_password_length_error_message, Constants.MIN_PASSWORD_LENGTH));
                return false;
            } else {
                password.setError(null);
                return true;
            }
        }
    }
}
