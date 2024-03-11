import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const db = admin.database();
const tzs_info = db.ref("tzs_info");
const usersRef = db.ref("users");

export const delete_user_ref = functions.region("europe-west1").auth.user().onDelete((user) => {
    return usersRef.child(user.uid).child("country").child("tz").once("value").then((snapshot) => {
        const time_zone: string = snapshot.val();
        let problems: number = 0;

        const promise1 = usersRef.child(user.uid).remove().catch((error) => {
            console.error("Deleting the database reference for user " + user.uid + " failed with error: " + error);
            problems++;
        });

        const promise2 = tzs_info.child(format(time_zone)).child("uids").child(user.uid).remove().catch((error) => {
            console.error("Deleting the uid " + user.uid + " from time zone " + time_zone + " failed with error: " + error);
            problems++;
        });

        const promise3 = tzs_info.child(format(time_zone)).child("props").child("users_count").once("value").then((snapshot) => {
            let users_count: number = snapshot.val();
            users_count--;

            return tzs_info.child(format(time_zone)).child("props").child("users_count").set(users_count).catch((error) => {
                console.error("Decrementing the users count for time zone " + time_zone + " failed with error: " + error);
                problems++;
            });
        }).catch((error) => {
            console.error("Getting the users count for time zone " + time_zone + " failed with error: " + error);
            problems++;
        });

        return Promise.allSettled([promise1, promise2, promise3]).then(() => {
            console.log("Decrementing the users count for time zone " + time_zone + " and deleting all database references for user " + user.uid + " has finished. Problems: " + problems);
        });
    }).catch((error) => {
        console.error("Getting the time zone for user " + user.uid + " failed with error: " + error);
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