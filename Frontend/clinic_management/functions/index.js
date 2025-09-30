
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendMessageNotification = functions.firestore
	.document('messages/{messageId}')
	.onCreate(async (snap, context) => {
		const message = snap.data();
		const receiverId = message.receiverId;

		// Lấy device token của receiver từ Firestore (giả sử bạn lưu token ở users/{userId}/deviceToken)
		const userDoc = await admin.firestore().collection('users').doc(receiverId).get();
		const deviceToken = userDoc.data().deviceToken;

		if (deviceToken) {
			const payload = {
				notification: {
					title: 'Tin nhắn mới',
					body: message.text || 'Bạn có tin nhắn mới!',
				}
			};
			await admin.messaging().sendToDevice(deviceToken, payload);
		}
	});
