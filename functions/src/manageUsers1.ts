import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as moment from "moment-timezone";

const db = admin.database();
const tzs_info = db.ref("tzs_info");
const usersRef = db.ref("users");
const dateRef = db.ref("ref_date");
const weekFlagsRef = db.ref("week_flags");
const cf_flag = db.ref("cf_flag");

const LAST_WEEK: string = "l_w";
const THIS_WEEK: string = "t_w";
const NEXT_WEEK: string = "n_w";
const OTHER_EVENTS: string = "other_ev";

const zones: Array<string> = [
    "Pacific/Kiritimati",
    // +14:00
    "Pacific/Apia",
    "Pacific/Enderbury",
    "Pacific/Tongatapu",
    // +13:00
    "Pacific/Chatham",
    // +12:45
    "Asia/Kamchatka",
    "Pacific/Auckland",
    "Pacific/Fiji",
    "Pacific/Funafuti",
    "Pacific/Majuro",
    "Pacific/Nauru",
    "Pacific/Tarawa",
    // +12:00
    "Antarctica/Casey",
    "Asia/Sakhalin",
    "Pacific/Bougainville",
    "Pacific/Efate",
    "Pacific/Guadalcanal",
    "Pacific/Noumea",
    "Pacific/Pohnpei",
    // +11:00
    "Australia/Lord_Howe",
    // +10:30
    "Antarctica/DumontDUrville",
    "Asia/Vladivostok",
    "Australia/Brisbane",
    "Australia/Sydney",
    "Pacific/Chuuk",
    "Pacific/Guam",
    "Pacific/Port_Moresby",
    // +10:00
    "Australia/Adelaide",
    "Australia/Darwin",
    // +09:30
    "Asia/Chita",
    "Asia/Dili",
    "Asia/Jayapura",
    "Asia/Pyongyang",
    "Asia/Seoul",
    "Asia/Tokyo",
    "Pacific/Palau",
    // +09:00
    "Australia/Eucla",
    // +08:45
    "Asia/Brunei",
    "Asia/Hong_Kong",
    "Asia/Irkutsk",
    "Asia/Kuala_Lumpur",
    "Asia/Macau",
    "Asia/Makassar",
    "Asia/Manila",
    "Asia/Shanghai",
    "Asia/Singapore",
    "Asia/Taipei",
    "Asia/Ulaanbaatar",
    "Australia/Perth",
    // +08:00
    "Antarctica/Davis",
    "Asia/Bangkok",
    "Asia/Ho_Chi_Minh",
    "Asia/Hovd",
    "Asia/Jakarta",
    "Asia/Novosibirsk",
    "Indian/Christmas",
    // +07:00
    "Asia/Yangon",
    "Indian/Cocos",
    // +06:30
    "Antarctica/Vostok",
    "Asia/Almaty",
    "Asia/Bishkek",
    "Asia/Dhaka",
    "Asia/Omsk",
    "Asia/Thimphu",
    // +06:00
    "Asia/Kathmandu",
    // +05:45
    "Asia/Colombo",
    "Asia/Kolkata",
    // +05:30
    "Antarctica/Mawson",
    "Asia/Aqtobe",
    "Asia/Ashgabat",
    "Asia/Dushanbe",
    "Asia/Karachi",
    "Asia/Tashkent",
    "Asia/Yekaterinburg",
    "Indian/Maldives",
    // +05:00
    "Asia/Kabul",
    // +04:30
    "Asia/Baku",
    "Asia/Dubai",
    "Asia/Tbilisi",
    "Asia/Yerevan",
    "Europe/Samara",
    "Indian/Mahe",
    "Indian/Mauritius",
    // +04:00
    "Asia/Tehran",
    // +03:30
    "Africa/Juba",
    "Africa/Nairobi",
    "Antarctica/Syowa",
    "Asia/Baghdad",
    "Asia/Qatar",
    "Asia/Riyadh",
    "Europe/Istanbul",
    "Europe/Minsk",
    "Europe/Moscow",
    // +03:00
    "Africa/Cairo",
    "Africa/Johannesburg",
    "Africa/Khartoum",
    "Africa/Maputo",
    "Africa/Tripoli",
    "Africa/Windhoek",
    "Asia/Amman",
    "Asia/Beirut",
    "Asia/Damascus",
    "Asia/Gaza",
    "Asia/Jerusalem",
    "Asia/Nicosia",
    "Europe/Athens",
    "Europe/Bucharest",
    "Europe/Chisinau",
    "Europe/Helsinki",
    "Europe/Kaliningrad",
    "Europe/Kiev",
    "Europe/Riga",
    "Europe/Sofia",
    "Europe/Tallinn",
    "Europe/Vilnius",
    // +02:00
    "Africa/Algiers",
    "Africa/Casablanca",
    "Africa/El_Aaiun",
    "Africa/Lagos",
    "Africa/Ndjamena",
    "Africa/Tunis",
    "Europe/Amsterdam",
    "Europe/Andorra",
    "Europe/Belgrade",
    "Europe/Berlin",
    "Europe/Brussels",
    "Europe/Budapest",
    "Europe/Copenhagen",
    "Europe/Dublin",
    "Europe/Gibraltar",
    "Europe/Luxembourg",
    "Europe/Madrid",
    "Europe/Malta",
    "Europe/Monaco",
    "Europe/Oslo",
    "Europe/Paris",
    "Europe/Prague",
    "Europe/Rome",
    "Europe/Stockholm",
    "Europe/Tirane",
    "Europe/Vienna",
    "Europe/Warsaw",
    "Europe/Zurich",
    // +01:00
    "Africa/Abidjan",
    "Africa/Accra",
    "Africa/Bissau",
    "Africa/Monrovia",
    "Africa/Sao_Tome",
    "America/Danmarkshavn",
    "Antarctica/Troll",
    "Atlantic/Canary",
    "Atlantic/Faroe",
    "Atlantic/Reykjavik",
    "Europe/Lisbon",
    "Europe/London",
    // +00:00
    "America/Scoresbysund",
    "Atlantic/Azores",
    "Atlantic/Cape_Verde",
    // -01:00
    "America/Noronha",
    // -02:00
    "America/Argentina/Buenos_Aires",
    "America/Montevideo",
    "America/Nuuk",
    "America/Paramaribo",
    "America/Punta_Arenas",
    "America/Sao_Paulo",
    "Antarctica/Palmer",
    // -03:00
    "America/St_Johns",
    // -03:30
    "America/Asuncion",
    "America/Barbados",
    "America/Blanc-Sablon",
    "America/Caracas",
    "America/Curacao",
    "America/Guyana",
    "America/Halifax",
    "America/La_Paz",
    "America/Manaus",
    "America/Port_of_Spain",
    "America/Puerto_Rico",
    "America/Santiago",
    "America/Santo_Domingo",
    "America/Thule",
    // -04:00
    "America/Atikokan",
    "America/Bogota",
    "America/Cancun",
    "America/Guayaquil",
    "America/Havana",
    "America/Jamaica",
    "America/Lima",
    "America/Nassau",
    "America/New_York",
    "America/Panama",
    "America/Port-au-Prince",
    "America/Rio_Branco",
    "America/Toronto",
    // -05:00
    "America/Belize",
    "America/Chicago",
    "America/Costa_Rica",
    "America/El_Salvador",
    "America/Guatemala",
    "America/Managua",
    "America/Mexico_City",
    "America/Regina",
    "America/Tegucigalpa",
    "America/Winnipeg",
    "Pacific/Easter",
    "Pacific/Galapagos",
    // -06:00
    "America/Chihuahua",
    "America/Denver",
    "America/Edmonton",
    "America/Hermosillo",
    "America/Phoenix",
    "America/Whitehorse",
    // -07:00
    "America/Los_Angeles",
    "America/Tijuana",
    "America/Vancouver",
    // -08:00
    "America/Anchorage",
    "Pacific/Gambier",
    // -09:00
    "Pacific/Marquesas",
    // -09:30
    "America/Adak",
    "Pacific/Honolulu",
    "Pacific/Tahiti",
    // -10:00
    "Pacific/Niue",
    "Pacific/Pago_Pago"
    // -11:00
];

