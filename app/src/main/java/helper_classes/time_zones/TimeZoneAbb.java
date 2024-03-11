package helper_classes.time_zones;

public class TimeZoneAbb {

    private String timeZone;
    private String abbreviation;

    public TimeZoneAbb(String timeZone, String abbreviation) {
        this.timeZone = timeZone;
        this.abbreviation = abbreviation;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
