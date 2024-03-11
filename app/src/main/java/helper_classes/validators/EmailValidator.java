package helper_classes.validators;

import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

import vz.apps.dailyevents.R;

public class EmailValidator {

     public static boolean validateEmail(TextInputLayout email, String Email, Context context) {
         if (Email.isEmpty()) {
             email.setError(context.getString(R.string.empty_field_error_message));
             return false;
         } else {
             email.setError(null);
             return true;
         }
     }
}