export const manage_users1 = functions.region("europe-west1").pubsub.schedule("*/15 * * * 1")
    .timeZone("Pacific/Kiritimati")
    .onRun((context) => {
        return cf_flag.once("value").then((flag) => {
            if (flag.val() === 0) {
                return firstRun();
            } else {
                return manage_otherAppts_weeks();
            }
        }).catch((error) => {
            console.error("Getting the value of the cloud function flag failed with error: " + error);
            console.error("Function cannot continue to operate properly!");
        });
    });

function firstRun() {
    return update_refDate_weekFlags_cfFlag("Pacific/Kiritimati").then(() => {
        const promises: Array<any> = new Array();
        let problems: number = 0;

        zones.forEach((zone) => {
            const promise = tzs_info.child(format(zone)).child("props").child("updated").set(0).catch((error) => {
                console.error("Setting the value of the \"updated\" node for time zone " + zone + " to 0 failed with error: " + error);
                problems++;
            });

            promises.push(promise);
        });

        return Promise.allSettled(promises).then(() => {
            console.log("Resetting the value of the \"updated\" node for all time zones has finished. Problems: " + problems);
            return manage_otherAppts_weeks();
        });
    }).catch((error) => {
        console.error("Updating the reference date, cloud function flag or week flags failed with error: " + error);
        console.error("Function cannot continue to operate properly!");
    });
}

