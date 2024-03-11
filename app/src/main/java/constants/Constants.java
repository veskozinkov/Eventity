package constants;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import helper_classes.Country;
import helper_classes.time_zones.TimeZoneAbb;
import helper_classes.time_zones.TimeZoneFormat;
import vz.apps.dailyevents.AccountSettingsActivity;
import vz.apps.dailyevents.R;

public class Constants {

    public static final String DATABASE_URL = "https://projectm-aba50-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);

    public static final FirebaseAuth auth = FirebaseAuth.getInstance();
    public static final DatabaseReference tzs_info = database.getReference("tzs_info");
    public static final DatabaseReference usersRef = database.getReference("users");
    public static final DatabaseReference dateRef = database.getReference("ref_date");
    public static final DatabaseReference weekFlagsRef = database.getReference("week_flags");

    public static final String WEEK0 = "week0";
    public static final String WEEK1 = "week1";
    public static final String WEEK2 = "week2";
    public static final String WEEK3 = "week3";
    public static final String LAST_WEEK = "l_w";
    public static final String THIS_WEEK = "t_w";
    public static final String NEXT_WEEK = "n_w";
    public static final String OTHER_EVENTS = "other_ev";
    public static final String OTHER_SETTINGS = "other_set";
    public static final String DEVICE_ID = "device_id";
    public static final String ID = "id";
    public static final String COUNTRY = "country";
    public static final String TZ = "tz";
    public static final String ISO = "iso";
    public static final String TIME_F = "time_f";
    public static final String DATE_F = "date_f";
    public static final String NOTIF = "notif";
    public static final String NOTIF_SCHED = "notif_sched";
    public static final String CHAR_INC = "char_inc";
    public static final String EVENTS_INC = "ev_inc";
    public static final String REF_DATE = "ref_date";
    public static final String DATE = "date";
    public static final String WEEK_FLAGS = "week_flags";
    public static final String FLAGS = "flags";
    public static final String INFO = "info";
    public static final String PROPS = "props";
    public static final String UPDATED = "updated";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String UID = "uid";

    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT_24 = "HH:mm";
    public static final String TIME_FORMAT_12 = "hh:mm a";
    public static final String TIME_ZONE_OFFSET_FORMAT = "ZZZZ";
    public static final int YEAR_MAX_VALUE = 2099;

    public static final String AM = "AM";
    public static final String PM = "PM";

    public static final String MONDAY = "monday";
    public static final String TUESDAY = "tuesday";
    public static final String WEDNESDAY = "wednesday";
    public static final String THURSDAY = "thursday";
    public static final String FRIDAY = "friday";
    public static final String SATURDAY = "saturday";
    public static final String SUNDAY = "sunday";

    public static final String BUNDLE_KEY1 = "SelectedWeek";
    public static final String BUNDLE_KEY2 = "SelectedDay";
    public static final String BUNDLE_KEY3 = "Details";
    public static final String BUNDLE_KEY4 = "FinalTime";
    public static final String BUNDLE_KEY5 = "SelectedDate";
    public static final String BUNDLE_KEY6 = "DatabaseNotification";
    public static final String ACTIVITY_KEY = "Activity";

    public static final String CONNECTION_LOST_WHILE_SIGNING_IN = "connection_lost_while_signing_in";
    public static final String CONNECTION = "connection";
    public static final String LOCAL_VARIABLES_MANAGED = "local_variables_managed";
    public static final String MANAGED = "managed";
    public static final String ALL_EVENTS_DELETED = "all_events_deleted";
    public static final String DELETED = "deleted";

    public static final String EVENT_NOTIFICATION_CHANNEL_ID = "event_notification_channel";
    public static final String NOTIFICATIONS = "notifications";
    public static final String NOTIFICATION_TITLE = "notification_title";
    public static final String NOTIFICATION_CONTENT = "notification_content";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String ID1 = "id1";
    public static final String ID2 = "id2";
    public static final int NOTIFICATION_LIGHTS_ON = 1000;
    public static final int NOTIFICATION_LIGHTS_OFF = 500;
    public static final long[] NOTIFICATION_VIBRATION_PATTERN = { 200, 200, 200, 200 };

    public static final String DAY_FRAGMENT = "DayFragment";

    public static final String ACTIVATE_AUTO_TIME_DIALOG_TAG = "ActivateAutoTimeDialog";
    public static final String ADD_CHANGE_INFO_DIALOG_TAG = "AddChangeInfoDialog";
    public static final String ADD_CHANGE_INFO_DIALOG2_TAG = "AddChangeInfoDialog2";
    public static final String CHANGE_EMAIL_DIALOG_TAG = "ChangeEmailDialog";
    public static final String CHANGE_NAME_DIALOG_TAG = "ChangeNameDialog";
    public static final String CHANGE_PASSWORD_DIALOG_TAG = "ChangePasswordDialog";
    public static final String CONFIRMATION_DIALOG_TAG = "ConfirmationDialog";
    public static final String DELETE_ACCOUNT_DIALOG_TAG = "DeleteAccountDialog";
    public static final String DELETE_ALL_EVENTS_DIALOG_TAG = "DeleteAllEventsDialog";
    public static final String OTHER_SETTINGS_DIALOG_TAG = "OtherSettingsDialog";
    public static final String RESET_PASSWORD_DIALOG_TAG = "ResetPasswordDialog";
    public static final String SELECT_COUNTRY_DIALOG_TAG = "SelectCountryDialog";

