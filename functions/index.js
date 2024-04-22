const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotification = functions.database
    .ref("/blindauction/{itemId}/startdate")
    .onUpdate(async (change, context) => {
      const startTime = change.after.val();
      const currentTime = new Date().getTime();
      const fiveMinutesInMillis = 5 * 60 * 1000;

      if (startTime - currentTime <= fiveMinutesInMillis) {
        // Send notification to all users
        const payload = {
          notification: {
            title: "Auction Reminder",
            body: "The auction is starting soon!",
          },
        };
        // Fetch tokens from the database
        const allTokensSnapshot = await admin.database()
            .ref("/tokens").once("value");
        if (allTokensSnapshot.exists()) {
          const allTokensData = allTokensSnapshot.val();
          const tokens = Object.values(allTokensData)
              .map((userTokens) => userTokens.fcmToken);
          await admin.messaging().sendToDevice(tokens, payload);
          console.log("Notification sent to all users.");
        } else {
          console.log("No tokens found.");
        }
      }
    });