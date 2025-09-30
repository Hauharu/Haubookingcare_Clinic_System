import { use, useState } from "react";
import { useTranslation } from "react-i18next";
import { Form, Button, Container, Row, Col, Alert } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { authApis, endpoint } from "../../configs/Apis";
import { FaStar } from "react-icons/fa";
import "./Styles/DoctorReview.css";
import { useEffect } from "react";

const DoctorReview = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const doctor = location.state?.doctor || {};
    const appointmentId = location.state?.appointmentId || null
    const patientId = location.state?.patientId || null
    const [rating, setRating] = useState(5);
    const [hover, setHover] = useState(null);
    const [comment, setComment] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { t } = useTranslation();

    //Chặn lại nếu người dùng paste đường dẫn vào khi không có doctor, appointment, patient
    useEffect(() => {
        if (!doctor || !patientId || !appointmentId) {
            navigate("/", { state: { message: t("doctorReview.cannotReview") } });
        }
    }, [])

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        // Validation
        if (!rating || rating < 1 || rating > 5) {
            setError(t("doctorReview.selectStarError"));
            return;
        }

        if (!appointmentId) {
            setError(t("doctorReview.noAppointmentError"));
            return;
        }

        setLoading(true);

        try {
            // Lấy thông tin user từ cookie thay vì localStorage
            const userCookie = document.cookie
                .split('; ')
                .find(row => row.startsWith('user='))
                ?.split('=')[1];
            
            let currentUser = null;
            if (userCookie) {
                currentUser = JSON.parse(decodeURIComponent(userCookie));
            }
            
            console.log("Current user from cookie:", currentUser);
            console.log("Current user role:", currentUser?.result?.role);
            console.log("Current user ID:", currentUser?.result?.id);
            console.log("Appointment ID being reviewed:", appointmentId);
            console.log("Token from cookie:", document.cookie);
            console.log("Sending review data:", { appointmentId, rating, comment: comment || "" });
            
            const requestData = {
                appointmentId: appointmentId,
                rating: parseInt(rating),
                comment: comment || "" // Cho phép comment rỗng
            };
            
            const res = await authApis().post(endpoint.reviews, requestData);
            console.log("Review response:", res);
            if (res.status === 200 || res.status === 201) {
                setMessage(t("doctorReview.success"));
                setTimeout(() => navigate("/appointment", { state: { message: t("doctorReview.success") } }), 2000);
            }
        } catch (err) {
            console.error("Error posting review:", err);
            console.error("Error response:", err.response?.data);
            console.error("Error status:", err.response?.status);
            
            let errorMessage = t("doctorReview.generalError");

            if (err.response?.status === 403) {
                errorMessage = t("doctorReview.permissionError");
            } else if (err.response?.data?.message) {
                errorMessage = err.response.data.message;
            } else if (err.response?.data?.error) {
                errorMessage = err.response.data.error;
            }

            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={7} lg={6}>
                    <div className="doctor-review-card shadow p-4 rounded bg-white">
                        <h2 className="mb-3 text-center text-primary">{t("doctorReview.title")}</h2>
                        <div className="text-center mb-3">
                            <img
                                src={doctor.user?.avatar || "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iODAiIHZpZXdCb3g9IjAgMCA4MCA4MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjgwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjRTVFNUU1Ii8+CjxjaXJjbGUgY3g9IjQwIiBjeT0iMzAiIHI9IjEyIiBmaWxsPSIjOTk5Ii8+CjxwYXRoIGQ9Ik0yMCA2MEMyMCA1Mi4yNjggMjYuMjY4IDQ2IDM0IDQ2SDQ2QzUzLjczMiA0NiA2MCA1Mi4yNjggNjAgNjBWNzBIMjBWNjBaIiBmaWxsPSIjOTk5Ii8+Cjx0ZXh0IHg9IjQwIiB5PSI3NSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjhweCIgZmlsbD0iIzk5OSI+QXZhdGFyPC90ZXh0Pgo8L3N2Zz4K"}
                                alt="avatar"
                                className="rounded-circle"
                                width={80}
                                height={80}
                                style={{ objectFit: "cover", border: "2px solid #0d6efd" }}
                                onError={(e) => {
                                    e.target.src = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iODAiIHZpZXdCb3g9IjAgMCA4MCA4MCIgZmlsbD0ibm9uZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjgwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjRTVFNUU1Ii8+CjxjaXJjbGUgY3g9IjQwIiBjeT0iMzAiIHI9IjEyIiBmaWxsPSIjOTk5Ii8+CjxwYXRoIGQ9Ik0yMCA2MEMyMCA1Mi4yNjggMjYuMjY4IDQ2IDM0IDQ2SDQ2QzUzLjczMiA0NiA2MCA1Mi4yNjggNjAgNjBWNzBIMjBWNjBaIiBmaWxsPSIjOTk5Ii8+Cjx0ZXh0IHg9IjQwIiB5PSI3NSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjhweCIgZmlsbD0iIzk5OSI+QXZhdGFyPC90ZXh0Pgo8L3N2Zz4K";
                                }}
                            />
                            <div className="mt-2 fw-bold">
                                {doctor.user?.firstName} {doctor.user?.lastName}
                            </div>
                        </div>
                        {message && <Alert variant="success">{message}</Alert>}
                        {error && <Alert variant="danger">{error}</Alert>}
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-3 text-center">
                                <Form.Label className="fw-bold">{t("doctorReview.selectStar")}</Form.Label>
                                <div>
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <FaStar
                                            key={star}
                                            size={32}
                                            style={{ cursor: "pointer", marginRight: 4 }}
                                            color={star <= (hover || rating) ? "#ffc107" : "#e4e5e9"}
                                            onClick={() => setRating(star)}
                                            onMouseEnter={() => setHover(star)}
                                            onMouseLeave={() => setHover(null)}
                                        />
                                    ))}
                                </div>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label className="fw-bold">{t("doctorReview.commentLabel")}</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={4}
                                    value={comment}
                                    onChange={e => setComment(e.target.value)}
                                    placeholder={t("doctorReview.commentPlaceholder")}
                                />
                            </Form.Group>
                            <div className="text-center">
                                <Button type="submit" variant="primary" disabled={loading}>
                                    {loading ? t("doctorReview.sending") : t("doctorReview.submit")}
                                </Button>
                            </div>
                        </Form>
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default DoctorReview;