import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const db = admin.database();
const cf_flag = db.ref("cf_flag");

export const manage_cfFlag = functions.region("europe-west1").pubsub.schedule("0 0 * * 4")
    .timeZone("Pacific/Kiritimati")
    .onRun((context) => {
        return cf_flag.set(0).then(() => {
            console.log("Resetting the cloud functions flag was successful.");
        }).catch((error) => {
            console.error("Resetting the cloud functions flag failed with error: " + error);
            console.error("Cloud function \"manage_users\" will not work properly!");
        });
    });