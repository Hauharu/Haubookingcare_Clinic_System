//Phần này cho phần cloud messaging


import { initializeApp } from "firebase/app";
import { getMessaging, getToken } from "firebase/messaging";

const firebaseConfig = {
  apiKey: "AIzaSyDzlLHsoU8yFfUmcj3erBDib9ekw8st_dc",
  authDomain: "clinicchat-65245.firebaseapp.com",
  projectId: "clinicchat-65245",
  storageBucket: "clinicchat-65245.appspot.com",
  messagingSenderId: "162634098218",
  appId: "1:162634098218:web:720b85ac64277fb3108553"
};

const app = initializeApp(firebaseConfig);
export const messaging = getMessaging(app);

export const generateToken = async () => {
    try {
        const permission = await Notification.requestPermission();
        console.log('Notification permission:', permission);

        if (permission === "granted") {
            const token = await getToken(messaging, {
                vapidKey: "BLlvwyutCIsF1y3dAHGWtiHmj1c_-CEHUkgCombQR4Jk2bltBa_gounfvQV3tGF8N5UTE71bnYp30J8ibI8sT2s",
            });
            console.log('FCM token:', token);
            return token;
        } else {
            console.log('Notification permission denied');
            return null;
        }
    } catch (error) {
        console.error('Error getting notification token:', error);
        return null;
    }
}