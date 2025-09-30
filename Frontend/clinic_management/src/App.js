import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Header from './component/layout/Header';
import Footer from './component/layout/Footer';
import MyToaster from './component/layout/MyToaster';
import Home from './component/layout/Home';
import Register from './component/authentication/Register';
import Login from './component/authentication/Login';
import { Container } from 'react-bootstrap';
import toast from "react-hot-toast"
import Finddoctor from './component/findDoctor/Finddoctor';
import { generateToken, messaging } from './component/notifications/firebase';
import { useEffect, useReducer } from "react"
import { onMessage } from 'firebase/messaging';
import MyUserReducer from './reducers/MyUserReducer';
import { MyDipatcherContext, MyUserContext } from './configs/MyContexts';
import cookie from 'react-cookies'
import Booking from './component/bookDoctor/Booking';
import Calendar from './component/findDoctor/Calendar';
import UploadLicense from './component/authentication/UploadLicense';
import Appointment from './component/bookDoctor/Appointment';
import RoomChat from './component/chat/RoomChat';
import Review from './component/reviewDoctor/Review';
import PaymentMethod from './component/payment/PaymentMethod';
import Invoice from './component/payment/Invoice';
import AppointmentUpdate from './component/bookDoctor/AppointmentUpdate';
import EditProfile from './component/authentication/EditProfile';
import ChangePassword from './component/authentication/ChangePassword';
import DoctorSchedule from './component/bookDoctor/DoctorSchedule';
import DoctorReview from './component/reviewDoctor/DoctorReview';
import HealthRecord from './component/healthRecord/HealthRecord';
import PaymentReturn from './component/payment/PaymentReturn';
import MedicineList from './component/medicine/MedicineList';
import MedicineDetail from './component/medicine/MedicineDetail';
import { endpoint } from "./configs/Apis";
const App = () => {
  //dispatch nhận action.type bên MyUserReducer.js -> F5 sẽ không mất vì đã lưu cookie
  const [user, dispatch] = useReducer(MyUserReducer, cookie.load('user') || null);

  // Tự động lấy lại thông tin user từ token khi reload app
  useEffect(() => {
    const checkAndRestoreUser = async () => {
      const token = cookie.load('token');
      if (token && (!user || !user.result)) {
        try {
          const res = await fetch(`${endpoint['current_user']}`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          const data = await res.json();
          if (data && data.result) {
            dispatch({ type: 'login', payload: data });
            cookie.save('user', data);
          }
        } catch (err) {
          cookie.remove('token');
          dispatch({ type: "logout" });
        }
      }
    };
    checkAndRestoreUser();
    generateToken();
    onMessage(messaging, (payload) => {
      console.log(payload);
      toast(payload.notification.body);
    })
  }, [user]);
  return (

    <MyUserContext.Provider value={user}>
      <MyDipatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Header />
          <Container fluid >
            <MyToaster />
            <Routes>
              {/* Permission lại bên BE */}
             
              <Route path="/doctorSchedule" element={<DoctorSchedule />} />
              <Route path="/updateAppointment" element={<AppointmentUpdate />} />
              <Route path="/roomchat" element={<RoomChat />} />
              <Route path="/appointment" element={<Appointment />} />
              <Route path="/upload-license" element={<UploadLicense />} />
              <Route path="/calendar" element={<Calendar />} />
              <Route path="/booking" element={<Booking />} />
              <Route path="/bookDoctor" element={<Booking />} />
              <Route path="/" element={<Home />} />
              <Route path="/register" element={<Register />} />
              <Route path="/login" element={<Login />} />
              <Route path="/findDoctor" element={<Finddoctor />} />
              <Route path="/review" element={<Review />} />
              <Route path="/payment-method" element={<PaymentMethod />} />
              <Route path="/payment-return" element={<PaymentReturn />} />
              <Route path="/invoice/:id" element={<Invoice />} />
              <Route path="/editProfile" element={<EditProfile />} />
              <Route path="/change-password" element={<ChangePassword />} />
              <Route path="/doctorReview" element={<DoctorReview />} />
              <Route path="/healthRecord" element={<HealthRecord />} />
              <Route path="/medicine" element={<MedicineList />} />
              <Route path="/medicine/:id" element={<MedicineDetail />} />
            </Routes>
          </Container>
          <Footer />
        </BrowserRouter>
      </MyDipatcherContext.Provider>
    </MyUserContext.Provider>


  )
}

export default App;