function update_refDate_weekFlags_cfFlag(time_zone: string) {
    const date: Date = new Date();
    const stringDate: string = moment.tz(date, time_zone).format("DD.MM.YYYY");

    const promise1 = dateRef.set(stringDate);
    const promise2 = weekFlagsRef.once("value").then((snapshot) => {
        const week0Flag: string = snapshot.child("week0").val();
        const week1Flag: string = snapshot.child("week1").val();
        const week2Flag: string = snapshot.child("week2").val();

        if (week0Flag === LAST_WEEK) {
            return updateWeekFalgs("", LAST_WEEK, THIS_WEEK, NEXT_WEEK);
        } else {
            if (week1Flag === LAST_WEEK) {
                return updateWeekFalgs(NEXT_WEEK, "", LAST_WEEK, THIS_WEEK);
            } else {
                if (week2Flag === LAST_WEEK) {
                    return updateWeekFalgs(THIS_WEEK, NEXT_WEEK, "", LAST_WEEK);
                } else {
                    return updateWeekFalgs(LAST_WEEK, THIS_WEEK, NEXT_WEEK, "");
                }
            }
        }
    });
    const promise3 = cf_flag.set(1);

    return Promise.all([promise1, promise2, promise3]);
}

function updateWeekFalgs(week0: string, week1: string, week2: string, week3: string) {
    return weekFlagsRef.update({
        "week0": week0,
        "week1": week1,
        "week2": week2,
        "week3": week3
    }).then(() => {
        console.log("Week flags update was successful.");
    }).catch((error) => {
        console.error("Week flags update failed with error: " + error);
    });
}

function manage_otherAppts_weeks() {
    const zonesToBeUpdated: Array<string> = new Array();
    const zonesUsersCount: Array<number> = new Array();
    const promises: Array<any> = new Array();
    let problems: number = 0;

    zones.forEach((zone) => {
        const promise = tzs_info.child(format(zone)).child("props").once("value");
        promises.push(promise);
    });

    return Promise.allSettled(promises).then((results) => {
        return dateRef.once("value").then((date) => {
            const ref_date: Date = getReferenceDate(date.val());

            results.forEach((result, i) => {
                if (result.status === "fulfilled") {
                    if (result.value.child("updated").val() === 0) {
                        const zone_date: Date = getTimeZoneDate(zones[i]);

                        if (zone_date > ref_date) {
                            zonesToBeUpdated.push(zones[i]);
                            zonesUsersCount.push(result.value.child("users_count").val());
                        }
                    }
                } else {
                    if (result.status === "rejected") {
                        console.error("Getting the value of the \"props\" node for time zone " + zones[i] + " failed with error: " + result.reason);
                        problems++;
                    }
                }
            });

            console.log("Getting the values of the \"props\" nodes for all time zones has finished. Problems: " + problems);
            console.log("Number of time zones that need to be updated now: " + zonesToBeUpdated.length);

            if (zonesToBeUpdated.length > 0) {
                return manageZonesToBeUpdated(zonesToBeUpdated, zonesUsersCount);
            } else { return null; }
        }).catch((error) => {
            console.error("Getting the reference date failed with error: " + error);
        });
    });
}

function manageZonesToBeUpdated(zonesToBeUpdated: Array<string>, zonesUsersCount: Array<number>) {
    const promises: Array<any> = new Array();

    if (zonesUsersCount.some((count) => count > 0)) {
        return weekFlagsRef.once("value").then((snapshot) => {
            const week0Flag: string = snapshot.child("week0").val();
            const week1Flag: string = snapshot.child("week1").val();
            const week2Flag: string = snapshot.child("week2").val();
            let weekToDelete: string = "";
    
            if (week0Flag === LAST_WEEK) {
                weekToDelete = "week3";
            } else {
                if (week1Flag === LAST_WEEK) {
                    weekToDelete = "week0";
                } else {
                    if (week2Flag === LAST_WEEK) {
                        weekToDelete = "week1";
                    } else {
                        weekToDelete = "week2";
                    }
                }
            }

            zonesToBeUpdated.forEach((zone, i) => {
                if (zonesUsersCount[i] > 0) {
                    const promise = tzs_info.child(format(zone)).child("uids").once("value").then((snapshot) => {
                        return manage(zone, snapshot, weekToDelete);
                    }).catch((error) => {
                        console.error("Getting all uids from time zone " + zone + " failed with error: " + error);
                    });

                    promises.push(promise);
                } else {
                    const promise = manage(zone);
                    promises.push(promise);
                }
            });

            return Promise.allSettled(promises);
        }).catch((error) => {
            console.error("Retrieving information from \"week_flags\" failed with error: " + error);
        });
    } else {
        zonesToBeUpdated.forEach((zone) => {
            const promise = manage(zone);
            promises.push(promise);
        });

        return Promise.allSettled(promises);
    }
}

