package helper_classes.comparators;

import java.util.ArrayList;
import java.util.Comparator;

import helper_classes.Information;

public class ChainedComparator implements Comparator<Information> {

    private final ArrayList<Comparator<Information>> comparators = new ArrayList<>();

    public ChainedComparator(Comparator<Information> comparator1, Comparator<Information> comparator2) {
        comparators.add(comparator1);
        comparators.add(comparator2);
    }

    @Override
    public int compare(Information o1, Information o2) {
        for (Comparator<Information> comparator : comparators) {
            int result = comparator.compare(o1, o2);

            if (result != 0) {
                return result;
            }
        }

        return 0;
    }
}
