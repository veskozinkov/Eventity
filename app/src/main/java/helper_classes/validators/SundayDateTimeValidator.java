package helper_classes.validators;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import constants.Constants;
import io.paperdb.Paper;

public class SundayDateTimeValidator {

    private static final String TAG = "SundayDateTimeValidator";

    public static boolean validateDateAndTime() {
        Calendar calendar1 = Calendar.getInstance(Locale.US);
        Calendar calendar2 = Calendar.getInstance(Locale.US);

        calendar1.setLenient(false);
        calendar2.setLenient(false);

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat.setLenient(false);

        String ref_date = Paper.book(Constants.REF_DATE).read(Constants.DATE);

        if (ref_date != null) {
            try {
                calendar1.setTime(Objects.requireNonNull(dateFormat.parse(ref_date)));
                calendar2.setTime(Objects.requireNonNull(dateFormat.parse(ref_date)));
            } catch (ParseException e) {
                Log.e(TAG, "validateDateAndTime: ParseException: " + e.getMessage());
            }
        } else {
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);

            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 1);

            calendar1.setTime(calendar.getTime());
            calendar2.setTime(calendar.getTime());
        }

        calendar1.add(Calendar.DAY_OF_MONTH, 6);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);

        calendar2.add(Calendar.DAY_OF_MONTH, 7);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);

        Date date = new Date();

        return date.compareTo(calendar1.getTime()) <= 0 || date.compareTo(calendar2.getTime()) > 0;
    }
}
