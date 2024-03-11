package helper_classes.comparators;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import constants.Constants;
import helper_classes.Information;
import io.paperdb.Paper;
import vz.apps.dailyevents.R;

public class DateComparator implements Comparator<Information> {

    private static final String TAG = "DateComparator";

    private final Context context;
    private final boolean reverseDate;

    public DateComparator(Context context, boolean reverseDate) {
        this.context = context;
        this.reverseDate = reverseDate;
    }

    @Override
    public int compare(Information o1, Information o2) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat.setLenient(false);

        try {
            if (reverseDate) return Objects.requireNonNull(dateFormat.parse(reverseFormatDate(o1.getDate()))).compareTo(dateFormat.parse(reverseFormatDate(o2.getDate())));
            else { return Objects.requireNonNull(dateFormat.parse(o1.getDate())).compareTo(dateFormat.parse(o2.getDate())); }
        } catch (ParseException e) {
            Log.e(TAG, "compare: ParseException: " + e.getMessage());
        }

        return 0;
    }

    private String reverseFormatDate(String dateToFormat) {
        String[] dateFormatOptions = getEnglishStringArray(R.array.date_formats);
        int dateFormatIndex = BigDecimal.valueOf(Objects.requireNonNull(Paper.book(Constants.OTHER_SETTINGS).read(Constants.DATE_F))).intValueExact();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat(dateFormatOptions[dateFormatIndex].replace("mm", "MM"), Locale.US);
        dateFormat1.setLenient(false);

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setLenient(false);

        Date date = new Date();

        try {
            date = dateFormat1.parse(dateToFormat);
        } catch (ParseException e) {
            Log.e(TAG, "formatDate: ParseException: " + e.getMessage());
        }

        calendar.setTime(Objects.requireNonNull(date));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        dateFormat2.setLenient(false);

        return dateFormat2.format(calendar.getTime());
    }

    private String[] getEnglishStringArray(int id) {
        Configuration configuration = getEnglishConfiguration();
        return context.createConfigurationContext(configuration).getResources().getStringArray(id);
    }

    private Configuration getEnglishConfiguration() {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale("en"));
        return configuration;
    }
}
