package helper_classes;

import java.util.ArrayList;

import helper_classes.time_zones.TimeZoneAbb;

public class Country {

    private int flag;
    private String name;
    private ArrayList<TimeZoneAbb> timeZonesAbbs;

    public Country(int flag, String name) {
        this.flag = flag;
        this.name = name;
    }

    public Country(int flag, String name, ArrayList<TimeZoneAbb> timeZonesAbbs) {
        this.flag = flag;
        this.name = name;
        this.timeZonesAbbs = new ArrayList<>(timeZonesAbbs);
    }

    public int getFlag() { return flag; }

    public void setFlag(int flag) { this.flag = flag; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TimeZoneAbb> getTimeZonesAbbs() {
        return timeZonesAbbs;
    }

    public void setTimeZonesAbbs(ArrayList<TimeZoneAbb> timeZonesAbbs) {
        this.timeZonesAbbs = timeZonesAbbs;
    }

    @Override
    public String toString() {
        return name;
    }
}
