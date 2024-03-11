import * as moment from 'moment-timezone';

const date: Date = new Date();
console.log(date);

console.log(moment.tz(date, "Pacific/Kiritimati").format());

const stringDate: string = moment.tz(date, "Pacific/Kiritimati").format("DD.MM.YYYY");
//const newDate: Date = new Date(moment(stringDate, "DD.MM.YYYY").utc(true).format());

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

console.log(stringDate);

/*const stringDate: string = moment.tz(date, "Pacific/Kiritimati").format();//.format("DD.MM.YYYY HH:mm:ss");
console.log("stringDate: " + stringDate);

const newDate: Date = new Date(stringDate);
console.log(newDate);*/

console.log("--------------- Zones: " + zones.length + " --------------------");

/*const zone_date: Date = getTimeZoneDate("Pacific/Chatham");
const ref_date: Date = getReferenceDate("21.12.2020");
const daate: Date = new Date(moment("21.12.2020", "DD.MM.YYYY").utc(true).format());

console.log(zone_date);
console.log(ref_date);
console.log(daate);

if (daate > ref_date) {
    console.log("yyyyyyyyyyyyyyyy");
} else {
    console.log("nnnnnnnnnnnnnnnn");
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
}*/