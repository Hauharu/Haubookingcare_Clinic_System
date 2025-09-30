import { useTranslation } from 'react-i18next';
import { useEffect, useRef, useState } from "react";
// FloatingLabel form nhập vào thì placeholder sẽ lên trên
import { Alert, Button, Col, Container, FloatingLabel, Form, Image, Row, Spinner } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Apis, { endpoint } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import toast from "react-hot-toast";

const Register = () => {



    const { t } = useTranslation();
    const info = [
        { title: t('register.lastName'), field: "lastName", type: "text" },
        { title: t('register.firstName'), field: "firstName", type: "text" },
        { title: t('register.username'), field: "username", type: "text" },
        { title: t('register.password'), field: "password", type: "password" },
        { title: t('register.confirmPassword'), field: "confirmPassword", type: "password" },
        { title: t('register.email'), field: "email", type: "email" },
        { title: t('register.phoneNumber'), field: "phoneNumber", type: "text" },
        { title: t('register.address'), field: "address", type: "text" },
        {
            title: t('register.gender'), field: "gender", type: "select", options: [
                { label: t('register.male'), value: "MALE" },
                { label: t('register.female'), value: "FEMALE" },
                { label: t('register.other'), value: "OTHER" }
            ]
        },
        { title: t('register.dateOfBirth'), field: "dateOfBirth", type: "date" }
    ];




    const [user, setUser] = useState({});
    const avatar = useRef();
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const [step, setStep] = useState(1);
    const nav = useNavigate();
    // const formRef = useRef(); // -> tham chiếu tới Form để các thành phần này đều như 1 form



    const setState = (value, field) => {
        setUser({ ...user, [field]: value })
    }



    const register = async (e) => {
        e.preventDefault();
        const userData = { ...user };

        // Validate từng trường theo quy tắc backend
        if (!userData.username || userData.username.length < 5) {
            setMsg("Tên đăng nhập phải có ít nhất 5 ký tự!");
            return;
        }
        if (!userData.password || userData.password.length < 6) {
            setMsg("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (userData.password !== userData.confirmPassword) {
            setMsg("Mật khẩu không khớp!");
            return;
        }
        if (!userData.firstName || userData.firstName.length < 1) {
            setMsg("Tên phải có ít nhất 1 ký tự!");
            return;
        }
        if (!userData.lastName || userData.lastName.length < 1) {
            setMsg("Họ và tên lót phải có ít nhất 1 ký tự!");
            return;
        }
        if (!userData.email || userData.email.length < 10) {
            setMsg("Email phải có ít nhất 10 ký tự!");
            return;
        }
        if (userData.email && !/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(userData.email)) {
            setMsg("Email không đúng định dạng!");
            return;
        }
        if (!userData.phoneNumber || userData.phoneNumber.length < 10) {
            setMsg("Số điện thoại phải có ít nhất 10 ký tự!");
            return;
        }
        if (userData.phoneNumber && !/^0[0-9]{9,10}$/.test(userData.phoneNumber)) {
            setMsg("Số điện thoại không đúng định dạng!");
            return;
        }
        if (!userData.address || userData.address.length < 10) {
            setMsg("Địa chỉ phải có ít nhất 10 ký tự!");
            return;
        }
        if (!userData.dateOfBirth) {
            setMsg("Vui lòng chọn ngày sinh!");
            return;
        }
        // Kiểm tra tuổi >= 18
        if (userData.dateOfBirth) {
            const dob = new Date(userData.dateOfBirth);
            const today = new Date();
            let age = today.getFullYear() - dob.getFullYear();
            const m = today.getMonth() - dob.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
                age--;
            }
            if (age < 18) {
                setMsg("Bạn phải đủ 18 tuổi trở lên!");
                return;
            }
        }
        if (!userData.gender) {
            setMsg("Vui lòng chọn giới tính!");
            return;
        }
        // Role mặc định là PATIENT
        userData.role = "PATIENT";

        let form = new FormData();
        for (let key in userData) {
            if (userData[key] !== undefined && userData[key] !== null)
                form.append(key, userData[key]);
        }
        if (avatar.current && avatar.current.files && avatar.current.files.length > 0) {
            form.append("avatar", avatar.current.files[0]);
        }
        try {
            setLoading(true);
            await Apis.post(endpoint['register'], form, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            nav("/login"); // đk thành công về đăng nhập
            toast.success("Đăng ký tài khoản thành công")
        } catch (ex) {
            console.error(ex);
            setMsg(`Đã có lỗi xảy ra ${ex}`);
        } finally {
            setLoading(false);
        }

    }

    useEffect(() => {
        if (msg) {
            const timer = setTimeout(() => {
                setMsg(null);
            }, 5000); // Thời gian hiển thị 5 giây

            return () => clearTimeout(timer);
        }
    }, [msg]);
    return (
        <>
            {/* Check validate -> làm thêm con mắt xem pass */}
            <Container fluid className="p-0">
                <Row className="justify-content-center custom-row-primary mt-4">
                    <Col lg={6} md={4} sm={12} >
                        <Image src="/assets/images/login-banner.png" alt="banner" style={{ width: '100%', height: '500px', objectFit: 'cover', marginTop: 0, marginLeft: 0 }} />
                        <p className="text-center mt-3 text-muted me-5" style={{ fontSize: "1.5rem", color: "#007bff", fontWeight: "bold" }}>" Đội ngũ bác sĩ tận tâm với bệnh nhân, luôn sẵn sàng hỗ trợ bạn trong hành trình chăm sóc sức khỏe."</p>

                    </Col>
                    <Col lg={5} md={6} sm={12}>
                        <Container className="p-3 shadow rounded bg-light me-5">
                            <h1 className="text-center text-success mb-4">{t('register.title')}</h1>
                            {msg && <Alert variant="danger">{msg}</Alert>}
                            <Form onSubmit={register}>
                                <Row>
                                    {/* Cột đầu tiên */}
                                    <Col lg={6} md={6} sm={12}>
                                        {info.slice(0, Math.ceil(info.length / 2)).map((i, index) => (
                                            <div key={i.field} className="mb-3">
                                                {i.type === "select" ? (
                                                    <Form.Select value={user[i.field] || ''} required
                                                        onChange={e => setState(e.target.value, i.field)}>
                                                        <option value="">-- {i.title} --</option>
                                                        {i.options.map(opt => (
                                                            <option key={opt.value} value={opt.value}>{opt.label}</option>
                                                        ))}
                                                    </Form.Select>
                                                ) : (
                                                    <FloatingLabel controlId={`floating-${i.field}`} label={i.title}>
                                                        <Form.Control type={i.type} placeholder={i.title} required
                                                            value={user[i.field] || ''} onChange={e => setState(e.target.value, i.field)} />
                                                    </FloatingLabel>
                                                )}
                                            </div>
                                        ))}
                                    </Col>

                                    {/* Cột thứ hai */}
                                    <Col lg={6} md={6} sm={12}>
                                        {info.slice(Math.ceil(info.length / 2)).map((i, index) => (
                                            <div key={i.field} className="mb-3">
                                                {i.type === "select" ? (
                                                    <Form.Select value={user[i.field] || ''} required
                                                        onChange={e => setState(e.target.value, i.field)}>
                                                        <option value="">-- {i.title} --</option>
                                                        {i.options.map(opt => (
                                                            <option key={opt.value} value={opt.value}>{opt.label}</option>
                                                        ))}
                                                    </Form.Select>
                                                ) : (
                                                    <FloatingLabel controlId={`floating-${i.field}`} label={i.title}>
                                                        <Form.Control type={i.type} placeholder={i.title} required
                                                            value={user[i.field] || ''} onChange={e => setState(e.target.value, i.field)} />
                                                    </FloatingLabel>
                                                )}
                                            </div>
                                        ))}
                                    </Col>
                                </Row>
                                <Row>
                                    <Col lg={12} className="mb-3">
                                        <Form.Control ref={avatar} type="file" placeholder="Ảnh đại diện" />
                                    </Col>
                                </Row>
                                <Button type="submit" variant="success" className="mt-3 w-100" disabled={loading}>
                                    {loading ? <MySpinner /> : t('register.button')}
                                </Button>
                            </Form>
                        </Container>
                    </Col>


                </Row>
            </Container>
        </>
    )
}

export default Register