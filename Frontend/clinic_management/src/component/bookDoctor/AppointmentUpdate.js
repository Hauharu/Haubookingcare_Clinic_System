import { useEffect, useState } from "react";
import Apis, { authApis, endpoint } from "../../configs/Apis";
import { useLocation, useNavigate } from "react-router-dom";
import { ConvertToVietnamTime } from "../../utils/ConvertToVietnamTime";
import toast from "react-hot-toast";
import { useTranslation } from 'react-i18next';
import MySpinner from "../layout/MySpinner";
import { Button, Card, Col, Container, Form, Row } from "react-bootstrap";
import MyConfirm from "../layout/MyConfirm";

const AppointmentUpdate = () => {

    const [slots, setSlots] = useState([]);
    const [showConfirm, setShowConfirm] = useState(false);
    const [loading, setLoading] = useState(false);
    const location = useLocation();
    const appointments = location.state?.appointment;
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [reason, setReason] = useState(appointments?.reason || "");
    const [consultationType, setConsultationType] = useState(appointments?.consultationType || "OFFLINE");
    const nav = useNavigate();
    const { t } = useTranslation();

    const fetchSlots = async () => {
        try {
            setLoading(true);
            // Truyền đúng doctorId cho API
            let doctorId = appointments.doctorId || appointments.doctorId?.doctorId;
            let res = await Apis.get(endpoint.findDoctorById(doctorId));
            // Lấy slot từ result.content, chỉ lấy slot chưa đặt
            let slotArr = [];
            if (res.data && res.data.result && Array.isArray(res.data.result.content)) {
                slotArr = res.data.result.content.filter(slot => !slot.isBooked);
            } else if (res.data && Array.isArray(res.data.result)) {
                slotArr = res.data.result.filter(slot => !slot.isBooked);
            } else if (Array.isArray(res.data)) {
                slotArr = res.data.filter(slot => !slot.isBooked);
            } else {
                slotArr = [];
            }
            setSlots(slotArr);
        } catch (ex) {
            setSlots([]);
        } finally {
            setLoading(false);
        }
    }



    const handleUpdateAppointment = async (appointmentId) => {
        try {
            setLoading(true);
            const payload = {
                doctorId: appointments.doctorId.doctorId,
                reason,
                consultationType,
            };
            // Nếu người dùng chọn thời gian mới, thêm appointmentTime vào payload
            if (selectedSlot) {
                payload.appointmentTime = `${selectedSlot.slotDate}T${selectedSlot.startTime}`;
            }
            const res = await authApis().put(endpoint.updateAppointment(appointmentId), payload);
            nav("/appointment");
            toast.success(res.data.message || t('appointmentUpdate.success'));
            window.dispatchEvent(new Event('refresh-notifications'));
        } catch (ex) {
            toast.error(t('appointmentUpdate.error'));
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


    useEffect(() => {
        fetchSlots();
    }, [appointments]);

    return (
        <>
            <Container fluid className="p-0">

                <Row className="justify-content-center custom-row-primary mt-4">
                    <Col lg={8} md={10} sm={12}>
                        <h2 className="text-center">{t('appointmentUpdate.title')}</h2>
                        {loading && <MySpinner />}
                        {!loading && (
                            <Card>
                                <Card.Body className="card-body-custom">
                                    {/* Hiển thị thời gian hiện tại của lịch hẹn */}
                                    <div className="mb-3">
                                        <strong>{t('appointmentUpdate.currentTime')}</strong>
                                        {appointments?.slotDate && appointments?.startTime ? (
                                            <span className="ms-2 text-primary">
                                                {t('appointmentUpdate.date')}: {ConvertToVietnamTime(appointments.slotDate)} - {t('appointmentUpdate.startTime')}: {appointments.startTime}
                                            </span>
                                        ) : (
                                            <span className="ms-2 text-muted">{t('appointmentUpdate.noOldTime')}</span>
                                        )}
                                    </div>
                                    <Form.Group>
                                        <Form.Label>{t('appointmentUpdate.selectNewTime')}</Form.Label>
                                        <Form.Select
                                            value={selectedSlot?.id || ""}
                                            onChange={(e) => {
                                                if (Array.isArray(slots)) {
                                                    setSelectedSlot(slots.find((slot) => slot.id === e.target.value));
                                                } else {
                                                    setSelectedSlot(null);
                                                }
                                            }}
                                        >
                                            <option value="">{t('appointmentUpdate.selectTimePlaceholder')}</option>
                                            {Array.isArray(slots) && slots.map((slot) => (
                                                <option key={slot.id} value={slot.id}>
                                                    {t('appointmentUpdate.date')}: {slot.slotDate} - {t('appointmentUpdate.startTime')}: {slot.startTime}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                    <Form.Group className="mt-3">
                                        <Form.Label>{t('appointmentUpdate.reason')}</Form.Label>
                                        <Form.Control
                                            as="textarea"
                                            rows={3}
                                            placeholder={t('appointmentUpdate.reasonPlaceholder')}
                                            value={reason}
                                            onChange={(e) => setReason(e.target.value)}
                                        />
                                    </Form.Group>
                                    <Form.Group className="mt-3">
                                        <Form.Label>{t('appointmentUpdate.consultationType')}</Form.Label>
                                        <Form.Select
                                            value={consultationType}
                                            onChange={e => setConsultationType(e.target.value)}
                                        >
                                            <option value="OFFLINE">{t('appointmentUpdate.offline')}</option>
                                            <option value="ONLINE">{t('appointmentUpdate.online')}</option>
                                        </Form.Select>
                                    </Form.Group>

                                    <Button variant="primary"
                                        className="mt-3"
                                        onClick={handleConfirm} disabled={loading}>
                                        {t('appointmentUpdate.button')}
                                    </Button>
                                </Card.Body>
                            </Card>
                        )}
                    </Col>
                </Row>

                <MyConfirm
                    show={showConfirm}
                    onHide={handleClose}
                    onConfirm={() => handleUpdateAppointment(appointments.appointmentId || appointments.id)}
                    loading={loading}
                    title={t('appointmentUpdate.confirmTitle')}
                    body={t('appointmentUpdate.confirmBody')}
                />
            </Container>
        </>
    )
}
export default AppointmentUpdate;