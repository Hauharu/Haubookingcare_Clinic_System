import { use, useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Apis, { endpoint } from "../../configs/Apis";
import { Alert, Button, Card, Col, Container, Form, Row } from "react-bootstrap";
import { load } from "react-cookies";
import MySpinner from "../layout/MySpinner";
import { Link, unstable_HistoryRouter, useLocation, useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/MyContexts";
import "./Styles/Calendar.css";
import LoadMoreButton from "../layout/LoadMoreButton";
import toast from "react-hot-toast";
import MyConfigs from "../../configs/MyConfigs";

const Calendar = () => {
    const { t } = useTranslation();
    const location = useLocation();
    const nav = useNavigate();
    const initialSlots = location.state?.slots || [];
    const [doctorId, setDoctorId] = useState(() => {
        let id = location.state?.doctorId;
        if (!id && initialSlots.length > 0) {
            id = initialSlots[0].doctorId;
        }
        return id;
    });

    // Helper: always get latest doctorId from state or slots
    const getCurrentDoctorId = () => {
        let id = doctorId;
        if (!id && slots.length > 0) {
            id = slots[0].doctorId;
        }
        return id;
    };

    const [loading, setLoading] = useState(false);
    const [slots, setSlots] = useState(initialSlots);
    const [page, setPage] = useState(0);
    const [date, setDate] = useState("");
    const [time, setTime] = useState("");
    const [hasMore, setHasMore] = useState(true);

    // Handle booking click
    const handleBookingClick = (slot) => {
        nav("/bookDoctor", { state: { slot } });
    };

    // Gom slot theo bác sĩ
    const groupSlotsByDoctor = (slotsArr) => {
        const doctorMap = {};
        slotsArr.forEach(slot => {
            const id = slot.doctorId;
            if (!doctorMap[id]) {
                doctorMap[id] = {
                    doctorName: slot.doctorName,
                    avatar: slot.avatar,
                    slots: []
                };
            }
            doctorMap[id].slots.push(slot);
        });
        return Object.entries(doctorMap).map(([doctorId, data]) => ({ doctorId, ...data }));
    };

    const user = useContext(MyUserContext);

    const formattedDate = date ? new Date(date).toISOString().split('T')[0] : "";
    const formattedTime = time ? `${time}:00` : "";

    //Theo doctorId ở bên Finddoctor.js -> nút xem lịch trống

    const loadSlots = async () => {
        const id = getCurrentDoctorId();
        console.log('--- loadSlots called ---');
        console.log('doctorId:', id, 'date:', date, 'time:', time);
        console.log('formattedDate:', formattedDate, 'formattedTime:', formattedTime);
        setLoading(true);
        try {
            if (!id) {
                toast.error(t('calendar.noDoctorInfo'));
                setLoading(false);
                return;
            }
            let url = `${endpoint['findDoctor']}?page=${page}&size=${MyConfigs.PAGE_SIZE}&doctorId=${id}`;
            if (formattedDate) {
                url += `&slotDate=${formattedDate}`;
            }
            if (formattedTime) {
                url += `&startTime=${formattedTime}`;
            }
            console.log('Request URL:', url);
            const res = await Apis.get(url);
            console.log('API response:', res.data);
            const slotList = res.data.result?.content || [];
            const isLastPage = res.data.result?.last;
            setSlots(slotList);
            setHasMore(!isLastPage);
        } catch (ex) {
            console.error('Error in loadSlots:', ex);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        (async () => {
            await loadSlots();
        })();
    }, [doctorId, page, date, time]);
    return (
        <Container fluid className="p-0 container-custom">


            <Row className="g-4 custom-row mt-5 align-items-center search-bar">
                <Col md={4} lg={4} xs={12}>
                    <Form.Group>
                        <Form.Label className="fw-bold text-primary">{t('calendar.selectDate')}</Form.Label>
                        <Form.Control
                            type="date"
                            value={date}
                            onChange={(e) => {
                                setDate(e.target.value);
                                setPage(0);
                            }}
                            className="rounded-pill shadow-sm"
                        />
                    </Form.Group>
                </Col>
                <Col md={4} lg={4} xs={12}>
                    <Form.Group>
                        <Form.Label className="fw-bold text-primary">{t('calendar.selectTime')}</Form.Label>
                        <Form.Control
                            type="time"
                            value={time}
                            onChange={(e) => {
                                setTime(e.target.value);
                                setPage(0);
                            }}
                            className="rounded-pill shadow-sm"
                        />
                    </Form.Group>
                </Col>
                <Col md={4} lg={4} xs={12} className="text-center d-flex flex-row justify-content-center align-items-center gap-2">
                    <Button
                        variant="primary"
                        className="search-button mt-4 rounded-pill px-4 shadow-sm"
                        onClick={() => {
                            setPage(0);
                            setTimeout(() => loadSlots(), 0);
                        }}
                    >
                        <i className="bi bi-search"></i> {t('calendar.search')}
                    </Button>
                    <Button
                        variant="outline-primary"
                        className="mt-4 rounded-pill px-4 shadow-sm btn-cancel-filter"
                        onClick={() => {
                            setDate("");
                            setTime("");
                            setPage(0);
                            setTimeout(() => loadSlots(), 0);
                        }}
                    >
                        <i className="bi bi-x-circle"></i> Hủy lọc
                    </Button>
                </Col>
            </Row>
            <Row className="justify-content-center g-4 mt-5">
                <Col xs="auto">
                    <h1 className="calendar-title animated-title">{t('calendar.selectEmptySlot')}</h1>
                </Col>
                <Col xs="auto">
                    <Button variant="secondary" onClick={() => nav("/findDoctor")}>{t('calendar.backToFindDoctor')}</Button>
                </Col>
            </Row>


            <Row className="justify-content-center g-4  mt-5">
                {loading && (
                    <div className="text-center">
                        <MySpinner />
                    </div>
                )}
                {slots.length === 0 && (
                    <Alert variant="info" className="text-center">
                        {t('calendar.notFound')}
                    </Alert>
                )}
                {groupSlotsByDoctor(slots).map(doctor => (
                    <Col key={doctor.doctorId} md={4} lg={4} xs={12} className="mb-4">
                        <Card className="card-doctor shadow-sm">
                            <Card.Img variant="top" src={doctor.avatar || '/default-avatar.png'} />
                            <Card.Body className="card-body-custom">
                                <div className="doctor-card-title">{t('calendar.doctorLabel')} {doctor.doctorName || t('calendar.noDoctorName')}</div>
                                <div className="doctor-card-divider"></div>
                                <div className="mt-2">
                                    <strong style={{ fontSize: '0.95rem', color: '#1976d2' }}>Lịch trống:</strong>
                                    <div className="doctor-slot-list">
                                        {doctor.slots.map((slot, idx) => (
                                            <button key={slot.id || idx} className="doctor-slot-btn"
                                                onClick={() => handleBookingClick(slot)}>
                                                <span style={{ fontWeight: 500 }}>{new Date(slot.slotDate).toLocaleDateString()}</span><br />
                                                <span style={{ fontSize: '0.98rem' }}>{slot.startTime} - {slot.endTime}</span>
                                            </button>
                                        ))}
                                        {doctor.slots.length === 0 && <span style={{ fontSize: '0.85rem' }}>Không có lịch trống</span>}
                                    </div>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}

            </Row>


            {/* Xem thêm */}

            <Row className="justify-content-center align-items-center g-4 mb-4 mt-4">
                {slots.length > 0 && !loading && (
                    <Col md={8} lg={6} xs={10} className="d-flex justify-content-center align-items-center gap-2">
                        <Button
                            variant="outline-primary"
                            disabled={page === 0}
                            onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                        >
                            &lt; {t('calendar.prev')}
                        </Button>
                        <span>{t('calendar.page')} {page + 1}</span>
                        <Button
                            variant="outline-primary"
                            disabled={!hasMore}
                            onClick={() => setPage((prev) => prev + 1)}
                        >
                            {t('calendar.next')} &gt;
                        </Button>
                    </Col>
                )}
            </Row>
            <Row className="g-4 mb-4 mt-4"></Row>
        </Container>
    );
}
export default Calendar;