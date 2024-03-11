package helper_classes.comparators;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import constants.Constants;
import helper_classes.Information;
import io.paperdb.Paper;

public class TimeComparator implements Comparator<Information> {

    private static final String TAG = "SortByTimeComparator";

    private final boolean reverseTime;

    public TimeComparator(boolean reverseTime) {
        this.reverseTime = reverseTime;
    }

    @Override
    public int compare(Information o1, Information o2) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIME_FORMAT_24, Locale.US);
        dateFormat.setLenient(false);

        try {
            if (reverseTime) return Objects.requireNonNull(dateFormat.parse(reverseFormatTime(o1.getTime()))).compareTo(dateFormat.parse(reverseFormatTime(o2.getTime())));
            else { return Objects.requireNonNull(dateFormat.parse(o1.getTime())).compareTo(dateFormat.parse(o2.getTime())); }
        } catch (ParseException e) {
            Log.e(TAG, "compare: ParseException: " + e.getMessage());
        }

        return 0;
    }

    private String reverseFormatTime(String timeToFormat) {
        if ((long) Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.TIME_F)) == 1) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat24 = new SimpleDateFormat(Constants.TIME_FORMAT_24, Locale.US);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat12 = new SimpleDateFormat(Constants.TIME_FORMAT_12, Locale.US);

            Date date = new Date();

            try {
                date = timeFormat12.parse(timeToFormat);
            } catch (ParseException e) {
                Log.e(TAG, "formatTime: ParseException: " + e.getMessage());
            }

            return timeFormat24.format(Objects.requireNonNull(date));
        } else { return timeToFormat; }
    }
}
