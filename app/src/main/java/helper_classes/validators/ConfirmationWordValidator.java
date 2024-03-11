package helper_classes.validators;

import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

import vz.apps.dailyevents.R;

public class ConfirmationWordValidator {

    public static boolean validateConfirmWord(TextInputLayout confirmation, String confirmationWord, Context context) {
        if (confirmationWord.isEmpty()) {
            confirmation.setError(context.getString(R.string.empty_field_error_message));
            return false;
        } else {
            if (!confirmationWord.equals(context.getString(R.string.confirmation_word))) {
                confirmation.setError(context.getString(R.string.wrong_confirmation_word_error_message));
                return false;
            } else {
                confirmation.setError(null);
                return true;
            }
        }
    }
}
