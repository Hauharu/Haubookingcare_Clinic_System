import { useContext, useState } from "react";
import { useTranslation } from 'react-i18next';
import { MyUserContext } from "../../configs/MyContexts";
import { Button, Card, Col, Container, FloatingLabel, Form, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { authApis, authformdataApis, endpoint } from "../../configs/Apis";
import toast from "react-hot-toast";
import MyToaster from "../layout/MyToaster";
import MyConfirm from "../layout/MyConfirm";
import { ConvertToVietnamTime } from "../../utils/ConvertToVietnamTime";
import { useEffect } from "react";

const Booking = () => {

    const [loading, setLoading] = useState(false);
    const { t } = useTranslation();
    const location = useLocation();
    const [showConfirm, setShowConfirm] = useState(false);
    const slot = location.state?.slot || {};
    const [appointment, setAppointment] = useState({});
    const [consultationType, setConsultationType] = useState("OFFLINE");
    const nav = useNavigate();
    const formattedDate = slot.slotDate ? ConvertToVietnamTime(slot.slotDate) : "";
    const formattedTime = slot.startTime || "";
    const fullTime = `${formattedDate} ${formattedTime}`;
    
    //của patient
    const user = useContext(MyUserContext);

    const addInvoice = async () => {
        setLoading(true);
        try {
            const today = new Date().toISOString().split("T")[0];

            // Tạo đối tượng FormData
            const formData = new FormData();
            formData.append("amount", appointment.doctorId.consultationFee);
            formData.append("dueDate", today);
            formData.append("appointmentId", appointment.appointmentId);

            // Gửi yêu cầu với FormData
            const response = await authApis().post(`invoice`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data", // Đặt header cho form-data
                },
            });

            if (response.data && response.data.result) {
                return response.data.result.invoiceId;
            } else {
                console.log("Không thể tạo hóa đơn . Vui lòng thử lại.");
                return null;
            }
        } catch (error) {
            console.error("Lỗi khi tạo hóa đơn:", error);
            console.log("Đã xảy ra lỗi. Vui lòng thử lại.");
            return null;
        }
        finally {
            setLoading(false);
        }
    }

    const Booking = async () => {

        try {
            setLoading(true);
            const currentUser = user?.result || user;
            const patientId = currentUser?.id;
            // doctorId có thể là object, cần lấy id
            const doctorId = slot?.doctorId?.doctorId || slot?.doctorId?.id || slot?.doctorId;
            // appointmentTime cần có giây
            let startTime = slot.startTime;
            if (startTime.length === 5) startTime += ':00';
            const appointmentTime = `${slot.slotDate}T${startTime}`;
            const bookingData = {
                patientId,
                doctorId,
                appointmentTime,
                durationMinutes: 30,
                reason: appointment.reason || t('booking.defaultReason'),
                consultationType,
                videoCallLink: ""
            };
            let res = await authApis().post(endpoint['appointments'], bookingData);
            setAppointment(res.data.result);
            // Lưu lịch mới vào localStorage để Appointment.js nhận
            localStorage.setItem('newAppointmentObj', JSON.stringify(res.data.result));
            await addInvoice();
            toast.success(res.data.message || t('booking.success'));
            if (window.refreshNotifications) window.refreshNotifications();
            nav("/appointment");
        } catch (ex) {
            toast.error(t('booking.error') + (ex.message || ""));
        } finally {
            setLoading(false);
            setShowConfirm(false);
        }

    }


    const handleConfirm = () => {
        setShowConfirm(true);
    };

    const handleClose = () => {
        setShowConfirm(false);
    };

    // Lấy tên bác sĩ từ slot hoặc appointment, fallback sang các trường phổ biến
    const getDoctorName = () => {
        if (slot.doctorId?.userDTO?.firstName && slot.doctorId?.userDTO?.lastName) {
            return `${slot.doctorId.userDTO.firstName} ${slot.doctorId.userDTO.lastName}`;
        }
        if (slot.doctorId?.firstName && slot.doctorId?.lastName) {
            return `${slot.doctorId.firstName} ${slot.doctorId.lastName}`;
        }
        if (slot.doctorName) return slot.doctorName;
        if (appointment.doctorName) return appointment.doctorName;
    return t('booking.unknownDoctor');
    }

    return (
        <>
            <Container fluid className="p-0">
                <Row className="mt-3 mb-2">
                    <Col xs={12} className="text-start">
                        <Button variant="secondary" onClick={() => nav(-1)}>
                            &larr; Quay lại
                        </Button>
                    </Col>
                </Row>
                <Row className="justify-content-center custom-row-primary mt-2">
                    <Col lg={8} md={10} sm={12}>
                        <Container className="p-3 shadow rounded bg-light me-5">
                            <h2 className="text-center">{t('booking.title')}</h2>
                            <Card>
                                <Card.Body className="card-body-custom">
                                    <Card.Text className="card-text">
                                        <strong >{t('booking.time')}:</strong> {slot.startTime} - {slot.endTime}
                                        <br />
                                        <strong>{t('booking.date')}:</strong> {new Date(slot.slotDate).toLocaleDateString()}
                                        <br />
                                        <strong>{t('booking.doctor')}:</strong> {getDoctorName()}
                                        <br />
                                    </Card.Text>
                                    <Form.Group className="mb-3">
                                        <Form.Label>{t('booking.consultationType')}</Form.Label>
                                        <Form.Select
                                            value={consultationType}
                                            onChange={e => setConsultationType(e.target.value)}
                                        >
                                            <option value="OFFLINE">{t('booking.offline')}</option>
                                            <option value="ONLINE">{t('booking.online')}</option>
                                        </Form.Select>
                                    </Form.Group>

                                    {Array.isArray(slot.doctorId?.specialties) && slot.doctorId.specialties.map(s => (
                                        <strong key={s.specialtyId}>{t('booking.specialty')}: {s.name}</strong>
                                    ))}

                                    {/* Hiển thị thông tin phòng khám duy nhất nếu có */}
                                    {slot.doctorId?.clinics && slot.doctorId.clinics[0] && (
                                        <div className="card-text">
                                            <strong>{t('booking.fee')}:</strong> {slot.doctorId.consultationFee?.toLocaleString('vi-VN') || '---'} VNĐ
                                            <br />
                                            <strong>{t('booking.hospital')}:</strong> {slot.doctorId.clinics[0].name}
                                            <br />
                                            <strong>{t('booking.address')}:</strong> {slot.doctorId.clinics[0].address}
                                        </div>
                                    )}

                                    <FloatingLabel label={t('booking.reason')} className="mb-3">
                                        <Form.Control type="text" placeholder={t('booking.reasonPlaceholder')} required
                                            value={appointment.reason || ''} onChange={(e) => setAppointment({ ...appointment, reason: e.target.value })} />
                                    </FloatingLabel>

                                    <Button variant="success" onClick={handleConfirm} disabled={loading}>
                                        {loading ? t('commonButton.processing') : t('booking.confirmButton')}
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Container>
                    </Col>
                </Row>
                <MyConfirm
                    show={showConfirm}
                    onHide={handleClose}
                    onConfirm={Booking}
                    loading={loading}
                    title={t('booking.confirmTitle')}
                    body={t('booking.confirmBody')}
                />
                <Row>
                </Row>
            </Container>
        </>
    );
}
export default Booking;