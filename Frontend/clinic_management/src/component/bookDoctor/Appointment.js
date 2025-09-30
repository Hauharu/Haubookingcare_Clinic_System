import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../../configs/MyContexts";
import { authApis, endpoint, fbApis } from "../../configs/Apis";
import { Alert, Button, Card, Col, Container, Row, Form, Modal } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import MySpinner from "../layout/MySpinner";
import MyConfigs from "../../configs/MyConfigs";
import toast from "react-hot-toast";
import MyConfirm from "../layout/MyConfirm";
import HealthRecordForm from "../healthRecord/HealthRecordForm";
import { useTranslation } from "react-i18next";

const Appointment = () => {
    // State cho filter theo ngày
    const [dateFilter, setDateFilter] = useState("");
    const [dateFilterApplied, setDateFilterApplied] = useState("");

    // Sử dụng i18next cho trạng thái
    const { t } = useTranslation();
    const statusToLabel = {
        SCHEDULED: t('appointment.status.scheduled'),
        COMPLETED: t('appointment.status.completed'),
        CANCELLED_BY_PATIENT: t('appointment.status.cancelled')
    };
    //Phân trang cho thằng này
    const [loading, setLoading] = useState(false);
    const [pageLoading, setPageLoading] = useState(false);
    const [appointments, setAppointments] = useState([]);
    const [allAppointments, setAllAppointments] = useState([]); // lưu toàn bộ lịch để lọc
    const [page, setPage] = useState(1);
    const user = useContext(MyUserContext);
    const [msg, setMsg] = useState("");
    const [hasMore, setHasMore] = useState(true);

    // State cho filter trạng thái
    const [statusFilter, setStatusFilter] = useState("SCHEDULED"); // Mặc định hiển thị lịch đang đặt
    const [filteredAppointments, setFilteredAppointments] = useState([]);

    // Sắp xếp lịch hẹn tăng dần theo ngày hẹn (appointmentTime ASC)
    useEffect(() => {
        if (dateFilterApplied) {
            // Nếu đang lọc, dùng allAppointments để lọc và phân trang
            let filtered = allAppointments.filter(a => {
                const apptDate = new Date(a.appointmentTime).toISOString().split('T')[0];
                return apptDate === dateFilterApplied;
            });
            filtered = filtered.sort((a, b) => new Date(a.appointmentTime) - new Date(b.appointmentTime));
            // Phân trang trên filtered
            const startIdx = (page - 1) * MyConfigs.PAGE_SIZE;
            const endIdx = startIdx + MyConfigs.PAGE_SIZE;
            setFilteredAppointments(filtered.slice(startIdx, endIdx));
            setHasMore(filtered.length > endIdx);
        } else {
            // Không lọc, dùng appointments như cũ
            let sorted = [...appointments].sort((a, b) => new Date(a.appointmentTime) - new Date(b.appointmentTime));
            setFilteredAppointments(sorted);
            setHasMore(appointments.length >= MyConfigs.PAGE_SIZE);
        }
    }, [appointments, dateFilterApplied, allAppointments, page]);

    const [showConfirm, setShowConfirm] = useState({
        show: false,
        appointmentId: null,
        doctorId: null,
        createdAt: null,
    });

    const nav = useNavigate();

    // Tạo roomchat -> làm này xem thêm

    const [room, setRoom] = useState();
    const [reviewedAppointments, setReviewedAppointments] = useState({});

    // State cho chỉnh sửa
    const [showEdit, setShowEdit] = useState(false);
    const [editRecord, setEditRecord] = useState({});
    const [records, setRecords] = useState([]);
    const [updateLoading, setUpdateLoading] = useState(false);
    //doctorId chỉ lấy 1 trong list appointment 

    const [showCompleteConfirm, setShowCompleteConfirm] = useState(null);
    const [showHealthRecordForm, setShowHealthRecordForm] = useState(null);

    // Hàm xác nhận hoàn thành
    const handleCompleteAppointment = async (appointmentId) => {
        try {
            setLoading(prev => ({ ...prev, [appointmentId]: true }));

            const res = await authApis().patch(`/appointments/${appointmentId}/status`, {
                status: "COMPLETED"
            });
            toast.success(res.data.message || t('appointment.success.complete'));
            await loadAppointments();
            window.dispatchEvent(new Event('refresh-notifications'));
        } catch (err) {
            toast.error(err.response?.data?.error || t('appointment.error.updateStatus'));
        } finally {
            setLoading(prev => ({ ...prev, [appointmentId]: false }));
            setShowCompleteConfirm(null);
        }
    };

    //prev ghi lại dữ liệu -> dùng loading sẽ bị đè
    const createRoom = async (doctorId, appointment) => {
        try {
            setLoading(prev => ({ ...prev, [appointment.id]: true }));
            let res = await fbApis().post(endpoint['chats'], {
                "userId1": user.result?.id,
                "userId2": doctorId,
            });
            const roomData = res.data;
            //truyền appointment vào để lấy thông tin của doctor 
            nav("/roomchat", {
                state: {
                    room: roomData,
                    appointment: appointment
                },
            });


        } catch (ex) {
            let errorMsg = t('appointment.error.createChat');
            if (ex?.message?.includes("Network Error") || ex?.code === "ERR_NETWORK") {
                errorMsg = t('appointment.error.network');
            }
            alert(errorMsg);
        }
        finally {
            setLoading(prev => ({ ...prev, [appointment.id]: false }));
        }
    }

    const deleteAppointment = async (appointmentId, doctorId, createdAt) => {
        try {
            setLoading(true);

            // Kiểm tra thời gian tạo lịch hẹn
            const now = new Date().getTime();
            const createdTime = new Date(createdAt).getTime();
            const diffInMilliseconds = now - createdTime;

            if (diffInMilliseconds > 24 * 60 * 60 * 1000) {
                toast.error(t('appointment.error.cancelLate'));
                setLoading(false);
                setShowConfirm({
                    show: false,
                    appointmentId: null,
                    doctorId: null,
                });
                return;
            }

            // Chỉ bệnh nhân mới được hủy lịch
            const res = await authApis().delete(endpoint.deleteAppointment(appointmentId));
            await loadAppointments();
            toast.success(res.data.message || t('appointment.success.cancel'));
            window.dispatchEvent(new Event('refresh-notifications'));
        } catch (ex) {
            if (ex.response) {
                toast.error(ex.response.data?.message || ex.response.data?.error || t('appointment.error.cancel'));
            } else {
                toast.error(t('appointment.error.cancelNoResponse'));
            }
        } finally {
            setLoading(false);
            setShowConfirm({
                show: false,
                appointmentId: null,
                doctorId: null,
                createdAt: null,
            });
        }
    };




    const loadAppointments = async () => {
        try {
            setPageLoading(true);
            // Nếu đang lọc, lấy toàn bộ lịch hẹn để lọc trên frontend
            let urlAll = `${endpoint['appointments']}?page=0&size=1000`;
            let urlPage = `${endpoint['appointments']}?page=${page - 1}&size=${MyConfigs.PAGE_SIZE}`;
            const apiInstance = authApis();
            if (dateFilterApplied) {
                // Lấy toàn bộ lịch để lọc
                const resAll = await apiInstance.get(urlAll);
                let allArray = [];
                if (Array.isArray(resAll.data)) {
                    allArray = resAll.data;
                } else if (resAll.data.result && Array.isArray(resAll.data.result)) {
                    allArray = resAll.data.result;
                } else if (resAll.data.result && Array.isArray(resAll.data.result.content)) {
                    allArray = resAll.data.result.content;
                }
                setAllAppointments(allArray);
            } else {
                // Lấy lịch theo trang như cũ
                const resPage = await apiInstance.get(urlPage);
                let appointmentsArray = [];
                if (Array.isArray(resPage.data)) {
                    appointmentsArray = resPage.data;
                } else if (resPage.data.result && Array.isArray(resPage.data.result)) {
                    appointmentsArray = resPage.data.result;
                } else if (resPage.data.result && Array.isArray(resPage.data.result.content)) {
                    appointmentsArray = resPage.data.result.content;
                }
                setAppointments(appointmentsArray);
            }
            // ...existing code for review...
        } catch (ex) {
            setMsg(t('appointment.error.general') + ` ${ex}`);
        }
        finally {
            setPageLoading(false);
        }
    }

    const loadReviewOfAppointment = async (appointmentId) => {
        try {
            setLoading(true);
            const res = await authApis().get(`reviews/appointment/${appointmentId}`);
            return res.data;
        } catch (ex) {
            console.error(ex);
        }
        finally {
            setLoading(false);
        }
    }


    useEffect(() => {
        setPage(1);
        setHasMore(true);
        setAppointments([]);
    }, []);


    useEffect(() => {
        loadAppointments();
    }, [page, statusFilter, dateFilterApplied]);

    useEffect(() => {
        setPage(1);
    }, [statusFilter]);

    // Effect để filter appointments theo status
    useEffect(() => {


        if (appointments.length > 0) {
            const filtered = appointments.filter(appointment => appointment.status === statusFilter);
            setFilteredAppointments(filtered);
        } else {
            setFilteredAppointments([]);
        }
    }, [appointments, statusFilter]);

    // Function để thay đổi filter
    const handleStatusFilterChange = (newStatus) => {
        setStatusFilter(newStatus);
        setPage(1);
    };

    // Khi nhấn nút lọc ngày
    const handleDateFilterApply = (date) => {
        setDateFilterApplied(date);
        setPage(1);
    };

    // Khi xóa lọc ngày
    const handleDateFilterClear = () => {
        setDateFilterApplied("");
        setDateFilter("");
        setAllAppointments([]);
        setPage(1);
    };

    const handleNavUpdate = (appointment) => {
        try {
            setLoading(true);
            const now = new Date().getTime();
            const createdAt = new Date(appointment.createdAt).getTime();
            const diffInMilliseconds = now - createdAt;
            if (diffInMilliseconds <= 24 * 60 * 60 * 1000) {
                // Tách slotDate và startTime từ appointmentTime nếu có
                let slotDate = appointment.slotDate || appointment.date || null;
                let startTime = appointment.startTime || appointment.timeStart || appointment.start || null;
                if (appointment.appointmentTime) {
                    const parts = appointment.appointmentTime.split('T');
                    if (parts.length === 2) {
                        slotDate = parts[0];
                        startTime = parts[1];
                    }
                }
                nav("/updateAppointment", {
                    state: {
                        appointment: {
                            ...appointment,
                            slotDate,
                            startTime
                        }
                    }
                });
            } else {
                toast.error(t('appointment.error.editLate'));
            }

        } catch (ex) {

        }
        finally {
            setLoading(false);
        }
    }


    const handleInvoiceRedirect = async (appointment) => {
        if (appointment) {
            // Nếu có invoiceId thì chuyển hướng đúng route /invoice/:id
            const invoiceId = appointment.invoiceId || appointment.invoice?.id;
            if (invoiceId) {
                nav(`/invoice/${invoiceId}`);
            } else {
                // Nếu chưa có, gọi API lấy hóa đơn theo appointmentId
                try {
                    console.log('Debug Appointment:', appointment);
                    const appointmentId = appointment.appointmentId || appointment.id;
                    if (!appointmentId) {
                        toast.error(t('appointment.error.noAppointmentId'));
                        return;
                    }
                    console.log('Debug appointmentId:', appointmentId);
                    let invoiceData;
                    try {
                        const res = await authApis().get(endpoint.invoiceByAppointment(appointmentId));
                        console.log('Debug invoice API response:', res);
                        invoiceData = res.data.result || res.data;
                        if (invoiceData && invoiceData.id) {
                            nav(`/invoice/${invoiceData.id}`);
                            return;
                        }
                    } catch (err) {
                        // Nếu lỗi 404 thì tạo hóa đơn mới
                        if (err?.response?.status === 404) {
                            console.warn('Không tìm thấy hóa đơn, tiến hành tạo mới...');
                            // Chuẩn bị dữ liệu tạo hóa đơn
                            // Fix dữ liệu tạo hóa đơn: amount > 0, status đúng enum, dueDate chuẩn yyyy-MM-dd
                            const amount = appointment.amount && appointment.amount > 0 ? appointment.amount : 100000;
                            let dueDate = undefined;
                            if (appointment.appointmentTime) {
                                // Format yyyy-MM-dd
                                const d = new Date(appointment.appointmentTime);
                                dueDate = d.toISOString().split('T')[0];
                            }
                            const status = 'PENDING'; // Enum đúng với backend
                            const invoiceRequest = {
                                appointmentId,
                                amount,
                                dueDate,
                                status
                            };
                            try {
                                const createRes = await authApis().post('/invoices', invoiceRequest);
                                const newInvoice = createRes.data.result || createRes.data;
                                if (newInvoice && newInvoice.id) {
                                    toast.success(t('appointment.success.createInvoice'));
                                    nav(`/invoice/${newInvoice.id}`);
                                } else {
                                    toast.error(t('appointment.error.createInvoice'));
                                }
                            } catch (createErr) {
                                console.error('Lỗi tạo hóa đơn:', createErr);
                                toast.error(t('appointment.error.createInvoiceGeneral') + (createErr?.response?.data?.error || createErr?.message || ''));
                            }
                        } else {
                            console.error('Lỗi lấy hóa đơn:', err);
                            toast.error(t('appointment.error.getInvoice') + (err?.response?.data?.error || err?.message || ''));
                        }
                    }
                } catch (err) {
                    console.error('Lỗi tổng quát:', err);
                    toast.error(t('appointment.error.processInvoice') + (err?.response?.data?.error || err?.message || ''));
                }
            }
        }
    };

    const handleReviewDoctorRedirect = (doctor, appointmentId, patientId) => {
        if (doctor) {
            //Neu dung navigate thi nho bo {Link} o trong Button
            nav("/doctorReview", { state: { doctor, appointmentId, patientId } });
        }
    };
    const handleConfirm = (appointmentId, doctorId, createdAt) => {
        const now = new Date().getTime();
        const createdTime = new Date(createdAt).getTime();
        const diffInMilliseconds = now - createdTime;

        if (diffInMilliseconds > 24 * 60 * 60 * 1000) {
            toast.error(t('appointment.error.cancelLate'));
            return;
        }

        setShowConfirm({
            show: true,
            appointmentId,
            doctorId,
            createdAt,
        });
    };

    const handleClose = () => {
        setShowConfirm({
            show: false,
            appointmentId: null,
            doctorId: null,
            createdAt: null,
        });
    };

    const handleEdit = async (appointment) => {
        try {
            setLoading(true);


            // Thử lấy hồ sơ có sẵn trước
            let res;
            try {
                res = await authApis().get(`/health-records/appointment/${appointment.id}`);
                // Nếu có hồ sơ rồi thì chỉnh sửa - Backend trả về ApiResponse<HealthRecordResponse>


                if (res.data.result) {
                    const existingRecord = res.data.result;


                    setEditRecord({
                        id: existingRecord.id,
                        appointmentId: existingRecord.appointmentId || appointment.id,
                        patientId: existingRecord.patientId || appointment.patientId,
                        doctorId: existingRecord.doctorId || user.result?.id,
                        symptoms: existingRecord.symptoms || '',
                        diagnosis: existingRecord.diagnosis || '',
                        notes: existingRecord.notes || '',
                        recordDate: existingRecord.recordDate || ''
                    });


                } else {

                    setEditRecord({
                        appointmentId: appointment.id,
                        patientId: appointment.patientId,
                        doctorId: appointment.doctorId || user.result?.id,
                        symptoms: '',
                        diagnosis: '',
                        notes: ''
                    });
                }
            } catch (error) {
                if (error.response?.status === 404) {
                    // Nếu chưa có hồ sơ thì tạo mới
                    // ...existing code...
                    setEditRecord({
                        appointmentId: appointment.id,
                        patientId: appointment.patientId,
                        doctorId: appointment.doctorId || user.result?.id,
                        symptoms: '',
                        diagnosis: '',
                        notes: ''
                    });
                } else {
                    throw error; // Ném lại lỗi khác
                }
            }

            setShowEdit(true);
        } catch (ex) {
            alert(t('appointment.error.openHealthRecord'));
        }
        finally {
            setLoading(false);
        }


    };

    const handleEditChange = (e) => {
        setEditRecord({ ...editRecord, [e.target.name]: e.target.value });
    };

    const handleEditSubmit = async (e) => {
        e.preventDefault();
        setUpdateLoading(true);
        try {
            // Theo HealthRecordCreationRequest từ backend
            const healthRecordData = {
                patientId: editRecord.patientId,
                appointmentId: editRecord.appointmentId,
                recordDate: new Date().toISOString().split('T')[0], // Format YYYY-MM-DD
                symptoms: editRecord.symptoms || "",
                diagnosis: editRecord.diagnosis || "",
                notes: editRecord.notes || ""
                // Không cần gửi doctorId vì backend tự lấy từ SecurityContext
            };



            let res;
            if (editRecord.id) {
                // Cập nhật hồ sơ có sẵn - sử dụng PATCH như trong controller
                res = await authApis().patch(`/health-records/${editRecord.id}`, {
                    recordDate: healthRecordData.recordDate,
                    symptoms: healthRecordData.symptoms,
                    diagnosis: healthRecordData.diagnosis,
                    notes: healthRecordData.notes
                });
            } else {
                // Tạo hồ sơ mới
                res = await authApis().post(`/health-records`, healthRecordData);
            }

            if (res.status === 200 || res.status === 201) {
                alert(t('appointment.success.updateHealthRecord'));
                setShowEdit(false);
                // Reload lại danh sách appointments để cập nhật dữ liệu mới
                loadAppointments();
            }
        } catch (error) {


            if (error.response?.status === 403) {
            } else {
                alert(t('appointment.error.updateHealthRecord') + (error.response?.data?.message || error.message));
            }
        }
        finally {
            setUpdateLoading(false);
        }
    };

    useEffect(() => {
        if (page === 1) {
            setHasMore(true);
        }
    }, [page, statusFilter]);

    return (
        <Container fluid className="p-0 container-custom">
            {/* Hiển thị thông báo lỗi nếu có */}
            {msg && (
                <Alert variant="danger" className="m-2 text-center">
                    {msg}
                </Alert>
            )}
            {/* Status filter tabs below date filter */}
            <Row className="mb-2 mt-4">
                <Col>
                    <div className="d-flex justify-content-center">
                        <Button
                            variant={statusFilter === "SCHEDULED" ? "primary" : "outline-primary"}
                            className="me-2"
                            onClick={() => handleStatusFilterChange("SCHEDULED")}
                        >
                            📅 {t('appointment.status.scheduled')}
                        </Button>
                        <Button
                            variant={statusFilter === "COMPLETED" ? "success" : "outline-success"}
                            className="me-2"
                            onClick={() => handleStatusFilterChange("COMPLETED")}
                        >
                            ✅ {t('appointment.status.completed')}
                        </Button>
                        <Button
                            variant={statusFilter === "CANCELLED_BY_PATIENT" ? "danger" : "outline-danger"}
                            onClick={() => handleStatusFilterChange("CANCELLED_BY_PATIENT")}
                        >
                            ❌ {t('appointment.status.cancelled')}
                        </Button>
                    </div>
                </Col>
            </Row>
            {/* Date filter above appointment list, left aligned */}
            <Row className="mt-3 mb-2">
                <Col xs={12} md={4} lg={3} className="d-flex align-items-center">
                    <Form.Group className="mb-0 w-100">
                        <Form.Label className="mb-0 me-2">{t('appointment.filterByDate')}</Form.Label>
                        <div className="d-flex align-items-center gap-2">
                            <Form.Control
                                type="date"
                                value={dateFilter}
                                onChange={e => setDateFilter(e.target.value)}
                                style={{ minWidth: 150 }}
                            />
                            <Button
                                variant="primary"
                                size="sm"
                                disabled={!dateFilter}
                                onClick={() => handleDateFilterApply(dateFilter)}
                            >{t('appointment.filter')}</Button>
                            {dateFilterApplied && (
                                <Button variant="outline-secondary" size="sm" onClick={handleDateFilterClear} style={{ minWidth: 110 }}>{t('appointment.clearFilter')}</Button>
                            )}
                        </div>
                    </Form.Group>
                </Col>
            </Row>
            <Row className="g-4 mt-2">
                {appointments.length === 0 && !pageLoading && (
                    <Alert variant="info" className="m-2 text-center">
                        {t('appointment.noAppointments')}
                    </Alert>
                )}
                {appointments.length > 0 && filteredAppointments.length === 0 && (
                    <Alert variant="info" className="m-2 text-center">
                        {t('appointment.noAppointmentsWithStatus', { status: statusToLabel[statusFilter] })}
                    </Alert>
                )}

                {filteredAppointments.map((a, idx) => {
                    return (
                        <Col key={a.id} md={4} lg={4} xs={12}>
                            <Card className="card-doctor shadow-sm position-relative">
                                <Card.Body className="card-body-custom">
                                    <div>
                                        <Card.Title className="card-title">{t('appointment.date')}: {new Date(a.appointmentTime).toLocaleDateString("vi-VN")}
                                        </Card.Title>
                                        <Card.Text className="card-text" style={{ fontSize: '0.85rem' }}>
                                            {user.result?.role === "PATIENT" && (
                                                <>
                                                    <strong>{t('appointment.doctor')}:</strong> {
                                                        a.doctorName || t('appointment.noDoctorName')
                                                    }
                                                    <br />
                                                </>
                                            )}
                                            {user.result?.role === "DOCTOR" && (
                                                <>
                                                    <strong>{t('appointment.patient')}:</strong> {
                                                        a.patientName || t('appointment.noPatientName')
                                                    }
                                                    <br />
                                                </>
                                            )}
                                        </Card.Text>
                                        <Card.Text className="card-text" style={{ fontSize: '0.85rem' }}>
                                            <strong>{t('appointment.startTime')}:</strong> {new Date(a.appointmentTime).toLocaleTimeString("vi-VN")}
                                            <br />
                                            <strong>{t('appointment.duration')}:</strong> {a.durationMinutes} {t('appointment.minutes')}
                                            <br />
                                            <strong>{t('appointment.reason')}:</strong> {a.reason}
                                            <br />
                                            <strong>{t('appointment.consultationType')}:</strong> {a.consultationType === 'ONLINE' ? t('booking.online') : t('booking.offline')}
                                            <br />
                                            <strong>{t('appointment.statusLabel')}:</strong> {statusToLabel[a.status] || a.status}
                                            <br />
                                        </Card.Text>
                                        <Card.Text className="card-text " style={{ fontSize: '0.85rem', color: 'red' }}>
                                            <strong>{t('appointment.createdAt')}:</strong>  {new Date(a.createdAt).toLocaleString("vi-VN")}
                                        </Card.Text>
                                    </div>
                                    {user.result?.role === "DOCTOR" && (
                                        <div className="d-grid gap-1 mt-2">
                                            {loading[a.id] === true ? (
                                                <MySpinner />
                                            ) : (
                                                <>
                                                    <Button
                                                        variant="success"
                                                        className="d-flex align-items-center justify-content-center mb-1"
                                                        onClick={() => createRoom(a.patientId, a)}
                                                        size="sm"
                                                        disabled={
                                                            Object.values(loading).some(v => v) && !loading[a.id]
                                                        }
                                                    >
                                                        <i className="bi bi-chat-dots me-1"></i> {t('appointment.chatWithPatient')}
                                                    </Button>

                                                    {(a.status === "SCHEDULED") && (
                                                        <>
                                                            <Button
                                                                variant="primary"
                                                                className="d-flex align-items-center justify-content-center mb-1"
                                                                onClick={() => {
                                                                    setShowHealthRecordForm({
                                                                        appointmentId: a.id,
                                                                        patientId: a.patient?.id || a.patientId,
                                                                        doctorId: a.doctorId,
                                                                        mode: "create"
                                                                    });
                                                                }}
                                                                size="sm"
                                                            >
                                                                <i className="bi bi-file-medical me-1"></i> {t('appointment.createHealthRecord')}
                                                            </Button>

                                                            <Button
                                                                variant="warning"
                                                                className="d-flex align-items-center justify-content-center mb-1"
                                                                onClick={() => setShowCompleteConfirm(a.id)}
                                                                size="sm"
                                                                disabled={Object.values(loading).some(v => v) && !loading[a.id]}
                                                            >
                                                                <i className="bi bi-check2-circle me-1"></i> {t('appointment.confirmCompleted')}
                                                            </Button>
                                                        </>
                                                    )}

                                                    {/* Chỉ bệnh nhân mới có thể hủy lịch */}
                                                    {a.status === "SCHEDULED" && user.result?.role === "PATIENT" && (
                                                        <Button
                                                            variant="danger"
                                                            className="d-flex align-items-center justify-content-center mb-1"
                                                            onClick={() => handleConfirm(a.id, a.doctorId, a.createdAt)}
                                                            size="sm"
                                                            disabled={loading}
                                                            title={t('appointment.cancelAppointment')}
                                                        >
                                                            <i className="bi bi-x-circle me-1"></i> {t('appointment.cancelAppointment')}
                                                        </Button>
                                                    )}
                                                    <Modal show={!!showCompleteConfirm} onHide={() => setShowCompleteConfirm(null)}>
                                                        <Modal.Header closeButton>
                                                            <Modal.Title>{t('appointment.confirmCompleted')}</Modal.Title>
                                                        </Modal.Header>
                                                        <Modal.Body>{t('appointment.confirmCompletedBody')}</Modal.Body>
                                                        <Modal.Body>{t('appointment.confirmCompletedNote')}</Modal.Body>
                                                        <Modal.Footer>
                                                            <Button variant="secondary" onClick={() => setShowCompleteConfirm(null)}>
                                                                {t('commonButton.cancel')}
                                                            </Button>
                                                            <Button
                                                                variant="warning"
                                                                onClick={() => handleCompleteAppointment(showCompleteConfirm)}
                                                                disabled={loading[showCompleteConfirm]}
                                                            >
                                                                {t('commonButton.confirm')}
                                                            </Button>
                                                        </Modal.Footer>
                                                    </Modal>
                                                    {a.status === "COMPLETED" && (
                                                        <Button
                                                            variant="primary"
                                                            disabled={loading[a.id] === true}
                                                            onClick={() => setShowHealthRecordForm({
                                                                appointmentId: a.id,
                                                                patientId: a.patient?.id || a.patientId,
                                                                doctorId: a.doctorId,
                                                                mode: "update"
                                                            })}
                                                            size="sm"
                                                        >
                                                            <i className="bi bi-journal-medical me-1"></i>
                                                            {t('appointment.updateHealthRecord')}
                                                        </Button>
                                                    )}
                                                </>
                                            )}
                                        </div>
                                    )}
                                    <Modal show={showEdit} onHide={() => setShowEdit(false)}>
                                        <Modal.Header closeButton>
                                            <Modal.Title>Chỉnh sửa hồ sơ</Modal.Title>
                                        </Modal.Header>
                                        <Form onSubmit={handleEditSubmit}>
                                            <Modal.Body>
                                                <Form.Group className="mb-2">
                                                    <Form.Label>Triệu chứng</Form.Label>
                                                    <Form.Control
                                                        name="symptoms"
                                                        value={editRecord?.symptoms || ""}
                                                        onChange={handleEditChange}
                                                    />
                                                </Form.Group>
                                                <Form.Group className="mb-2">
                                                    <Form.Label>Chẩn đoán</Form.Label>
                                                    <Form.Control
                                                        name="diagnosis"
                                                        value={editRecord?.diagnosis || ""}
                                                        readOnly={user.result?.role !== "DOCTOR"}
                                                        onChange={handleEditChange}
                                                    />
                                                </Form.Group>
                                                <Form.Group className="mb-2">
                                                    <Form.Label>Ghi chú</Form.Label>
                                                    <Form.Control
                                                        name="notes"
                                                        value={editRecord?.notes || ""}
                                                        onChange={handleEditChange}
                                                    />
                                                </Form.Group>
                                            </Modal.Body>
                                            {updateLoading ? (
                                                <MySpinner />) :
                                                <Modal.Footer>
                                                    <Button variant="secondary" onClick={() => setShowEdit(false)}>
                                                        Hủy
                                                    </Button>
                                                    <Button type="submit" variant="primary">
                                                        Lưu
                                                    </Button>
                                                </Modal.Footer>
                                            }
                                        </Form>
                                    </Modal>
                                    {user.result?.role === "PATIENT" && (
                                        <div className="d-grid gap-1 mt-2">
                                            {loading[a.id] === true ? (
                                                <MySpinner />
                                            ) : (
                                                <>
                                                    {/* Sửa lịch hẹn */}
                                                    {a.status === "SCHEDULED" && (
                                                        <Button
                                                            variant="warning"
                                                            disabled={loading}
                                                            className="d-flex align-items-center justify-content-center mb-1"
                                                            onClick={() => handleNavUpdate(a)}
                                                            size="sm"
                                                            title={t('appointment.editAppointment')}
                                                        >
                                                            <i className="bi bi-pencil-square me-1"></i> {t('appointment.editAppointment')}
                                                        </Button>
                                                    )}
                                                    {/* Hủy lịch hẹn */}
                                                    {a.status === "SCHEDULED" && (
                                                        <Button
                                                            variant="danger"
                                                            disabled={loading}
                                                            className="d-flex align-items-center justify-content-center mb-1"
                                                            onClick={() => handleConfirm(a.id, a.doctorId, a.createdAt)}
                                                            size="sm"
                                                            title={t('appointment.cancelAppointment')}
                                                        >
                                                            <i className="bi bi-x-circle me-1"></i> {t('appointment.cancelAppointment')}
                                                        </Button>
                                                    )}
                                                    {/* Chat với bác sĩ */}
                                                    <Button
                                                        variant="success"
                                                        className="d-flex align-items-center justify-content-center mb-1"
                                                        onClick={() => createRoom(a.doctorId, a)}
                                                        size="sm"
                                                        disabled={
                                                            Object.values(loading).some(v => v) && !loading[a.id]
                                                        }
                                                        title={t('appointment.chatWithDoctor')}
                                                    >
                                                        <i className="bi bi-chat-dots me-1"></i> {t('appointment.chatWithDoctor')}
                                                    </Button>
                                                    {/* Hóa đơn */}
                                                    {a.status !== "CANCELLED_BY_PATIENT" && a.status !== "CANCELLED_BY_DOCTOR" && (
                                                        <Button variant="info" disabled={loading} onClick={() => handleInvoiceRedirect(a)} size="sm" title={t('appointment.invoice')}>
                                                            <i className="bi bi-receipt me-1"></i> {t('appointment.invoice')}
                                                        </Button>
                                                    )}
                                                    {/* Đánh giá/Xem đánh giá */}
                                                    {a.status === "COMPLETED" && !reviewedAppointments[a.id] && (
                                                        <Button
                                                            variant="primary"
                                                            title={t('appointment.reviewDoctor')}
                                                            className="d-flex align-items-center justify-content-center mb-1"
                                                            disabled={loading}
                                                            onClick={() => handleReviewDoctorRedirect(a.doctorId, a.id, a.patientId)}
                                                            size="sm"
                                                        >
                                                            <i className="bi bi-star me-1"></i> {t('appointment.reviewDoctor')}
                                                        </Button>
                                                    )}
                                                    {a.status === "COMPLETED" && reviewedAppointments[a.id] && (
                                                        <Button
                                                            variant="primary"
                                                            disabled={loading}
                                                            as={Link}
                                                            to={`/review/?doctorId=${a.doctorId}`}
                                                            size="sm"
                                                            className="d-flex align-items-center justify-content-center mb-1"
                                                            title={t('appointment.viewReviewDoctor')}
                                                        >
                                                            <i className="bi bi-star-fill me-1"></i> {t('appointment.viewReviewDoctor')}
                                                        </Button>
                                                    )}
                                                </>
                                            )}
                                        </div>
                                    )}
                                </Card.Body>
                            </Card>
                            <MyConfirm
                                show={showConfirm.show}
                                onHide={handleClose}
                                onConfirm={() =>
                                    deleteAppointment(
                                        showConfirm.appointmentId,
                                        showConfirm.doctorId,
                                        showConfirm.createdAt
                                    )
                                }
                                loading={loading}
                                title={t('appointment.confirmCancelTitle')}
                                body={t('appointment.confirmCancelBody')}
                            />
                        </Col>
                    );
                })}


            </Row>



            {/* Chỉ hiện nút phân trang khi có lịch hẹn sau khi lọc */}
            {filteredAppointments.length > 0 && !loading && (
                <Row className="justify-content-center align-items-center g-4 mb-4 mt-4">
                    <Col md={8} lg={6} xs={10} className="d-flex justify-content-center align-items-center gap-2">
                        <Button
                            variant="outline-primary"
                            disabled={page === 1}
                            onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                        >
                            &lt; Trước
                        </Button>
                        <span>Trang {page}</span>
                        <Button
                            variant="outline-primary"
                            disabled={!hasMore || filteredAppointments.length === 0}
                            onClick={() => setPage((prev) => (hasMore && filteredAppointments.length > 0) ? prev + 1 : prev)}
                        >
                            Sau &gt;
                        </Button>
                    </Col>
                </Row>
            )}
            <Row className="mt-5 mb-4" ></Row>


            {pageLoading && (
                <div className="text-center mt-4">
                    <MySpinner />
                </div>
            )}

            {/* Health Record Form Modal */}
            {showHealthRecordForm && (
                <HealthRecordForm
                    show={!!showHealthRecordForm}
                    onHide={() => setShowHealthRecordForm(null)}
                    appointmentId={showHealthRecordForm.appointmentId}
                    patientId={showHealthRecordForm.patientId}
                    doctorId={showHealthRecordForm.doctorId}
                    mode={showHealthRecordForm.mode}
                    onSuccess={() => {
                        toast.success("Hồ sơ bệnh án đã được cập nhật!");
                        setShowHealthRecordForm(null);
                    }}
                />
            )}
        </Container>
    )
}
export default Appointment;