    public static final String DEVICE_ID_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_~!@#$%^&*";
    public static final int DEVICE_ID_LENGTH = 36;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LABEL_LENGTH = 16;
    public static final int MAX_NAME_LENGTH = 32;
    public static final int MAX_INFO_LENGTH = 65;
    public static final int MAX_INCREASED_INFO_LENGTH = 130;
    public static final int MAX_INFO_LINES = 5;
    public static final int MAX_EVENTS_PER_DAY = 30;
    public static final int MAX_INCREASED_EVENTS_PER_DAY = 60;
    public static final int MAX_OTHER_EVENTS = 60;
    public static final int MAX_INCREASED_OTHER_EVENTS = 120;

    public static final int BOTTOM_NAV_ANIM_DURATION = 250;
    public static final int SPLASH_SCREEN_ANIM_DURATION = 500;
    public static final int TOAST_LONG_DURATION = 3500;
    public static final int TOAST_SHORT_DURATION = 2000;
    public static final int UPDATE_FRAGMENT_INFO_DELAY = 1000;

    public static final float ACTIVITY_SINGLE_TITLE_PER = 0.1f;
    public static final float FRAGMENT_MULTI_TITLE_PER = 0.2f;
    public static final float DIALOG_SINGLE_TITLE_PER = 0.1f;
    public static final float DIALOG_MULTI_TITLE_PER = 0.05f;

    public static final String EN_LANG = new Locale("en").getLanguage();
    public static final String BG_LANG = new Locale("bg").getLanguage();
    public static final String DEFAULT_LANG = getDefaultLang();

    public static final int sw360dp = 360;
    public static final int sw400dp = 400;
    public static final int sw440dp = 440;
    public static final int sw480dp = 480;
    public static final int sw540dp = 540;
    public static final int sw600dp = 600;
    public static final int sw720dp = 720;
    public static final int sw800dp = 800;

    public static final double sw320dp_DEFAULT_HEIGHT_DP = 426.67;
    public static final int sw360dp_DEFAULT_HEIGHT_DP = 780;
    public static final int sw400dp_DEFAULT_HEIGHT_DP = 880;
    public static final int sw440dp_DEFAULT_HEIGHT_DP = 980;
    public static final int sw480dp_DEFAULT_HEIGHT_DP = 1040;
    public static final int sw540dp_DEFAULT_HEIGHT_DP = 1170;
    public static final int sw600dp_DEFAULT_HEIGHT_DP = 1024;
    public static final int sw720dp_DEFAULT_HEIGHT_DP = 1280;
    public static final int sw800dp_DEFAULT_HEIGHT_DP = 1280;

    public static final int sw320dp_DEFAULT_HEIGHT_PX = 320;
    public static final int sw360dp_DEFAULT_HEIGHT_PX = 2340;
    public static final int sw400dp_DEFAULT_HEIGHT_PX = 2640;
    public static final int sw440dp_DEFAULT_HEIGHT_PX = 2940;
    public static final int sw480dp_DEFAULT_HEIGHT_PX = 3120;
    public static final int sw540dp_DEFAULT_HEIGHT_PX = 2340;
    public static final int sw600dp_DEFAULT_HEIGHT_PX = 1024;
    public static final int sw720dp_DEFAULT_HEIGHT_PX = 1280;
    public static final int sw800dp_DEFAULT_HEIGHT_PX = 1280;

    public static final int sw320dp_DEFAULT_DM_HEIGHT_PX = 320;
    public static final int sw360dp_DEFAULT_DM_HEIGHT_PX = 2110;
    public static final int sw400dp_DEFAULT_DM_HEIGHT_PX = 2360;
    public static final int sw440dp_DEFAULT_DM_HEIGHT_PX = 2660;
    public static final int sw480dp_DEFAULT_DM_HEIGHT_PX = 2840;
    public static final int sw540dp_DEFAULT_DM_HEIGHT_PX = 2108;
    public static final int sw600dp_DEFAULT_DM_HEIGHT_PX = 976;
    public static final int sw720dp_DEFAULT_DM_HEIGHT_PX = 1232;
    public static final int sw800dp_DEFAULT_DM_HEIGHT_PX = 1232;

    public static final double sw320dp_DEFAULT_DENSITY = 0.75;
    public static final double sw360dp_DEFAULT_DENSITY = 3.0;
    public static final double sw400dp_DEFAULT_DENSITY = 3.0;
    public static final double sw440dp_DEFAULT_DENSITY = 3.0;
    public static final double sw480dp_DEFAULT_DENSITY = 3.0;
    public static final double sw540dp_DEFAULT_DENSITY = 2.0;
    public static final double sw600dp_DEFAULT_DENSITY = 1.0;
    public static final double sw720dp_DEFAULT_DENSITY = 1.0;
    public static final double sw800dp_DEFAULT_DENSITY = 1.0;

    public static final int GRADIENT_ANGLE = getGradientAngle();

