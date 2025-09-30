// firebase-messaging-sw.js (Đặt ở thư mục public nếu dùng React hoặc Vite)

importScripts('https://www.gstatic.com/firebasejs/9.6.10/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.6.10/firebase-messaging-compat.js');

// Khởi tạo Firebase App
firebase.initializeApp({
  apiKey: "AIzaSyA2...",
  authDomain: "clinicchat-65245.firebaseapp.com",
  projectId: "clinicchat-65245",
  storageBucket: "clinicchat-65245.appspot.com",
  messagingSenderId: "...",
  appId: "1:...:web:...",
  measurementId: "G-..."
});

// Lấy instance messaging
const messaging = firebase.messaging();

// Xử lý thông báo khi ở background
messaging.onBackgroundMessage(function (payload) {
  console.log('[firebase-messaging-sw.js] Received background message ', payload);

  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: payload.notification.image,
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
