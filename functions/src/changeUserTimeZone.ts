import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const db = admin.database();
const tzs_info = db.ref("tzs_info");
const usersRef = db.ref("users");

export const change_user_tz = functions.region("europe-west1").https.onCall((data, context) => {
    const userID: string = context.auth!.uid;
    const new_time_zone: string = data.new_tz;
    const old_time_zone: string = data.old_tz;
    const iso: string = data.iso;
    const promises: Array<any> = new Array();

    const promise1 = usersRef.child(userID).child("country").child("tz").set(new_time_zone).catch((error) => {
        console.error("Changing the \"country/tz\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise1);

    const promise2 = usersRef.child(userID).child("country").child("iso").set(iso).catch((error) => {
        console.error("Changing the \"country/iso\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise2);

    const promise3 = tzs_info.child(format(old_time_zone)).child("uids").child(userID).remove().catch((error) => {
        console.error("Deleting the uid " + userID + " from time zone " + old_time_zone + " failed with error: " + error);
    });
    promises.push(promise3);

    const promise4 = tzs_info.child(format(old_time_zone)).child("props").child("users_count").once("value").then((snapshot) => {
        let users_count: number = snapshot.val();
        users_count--;

        return tzs_info.child(format(old_time_zone)).child("props").child("users_count").set(users_count).catch((error) => {
            console.error("Decrementing the users count for time zone " + old_time_zone + " failed with error: " + error);
        });
    }).catch((error) => {
        console.error("Getting the users count for time zone " + old_time_zone + " failed with error: " + error);
    });
    promises.push(promise4);

    const promise5 = tzs_info.child(format(new_time_zone)).child("uids").child(userID).set("").catch((error) => {
        console.error("Adding the uid " + userID + " in time zone " + new_time_zone + " failed with error: " + error);
    });
    promises.push(promise5);

    const promise6 = tzs_info.child(format(new_time_zone)).child("props").child("users_count").once("value").then((snapshot) => {
        let users_count: number = snapshot.val();
        users_count++;

        return tzs_info.child(format(new_time_zone)).child("props").child("users_count").set(users_count).catch((error) => {
            console.error("Incrementing the users count for time zone " + new_time_zone + " failed with error: " + error);
        });
    }).catch((error) => {
        console.error("Getting the users count for time zone " + new_time_zone + " failed with error: " + error);
    });
    promises.push(promise6);

    return Promise.all(promises).catch((error) => {
        console.error("Changing the the time zone for user " + userID + " from " + old_time_zone + " to " + new_time_zone + " failed with error: " + error);
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