import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Modal, Button, Form, Alert, Spinner } from 'react-bootstrap';
import { authApis, endpoint } from "../../configs/Apis";
import toast from 'react-hot-toast';

const HealthRecordForm = ({ show, onHide, appointmentId, patientId, doctorId, onSuccess, mode = "auto" }) => {
    const [loading, setLoading] = useState(false);
    const { t } = useTranslation();
    const [formData, setFormData] = useState({
        appointmentId: appointmentId || '',
        patientId: patientId || '',
        symptoms: '',
        diagnosis: '',
        notes: ''
    });
    // State cho danh sách đơn thuốc
    const [prescriptions, setPrescriptions] = useState([]);
    // State cho danh sách thuốc
    const [medicineList, setMedicineList] = useState([]);
    const [medicineLoading, setMedicineLoading] = useState(false);
    // Lấy danh sách thuốc từ API khi mở form
    useEffect(() => {
        if (show) {
            const fetchMedicines = async () => {
                setMedicineLoading(true);
                try {
                    const res = await authApis().get(endpoint.medicines);
                    let meds = [];
                    if (res.data && res.data.result && Array.isArray(res.data.result.content)) {
                        meds = res.data.result.content;
                    } else if (res.data && Array.isArray(res.data.result)) {
                        meds = res.data.result;
                    } else if (Array.isArray(res.data)) {
                        meds = res.data;
                    }
                    setMedicineList(meds);
                } catch (e) {
                    setMedicineList([]);
                } finally {
                    setMedicineLoading(false);
                }
            };
            fetchMedicines();
        }
    }, [show]);
    const [error, setError] = useState('');
    const [existingRecord, setExistingRecord] = useState(null);

    // Load existing health record if exists
    useEffect(() => {
        if (show && appointmentId) {
            loadExistingRecord();
        }
    }, [show, appointmentId]);

    const loadExistingRecord = async () => {
        try {
            setLoading(true);
            const response = await authApis().get(endpoint.health_record_by_appointment(appointmentId));
            if (response.data && response.data.result) {
                const record = response.data.result;
                setExistingRecord(record);
                setFormData({
                    appointmentId: appointmentId,
                    patientId: patientId,
                    symptoms: record.symptoms || '',
                    diagnosis: record.diagnosis || '',
                    notes: record.notes || ''
                });
                // Nếu có đơn thuốc, load vào state
                if (Array.isArray(record.prescriptions) && record.prescriptions.length > 0) {
                    setPrescriptions(record.prescriptions.map(p => ({
                        medicationName: p.medicationName || '',
                        dosage: p.dosage || '',
                        frequency: p.frequency || '',
                        instructions: p.instructions || ''
                    })));
                } else {
                    setPrescriptions([]);
                }
            }
        } catch (err) {
            // 404 is expected when no health record exists yet - not an error
            if (err.response?.status !== 404) {
                console.error('Error loading existing record:', err);
            }
            // Reset form if no existing record
            setFormData({
                appointmentId: appointmentId || '',
                patientId: patientId || '',
                symptoms: '',
                diagnosis: '',
                notes: ''
            });
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Xử lý thay đổi đơn thuốc
    const handlePrescriptionChange = (idx, e) => {
        const { name, value } = e.target;
        setPrescriptions(prev => prev.map((p, i) => i === idx ? { ...p, [name === 'medicationName' ? 'medicineId' : name]: value } : p));
    };

    // Thêm đơn thuốc mới
    const handleAddPrescription = () => {
        setPrescriptions(prev => [...prev, { medicineId: '', dosage: '', frequency: '', instructions: '' }]);
    };

    // Xóa đơn thuốc
    const handleRemovePrescription = (idx) => {
        setPrescriptions(prev => prev.filter((_, i) => i !== idx));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        // Validation before submit
        if (!appointmentId) {
            setError(t('healthRecordForm.appointmentIdRequired'));
            setLoading(false);
            return;
        }
        if (!patientId) {
            setError(t('healthRecordForm.patientIdRequired'));
            setLoading(false);
            return;
        }
        if (!formData.symptoms || !formData.diagnosis) {
            setError(t('healthRecordForm.symptomsDiagnosisRequired'));
            setLoading(false);
            return;
        }
        // Validate đơn thuốc
        for (let i = 0; i < prescriptions.length; i++) {
            const p = prescriptions[i];
            if (!p.medicationName || !p.dosage || !p.frequency) {
                setError(`Vui lòng nhập đầy đủ thông tin đơn thuốc thứ ${i + 1}`);
                setLoading(false);
                return;
            }
        }

        try {
            let response;
            // Lọc prescriptions: loại bỏ trường isCustom và các đơn thuốc rỗng
            const filteredPrescriptions = prescriptions
                .filter(p => p.medicineId && p.dosage && p.frequency)
                .map(({ medicineId, dosage, frequency, instructions }) => ({ medicineId, dosage, frequency, instructions }));
            const submitData = {
                ...formData,
                prescriptions: filteredPrescriptions,
                doctorId: doctorId || ''
            };
            if (existingRecord) {
                // Update existing record
                response = await authApis().patch(endpoint.health_record_update(existingRecord.id), submitData);
                toast.success(t('healthRecordForm.updateSuccess'));
            } else {
                // Create new record
                response = await authApis().post(endpoint.health_record_create, submitData);
                toast.success(t('healthRecordForm.createSuccess'));
            }

            if (onSuccess) {
                onSuccess(response.data);
            }
            onHide();
        } catch (err) {
            console.error('Error saving health record:', err);
            const errorMsg = err.response?.data?.message || err.response?.data?.error || t('healthRecordForm.saveError');
            setError(errorMsg);
            toast.error(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const resetForm = () => {
        setFormData({
            appointmentId: appointmentId || '',
            patientId: patientId || '',
            symptoms: '',
            diagnosis: '',
            notes: ''
        });
    setPrescriptions([]);
        setError('');
        setExistingRecord(null);
    };

    const handleClose = () => {
        resetForm();
        onHide();
    };

    return (
        <Modal show={show} onHide={handleClose} size="lg">
            <Modal.Header closeButton>
                <Modal.Title>
                    {(() => {
                        if (mode === "create") return t('healthRecordForm.createTitle');
                        if (mode === "update") return t('healthRecordForm.updateTitle');
                        return existingRecord ? t('healthRecordForm.updateTitle') : t('healthRecordForm.createTitle');
                    })()}
                </Modal.Title>
            </Modal.Header>
            <Form onSubmit={handleSubmit}>
                <Modal.Body>
                    {error && <Alert variant="danger">{error}</Alert>}
                    {loading && !existingRecord ? (
                        <div className="text-center p-4">
                            <Spinner animation="border" role="status" />
                            <div>Đang tải dữ liệu...</div>
                        </div>
                    ) : (
                        <>
                            <Form.Group className="mb-3">
                                <Form.Label><strong>{t('healthRecordForm.symptomsLabel')}</strong></Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    name="symptoms"
                                    value={formData.symptoms}
                                    onChange={handleInputChange}
                                    placeholder={t('healthRecordForm.symptomsPlaceholder')}
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label><strong>{t('healthRecordForm.diagnosisLabel')}</strong></Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    name="diagnosis"
                                    value={formData.diagnosis}
                                    onChange={handleInputChange}
                                    placeholder={t('healthRecordForm.diagnosisPlaceholder')}
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label><strong>{t('healthRecordForm.notesLabel')}</strong></Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={4}
                                    name="notes"
                                    value={formData.notes}
                                    onChange={handleInputChange}
                                    placeholder={t('healthRecordForm.notesPlaceholder')}
                                />
                            </Form.Group>
                            {/* Đơn thuốc */}
                            <hr />
                            <h5>Đơn thuốc</h5>
                            {prescriptions.length > 0 && prescriptions.map((p, idx) => (
                                <div key={idx} className="border rounded p-3 mb-3">
                                    <Form.Group className="mb-2">
                                        <div className="row g-2">
                                            <div className="col-md-4">
                                                <Form.Label>Tên thuốc</Form.Label>
                                                <Form.Select
                                                    name="medicineId"
                                                    value={p.medicineId}
                                                    onChange={e => setPrescriptions(prev => prev.map((item, i) => i === idx ? { ...item, medicineId: e.target.value } : item))}
                                                    required
                                                    disabled={medicineLoading}
                                                >
                                                    <option value="">-- Chọn thuốc --</option>
                                                    {medicineList.map(med => (
                                                        <option key={med.id} value={med.id}>{med.name}</option>
                                                    ))}
                                                </Form.Select>
                                            </div>
                                            <div className="col-md-4">
                                                <Form.Label>Liều lượng</Form.Label>
                                                <Form.Control
                                                    name="dosage"
                                                    value={p.dosage}
                                                    onChange={e => handlePrescriptionChange(idx, e)}
                                                    placeholder="Nhập liều lượng"
                                                    maxLength={100}
                                                    required
                                                />
                                            </div>
                                            <div className="col-md-4">
                                                <Form.Label>Tần suất sử dụng</Form.Label>
                                                <Form.Control
                                                    name="frequency"
                                                    value={p.frequency}
                                                    onChange={e => handlePrescriptionChange(idx, e)}
                                                    placeholder="Nhập tần suất sử dụng"
                                                    maxLength={100}
                                                    required
                                                />
                                            </div>
                                        </div>
                                    </Form.Group>
                                    <Form.Group className="mb-2 mt-2">
                                        <Form.Label>Hướng dẫn sử dụng (có thể để trống)</Form.Label>
                                        <Form.Control
                                            name="instructions"
                                            value={p.instructions}
                                            onChange={e => handlePrescriptionChange(idx, e)}
                                            placeholder="Nhập hướng dẫn sử dụng"
                                            maxLength={1000}
                                        />
                                    </Form.Group>
                                    <div className="d-flex justify-content-end">
                                        <Button variant="danger" size="sm" onClick={() => handleRemovePrescription(idx)}>Xóa</Button>
                                    </div>
                                </div>
                            ))}
                            <Button variant="success" size="sm" onClick={handleAddPrescription}>Thêm đơn thuốc</Button>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        {t('healthRecordForm.cancel')}
                    </Button>
                    <Button 
                        variant="primary" 
                        type="submit" 
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                {t('healthRecordForm.saving')}
                            </>
                        ) : (() => {
                            if (mode === "create") return t('healthRecordForm.createButton');
                            if (mode === "update") return t('healthRecordForm.updateButton');
                            return existingRecord ? t('healthRecordForm.updateButton') : t('healthRecordForm.createButton');
                        })()}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
};

export default HealthRecordForm;
