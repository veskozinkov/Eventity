import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

export const admin_delete_user = functions.region("europe-west1").https.onCall((data) => {
    const userID: string = data.uid;

    return admin.auth().deleteUser(userID).catch((error) => {
        console.error("Deleting user " + userID + " by the admin SDK failed with error: " + error);
    });
});