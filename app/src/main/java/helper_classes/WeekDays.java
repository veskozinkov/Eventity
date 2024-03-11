package helper_classes;

public class WeekDays {

    private String weekDay_localString;
    private String weekDay_ENG;

    public WeekDays(String localString, String eng) {
        weekDay_localString = localString;
        weekDay_ENG = eng;
    }

    public String getWeekDay_localString() {
        return weekDay_localString;
    }

    public void setWeekDay_localString(String weekDay_localString) {
        this.weekDay_localString = weekDay_localString;
    }

    public String getWeekDay_ENG() {
        return weekDay_ENG;
    }

    public void setWeekDay_ENG(String weekDay_ENG) {
        this.weekDay_ENG = weekDay_ENG;
    }
}
