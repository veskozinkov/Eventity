package helper_classes.time_zones;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import constants.Constants;

public class TimeZoneFormat {

    public static String format(String timeZone) {
        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < timeZone.length(); i++) {
            if (timeZone.charAt(i) == '/') indexes.add(i);
        }

        if (indexes.size() == 2) {
            String stringToRemove = timeZone.substring(indexes.get(0), indexes.get(1));
            timeZone = timeZone.replace(stringToRemove, "");
        }

        return timeZone;
    }

    public static String getOffsetString(String timeZone) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIME_ZONE_OFFSET_FORMAT, Locale.US);
        dateFormat.setLenient(false);

        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat.format(new Date());
    }
}