function manage(zone: string, snapshot?: admin.database.DataSnapshot, weekToDelete?: string) {
    const promises: Array<any> = new Array();
    const usersToUpdate: Array<string> = new Array();

    if (snapshot !== undefined) {
        snapshot.forEach((child) => {
            usersToUpdate.push(child.key!);
        });
    }

    if (usersToUpdate.length > 0) {
        const promise1 = deleteLastWeek(zone, usersToUpdate, weekToDelete!);
        const promise2 = manageOtherAppts(zone, usersToUpdate);

        promises.push(promise1);
        promises.push(promise2);
    }

    const promise = tzs_info.child(format(zone)).child("props").child("updated").set(1).catch((error) => {
        console.error("Setting the value of the \"updated\" node for time zone " + zone + " failed with error: " + error);
    });
    promises.push(promise);

    return Promise.allSettled(promises);
}

function deleteLastWeek(time_zone: string, usersToUpdate: Array<string>, weekToDelete: string) {
    const promises: Array<any> = new Array();
    let problems: number = 0;

    usersToUpdate.forEach((uid) => {
        const promise = usersRef.child(uid).child(weekToDelete).remove().catch((error) => {
            console.error("Deleting " + weekToDelete + " for user " + uid + " failed with error: " + error);
            problems++;
        });

        promises.push(promise);
    });

    return Promise.allSettled(promises).then(() => {
        console.log("Deleting " + weekToDelete + " for all users in time zone " + time_zone + " has finished. Problems: " + problems);
    });
}

function manageOtherAppts(time_zone: string, usersToUpdate: Array<string>) {
    const promises: Array<any> = new Array();

    usersToUpdate.forEach((uid) => {
        const promise = usersRef.child(uid).child(OTHER_EVENTS).child("flags").once("value");
        promises.push(promise);
    });

    return Promise.allSettled(promises).then((results) => {
        const promises: Array<any> = new Array();
        let problems: number = 0;

        results.forEach((result, i) => {
            if (result.status === "fulfilled") {
                if (result.value.exists()) {
                    result.value.forEach((child: any) => {
                        let flag: number = child.val();
                        flag--;

                        if (flag < -2) {
                            const promise1 = usersRef.child(usersToUpdate[i]).child(OTHER_EVENTS).child("flags").child(child.key).remove().catch((error) => {
                                console.error("Deleting " + child.key + " flag for user " + usersToUpdate[i] + " failed with error: " + error);
                                problems++;
                            });

                            const promise2 = usersRef.child(usersToUpdate[i]).child(OTHER_EVENTS).child("info").child(child.key).remove().catch((error) => {
                                console.error("Deleting " + child.key + " info for user " + usersToUpdate[i] + " failed with error: " + error);
                                problems++;
                            });

                            promises.push(promise1);
                            promises.push(promise2);
                        } else {
                            const promise = usersRef.child(usersToUpdate[i]).child(OTHER_EVENTS).child("flags").child(child.key).set(flag).catch((error) => {
                                console.error("Updating " + child.key + " flag for user " + usersToUpdate[i] + " failed with error: " + error);
                                problems++;
                            });

                            promises.push(promise);
                        }
                    });
                }
            } else {
                if (result.status === "rejected") {
                    console.error("Getting the value of the \"other_ev/flags\" node for user " + usersToUpdate[i] + " in time zone " + time_zone + "failed with error: " + result.reason);
                    problems++;
                }
            }
        });
        
        return Promise.allSettled(promises).then(() => {
            console.log("Managing the \"other_ev\" node for all users in time zone " + time_zone + " has finished. Problems: " + problems);
        });
    });
}

function getTimeZoneDate(time_zone: string) {
    const date: Date = new Date();
    const stringDate: string = moment.tz(date, time_zone).format("DD.MM.YYYY");
    const timeZoneDate: Date = new Date(moment(stringDate, "DD.MM.YYYY").utc(true).format());

    return timeZoneDate;
}

function getReferenceDate(ref_date: string) {
    const date: Date = new Date(moment(ref_date, "DD.MM.YYYY").utc(true).format());
    date.setSeconds(date.getSeconds() - 1);

    return date;
}

function format(time_zone: string) {
    const indexes: Array<number> = new Array();

    for (let index = 0; index < time_zone.length; index++) {
        if (time_zone.charAt(index) === '/') indexes.push(index);
    }

    if (indexes.length == 2) {
        let stringToRemove: string = time_zone.substring(indexes[0], indexes[1]);
        time_zone = time_zone.replace(stringToRemove, "");
    }

    return time_zone;
}