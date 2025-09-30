import { useTranslation } from 'react-i18next';
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApis, endpoint } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import { Alert, Button, Col, Container, FloatingLabel, Form, Row } from "react-bootstrap";
import cookie from 'react-cookies'
import toast from "react-hot-toast";

const UploadLicense = () => {
    // Nên thêm hình ảnh chứng chỉ
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [license, setLicense] = useState({});

    const [existingLicense, setExistingLicense] = useState(null);
    const [checkingLicense, setCheckingLicense] = useState(true);
    const nav = useNavigate();


    //Lấy id trong sessionStorage
    const doctorId = sessionStorage.getItem("doctorId");

    const { t } = useTranslation();
    const info = [
        { label: t('uploadLicense.licenseNumber'), field: "licenseNumber", type: "text" },
        { label: t('uploadLicense.issueDate'), field: "issueDate", type: "date" },
        { label: t('uploadLicense.expiryDate'), field: "expiryDate", type: "date" },
        { label: t('uploadLicense.issuingAuthority'), field: "issuingAuthority", type: "text" },
        { label: t('uploadLicense.scopeDescription'), field: "scopeDescription", type: "text" },
    ];


    const setState = (value, field) => {

        setLicense({ ...license, [field]: value })
    }

    //Check bác sĩ đã gửi giấy phép chưa

    useEffect(() => {
        const checkExist = async () => {
            try {
                setCheckingLicense(true);
                const res = await authApis().get(endpoint.findLicenseByDoctorId(doctorId));
                if (res.data) {
                    setExistingLicense(res.data);
                }
            } catch (error) {
                console.error("Error checking existing license:", error);

                if (error.response && error.response.status !== 404) {
                    setMsg("Có lỗi khi kiểm tra thông tin chứng chỉ");
                }
            }
            finally {
                setCheckingLicense(false);
            }
        }
        if (doctorId) {
            checkExist();
        }
    }, [doctorId])

    //Thêm thông báo 
    const upload = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            // Đúng format backend
            let res = await authApis().post(endpoint['doctor_license'], {
                doctorId: doctorId,
                licenseNumber: license.licenseNumber,
                issuingAuthority: license.issuingAuthority,
                issueDate: license.issueDate,
                expiryDate: license.expiryDate,
                scopeDescription: license.scopeDescription
            });
            toast.success("Chứng chỉ hành nghề đã gửi thành công, vui lòng chờ duyệt!");
            sessionStorage.removeItem("doctorId");
            nav("/login");
        } catch (ex) {
            console.error(ex);
            setMsg(`Có lỗi xảy ra. Vui lòng thử lại! ${ex}`);
        } finally {
            setLoading(false);
        }
    }
    if (checkingLicense) {
        return <MySpinner />;
    }

    if (existingLicense) {
        // Nếu license chưa được xác minh, hiển thị lại form nộp license
        if (!existingLicense.isVerified) {
            return (
                <Container fluid className="p-0">
                    <Row className="justify-content-center custom-row-primary mt-4">
                        <Col lg={6} md={4} sm={12} >
                            <h1 className="text-center text-success mb-4">{t('uploadLicense.title')}</h1>
                            <Alert variant="info">{t('uploadLicense.provideLicense')}</Alert>
                            <Form onSubmit={upload}>
                                {info.map(l => <FloatingLabel key={l.field} label={l.label} className="mb-3">
                                    <Form.Control type={l.type} placeholder={l.label} required value={license[l.field] || ""}
                                        onChange={e => setState(e.target.value, l.field)} />
                                </FloatingLabel>)}
                                {loading === true ? <MySpinner /> : <Button type="submit" className="btn btn-success mt-1 mb-1">{t('uploadLicense.button')}</Button>}
                            </Form>
                        </Col>
                    </Row>
                </Container>
            );
        }
        // Nếu đã xác minh, hiển thị trạng thái
        return (
            <Container fluid className="p-0">
                <Row className="justify-content-center custom-row-primary mt-4">
                    <Col lg={6} md={4} sm={12} >
                        <h1 className="text-center text-success mb-4">{t('uploadLicense.statusTitle')}</h1>
                        <Alert variant="info">
                            {t('uploadLicense.verified')}
                        </Alert>
                        <div className="text-center mt-3">
                            <Button
                                onClick={() => nav("/login")}
                                className="btn btn-success"
                            >
                                {t('uploadLicense.backToLogin')}
                            </Button>
                        </div>
                    </Col>
                </Row>
            </Container>
        );
    }
    // Nếu không có license (existingLicense == null), chỉ hiển thị form nộp mới
    return (
        <Container fluid className="p-0">
            <Row className="justify-content-center custom-row-primary mt-4">
                <Col lg={6} md={4} sm={12} >
                    <h1 className="text-center text-success mb-4">{t('uploadLicense.title')}</h1>
                    {msg && <Alert variant="danger">{msg}</Alert>}
                    <Form onSubmit={upload}>
                        {info.map(l => <FloatingLabel key={l.field} label={l.label} className="mb-3">
                            <Form.Control type={l.type} placeholder={l.label} required value={license[l.field] || ""}
                                onChange={e => setState(e.target.value, l.field)} />
                        </FloatingLabel>)}
                        {loading === true ? <MySpinner /> : <Button type="submit" className="btn btn-success mt-1 mb-1">{t('uploadLicense.button')}</Button>}
                    </Form>
                </Col>
            </Row>
        </Container>
    );

    // ...existing code...
};
export default UploadLicense;