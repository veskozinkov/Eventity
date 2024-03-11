import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const db = admin.database();
const usersRef = db.ref("users");

export const delete_all_user_events = functions.region("europe-west1").https.onCall((data, context) => {
    const userID: string = context.auth!.uid;
    const promises: Array<any> = new Array();

    const promise1 = usersRef.child(userID).child("week0").remove().catch((error) => {
        console.error("Deleting the \"week0\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise1);

    const promise2 = usersRef.child(userID).child("week1").remove().catch((error) => {
        console.error("Deleting the \"week1\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise2);

    const promise3 = usersRef.child(userID).child("week2").remove().catch((error) => {
        console.error("Deleting the \"week2\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise3);

    const promise4 = usersRef.child(userID).child("week3").remove().catch((error) => {
        console.error("Deleting the \"week3\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise4);

    const promise5 = usersRef.child(userID).child("other_ev").remove().catch((error) => {
        console.error("Deleting the \"other_ev\" node for user " + userID + " failed with error: " + error);
    });
    promises.push(promise5);

    return Promise.all(promises).catch((error) => {
        console.error("Deleting all saved events for user " + userID + " failed with error: " + error);
    });
});