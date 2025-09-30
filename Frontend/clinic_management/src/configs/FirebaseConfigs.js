
import { initializeApp, getApps } from "firebase/app";
import { getFirestore } from "firebase/firestore";


const firebaseConfig = {
  apiKey: "AIzaSyDzlLHsoU8yFfUmcj3erBDib9ekw8st_dc",
  authDomain: "clinicchat-65245.firebaseapp.com",
  projectId: "clinicchat-65245",
  storageBucket: "clinicchat-65245.appspot.com",
  messagingSenderId: "162634098218",
  appId: "1:162634098218:web:720b85ac64277fb3108553"
};

const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApps()[0];

const db = getFirestore(app);

export { db };
