import axios from "axios"
import cookie from 'react-cookies'

export const AI_URL = 'https://openrouter.ai/api/v1/chat/completions';
export const OPENROUTER_API_KEY = "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";
export const BASE_URL_FIREBASE = 'http://127.0.0.1:5001/clinicchat-65245/us-central1/app'

// CÓ thể THAY ĐỔI 
// chạy local
// export const BASE_URL = 'http://localhost:8080/Clinic-Haubookingcare/api/'
// deploy render
export const BASE_URL = 'https://haubookingcare-clinic-system.onrender.com/api/'

export const CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dwwfgtxv4/image/upload";
export const CLOUDINARY_PRESET = "clinicapp";

export const endpoint = {
    doctorById: (doctorId) => `/doctors/${doctorId}`,
    deleteAllNotifications: '/notifications/all', // DELETE để xóa tất cả thông báo
    markAllNotificationsRead: '/notifications/read-all', // POST để đánh dấu tất cả đã đọc
    markNotificationRead: (id) => `/notifications/${id}/read`, // POST để đánh dấu 1 thông báo đã đọc
    // Quản lý người dùng
    'register': '/users', // Đăng ký
    'login': '/auth/login', // Đăng nhập
    'logout': '/auth/logout', // Đăng xuất
    'introspect': '/auth/introspect', // Kiểm tra token
    // 'refresh': '/auth/refresh', // Đã bỏ làm mới token
    'current_user': '/users/profile', // Lấy thông tin user hiện tại
    'update_user': (userId) => `/users/${userId}`, // Cập nhật thông tin user
    'change_password': '/users/change-password', // Đổi mật khẩu

    // Device Token cho thông báo push
    saveDeviceToken: '/device-token', // POST để lưu token
    removeDeviceToken: '/device-token', // DELETE để xóa token

    // Notification
    notifications: '/notifications', // GET để lấy thông báo

    // Quản lý hồ sơ sức khỏe
    health_record_create: '/health-records', // Tạo hồ sơ
    health_record_update: (id) => `/health-records/${id}`, // Cập nhật hồ sơ
    health_record_by_user: (userId) => `/health-records/user/${userId}`, // Lấy hồ sơ theo user
    health_record_by_appointment: (appointmentId) => `/health-records/appointment/${appointmentId}`, // Lấy hồ sơ theo lịch hẹn

    'doctor_license': '/doctor-licenses', // Tải lên giấy phép (POST)
    update_doctor_license: (id) => `/doctor-licenses/${id}`, // Cập nhật giấy phép (PUT)
    findLicenseByDoctorId : (doctorId) => `/doctor-licenses/${doctorId}`, // Lấy giấy phép theo bác sĩ (GET)

    // Quản lý bác sĩ
    'doctor': '/doctors', // Lấy danh sách bác sĩ
    'findDoctor': '/find_slot', // Tìm lịch trống
    findDoctorById: (doctorId) => `/find_slot?doctorId=${doctorId}`, // Tìm lịch trống theo bác sĩ

    // Quản lý lịch hẹn (theo backend controller)
    'appointments': '/appointments', // GET: Danh sách lịch hẹn, POST: Tạo lịch hẹn
    updateAppointment: (appointmentId) => `/appointments/${appointmentId}`, // PUT: Cập nhật lịch hẹn
    deleteAppointment: (appointmentId) => `/appointments/${appointmentId}`, // DELETE: Xóa lịch hẹn
    updateAppointmentStatus: (appointmentId) => `/appointments/${appointmentId}/status`, // PATCH: Cập nhật trạng thái

    // Quản lý đánh giá bác sĩ
    'reviews': '/reviews', // Endpoint gốc cho đánh giá
    reviewsOfDoctor: (doctorId) => `/reviews/doctor/${doctorId}`, // Lấy đánh giá theo bác sĩ
    reviewByAppointment: (appointmentId) => `/reviews/appointment/${appointmentId}`, // Lấy đánh giá theo lịch hẹn
    updateDoctorResponse: (reviewId) => `/reviews/${reviewId}`, // Sửa phản hồi bác sĩ

    // Quản lý hóa đơn (Invoice)
    'invoices': '/invoices', // Tạo hóa đơn
    invoiceById: (id) => `/invoices/${id}`, // Lấy hóa đơn theo ID
    invoiceByAppointment: (appointmentId) => `/invoices/appointment/${appointmentId}`, // Lấy hóa đơn theo lịch hẹn
    invoicesByUser: (userId) => `/invoices/user/${userId}`, // Lấy tất cả hóa đơn của user
    updateInvoiceStatus: (invoiceId) => `/invoices/${invoiceId}`, // Cập nhật trạng thái hóa đơn

    // Quản lý thanh toán (Payment)
    'payments': '/payments', // Endpoint gốc cho thanh toán
    paymentById: (id) => `/payments/${id}`, // Lấy payment theo ID
    paymentsByInvoice: (invoiceId) => `/payments/invoice/${invoiceId}`, // Lấy payments theo hóa đơn
    paymentByTransaction: (transactionId) => `/payments/transaction/${transactionId}`, // Lấy thanh toán theo transaction
    updatePaymentStatus: (paymentId) => `/payments/${paymentId}`, // Cập nhật trạng thái thanh toán

    // VNPay Integration
    'vnpay_create': '/vnpay/create-payment', // Tạo payment VNPay
    'vnpay_return': '/vnpay/return', // VNPay callback return URL
    vnpay_status: (txnRef) => `/vnpay/payment-status/${txnRef}`, // Kiểm tra trạng thái thanh toán

    // VNPay Integration
    vnpayCreatePayment: '/vnpay/create-payment', // Tạo URL thanh toán VNPay
    vnpayReturn: '/vnpay/return', // Callback từ VNPay
    vnpayStatus: (txnRef) => `/vnpay/payment-status/${txnRef}`, // Kiểm tra trạng thái thanh toán

    // Quản lý thuốc
    medicines: '/medicines', // Lấy danh sách, tạo mới thuốc
    medicineById: (id) => `/medicines/${id}`, // Lấy chi tiết thuốc theo id

    // Quản lý lịch làm việc bác sĩ (DoctorSchedule)
    doctorSchedule: '/doctor_schedule', // Đăng ký lịch làm việc
    updateDoctorSchedule: (id) => `/doctor_schedule/${id}`, // Cập nhật lịch làm việc
    getDoctorScheduleById: (doctorId) => `/doctor_schedule/${doctorId}`, // Lấy lịch làm việc theo bác sĩ
    deleteDoctorSchedule: (id) => `/doctor_schedule/${id}`, // Xóa lịch làm việc

    // Quản lý chat (Firebase)
    'chats': '/chats', // Tạo phòng chat
    'uploadImage': '/upload-image', // Upload ảnh
    chatMessages: (chatId) => `/chats/${chatId}/messages`, // Lấy/gửi tin nhắn

    // PayPal Integration
    'paypal_create': '/paypal/create-payment', // Tạo payment PayPal
    'paypal_success': '/paypal/success', // PayPal callback success
    'paypal_cancel': '/paypal/cancel', // PayPal callback cancel
}


export const fbApis = () => {
    return axios.create({
        baseURL: BASE_URL_FIREBASE,

    })
};

//json
export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`,
            'Content-Type': 'application/json'
        }
    })
}


export const authformdataApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`,
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}


export const apisAI = () => {
    return axios.create({
        baseURL: AI_URL,
        timeout: 50000,
        headers: {
            "Authorization": "Bearer " + OPENROUTER_API_KEY,
            "Content-Type": "application/json",
            "X-Title": "HAUBOOKINGCARE",
            "HTTP-Referer": "http://localhost:3000/",

        }
    })


}

export default axios.create({
    baseURL: BASE_URL
})