    public static ArrayList<Country> getCountryList(Context context) {
        ArrayList<Country> countriesList = new ArrayList<>(
                // region Countries and time zones
                Arrays.asList(
                        new Country(R.drawable.unknown, context.getString(R.string.select_country)),
                        new Country(R.drawable.af, context.getString(R.string.afghanistan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Kabul", null)))),
                        new Country(R.drawable.al, context.getString(R.string.albania), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Tirane", null)))),
                        new Country(R.drawable.dz, context.getString(R.string.аlgeria), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Algiers", null)))),
                        new Country(R.drawable.as, context.getString(R.string.american_samoa), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Pago_Pago", null)))),
                        new Country(R.drawable.ad, context.getString(R.string.andorra), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Andorra", null)))),
                        new Country(R.drawable.ao, context.getString(R.string.angola), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.aq, context.getString(R.string.antarctica), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Antarctica/Casey", "(" + TimeZoneFormat.getOffsetString("Antarctica/Casey") + ") CAST"),
                                new TimeZoneAbb("Antarctica/Davis", "(" + TimeZoneFormat.getOffsetString("Antarctica/Davis") + ") DAVT"),
                                new TimeZoneAbb("Antarctica/DumontDUrville", "(" + TimeZoneFormat.getOffsetString("Antarctica/DumontDUrville") + ") DDUT"),
                                new TimeZoneAbb("Antarctica/Mawson", "(" + TimeZoneFormat.getOffsetString("Antarctica/Mawson") + ") MAWT"),
                                new TimeZoneAbb("Antarctica/Palmer", "(" + TimeZoneFormat.getOffsetString("Antarctica/Palmer") + ") CLST"),
                                new TimeZoneAbb("Antarctica/Syowa", "(" + TimeZoneFormat.getOffsetString("Antarctica/Syowa") + ") SYOT"),
                                new TimeZoneAbb("Antarctica/Troll", "(" + TimeZoneFormat.getOffsetString("Antarctica/Troll") + ") GMT/CEST"),
                                new TimeZoneAbb("Antarctica/Vostok", "(" + TimeZoneFormat.getOffsetString("Antarctica/Vostok") + ") VOST")
                        ))),
                        new Country(R.drawable.ag, context.getString(R.string.antigua_barbuda), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.ar, context.getString(R.string.argentina), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Argentina/Buenos_Aires", null)))),
                        new Country(R.drawable.am, context.getString(R.string.armenia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Yerevan", null)))),
                        new Country(R.drawable.aw, context.getString(R.string.aruba), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Curacao", null)))),
                        new Country(R.drawable.au, context.getString(R.string.australia), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Australia/Adelaide", "(" + TimeZoneFormat.getOffsetString("Australia/Adelaide") + ") ACST/ACDT"),
                                new TimeZoneAbb("Australia/Brisbane", "(" + TimeZoneFormat.getOffsetString("Australia/Brisbane") + ") AEST"),
                                new TimeZoneAbb("Australia/Darwin", "(" + TimeZoneFormat.getOffsetString("Australia/Darwin") + ") ACST"),
                                new TimeZoneAbb("Australia/Eucla", "(" + TimeZoneFormat.getOffsetString("Australia/Eucla") + ") ACWST"),
                                new TimeZoneAbb("Australia/Lord_Howe", "(" + TimeZoneFormat.getOffsetString("Australia/Lord_Howe") + ") LHST/LHDT"),
                                new TimeZoneAbb("Australia/Perth", "(" + TimeZoneFormat.getOffsetString("Australia/Perth") + ") AWST"),
                                new TimeZoneAbb("Australia/Sydney", "(" + TimeZoneFormat.getOffsetString("Australia/Sydney") + ") AEST/AEDT")
                        ))),
                        new Country(R.drawable.at, context.getString(R.string.austria), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Vienna", null)))),
                        new Country(R.drawable.az, context.getString(R.string.azerbaijan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Baku", null)))),
                        new Country(R.drawable.bs, context.getString(R.string.bahamas), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Nassau", null)))),
                        new Country(R.drawable.bh, context.getString(R.string.bahrain), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Qatar", null)))),
                        new Country(R.drawable.bd, context.getString(R.string.bangladesh), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Dhaka", null)))),
                        new Country(R.drawable.bb, context.getString(R.string.barbados), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Barbados", null)))),
                        new Country(R.drawable.by, context.getString(R.string.belarus), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Minsk", null)))),
                        new Country(R.drawable.be, context.getString(R.string.belgium), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Brussels", null)))),
                        new Country(R.drawable.bz, context.getString(R.string.belize), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Belize", null)))),
                        new Country(R.drawable.bj, context.getString(R.string.benin), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.bt, context.getString(R.string.bhutan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Thimphu", null)))),
                        new Country(R.drawable.bo, context.getString(R.string.bolivia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/La_Paz", null)))),
                        new Country(R.drawable.ba, context.getString(R.string.bosnia_herzegovina), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Belgrade", null)))),
                        new Country(R.drawable.bw, context.getString(R.string.botswana), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null)))),
                        new Country(R.drawable.br, context.getString(R.string.brazil), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Manaus", "(" + TimeZoneFormat.getOffsetString("America/Manaus") + ") AMT"),
                                new TimeZoneAbb("America/Noronha", "(" + TimeZoneFormat.getOffsetString("America/Noronha") + ") FNT"),
                                new TimeZoneAbb("America/Rio_Branco", "(" + TimeZoneFormat.getOffsetString("America/Rio_Branco") + ") ACT"),
                                new TimeZoneAbb("America/Sao_Paulo", "(" + TimeZoneFormat.getOffsetString("America/Sao_Paulo") + ") BRT")
                        ))),
                        new Country(R.drawable.bn, context.getString(R.string.brunei), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Brunei", null)))),
                        new Country(R.drawable.bg, context.getString(R.string.bulgaria), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Sofia", null)))),
                        new Country(R.drawable.bf, context.getString(R.string.burkina_faso), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.bi, context.getString(R.string.burundi), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null)))),
                        new Country(R.drawable.kh, context.getString(R.string.cambodia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Bangkok", null)))),
                        new Country(R.drawable.cm, context.getString(R.string.cameroon), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.ca, context.getString(R.string.canada), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Atikokan", "(" + TimeZoneFormat.getOffsetString("America/Atikokan") + ") EST"),
                                new TimeZoneAbb("America/Blanc-Sablon", "(" + TimeZoneFormat.getOffsetString("America/Blanc-Sablon") + ") AST"),
                                new TimeZoneAbb("America/Edmonton", "(" + TimeZoneFormat.getOffsetString("America/Edmonton") + ") MST/MDT"),
                                new TimeZoneAbb("America/Halifax", "(" + TimeZoneFormat.getOffsetString("America/Halifax") + ") AST/ADT"),
                                new TimeZoneAbb("America/Regina", "(" + TimeZoneFormat.getOffsetString("America/Regina") + ") CST"),
                                new TimeZoneAbb("America/St_Johns", "(" + TimeZoneFormat.getOffsetString("America/St_Johns") + ") NST/NDT"),
                                new TimeZoneAbb("America/Toronto", "(" + TimeZoneFormat.getOffsetString("America/Toronto") + ") EST/EDT"),
                                new TimeZoneAbb("America/Vancouver", "(" + TimeZoneFormat.getOffsetString("America/Vancouver") + ") PST/PDT"),
                                new TimeZoneAbb("America/Whitehorse", "(" + TimeZoneFormat.getOffsetString("America/Whitehorse") + ") MST"),
                                new TimeZoneAbb("America/Winnipeg", "(" + TimeZoneFormat.getOffsetString("America/Winnipeg") + ") CST/CDT")
                        ))),
                        new Country(R.drawable.cv, context.getString(R.string.cape_verde), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Atlantic/Cape_Verde", null)))),
                        new Country(R.drawable.cf, context.getString(R.string.central_african_republic), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.td, context.getString(R.string.chad), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Ndjamena", null)))),
                        new Country(R.drawable.cl, context.getString(R.string.chile), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Punta_Arenas", "(" + TimeZoneFormat.getOffsetString("America/Punta_Arenas") + ") CLST"),
                                new TimeZoneAbb("America/Santiago", "(" + TimeZoneFormat.getOffsetString("America/Santiago") + ") CLT/CLST"),
                                new TimeZoneAbb("Pacific/Easter", "(" + TimeZoneFormat.getOffsetString("Pacific/Easter") + ") EAST/EASST")
                        ))),
                        new Country(R.drawable.cn, context.getString(R.string.china), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Shanghai", null)))),
                        new Country(R.drawable.cx, context.getString(R.string.christmas_island), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Indian/Christmas", null)))),
                        new Country(R.drawable.cc, context.getString(R.string.cocos_islands), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Indian/Cocos", null)))),
                        new Country(R.drawable.co, context.getString(R.string.colombia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Bogota", null)))),
                        new Country(R.drawable.km, context.getString(R.string.comoros), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.cr, context.getString(R.string.costa_rica), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Costa_Rica", null)))),
                        new Country(R.drawable.hr, context.getString(R.string.croatia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Belgrade", null)))),
                        new Country(R.drawable.cu, context.getString(R.string.cuba), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Havana", null)))),
                        new Country(R.drawable.cw, context.getString(R.string.curacao), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Curacao", null)))),
                        new Country(R.drawable.cy, context.getString(R.string.cyprus), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Nicosia", null)))),
                        new Country(R.drawable.cz, context.getString(R.string.czech_republic), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Prague", null)))),
                        new Country(R.drawable.cd, context.getString(R.string.democratic_republic_congo), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Africa/Lagos", "(" + TimeZoneFormat.getOffsetString("Africa/Lagos") + ") WAT"),
                                new TimeZoneAbb("Africa/Maputo", "(" + TimeZoneFormat.getOffsetString("Africa/Maputo") + ") CAT")
                        ))),
                        new Country(R.drawable.dk, context.getString(R.string.denmark), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Copenhagen", null)))),
                        new Country(R.drawable.dj, context.getString(R.string.djibouti), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.dm, context.getString(R.string.dominica), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.dom, context.getString(R.string.dominican_republic), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Santo_Domingo", null)))),
                        new Country(R.drawable.tl, context.getString(R.string.east_timor), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Dili", null)))),
                        new Country(R.drawable.ec, context.getString(R.string.ecuador), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Guayaquil", "(" + TimeZoneFormat.getOffsetString("America/Guayaquil") + ") ECT"),
                                new TimeZoneAbb("Pacific/Galapagos", "(" + TimeZoneFormat.getOffsetString("Pacific/Galapagos") + ") GALT")
                        ))),
                        new Country(R.drawable.eg, context.getString(R.string.egypt), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Cairo", null)))),
                        new Country(R.drawable.sv, context.getString(R.string.el_salvador), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/El_Salvador", null)))),
                        new Country(R.drawable.gq, context.getString(R.string.equatorial_guinea), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.er, context.getString(R.string.eritrea), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.ee, context.getString(R.string.estonia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Tallinn", null)))),
                        new Country(R.drawable.et, context.getString(R.string.ethiopia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.fo, context.getString(R.string.faroe_islands), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Atlantic/Faroe", null)))),
                        new Country(R.drawable.fj, context.getString(R.string.fiji), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Fiji", null)))),
                        new Country(R.drawable.fi, context.getString(R.string.finland), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Helsinki", null)))),
                        new Country(R.drawable.fr, context.getString(R.string.france), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Paris", null)))),
                        new Country(R.drawable.pf, context.getString(R.string.french_polynesia), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Pacific/Gambier", "(" + TimeZoneFormat.getOffsetString("Pacific/Gambier") + ") GAMT"),
                                new TimeZoneAbb("Pacific/Marquesas", "(" + TimeZoneFormat.getOffsetString("Pacific/Marquesas") + ") MART"),
                                new TimeZoneAbb("Pacific/Tahiti", "(" + TimeZoneFormat.getOffsetString("Pacific/Tahiti") + ") TAHT")
                        ))),
                        new Country(R.drawable.ga, context.getString(R.string.gabon), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.gm, context.getString(R.string.gambia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.ge, context.getString(R.string.georgia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Tbilisi", null)))),
                        new Country(R.drawable.de, context.getString(R.string.germany), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Berlin", null)))),
                        new Country(R.drawable.gh, context.getString(R.string.ghana), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Accra", null)))),
                        new Country(R.drawable.gi, context.getString(R.string.gibraltar), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Gibraltar", null)))),
                        new Country(R.drawable.gr, context.getString(R.string.greece), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Athens", null)))),
                        new Country(R.drawable.gl, context.getString(R.string.greenland), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Danmarkshavn", "(" + TimeZoneFormat.getOffsetString("America/Danmarkshavn") + ") GMT"),
                                new TimeZoneAbb("America/Nuuk", "(" + TimeZoneFormat.getOffsetString("America/Nuuk") + ") WGT/WGST"),
                                new TimeZoneAbb("America/Scoresbysund", "(" + TimeZoneFormat.getOffsetString("America/Scoresbysund") + ") EGT/EGST"),
                                new TimeZoneAbb("America/Thule", "(" + TimeZoneFormat.getOffsetString("America/Thule") + ") AST/ADT")
                        ))),
                        new Country(R.drawable.gd, context.getString(R.string.grenada), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.gu, context.getString(R.string.guam), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Guam", null)))),
                        new Country(R.drawable.gt, context.getString(R.string.guatemala), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Guatemala", null)))),
                        new Country(R.drawable.gn, context.getString(R.string.guinea), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.gw, context.getString(R.string.guinea_bissau), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Bissau", null)))),
                        new Country(R.drawable.gy, context.getString(R.string.guyana), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Guyana", null)))),
                        new Country(R.drawable.ht, context.getString(R.string.haiti), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port-au-Prince", null)))),
                        new Country(R.drawable.hn, context.getString(R.string.honduras), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Tegucigalpa", null)))),
                        new Country(R.drawable.hk, context.getString(R.string.hong_kong), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Hong_Kong", null)))),
                        new Country(R.drawable.hu, context.getString(R.string.hungary), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Budapest", null)))),
                        new Country(R.drawable.is, context.getString(R.string.iceland), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Atlantic/Reykjavik", null)))),
                        new Country(R.drawable.in, context.getString(R.string.india), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Kolkata", null)))),
                        new Country(R.drawable.id, context.getString(R.string.indonesia), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Asia/Jakarta", "(" + TimeZoneFormat.getOffsetString("Asia/Jakarta") + ") WIB"),
                                new TimeZoneAbb("Asia/Jayapura", "(" + TimeZoneFormat.getOffsetString("Asia/Jayapura") + ") WIT"),
                                new TimeZoneAbb("Asia/Makassar", "(" + TimeZoneFormat.getOffsetString("Asia/Makassar") + ") WITA")
                        ))),
                        new Country(R.drawable.ir, context.getString(R.string.iran), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Tehran", null)))),
                        new Country(R.drawable.iq, context.getString(R.string.iraq), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Baghdad", null)))),
                        new Country(R.drawable.ie, context.getString(R.string.ireland), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Dublin", null)))),
                        new Country(R.drawable.il, context.getString(R.string.israel), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Jerusalem", null)))),
                        new Country(R.drawable.it, context.getString(R.string.italy), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Rome", null)))),
                        new Country(R.drawable.ci, context.getString(R.string.ivory_coast), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.jm, context.getString(R.string.jamaica), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Jamaica", null)))),
                        new Country(R.drawable.jp, context.getString(R.string.japan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Tokyo", null)))),
                        new Country(R.drawable.jo, context.getString(R.string.jordan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Amman", null)))),
                        new Country(R.drawable.kz, context.getString(R.string.kazakhstan), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Asia/Almaty", "(" + TimeZoneFormat.getOffsetString("Asia/Almaty") + ") ALMT"),
                                new TimeZoneAbb("Asia/Aqtobe", "(" + TimeZoneFormat.getOffsetString("Asia/Aqtobe") + ") AQTT")
                        ))),
                        new Country(R.drawable.ke, context.getString(R.string.kenya), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.ki, context.getString(R.string.kiribati), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Pacific/Enderbury", "(" + TimeZoneFormat.getOffsetString("Pacific/Enderbury") + ") PHOT"),
                                new TimeZoneAbb("Pacific/Kiritimati", "(" + TimeZoneFormat.getOffsetString("Pacific/Kiritimati") + ") LINT"),
                                new TimeZoneAbb("Pacific/Tarawa", "(" + TimeZoneFormat.getOffsetString("Pacific/Tarawa") + ") GILT")
                        ))),
                        new Country(R.drawable.kw, context.getString(R.string.kuwait), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Riyadh", null)))),
                        new Country(R.drawable.kg, context.getString(R.string.kyrgyzstan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Bishkek", null)))),
                        new Country(R.drawable.la, context.getString(R.string.laos), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Bangkok", null)))),
                        new Country(R.drawable.lv, context.getString(R.string.latvia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Riga", null)))),
                        new Country(R.drawable.lb, context.getString(R.string.lebanon), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Beirut", null)))),
                        new Country(R.drawable.ls, context.getString(R.string.lesotho), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Johannesburg", null)))),
                        new Country(R.drawable.lr, context.getString(R.string.liberia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Monrovia", null)))),
                        new Country(R.drawable.ly, context.getString(R.string.libya), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Tripoli", null)))),
                        new Country(R.drawable.li, context.getString(R.string.liechtenstein), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Zurich", null)))),
                        new Country(R.drawable.lt, context.getString(R.string.lithuania), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Vilnius", null)))),
                        new Country(R.drawable.lu, context.getString(R.string.luxembourg), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Luxembourg", null)))),
                        new Country(R.drawable.mo, context.getString(R.string.macao), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Macau", null)))),
                        new Country(R.drawable.mk, context.getString(R.string.north_macedonia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Belgrade", null)))),
                        new Country(R.drawable.mg, context.getString(R.string.madagascar), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.mw, context.getString(R.string.malawi), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null)))),
                        new Country(R.drawable.my, context.getString(R.string.malaysia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Kuala_Lumpur", null)))),
                        new Country(R.drawable.mv, context.getString(R.string.maldives), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Indian/Maldives", null)))),
                        new Country(R.drawable.ml, context.getString(R.string.mali), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.mt, context.getString(R.string.malta), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Malta", null)))),
                        new Country(R.drawable.mh, context.getString(R.string.marshall_islands), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Majuro", null)))),
                        new Country(R.drawable.mr, context.getString(R.string.mauritania), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.mu, context.getString(R.string.mauritius), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Indian/Mauritius", null)))),
                        new Country(R.drawable.mx, context.getString(R.string.mexico), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Cancun", "(" + TimeZoneFormat.getOffsetString("America/Cancun") + ") EST"),
                                new TimeZoneAbb("America/Chihuahua", "(" + TimeZoneFormat.getOffsetString("America/Chihuahua") + ") MST/MDT"),
                                new TimeZoneAbb("America/Hermosillo", "(" + TimeZoneFormat.getOffsetString("America/Hermosillo") + ") MST"),
                                new TimeZoneAbb("America/Mexico_City", "(" + TimeZoneFormat.getOffsetString("America/Mexico_City") + ") CST/CDT"),
                                new TimeZoneAbb("America/Tijuana", "(" + TimeZoneFormat.getOffsetString("America/Tijuana") + ") PST/PDT")
                        ))),
                        new Country(R.drawable.fm, context.getString(R.string.micronesia), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Pacific/Chuuk", "(" + TimeZoneFormat.getOffsetString("Pacific/Chuuk") + ") CHUT"),
                                new TimeZoneAbb("Pacific/Pohnpei", "(" + TimeZoneFormat.getOffsetString("Pacific/Pohnpei") + ") PONT")
                        ))),
                        new Country(R.drawable.md, context.getString(R.string.moldova), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Chisinau", null)))),
                        new Country(R.drawable.mc, context.getString(R.string.monaco), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Monaco", null)))),
                        new Country(R.drawable.mn, context.getString(R.string.mongolia), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Asia/Hovd", "(" + TimeZoneFormat.getOffsetString("Asia/Hovd") + ") HOVT"),
                                new TimeZoneAbb("Asia/Ulaanbaatar", "(" + TimeZoneFormat.getOffsetString("Asia/Ulaanbaatar") + ") ULAT")
                        ))),
                        new Country(R.drawable.me, context.getString(R.string.montenegro), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Belgrade", null)))),
                        new Country(R.drawable.ma, context.getString(R.string.morocco), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Casablanca", null)))),
                        new Country(R.drawable.mz, context.getString(R.string.mozambique), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null)))),
                        new Country(R.drawable.mm, context.getString(R.string.myanmar), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Yangon", null)))),
                        new Country(R.drawable.na, context.getString(R.string.namibia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Windhoek", null)))),
                        new Country(R.drawable.nr, context.getString(R.string.nauru), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Nauru", null)))),
                        new Country(R.drawable.np, context.getString(R.string.nepal), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Kathmandu", null)))),
                        new Country(R.drawable.nl, context.getString(R.string.netherlands), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Amsterdam", null)))),
                        new Country(R.drawable.nc, context.getString(R.string.new_caledonia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Noumea", null)))),
                        new Country(R.drawable.nz, context.getString(R.string.new_zealand), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Pacific/Auckland", "(" + TimeZoneFormat.getOffsetString("Pacific/Auckland") + ") NZST/NZDT"),
                                new TimeZoneAbb("Pacific/Chatham", "(" + TimeZoneFormat.getOffsetString("Pacific/Chatham") + ") CHAST/CHADT")
                        ))),
                        new Country(R.drawable.ni, context.getString(R.string.nicaragua), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Managua", null)))),
                        new Country(R.drawable.ne, context.getString(R.string.niger), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.ng, context.getString(R.string.nigeria), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.nu, context.getString(R.string.niue), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Niue", null)))),
                        new Country(R.drawable.kp, context.getString(R.string.north_korea), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Pyongyang", null)))),
                        new Country(R.drawable.mp, context.getString(R.string.northern_mariana_islands), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Guam", null)))),
                        new Country(R.drawable.no, context.getString(R.string.norway), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Oslo", null)))),
                        new Country(R.drawable.om, context.getString(R.string.oman), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Dubai", null)))),
                        new Country(R.drawable.pk, context.getString(R.string.pakistan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Karachi", null)))),
                        new Country(R.drawable.pw, context.getString(R.string.palau), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Palau", null)))),
                        new Country(R.drawable.ps, context.getString(R.string.palestine), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Gaza", null)))),
                        new Country(R.drawable.pa, context.getString(R.string.panama), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Panama", null)))),
                        new Country(R.drawable.pg, context.getString(R.string.papua_new_guinea), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Pacific/Bougainville", "(" + TimeZoneFormat.getOffsetString("Pacific/Bougainville") + ") BST"),
                                new TimeZoneAbb("Pacific/Port_Moresby", "(" + TimeZoneFormat.getOffsetString("Pacific/Port_Moresby") + ") PGT")
                        ))),
                        new Country(R.drawable.py, context.getString(R.string.paraguay), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Asuncion", null)))),
                        new Country(R.drawable.pe, context.getString(R.string.peru), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Lima", null)))),
                        new Country(R.drawable.ph, context.getString(R.string.philippines), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Manila", null)))),
                        new Country(R.drawable.pl, context.getString(R.string.poland), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Warsaw", null)))),
                        new Country(R.drawable.pt, context.getString(R.string.portugal), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Atlantic/Azores", "(" + TimeZoneFormat.getOffsetString("Atlantic/Azores") + ") AZOT/AZOST"),
                                new TimeZoneAbb("Europe/Lisbon", "(" + TimeZoneFormat.getOffsetString("Europe/Lisbon") + ") WET/WEST")
                        ))),
                        new Country(R.drawable.pr, context.getString(R.string.puerto_rico), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Puerto_Rico", null)))),
                        new Country(R.drawable.qa, context.getString(R.string.qatar), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Qatar", null)))),
                        new Country(R.drawable.cg, context.getString(R.string.republic_congo), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Lagos", null)))),
                        new Country(R.drawable.ro, context.getString(R.string.romania), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Bucharest", null)))),
                        new Country(R.drawable.ru, context.getString(R.string.russia), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Asia/Chita", "(" + TimeZoneFormat.getOffsetString("Asia/Chita") + ") YAKT"),
                                new TimeZoneAbb("Asia/Irkutsk", "(" + TimeZoneFormat.getOffsetString("Asia/Irkutsk") + ") IRKT"),
                                new TimeZoneAbb("Asia/Kamchatka", "(" + TimeZoneFormat.getOffsetString("Asia/Kamchatka") + ") PETT"),
                                new TimeZoneAbb("Asia/Novosibirsk", "(" + TimeZoneFormat.getOffsetString("Asia/Novosibirsk") + ") NOVT"),
                                new TimeZoneAbb("Asia/Omsk", "(" + TimeZoneFormat.getOffsetString("Asia/Omsk") + ") OMST"),
                                new TimeZoneAbb("Asia/Sakhalin", "(" + TimeZoneFormat.getOffsetString("Asia/Sakhalin") + ") SAKT"),
                                new TimeZoneAbb("Asia/Vladivostok", "(" + TimeZoneFormat.getOffsetString("Asia/Vladivostok") + ") VLAT"),
                                new TimeZoneAbb("Asia/Yekaterinburg", "(" + TimeZoneFormat.getOffsetString("Asia/Yekaterinburg") + ") YEKT"),
                                new TimeZoneAbb("Europe/Kaliningrad", "(" + TimeZoneFormat.getOffsetString("Europe/Kaliningrad") + ") EET"),
                                new TimeZoneAbb("Europe/Moscow", "(" + TimeZoneFormat.getOffsetString("Europe/Moscow") + ") MSK"),
                                new TimeZoneAbb("Europe/Samara", "(" + TimeZoneFormat.getOffsetString("Europe/Samara") + ") SAMT")
                        ))),
                        new Country(R.drawable.rw, context.getString(R.string.rwanda), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null)))),
                        new Country(R.drawable.kn, context.getString(R.string.saint_kitts_nevis), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.lc, context.getString(R.string.saint_lucia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.vc, context.getString(R.string.saint_vincent_grenadines), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.ws, context.getString(R.string.samoa), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Apia", null)))),
                        new Country(R.drawable.sm, context.getString(R.string.san_marino), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Rome", null)))),
                        new Country(R.drawable.st, context.getString(R.string.sao_tome_principe), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Sao_Tome", null)))),
                        new Country(R.drawable.sa, context.getString(R.string.saudi_arabia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Riyadh", null)))),
                        new Country(R.drawable.sn, context.getString(R.string.senegal), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.rs, context.getString(R.string.serbia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Belgrade", null)))),
                        new Country(R.drawable.sc, context.getString(R.string.seychelles), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Indian/Mahe", null)))),
                        new Country(R.drawable.sl, context.getString(R.string.sierra_leone), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.sg, context.getString(R.string.singapore), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Singapore", null)))),
                        new Country(R.drawable.sk, context.getString(R.string.slovakia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Prague", null)))),
                        new Country(R.drawable.si, context.getString(R.string.slovenia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Belgrade", null)))),
                        new Country(R.drawable.sb, context.getString(R.string.solomon_islands), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Guadalcanal", null)))),
                        new Country(R.drawable.so, context.getString(R.string.somalia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.za, context.getString(R.string.south_africa), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Johannesburg", null)))),
                        new Country(R.drawable.kr, context.getString(R.string.south_korea), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Seoul", null)))),
                        new Country(R.drawable.ss, context.getString(R.string.south_sudan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Juba", null)))),
                        new Country(R.drawable.es, context.getString(R.string.spain), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("Atlantic/Canary", "(" + TimeZoneFormat.getOffsetString("Atlantic/Canary") + ") WET/WEST"),
                                new TimeZoneAbb("Europe/Madrid", "(" + TimeZoneFormat.getOffsetString("Europe/Madrid") + ") CET/CEST")
                        ))),
                        new Country(R.drawable.lk, context.getString(R.string.sri_lanka), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Colombo", null)))),
                        new Country(R.drawable.sd, context.getString(R.string.sudan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Khartoum", null)))),
                        new Country(R.drawable.sr, context.getString(R.string.suriname), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Paramaribo", null)))),
                        new Country(R.drawable.se, context.getString(R.string.sweden), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Stockholm", null)))),
                        new Country(R.drawable.ch, context.getString(R.string.switzerland), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Zurich", null)))),
                        new Country(R.drawable.sy, context.getString(R.string.syria), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Damascus", null)))),
                        new Country(R.drawable.tw, context.getString(R.string.taiwan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Taipei", null)))),
                        new Country(R.drawable.tj, context.getString(R.string.tajikistan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Dushanbe", null)))),
                        new Country(R.drawable.tz, context.getString(R.string.tanzania), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.th, context.getString(R.string.thailand), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Bangkok", null)))),
                        new Country(R.drawable.tg, context.getString(R.string.togo), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Abidjan", null)))),
                        new Country(R.drawable.to, context.getString(R.string.tonga), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Tongatapu", null)))),
                        new Country(R.drawable.tt, context.getString(R.string.trinidad_tobago), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Port_of_Spain", null)))),
                        new Country(R.drawable.tn, context.getString(R.string.tunisia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Tunis", null)))),
                        new Country(R.drawable.tr, context.getString(R.string.turkey), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Istanbul", null)))),
                        new Country(R.drawable.tm, context.getString(R.string.turkmenistan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Ashgabat", null)))),
                        new Country(R.drawable.tv, context.getString(R.string.tuvalu), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Funafuti", null)))),
                        new Country(R.drawable.ug, context.getString(R.string.uganda), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Nairobi", null)))),
                        new Country(R.drawable.ua, context.getString(R.string.ukraine), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Kiev", null)))),
                        new Country(R.drawable.ae, context.getString(R.string.united_arab_emirates), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Dubai", null)))),
                        new Country(R.drawable.gb, context.getString(R.string.united_kingdom), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/London", null)))),
                        new Country(R.drawable.us, context.getString(R.string.united_states), new ArrayList<>(Arrays.asList(
                                new TimeZoneAbb("America/Adak", "(" + TimeZoneFormat.getOffsetString("America/Adak") + ") HST/HDT"),
                                new TimeZoneAbb("America/Anchorage", "(" + TimeZoneFormat.getOffsetString("America/Anchorage") + ") AKST/AKDT"),
                                new TimeZoneAbb("America/Chicago", "(" + TimeZoneFormat.getOffsetString("America/Chicago") + ") CST/CDT"),
                                new TimeZoneAbb("America/Denver", "(" + TimeZoneFormat.getOffsetString("America/Denver") + ") MST/MDT"),
                                new TimeZoneAbb("America/Los_Angeles", "(" + TimeZoneFormat.getOffsetString("America/Los_Angeles") + ") PST/PDT"),
                                new TimeZoneAbb("America/New_York", "(" + TimeZoneFormat.getOffsetString("America/New_York") + ") EST/EDT"),
                                new TimeZoneAbb("America/Phoenix", "(" + TimeZoneFormat.getOffsetString("America/Phoenix") + ") MST"),
                                new TimeZoneAbb("Pacific/Honolulu", "(" + TimeZoneFormat.getOffsetString("Pacific/Honolulu") + ") HST")
                        ))),
                        new Country(R.drawable.uy, context.getString(R.string.uruguay), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Montevideo", null)))),
                        new Country(R.drawable.uz, context.getString(R.string.uzbekistan), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Tashkent", null)))),
                        new Country(R.drawable.vu, context.getString(R.string.vanuatu), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Pacific/Efate", null)))),
                        new Country(R.drawable.va, context.getString(R.string.vatican), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Europe/Rome", null)))),
                        new Country(R.drawable.ve, context.getString(R.string.venezuela), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("America/Caracas", null)))),
                        new Country(R.drawable.vn, context.getString(R.string.vietnam), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Ho_Chi_Minh", null)))),
                        new Country(R.drawable.eh, context.getString(R.string.western_sahara), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/El_Aaiun", null)))),
                        new Country(R.drawable.ye, context.getString(R.string.yemen), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Asia/Riyadh", null)))),
                        new Country(R.drawable.zm, context.getString(R.string.zambia), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null)))),
                        new Country(R.drawable.zw, context.getString(R.string.zimbabwe), new ArrayList<>(Collections.singletonList(new TimeZoneAbb("Africa/Maputo", null))))
                )
                // endregion
        );

        if (context instanceof AccountSettingsActivity) {
            countriesList.remove(0);
        }

        return countriesList;
    }

    private static int getGradientAngle() {
        int random = new Random().nextInt(8);

        while (random == 2 || random == 6) {
            random = new Random().nextInt(8);
        }

        return random * 45;
    }

    private static String getDefaultLang() {
        String defaultLang = Locale.getDefault().getLanguage();

        if (defaultLang.equals(EN_LANG) || defaultLang.equals(BG_LANG)) return defaultLang;
        else { return EN_LANG; }
    }
}
