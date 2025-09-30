import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap"
import { Link } from "react-router-dom"
import 'bootstrap-icons/font/bootstrap-icons.css';
import "./Styles/Header.css";
import { useContext, useEffect, useState } from "react";
import { useRef } from "react";
import { MyDipatcherContext, MyUserContext } from "../../configs/MyContexts";
import { endpoint } from "../../configs/Apis";
import Apis from "../../configs/Apis";
// Đã bỏ import refreshToken
import cookie from 'react-cookies';
import { useNavigate } from "react-router-dom";
import { generateToken, messaging } from "../notifications/firebase";
import { useTranslation } from 'react-i18next';
import { onMessage } from "firebase/messaging";
import GlitchText from "../reactbits/GlitchText";
const Header = () => {
    const { t, i18n } = useTranslation();
    // Ref cho dropdown trò chuyện
    const chatDropdownRef = useRef(null);
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDipatcherContext);
    // State cho danh sách lịch hẹn để chat
    const [chatAppointments, setChatAppointments] = useState([]);
    const [showChatDropdown, setShowChatDropdown] = useState(false);

    // Đóng hộp thoại Trò chuyện khi click ra ngoài
    useEffect(() => {
        if (!showChatDropdown) return;
        function handleClickOutside(event) {
            if (chatDropdownRef.current && !chatDropdownRef.current.contains(event.target)) {
                setShowChatDropdown(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [showChatDropdown]);

    // Lấy lịch hẹn đã đặt, đã khám cho user
    const fetchAppointments = async () => {
        if (!user?.result?.id) return;
        try {
            const token = cookie.load('token');
            // Lấy tất cả lịch hẹn của user (bệnh nhân hoặc bác sĩ) giống Appointment.js
            let url = endpoint.appointments + `?page=0`;
            const res = await Apis.get(url, {
                headers: { Authorization: `Bearer ${token}` }
            });
            let arr = Array.isArray(res.data.result) ? res.data.result : (Array.isArray(res.data) ? res.data : []);
            // Chỉ lấy trạng thái đã đặt, đã khám
            arr = arr.filter(a => a.status === 'SCHEDULED' || a.status === 'COMPLETED');
            // Lọc chỉ lấy 1 cuộc trò chuyện cho mỗi bác sĩ (lấy lịch mới nhất)
            const latestAppointments = {};
            arr.forEach(a => {
                if (!latestAppointments[a.doctorId] || new Date(a.appointmentTime) > new Date(latestAppointments[a.doctorId].appointmentTime)) {
                    latestAppointments[a.doctorId] = a;
                }
            });
            setChatAppointments(Object.values(latestAppointments));
        } catch (err) {
            setChatAppointments([]);
        }
    };
    useEffect(() => {
        fetchAppointments();
    }, [user]);

    // Cho phép gọi từ ngoài component để cập nhật chatAppointments
    window.refreshChatAppointments = fetchAppointments;
    // Hàm lấy thông báo từ backend
    const fetchNotifications = async () => {
        if (user?.result?.id) {
            try {
                const token = cookie.load('token');
                const res = await Apis.get(endpoint.notifications, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (Array.isArray(res.data.result)) {
                    setNotifications(res.data.result);
                } else {
                    setNotifications([]);
                }
            } catch (err) {
                // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            }
        }
    };

    // Cho phép gọi từ ngoài component
    window.refreshNotifications = fetchNotifications;
    // Ref cho dropdown thông báo
    const dropdownRef = useRef(null);
    const nav = useNavigate();

    // Hàm logout gọi API trước khi dispatch
    const handleLogout = async () => {
        const token = cookie.load('token');
        if (token) {
            try {
                await Apis.post(endpoint['logout'], { token });
            } catch (err) {
                if (err.response && err.response.status === 401) {
                    // Nếu lỗi 401, thử làm mới token
                }
                // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            }
        }
        if (user?.result?.id) {
            try {
                await Apis.delete(endpoint.removeDeviceToken, { params: { userId: user.result.id } });
            } catch (err) {
                // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            }
        }
        dispatch({ type: "logout" });
        nav("/login");
    };


    //Lấy tb ra
    const [notifications, setNotifications] = useState([]);
    //đếm tb đọc hay chưa
    const [unreadCount, setUnreadCount] = useState(0);
    const [showDropdown, setShowDropdown] = useState(false);




    // Khi user đăng nhập, xin quyền notification nếu chưa cấp
    useEffect(() => {
        if (user) {
            if (window.Notification && Notification.permission === "default") {
                Notification.requestPermission().finally(() => {
                    fetchNotifications();
                });
            } else {
                fetchNotifications();
            }
        }
    }, [user]);

    // Gọi lại fetchNotifications mỗi khi appointments thay đổi
    useEffect(() => {
        fetchNotifications();
    }, [chatAppointments]);

    // Lắng nghe sự kiện custom để cập nhật chuông thông báo từ các component khác
    useEffect(() => {
        const handler = () => fetchNotifications();
        window.addEventListener('refresh-notifications', handler);
        return () => window.removeEventListener('refresh-notifications', handler);
    }, []);

    // Đóng dropdown khi click ra ngoài
    useEffect(() => {
        if (!showDropdown) return;
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [showDropdown]);

    useEffect(() => {
        // Lắng nghe thông báo push từ FCM
        const unsubscribe = onMessage(messaging, (payload) => {
            try {
                fetchNotifications();
            } catch (error) {
                // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            }
        });
        return () => unsubscribe();
    }, [user]);

    useEffect(() => {
    const unread = notifications.filter(n => !n.read).length;
    setUnreadCount(unread);
    }, [notifications]);
    // Không lưu vào localStorage nữa, luôn lấy từ backend




    // Đánh dấu tất cả đã đọc: gọi API backend để cập nhật trạng thái
    const handleReadAll = async () => {
        const token = cookie.load('token');
        try {
            await Apis.post(endpoint.markAllNotificationsRead, {}, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            setShowDropdown(false);
            window.dispatchEvent(new Event('refresh-notifications'));
            await fetchNotifications();
            window.toast && window.toast.success("Đã đánh dấu tất cả thông báo đã đọc!");
        } catch (err) {
            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            window.toast && window.toast.error("Lỗi khi đánh dấu tất cả đã đọc!");
        }
    };


    return (
        <Navbar collapseOnSelect expand="lg" variant="light" bg="light" className="custom-header">
            <Container className="p-0">
                <Navbar.Brand as={Link} to="/" className="header-logo-link ">


                    <h2 className="logo-title">
                        <span className="logo-health">OU</span>
                        <span className="logo-care">BOOKINGCARE</span>
                    </h2>


                </Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto header-menu text-center gap-3 gap-lg-4">
                        
                        {user === null || (user.result && user.result.role === "PATIENT") ? (
                            <Link to="/findDoctor" className="nav-item-with-subtext nav-link ms-4 text-center">
                                {t('header.bookDoctor')}
                                <span>{t('header.bookNow')}</span>
                            </Link>
                        ) : null}

                        {user !== null ? <>
                            <Link to="/appointment" className="nav-item-with-subtext nav-link ms-4 text-center">
                                {t('header.myAppointment')}
                                <span>{t('header.viewAppointment')}</span>
                            </Link>
                        </> : <></>}

                        <Link to="/review" className="nav-item-with-subtext nav-link ms-4 text-center">
                            {t('header.viewReview')}
                            <span>{t('header.doctorReview')}</span>
                        </Link>

                        <Link to="/medicine" className="nav-item-with-subtext nav-link ms-4 text-center">
                            {t('header.medicineLookup')}
                            <span>{t('header.viewPrescription')}</span>
                        </Link>

                        {user !== null && user.result && user.result.role === "DOCTOR" ? <>
                            <Link className="nav-item-with-subtext nav-link ms-4 text-center" to={`/review/?doctorId=${user.result.id}`}>{t('header.personalReview')}
                                <span>{t('header.patientReview')}</span> </Link>
                        </> : <></>}

                    </Nav>
                    {/* Mục Trò chuyện */}
                    {user !== null && (
                        <Nav.Item className="position-relative me-4">
                            <Button variant="link" className="p-0" onClick={() => setShowChatDropdown(!showChatDropdown)}>
                                <i className="bi bi-chat-dots" style={{ fontSize: 24 }}></i>
                            </Button>
                            {showChatDropdown && (
                                <div ref={chatDropdownRef} className="notification-dropdown shadow rounded bg-white position-absolute" style={{ right: 0, zIndex: 1000, width: 300, maxHeight: 400, overflowY: 'auto' }}>
                                    <div className="d-flex justify-content-between align-items-center p-2 border-bottom">
                                        <strong>Trò chuyện</strong>
                                        <Button variant="link" size="sm" onClick={() => setShowChatDropdown(false)}>Đóng</Button>
                                    </div>
                                    {chatAppointments.length === 0 ? (
                                        <div className="text-center p-2">Không có lịch hẹn để trò chuyện</div>
                                    ) : (
                                        chatAppointments.slice(0, 6).map((a, idx) => {
                                            // Xác định trạng thái lịch hẹn
                                            let statusText = '';
                                            switch (a.status) {
                                                case 'SCHEDULED':
                                                    statusText = 'Đã đặt';
                                                    break;
                                                case 'COMPLETED':
                                                    statusText = 'Đã khám';
                                                    break;
                                                case 'CANCELLED':
                                                    statusText = 'Đã hủy';
                                                    break;
                                                default:
                                                    statusText = a.status;
                                            }
                                            // Xác định tên đối tượng trò chuyện
                                            let chatName = '';
                                            if (user?.result?.role === 'PATIENT') {
                                                chatName = a.doctorName || '---';
                                            } else if (user?.result?.role === 'DOCTOR') {
                                                chatName = a.patientName || a.patientFullName || '---';
                                            } else {
                                                chatName = a.doctorName || a.patientName || '---';
                                            }
                                            return (
                                                <div key={a.id || idx} style={{background: '#f8f9fa', padding: '8px 12px', borderBottom: '1px solid #dee2e6'}}>
                                                    <div><strong>Ngày hẹn:</strong> {new Date(a.appointmentTime).toLocaleDateString('vi-VN')}</div>
                                                    <div><strong>Thời gian:</strong> {new Date(a.appointmentTime).toLocaleTimeString('vi-VN')}</div>
                                                    <div><strong>{user?.result?.role === 'PATIENT' ? 'Bác sĩ' : 'Bệnh nhân'}:</strong> {chatName}</div>
                                                    <div><strong>Trạng thái:</strong> {statusText}</div>
                                                    <Button variant="success" size="sm" className="mt-1" onClick={() => {
                                                        // Chuyển đến phòng chat, truyền appointment
                                                        nav('/roomchat', { state: { appointment: a } });
                                                        setShowChatDropdown(false);
                                                    }}>
                                                        <i className="bi bi-chat-dots me-1"></i> Vào trò chuyện
                                                    </Button>
                                                </div>
                                            );
                                        })
                                    )}
                                </div>
                            )}
                        </Nav.Item>
                    )}
                    {/* Thông báo ưu đãi */}
                    {user !== null ? <>
                        <Nav.Item className="position-relative me-3">
                            <Button variant="link" className="p-0" onClick={() => setShowDropdown(!showDropdown)}>
                                <i className="bi bi-bell" style={{ fontSize: 24 }}></i>
                                {unreadCount > 0 && (
                                    <span
                                        style={{
                                            position: "absolute",
                                            top: 0,
                                            right: 0,
                                            background: "red",
                                            color: "white",
                                            borderRadius: "50%",
                                            fontSize: 12,
                                            width: 18,
                                            height: 18,
                                            display: "flex",
                                            alignItems: "center",
                                            justifyContent: "center"
                                        }}
                                    >
                                        {unreadCount}
                                    </span>
                                )}
                            </Button>

                            {showDropdown && (
                                <div ref={dropdownRef} className="notification-dropdown shadow rounded bg-white position-absolute" style={{ right: 0, zIndex: 1000, width: 300 }}>
                                    <div className="d-flex justify-content-between align-items-center p-2 border-bottom">
                                        <strong>{t("header.notifications")}</strong>
                                        <div>
                                            <Button variant="link" size="sm" onClick={handleReadAll}>{t("header.markReadAll")}</Button>
                                            <Button variant="link" size="sm" style={{ color: 'red' }} onClick={async () => {
                                                const token = cookie.load('token');
                                                try {
                                                    await Apis.delete(endpoint.deleteAllNotifications, {
                                                        headers: {
                                                            Authorization: `Bearer ${token}`
                                                        }
                                                    });
                                                    setNotifications([]);
                                                    setUnreadCount(0);
                                                    await fetchNotifications(); // Lấy lại thông báo từ backend
                                                } catch (err) {
                                                    console.error(t("header.deleteAllError"), err);
                                                }
                                            }}>{t("header.deleteAll")}</Button>
                                        </div>
                                    </div>
                                    {notifications.length === 0 ? (
                                        <div className="text-center p-2">{t("header.noNotifications")}</div>
                                    ) : (
                                        notifications.slice(0, 5).map((n, idx) => (
                                            <div
                                                key={n.id || idx}
                                                style={{
                                                    background: n.isRead ? "#f8f9fa" : "#e9ecef",
                                                    padding: "8px 12px",
                                                    borderBottom: "1px solid #dee2e6"
                                                }}
                                            >
                                                <strong>{n.title}</strong>
                                                <div>{n.content}</div>
                                                <small className="text-muted">
                                                    {n.createdAt && !isNaN(new Date(n.createdAt).getTime())
                                                        ? new Date(n.createdAt).toLocaleString("vi-VN")
                                                        : t("header.unknownTime")}
                                                </small>
                                                {!n.isRead && (
                                                    <Button variant="link" size="sm" style={{color: 'blue'}} onClick={async () => {
                                                        const token = cookie.load('token');
                                                        try {
                                                            await Apis.post(endpoint.markNotificationRead(n.id), {}, {
                                                                headers: {
                                                                    Authorization: `Bearer ${token}`
                                                                }
                                                            });
                                                            window.dispatchEvent(new Event('refresh-notifications'));
                                                            await fetchNotifications();
                                                            window.toast && window.toast.success(t("header.markReadSuccess"));
                                                        } catch (err) {
                                                            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
                                                            window.toast && window.toast.error(t("header.markReadError"));
                                                        }
                                                    }}>{t("header.markRead")}</Button>
                                                )}
                                            </div>
                                        ))
                                    )}
                                </div>
                            )}
                        </Nav.Item>

                    </> : <>
                    </>}

                    <Nav className="header-auth ">
                    {/* Dropdown chuyển đổi ngôn ngữ bằng ảnh lá cờ từ public, căn giữa bằng flexbox */}
                    <Nav.Item className="position-relative me-2" style={{ display: 'flex', alignItems: 'center' }}>
                        <NavDropdown
                            title={
                                <span>
                                    <img
                                        src={i18n.language === 'vi' ? '/assets/images/vn.png' : '/assets/images/en.png'}
                                        alt={i18n.language === 'vi' ? 'Vietnamese' : 'English'}
                                        style={{ width: 28, height: 20, objectFit: 'cover', borderRadius: 3 }}
                                    />
                                </span>
                            }
                            id="language-dropdown"
                            align="end"
                        >
                            <NavDropdown.Item onClick={() => i18n.changeLanguage('vi')} active={i18n.language === 'vi'}>
                                <img src="/assets/images/vn.png" alt={t('header.vietnamese')} style={{ width: 24, height: 16, marginRight: 8, borderRadius: 2 }} /> {t('header.vietnamese')}
                            </NavDropdown.Item>
                            <NavDropdown.Item onClick={() => i18n.changeLanguage('en')} active={i18n.language === 'en'}>
                                <img src="/assets/images/en.png" alt={t('header.english')} style={{ width: 24, height: 16, marginRight: 8, borderRadius: 2 }} /> {t('header.english')}
                            </NavDropdown.Item>
                        </NavDropdown>
                    </Nav.Item>

                        {user === null ? <>
                            <Button variant="outline-success" as={Link} to="/register" className="me-2 sign-in-btn">
                                {t('header.register')}
                            </Button>
                            <Button variant="primary" as={Link} to="/login" className="log-in-btn">
                                {t('header.login')}
                            </Button>
                        </> : <>
                            <div className="nav-link text-danger">
                                <NavDropdown
                                    title={
                                        <span>
                                            <img src={user?.result?.avatar || '/default-avatar.png'} width="40" className="rounded-circle" alt="Avatar" />
                                            {t('header.welcome')} {user?.result?.username || ''}
                                        </span>
                                    }
                                    id="user-dropdown"
                                    align="end"
                                >
                                    <NavDropdown.Item as={Link} to="/editProfile">
                                        {t('header.editProfile')}
                                    </NavDropdown.Item>

                                    {user?.result?.role === "DOCTOR" ? <> <NavDropdown.Item as={Link} to="/doctorSchedule">
                                        {t('header.doctorSchedule')}
                                    </NavDropdown.Item>
                                    </> : <NavDropdown.Item as={Link} to={`/healthRecord`}>
                                        {t('header.healthRecord')}
                                    </NavDropdown.Item>}

                                    <NavDropdown.Item as={Link} to="/change-password">
                                        {t('header.changePassword')}
                                    </NavDropdown.Item>

                                    <NavDropdown.Divider />
                                </NavDropdown>
                            </div>
                        </>}
                        {/* Nút đăng xuất luôn hiển thị bên ngoài */}
                        {user !== null && (
                            <Button
                                variant="danger"
                                className="logout-btn d-flex align-items-center"
                                style={{ marginTop: "15px", marginBottom: "15px" }}
                                onClick={handleLogout}
                                as={Link}
                                to="/login"
                            >
                                <i className="bi bi-box-arrow-right me-2"></i> {t('header.logout')}
                            </Button>
                        )}
                    </Nav>

                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}

export default Header