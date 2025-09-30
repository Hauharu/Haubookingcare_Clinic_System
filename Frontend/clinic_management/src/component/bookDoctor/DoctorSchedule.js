import { useContext, useEffect, useState } from "react";
import { useTranslation } from 'react-i18next';
import { MyUserContext } from "../../configs/MyContexts";
import toast from "react-hot-toast";
import { authApis, authformdataApis, endpoint } from "../../configs/Apis";
import { Alert, Button, Card, Col, Container, Image, Modal, Row, Table } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";
import { Form } from "react-bootstrap";

const DoctorSchedule = () => {

    const [loading, setLoading] = useState(false);
    const user = useContext(MyUserContext);
    const { t } = useTranslation();
    const [schedules, setSchedules] = useState([]);
    const [showEdit, setShowEdit] = useState(false);
    const [editData, setEditData] = useState({});
    const [showConfirm, setShowConfirm] = useState(false);
    const [showDelete, setShowdelete] = useState(false);
    const [deleteId, setDeleteId] = useState(null);


    const daysOfWeek = [
        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    ];
    const [newSchedule, setNewSchedule] = useState({
        dayOfWeek: "",
        startTime: "",
        endTime: "",
        isAvailable: true
    });
    const normalizeTime = (t) => {
        if (!t) return "";
        return t.length === 5 ? t + ":00" : t  // "08:00" => "08:00:00"


    }


    const loadSchedules = async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoint.getDoctorScheduleById(user.result.doctorId));
            const schedulesData = res.data?.result || [];
            setSchedules(schedulesData);
            if (Array.isArray(schedulesData) && schedulesData.length === 0) {
                toast.info(t('doctorSchedule.noSchedules'));
            }
        } catch (error) {
            toast.error(t('doctorSchedule.loadError'));
        } finally {
            setLoading(false);
        }
    }
    useEffect(() => {
        loadSchedules();
    }, [])


    const handleCreate = async (e) => {
        e.preventDefault();
        if (!newSchedule.dayOfWeek || !newSchedule.startTime || !newSchedule.endTime) {
            toast.error(t('doctorSchedule.missingInfo'));
            return;
        }
        try {
            setLoading(true);
            await authApis().post(endpoint.doctorSchedule, {
                doctorId: user.userId,
                dayOfWeek: newSchedule.dayOfWeek,
                startTime: normalizeTime(newSchedule.startTime),
                endTime: normalizeTime(newSchedule.endTime),
                isAvailable: newSchedule.isAvailable
            });
            toast.success(t('doctorSchedule.createSuccess'));
            setNewSchedule({ dayOfWeek: "", startTime: "", endTime: "", isAvailable: true });
            loadSchedules();
        } catch (error) {
            toast.error(t('doctorSchedule.createError'));
        } finally {
            setLoading(false);
        }
    }

    // Sửa lịch làm việc
    const handleEdit = (item) => {
        setEditData({
            ...item
        });
        setShowEdit(true);
    };

    const handleUpdate = async () => {
        setShowConfirm(false);
        try {
            setLoading(true);
            await authApis().patch(endpoint.updateDoctorSchedule(editData.id), {
                dayOfWeek: editData.dayOfWeek,
                startTime: normalizeTime(editData.startTime),
                endTime: normalizeTime(editData.endTime),
                isAvailable: editData.isAvailable
            });
            toast.success(t('doctorSchedule.updateSuccess'));
            setShowEdit(false);
            loadSchedules();
        } catch (err) {
            toast.error(t('doctorSchedule.updateError'));
        } finally {
            setLoading(false);
        }
    }
    const handleDelete = async() => {

        try {
            setShowdelete(false);
            setLoading(true);
            await authApis().delete(endpoint.deleteDoctorSchedule(deleteId));
            toast.success(t('doctorSchedule.deleteSuccess'));
            loadSchedules();
        } catch (error) {
            toast.error(t('doctorSchedule.deleteError'));
        }
        finally {
            setLoading(false);
        }
    }

    const dayOfWeekVN = {
        MONDAY: t('doctorSchedule.days.MONDAY'),
        TUESDAY: t('doctorSchedule.days.TUESDAY'),
        WEDNESDAY: t('doctorSchedule.days.WEDNESDAY'),
        THURSDAY: t('doctorSchedule.days.THURSDAY'),
        FRIDAY: t('doctorSchedule.days.FRIDAY'),
        SATURDAY: t('doctorSchedule.days.SATURDAY'),
        SUNDAY: t('doctorSchedule.days.SUNDAY')
    };

    return (

        <Container fluid className="p-0 mt-4 ">

            <h2
                className="mb-4 text-center py-3"
                style={{
                    background: "#f8f9fa",
                    borderRadius: "12px",
                    border: "1px solid #dee2e6",
                    fontWeight: "bold",
                    letterSpacing: "1px",
                    boxShadow: "0 2px 8px rgba(0,0,0,0.04)"
                }}
            >
                <i className="bi bi-calendar2-week me-2" style={{ color: "#0d6efd", fontSize: 32, verticalAlign: "middle" }}></i>
                {t('doctorSchedule.title')}
            </h2>
            <Card className="mb-4 shadow">
                <Card.Body>
                    <Row className="justify-content-center align-items-center">
                        <Col md={2}>
                            <Image 
                                src={user.result.avatar || "https://via.placeholder.com/80x80?text=Doctor"} 
                                roundedCircle 
                                width={80} 
                                height={80}
                                onError={(e) => {
                                    e.target.src = "https://via.placeholder.com/80x80?text=Doctor";
                                }}
                            />
                        </Col>
                        <Col>
                            <h5 style={{ color: '#0d6efd', fontWeight: 'bold', fontSize: '1.5rem', display: 'flex', alignItems: 'center' }}>
                                <i style={{ fontSize: '1.7rem' }}></i>
                                {t('doctorSchedule.doctor')}: {user.result.firstName} {user.result.lastName}
                            </h5>
                            <div><strong>{t('doctorSchedule.email')}:</strong> {user.result.email}</div>
                        </Col>
                    </Row>
                </Card.Body>

            </Card>

            {/* Tạo lịch */}

            <Card className="mb-4">
                <Card.Body>
                    <Form onSubmit={handleCreate} className="row g-2 align-items-end">
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>{t('doctorSchedule.dayOfWeek')}</Form.Label>
                                <Form.Select
                                    value={newSchedule.dayOfWeek}
                                    onChange={e => setNewSchedule({ ...newSchedule, dayOfWeek: e.target.value })}
                                    required
                                >
                                    <option value="">{t('doctorSchedule.selectDay')}</option>
                                    {daysOfWeek.map(day => (
                                        <option key={day} value={day}>{t(`doctorSchedule.days.${day}`)}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>{t('doctorSchedule.startTime')}</Form.Label>
                                <Form.Control
                                    type="time"
                                    value={newSchedule.startTime}
                                    onChange={e => setNewSchedule({ ...newSchedule, startTime: e.target.value })}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>{t('doctorSchedule.endTime')}</Form.Label>
                                <Form.Control
                                    type="time"
                                    value={newSchedule.endTime}
                                    onChange={e => setNewSchedule({ ...newSchedule, endTime: e.target.value })}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>{t('doctorSchedule.status')}</Form.Label>
                                <Form.Select
                                    value={newSchedule.isAvailable ? "true" : "false"}
                                    onChange={e => setNewSchedule({ ...newSchedule, isAvailable: e.target.value === "true" })}
                                >
                                    <option value="true">{t('doctorSchedule.working')}</option>
                                    <option value="false">{t('doctorSchedule.off')}</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Button type="submit" variant="success" className="w-100">
                                <i className="bi bi-plus-circle me-2"></i> {t('doctorSchedule.createButton')}
                            </Button>
                        </Col>
                    </Form>
                </Card.Body>
            </Card>


            {loading ? (
                <div className="text-center"><MySpinner /></div>
            ) : schedules.length === 0 ? (
                <Alert variant="info">{t('doctorSchedule.noSchedules')}</Alert>
            ) : (
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>{t('doctorSchedule.dayOfWeek')}</th>
                            <th>{t('doctorSchedule.startTime')}</th>
                            <th>{t('doctorSchedule.endTime')}</th>
                            <th>{t('doctorSchedule.status')}</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {schedules.map(s => {
                            const getAmPm = (time) => {
                                if (!time) return '';
                                const hour = parseInt(time.split(':')[0], 10);
                                return hour < 12 ? t('doctorSchedule.am') : t('doctorSchedule.pm');
                            };
                            return (
                                <tr key={s.id}>
                                    <td>{dayOfWeekVN[s.dayOfWeek] || s.dayOfWeek}</td>
                                    <td>{s.startTime} {getAmPm(s.startTime)}</td>
                                    <td>{s.endTime} {getAmPm(s.endTime)}</td>
                                    <td>{s.isAvailable ? (<span className="text-success">{t('doctorSchedule.working')}</span>) : (<span className="text-danger">{t('doctorSchedule.off')}</span>)}
                                    </td>
                                    <td>
                                        <Button variant="warning" size="sm" onClick={() => handleEdit(s)} className="me-2">
                                            <i className="bi bi-pencil-square"></i> {t('doctorSchedule.editButton')}
                                        </Button>
                                        <Button variant="danger" size="sm" onClick={() => { setDeleteId(s.id); setShowdelete(true); }}>
                                            <i className="bi bi-trash"></i> {t('doctorSchedule.deleteButton')}
                                        </Button>
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </Table>
            )}

            {/* Modal sửa */}
            <Modal show={showEdit} onHide={() => setShowEdit(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <i className="bi bi-pencil-square me-2"></i>
                        {t('doctorSchedule.editTitle')}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('doctorSchedule.dayOfWeek')}</Form.Label>
                            <Form.Select
                                value={editData.dayOfWeek || ""}
                                onChange={e => setEditData({ ...editData, dayOfWeek: e.target.value })}
                            >
                                <option value="">{t('doctorSchedule.selectDay')}</option>
                                {daysOfWeek.map(day => (
                                    <option key={day} value={day}>{t(`doctorSchedule.days.${day}`)}</option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('doctorSchedule.newStartTime')}</Form.Label>
                            <Form.Control
                                type="time"
                                value={editData.startTime || ""}
                                onChange={e => setEditData({ ...editData, startTime: e.target.value })}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('doctorSchedule.newEndTime')}</Form.Label>
                            <Form.Control
                                type="time"
                                value={editData.endTime || ""}
                                onChange={e => setEditData({ ...editData, endTime: e.target.value })}
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowEdit(false)}>
                        {t('doctorSchedule.cancel')}
                    </Button>
                    <Button
                        variant="primary"
                        onClick={() => setShowConfirm(true)}
                    >
                        <i className="bi bi-check-circle me-2"></i>
                        {t('doctorSchedule.confirmEdit')}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Model xóa */}
            <Modal show={showDelete} onHide={() => setShowdelete(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>{t('doctorSchedule.deleteTitle')}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {t('doctorSchedule.deleteBody')}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowdelete(false)}>
                        {t('doctorSchedule.cancel')}
                    </Button>
                    <Button variant="danger" onClick={handleDelete}>
                        <i className="bi bi-trash"></i> {t('doctorSchedule.deleteButton')}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Modal xác nhận */}
            <Modal show={showConfirm} onHide={() => setShowConfirm(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>{t('doctorSchedule.confirmEditTitle')}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {t('doctorSchedule.confirmEditBody', { endTime: editData.endTime })}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowConfirm(false)}>
                        {t('doctorSchedule.cancel')}
                    </Button>
                    <Button variant="success" onClick={handleUpdate}>
                        <i className="bi bi-check-circle me-2"></i>
                        {t('doctorSchedule.agree')}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    )
}
export default DoctorSchedule;