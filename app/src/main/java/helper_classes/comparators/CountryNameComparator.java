package helper_classes.comparators;

import android.content.Context;

import java.util.Comparator;

import helper_classes.Country;
import vz.apps.dailyevents.R;

public class CountryNameComparator implements Comparator<Country> {

    Context context;

    public CountryNameComparator(Context context) {
        this.context = context;
    }

    @Override
    public int compare(Country o1, Country o2) {
        if (o1.getName().equals(context.getString(R.string.select_country)) && o1.getName().compareTo(o2.getName()) < 0) {
            return -1;
        }

        if (o2.getName().equals(context.getString(R.string.select_country))) {
            return 1;
        }

        return o1.getName().compareTo(o2.getName());
    }
}
