import { useContext, useState, useEffect } from "react";
import { Alert, Button, Col, Container, FloatingLabel, Form, Row } from "react-bootstrap"
import Apis, { authApis, endpoint } from "../../configs/Apis";
import { MyDipatcherContext } from "../../configs/MyContexts";
import { showCustomToast } from "../layout/MyToaster";
import { useNavigate, useLocation } from "react-router-dom";
import cookie from 'react-cookies';
import toast from "react-hot-toast";
import MySpinner from "../layout/MySpinner";
import "./Styles/Login.css";
import { useTranslation } from "react-i18next";

const Login = () => {
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState();
    const dispatch = useContext(MyDipatcherContext);
    const nav = useNavigate();

    const location = useLocation();
    const [message, setMessage] = useState(location.state?.message || "");

    useEffect(() => {
        if (message) {
            const timer = setTimeout(() => {
                setMessage("");
            }, 2000);
            return () => clearTimeout(timer);
        }
    }, [message]);

    const { t } = useTranslation();
    const info = [
        { label: t('login.username'), field: "username", type: "text" },
        { label: t('login.password'), field: "password", type: "password" },
    ];

    //Cập nhật value vào field vào user 
    const setState = (value, field) => {
        setUser({ ...user, [field]: value })
    }

    const login = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            setMsg(null);
            let res = await Apis.post(endpoint['login'], {
                ...user
            });

            // Lưu token từ backend, lấy từ result.token hoặc res.data?.result?.accessToken
            const token = res.data?.result?.token || res.data?.result?.accessToken || res.data.token;
            cookie.save('token', token);

            let u = await authApis().get(endpoint['current_user']);
            console.info(u.data);
            const userInfo = u.data.result;

            // Kiểm tra trạng thái license của bác sĩ
            if (userInfo.role === "DOCTOR") {
                let license = null;
                try {
                    const licenseRes = await authApis().get(endpoint.findLicenseByDoctorId(userInfo.id));
                    license = licenseRes.data?.result;
                } catch (err) {
                    license = null;
                }
                if (!license) {
                    sessionStorage.setItem("doctorId", userInfo.id);
                    showCustomToast("Vui lòng cung cấp chứng chỉ hành nghề!");
                    nav("/upload-license");
                    return;
                }   
                if (license.isVerified === false) {
                    sessionStorage.setItem("doctorId", userInfo.id);
                    showCustomToast("Chứng chỉ hành nghề của bạn đang chờ duyệt!");
                    nav("/upload-license");
                    return;
                }
            }

            //Luu lai cookie chỉ khi bác sĩ đã  đc duyệt 
            cookie.save('user', u.data);

            //bác sĩ chưa đăng nhập không lưu context
            dispatch({
                "type": "login",
                "payload": u.data
            });
            toast.success(t('login.success'));
            nav("/");
        } catch (ex) {
            console.error("Lỗi đăng nhập:", ex);

            if (ex.response && ex.response.status === 401) {
                // Nếu lỗi 401, thử làm mới token
                    // Đã bỏ logic refresh token khi logout
            } else {
                // Lỗi khác
                setMsg(t('login.error'));
            }
        } finally {
            setLoading(false);
        }
    }



    return (

        <Container fluid className="p-0">
            {message && (
                <div className="fade-out-message">
                    {message}
                </div>
            )}
            <Row className="justify-content-center custom-row-primary mt-4">
                <Col lg={6} md={4} sm={12} >
                    <h1 className="text-center text-success mb-4">{t('login.title')}</h1>
                    {msg && <Alert variant="danger">{msg}</Alert>}
                    <Form onSubmit={login}>

                        {/* required: Bắt buộc phải nhập trước khi submit. value theo từng field onChange set dữ liệu mới*/}
                        {info.map(f => <FloatingLabel key={f.field} controlId="floatingInput" label={f.label} className="mb-3">
                            <Form.Control type={f.type} placeholder={f.label} required value={user[f.field] || ""}
                                onChange={e => setState(e.target.value, f.field)} />
                        </FloatingLabel>)}
                        {loading === true ? <MySpinner /> : <Button type="submit" variant="success" className="mt-1 mb-1">{t('login.button')}</Button>}
                    </Form>
                </Col>
            </Row>
        </Container>

    )

}

export default Login;