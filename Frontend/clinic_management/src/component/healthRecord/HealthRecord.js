import { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Cookies from "js-cookie";
import { MyUserContext } from "../../configs/MyContexts";
import MySpinner from "../layout/MySpinner";
import { authApis, endpoint } from "../../configs/Apis";
import { Alert, Container, Row, Col, Button } from "react-bootstrap";

const HealthRecord = () => {
    let user = useContext(MyUserContext);
    const { t } = useTranslation();
    
    // Helper function để format thời gian
    const formatDateTime = (dateTimeString) => {
        if (!dateTimeString) return <em className="text-muted">{t('healthRecord.notUpdated')}</em>;
        
        try {
            const date = new Date(dateTimeString);
            const dateStr = date.toLocaleDateString('vi-VN', {
                day: '2-digit',
                month: '2-digit', 
                year: 'numeric'
            });
            const timeStr = date.toLocaleTimeString('vi-VN', {
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
            return `${dateStr} ${timeStr}`;
        } catch (error) {
            console.error("Error formatting date:", dateTimeString);
            return dateTimeString;
        }
    };
    
    // Nếu user context bị null hoặc không có id, thử lấy từ cookie
    if (!user || !user.id) {
        try {
            const userCookie = Cookies.get("user");
            if (userCookie) {
                const parsed = JSON.parse(decodeURIComponent(userCookie));
                if (parsed.result && parsed.result.id) {
                    user = parsed.result;
                    console.log("User lấy từ cookie:", user);
                }
            }
        } catch (e) {
            console.error("Lỗi lấy user từ cookie:", e);
        }
    }

    const [loading, setLoading] = useState(false);
    const [records, setRecords] = useState([]);
    const [message, setMessage] = useState("");

    // Test function để debug
    const testAPI = async () => {
        console.log("=== TEST API BUTTON CLICKED ===");
        console.log("Current user:", user);
        
        if (!user || !user.id) {
            console.log("No user found!");
            alert("Không có user!");
            return;
        }

        try {
            const url = endpoint.health_record_by_user(user.id);
            console.log("Test API URL:", url);
            
            const response = await authApis().get(url);
            console.log("Test API Response:", response);
            alert("API thành công! Check console.");
        } catch (error) {
            console.error("Test API Error:", error);
            alert("API lỗi! Check console.");
        }
    };

    useEffect(() => {
        const fetchRecords = async () => {
            console.log("Starting fetchRecords...");
            console.log("User:", user);
            
            if (!user || !user.id) {
                console.log("No user found, stopping loading");
                setLoading(false);
                setMessage(t('healthRecord.noUser'));
                return;
            }

            setLoading(true);
            try {
                // Kiểm tra endpoint có tồn tại không
                if (!endpoint.health_record_by_user) {
                    console.error("endpoint.health_record_by_user not found");
                    throw new Error("Endpoint không tồn tại");
                }

                const url = endpoint.health_record_by_user(user.id);
                console.log("Fetching health records from:", url);
                
                const response = await authApis().get(url);
                console.log("API response:", response);
                console.log("API response data:", response.data);
                
                if (response.status === 200) {
                    if (response.data && response.data.result) {
                        const healthRecords = response.data.result;
                        console.log("Health records found:", healthRecords);
                        
                        if (Array.isArray(healthRecords) && healthRecords.length > 0) {
                            setRecords(healthRecords);
                            setMessage("");
                        } else if (Array.isArray(healthRecords) && healthRecords.length === 0) {
                            setRecords([]);
                            setMessage(t('healthRecord.noRecords'));
                        } else {
                            console.log("Response data.result is not an array:", healthRecords);
                            setRecords([]);
                            setMessage(t('healthRecord.invalidData'));
                        }
                    } else {
                        console.log("No result in response data");
                        setRecords([]);
                        setMessage(t('healthRecord.noData'));
                    }
                } else {
                    console.log("Response status not 200:", response.status);
                    setRecords([]);
                    setMessage(t('healthRecord.loadError'));
                }
            } catch (error) {
                console.error("Error fetching health records:", error);
                console.error("Error response:", error.response?.data);
                console.error("Error status:", error.response?.status);
                setRecords([]);
                setMessage(`${t('healthRecord.error')}: ${error.message || t('healthRecord.loadError')}`);
            } finally {
                console.log("Setting loading to false");
                setLoading(false);
            }
        };

        fetchRecords();
    }, [user?.id]); // Dependency chỉ phụ thuộc vào user.id

    return (
        <Container fluid className="p-4">
            <Row className="justify-content-center">
                <Col md={10}>
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h2 className="text-primary fw-bold mb-0 d-flex align-items-center">
                            <i className="bi bi-journal-medical me-3"></i>
                            {t('healthRecord.title')}
                        </h2>

                    </div>

                    {loading ? (
                        <div className="text-center">
                            <MySpinner />
                        </div>
                    ) : (
                        <>
                            {message && (
                                <Alert variant="info" className="text-center">
                                    <i className="bi bi-info-circle me-2"></i>
                                    {message}
                                </Alert>
                            )}

                            {records.length > 0 && (
                                <div>
                                    <p className="text-muted mb-4">
                                        {t('healthRecord.totalRecords')}: <strong>{records.length}</strong>
                                    </p>
                                    
                                    {records.map((healthRecord, index) => (
                                        <div key={healthRecord.id || index} className="card shadow border-0 rounded-4 mb-4">
                                            <div className="card-body p-4">
                                                <div className="d-flex justify-content-between align-items-center mb-3">
                                                    <h5 className="card-title text-success fw-bold mb-0 d-flex align-items-center">
                                                        <i className="bi bi-clipboard-data me-2"></i>
                                                        {t('healthRecord.recordTitle', { number: index + 1 })}
                                                    </h5>
                                                    <span className="badge bg-primary">ID: {healthRecord.id}</span>
                                                </div>
                                                <Row>
                                                    <Col md={6}>
                                                        <div className="mb-3">
                                                            <label className="fw-bold text-info">
                                                                <i className="bi bi-calendar-event me-2"></i>{t('healthRecord.examDate')}:
                                                            </label>
                                                            <p className="ms-3">{healthRecord.recordDate || <em className="text-muted">{t('healthRecord.notUpdated')}</em>}</p>
                                                        </div>
                                                        <div className="mb-3">
                                                            <label className="fw-bold text-secondary">
                                                                <i className="bi bi-clock me-2"></i>{t('healthRecord.createdAt')}:
                                                            </label>
                                                            <p className="ms-3">{formatDateTime(healthRecord.createdAt)}</p>
                                                        </div>
                                                        {healthRecord.doctorResponse && (
                                                            <div className="mb-3">
                                                                <label className="fw-bold text-primary">
                                                                    <i className="bi bi-person-badge me-2"></i>{t('healthRecord.doctor')}:
                                                                </label>
                                                                <p className="ms-3">{healthRecord.doctorResponse.firstName} {healthRecord.doctorResponse.lastName}</p>
                                                            </div>
                                                        )}
                                                        {healthRecord.appointmentTime && (
                                                            <div className="mb-3">
                                                                <label className="fw-bold text-info">
                                                                    <i className="bi bi-calendar2-event me-2"></i>{t('healthRecord.appointmentTime')}:
                                                                </label>
                                                                <p className="ms-3">{formatDateTime(healthRecord.appointmentTime)}</p>
                                                            </div>
                                                        )}
                                                    </Col>
                                                    <Col md={6}>
                                                        <div className="mb-3">
                                                            <label className="fw-bold text-success">
                                                                <i className="bi bi-activity me-2"></i>{t('healthRecord.symptoms')}:
                                                            </label>
                                                            <p className="ms-3">{healthRecord.symptoms || <em className="text-muted">{t('healthRecord.notUpdated')}</em>}</p>
                                                        </div>
                                                        <div className="mb-3">
                                                            <label className="fw-bold text-primary">
                                                                <i className="bi bi-clipboard-check me-2"></i>{t('healthRecord.diagnosis')}:
                                                            </label>
                                                            <p className="ms-3">{healthRecord.diagnosis || <em className="text-muted">{t('healthRecord.notUpdated')}</em>}</p>
                                                        </div>
                                                        <div className="mb-3">
                                                            <label className="fw-bold text-warning">
                                                                <i className="bi bi-journal-text me-2"></i>{t('healthRecord.notes')}:
                                                            </label>
                                                            <p className="ms-3">{healthRecord.notes || <em className="text-muted">{t('healthRecord.notUpdated')}</em>}</p>
                                                        </div>
                                                    </Col>
                                                </Row>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </>
                    )}
                </Col>
            </Row>
        </Container>
    );
};

export default HealthRecord;
