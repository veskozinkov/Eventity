import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const db = admin.database();
const tzs_info = db.ref("tzs_info");
const usersRef = db.ref("users");

export const create_user_ref = functions.region("europe-west1").https.onCall((data, context) => {
    const userID: string = context.auth!.uid;
    const time_zone: string = data.tz;
    const iso: string = data.iso;
    const displayName: string = data.first_name + " " + data.last_name;
    const promises: Array<any> = new Array();
    let problems: number = 0;

    const promise1 = usersRef.child(userID).child("country").child("tz").set(time_zone).catch((error) => {
        console.error("Creating the \"country/tz\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise1);

    const promise2 = usersRef.child(userID).child("country").child("iso").set(iso).catch((error) => {
        console.error("Creating the \"country/iso\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise2);

    const promise3 = usersRef.child(userID).child("device_id").set("").catch((error) => {
        console.error("Creating the \"device_id\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise3);

    const promise4 = tzs_info.child(format(time_zone)).child("uids").child(userID).set("").catch((error) => {
        console.error("Adding the uid " + userID + " in time zone " + time_zone + " failed with error: " + error);
        problems++;
    });
    promises.push(promise4);

    const promise5 = tzs_info.child(format(time_zone)).child("props").child("users_count").once("value").then((snapshot) => {
        let users_count: number = snapshot.val();
        users_count++;

        return tzs_info.child(format(time_zone)).child("props").child("users_count").set(users_count).catch((error) => {
            console.error("Incrementing the users count for time zone " + time_zone + " failed with error: " + error);
            problems++;
        });
    }).catch((error) => {
        console.error("Getting the users count for time zone " + time_zone + " failed with error: " + error);
        problems++;
    });
    promises.push(promise5);

    const promise6 = admin.auth().updateUser(userID, {
        displayName: displayName
    }).catch((error) => {
        console.error("Setting the display name for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise6);

    const promise7 = usersRef.child(userID).child("other_set").child("time_f").set(0).catch((error) => {
        console.error("Creating the \"other_set/time_f\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise7);

    const promise8 = usersRef.child(userID).child("other_set").child("date_f").set(0).catch((error) => {
        console.error("Creating the \"other_set/date_f\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise8);

    const promise9 = usersRef.child(userID).child("other_set").child("notif").set(0).catch((error) => {
        console.error("Creating the \"other_set/notif\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise9);

    const promise10 = usersRef.child(userID).child("other_set").child("notif_sched").set(0).catch((error) => {
        console.error("Creating the \"other_set/notif_sched\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise10);

    const promise11 = usersRef.child(userID).child("other_set").child("char_inc").set(0).catch((error) => {
        console.error("Creating the \"other_set/char_inc\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise11);

    const promise12 = usersRef.child(userID).child("other_set").child("ev_inc").set(0).catch((error) => {
        console.error("Creating the \"other_set/ev_inc\" node for user " + userID + " failed with error: " + error);
        problems++;
    });
    promises.push(promise12);

    return Promise.allSettled(promises).then(() => {
        console.log("Incrementing the users count for time zone " + time_zone + " and creating all database references for user " + userID + " has finished. Problems: " + problems);
    });
});

function format(time_zone: string) {
    let indexes: Array<number> = new Array();

    for (let index = 0; index < time_zone.length; index++) {
        if (time_zone.charAt(index) == '/') indexes.push(index);
    }

    if (indexes.length == 2) {
        let stringToRemove: string = time_zone.substring(indexes[0], indexes[1]);
        time_zone = time_zone.replace(stringToRemove, "");
    }

    return time_zone;
}