package helper_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import constants.Constants;
import io.paperdb.Paper;

public class ID {

    public static String generateDeviceID(int n) {
        StringBuilder result = new StringBuilder();

        while (n > 0) {
            Random random = new Random();
            result.append(Constants.DEVICE_ID_CHARACTERS.charAt(random.nextInt(Constants.DEVICE_ID_CHARACTERS.length())));
            n--;
        }

        return result.toString();
    }

    public static int generateNotificationID() {
        ArrayList<Integer> week0IDs = getWeekAllNotificationIDs(Constants.WEEK0);
        ArrayList<Integer> week1IDs = getWeekAllNotificationIDs(Constants.WEEK1);
        ArrayList<Integer> week2IDs = getWeekAllNotificationIDs(Constants.WEEK2);
        ArrayList<Integer> week3IDs = getWeekAllNotificationIDs(Constants.WEEK3);

        List<String> allBookKeys = Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).getAllKeys();
        ArrayList<Integer> otherEventsIDs = new ArrayList<>();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                otherEventsIDs.add(notificationID);
            }
        } else { Paper.book(Constants.OTHER_EVENTS + "." + Constants.NOTIFICATIONS).destroy(); }

        int id = new Random().nextInt(Integer.MAX_VALUE) + 1;

        while (week0IDs.contains(id) || week1IDs.contains(id) || week2IDs.contains(id) || week3IDs.contains(id) || otherEventsIDs.contains(id)) {
            id = new Random().nextInt(Integer.MAX_VALUE) + 1;
        }

        return id;
    }

    private static ArrayList<Integer> getWeekAllNotificationIDs(String week) {
        ArrayList<Integer> allNotificationIDs = new ArrayList<>();
        List<String> allBookKeys = Paper.book(week + "." + Constants.MONDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.MONDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.MONDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        allBookKeys = Paper.book(week + "." + Constants.TUESDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.TUESDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.TUESDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        allBookKeys = Paper.book(week + "." + Constants.WEDNESDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.WEDNESDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.WEDNESDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        allBookKeys = Paper.book(week + "." + Constants.THURSDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.THURSDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.THURSDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        allBookKeys = Paper.book(week + "." + Constants.FRIDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.FRIDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.FRIDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        allBookKeys = Paper.book(week + "." + Constants.SATURDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.SATURDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.SATURDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        allBookKeys = Paper.book(week + "." + Constants.SUNDAY + "." + Constants.NOTIFICATIONS).getAllKeys();

        if (allBookKeys.size() > 0) {
            for (int i = 0; i < allBookKeys.size(); i++) {
                int notificationID = Objects.requireNonNull(Paper.book(week + "." + Constants.SUNDAY + "." + Constants.NOTIFICATIONS).read(allBookKeys.get(i)));
                allNotificationIDs.add(notificationID);
            }
        } else { Paper.book(week + "." + Constants.SUNDAY + "." + Constants.NOTIFICATIONS).destroy(); }

        return allNotificationIDs;
    }
}
