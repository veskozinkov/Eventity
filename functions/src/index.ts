import * as admin from "firebase-admin";
admin.initializeApp();

import * as func1 from "./manage_cfFlag";
import * as func2 from "./onDeleteUser";
import * as func3 from "./onCreateUser";
import * as func4 from "./manageUsers1";
import * as func5 from "./manageUsers2";
import * as func6 from "./changeUserTimeZone";
import * as func7 from "./adminDeleteUser";
import * as func8 from "./deleteAllUserEvents";

exports.manage_cfFlag = func1.manage_cfFlag;
exports.delete_user_ref = func2.delete_user_ref;
exports.create_user_ref = func3.create_user_ref;
exports.manage_users1 = func4.manage_users1;
exports.manage_users2 = func5.manage_users2;
exports.change_user_tz = func6.change_user_tz;
exports.admin_delete_user = func7.admin_delete_user;
exports.delete_all_user_events = func8.delete_all_user_events;