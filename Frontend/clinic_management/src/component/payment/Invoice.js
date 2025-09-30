import React, { useState, useEffect } from "react";
import { Card, Table, Button, Spinner } from "react-bootstrap";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { authApis, endpoint } from "../../configs/Apis";
import "./Styles/Invoice.css";
import { useTranslation } from "react-i18next";

const Invoice = () => {
    const { t } = useTranslation();
    const location = useLocation();
    const navigate = useNavigate();
    const { id: invoiceIdParam } = useParams();
    const appointment = location.state?.appointment;
    const [invoice, setInvoice] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [invoiceStatus, setInvoiceStatus] = useState(null);
    const [paymentMethod, setPaymentMethod] = useState(null);

    const getStatusClass = (status) => {
        return status === "PAID" ? "status-paid" : "status-pending";
    };
    const handlePaymentRedirect = () => {
        if (!invoice || !invoice.amount || isNaN(Number(invoice.amount))) {
            alert("Hóa đơn chưa sẵn sàng hoặc không có số tiền hợp lệ!");
            return;
        }
        // Nếu không có appointment, lấy từ invoice
        let appointmentData = appointment;
        if (!appointmentData) {
            appointmentData = {
                id: invoice.appointmentId,
                doctorId: invoice.doctorId,
                patientId: invoice.patientId,
                amount: invoice.amount,
                appointmentTime: invoice.issueDate,
                // Thêm các trường khác nếu cần
            };
        }
        navigate("/payment-method", {
            state: {
                appointment: appointmentData,
                invoiceId: invoice?.id,
                invoice: invoice
            },
        });
    }


    useEffect(() => {
        if (invoiceIdParam) {
            loadInvoiceById(invoiceIdParam);
        } else if (appointment) {
            loadInvoice();
        } else {
            console.error("Không tìm thấy appointment hoặc invoiceId");
            navigate("/", { replace: true });
        }
    }, [appointment, invoiceIdParam]);
    // Reload paymentMethod khi invoice thay đổi
    useEffect(() => {
        if (invoice && invoice.id) {
            const fetchPayment = async () => {
                const payment = await loadPaymentMethod(invoice.id);
                if (payment) setPaymentMethod(payment);
            };
            fetchPayment();
        }
    }, [invoice]);
    // Thêm hàm mới để load hóa đơn theo id
    const loadInvoiceById = async (invoiceId) => {
        setLoading(true);
        setError(null);
        try {
            const response = await authApis().get(endpoint.invoiceById(invoiceId));
            if (response.status !== 200) {
                throw new Error("Failed to fetch invoice data");
            }
            const invoiceData = response.data.result || response.data;
            setInvoice(invoiceData);
            setInvoiceStatus(invoiceData.status);
            if (invoiceData.id) {
                const payment = await loadPaymentMethod(invoiceData.id);
                if (payment) setPaymentMethod(payment);
            }
        } catch (error) {
            setError("Không thể tải hóa đơn. Vui lòng thử lại sau.");
        } finally {
            setLoading(false);
        }
    };

    const loadInvoice = async () => {
        setLoading(true);
        setError(null);
        
        // Tìm appointmentId từ các field có thể có
        const appointmentId = appointment.appointmentId || appointment.id || appointment.appointmentID;
        
        if (!appointmentId) {
            setError("Không tìm thấy appointment ID");
            setLoading(false);
            return;
        }
        
        try {
            // Debug appointment object
            console.log('Appointment object:', appointment);
            console.log('Appointment keys:', Object.keys(appointment || {}));
            console.log('Using appointment ID:', appointmentId);
            
            const response = await authApis().get(endpoint.invoiceByAppointment(appointmentId));

            if (response.status !== 200) {
                throw new Error("Failed to fetch invoice data");
            }
            
            // Backend trả về ApiResponse wrapper
            const invoiceData = response.data.result || response.data;
            setInvoice(invoiceData);
            setInvoiceStatus(invoiceData.status);
            
            // Load payment information nếu có
            if (invoiceData.id) {
                const payment = await loadPaymentMethod(invoiceData.id);
                if (payment) {
                    setPaymentMethod(payment);
                }
            }
        } catch (error) {
            console.error("Lỗi khi lấy hóa đơn:", error);
            
            // Nếu không tìm thấy invoice (404) → Tự động tạo
            if (error.response?.status === 404) {
                console.log("Invoice không tồn tại, đang tạo invoice mới...");
                try {
                    await createInvoiceForAppointment(appointmentId);
                } catch (createError) {
                    console.error("Lỗi khi tạo invoice:", createError);
                    setError("Không thể tạo hóa đơn. Vui lòng thử lại sau.");
                }
            } else {
                setError("Không thể tải hóa đơn. Vui lòng thử lại sau.");
            }
        } finally {
            setLoading(false);
        }
    };

    const createInvoiceForAppointment = async (appointmentId) => {
        try {
            // Lấy consultation fee từ backend thông qua doctor API
            const doctorResponse = await authApis().get(endpoint.doctorById(appointment.doctorId));
            
            console.log("Doctor API response:", doctorResponse.data);
            
            // Backend có thể trả về trực tiếp DoctorResponse hoặc ApiResponse wrapper
            const doctorData = doctorResponse.data.result || doctorResponse.data;
            
            if (!doctorData || !doctorData.consultationFee) {
                throw new Error("Không tìm thấy thông tin phí khám của bác sĩ");
            }
            
            const consultationFee = doctorData.consultationFee;
            console.log("Lấy consultation fee từ doctor:", consultationFee);
            
            const invoiceRequest = {
                appointmentId: appointmentId,
                amount: consultationFee.toString() + ".00", // Convert to BigDecimal string format
                status: "PENDING", // InvoiceStatus enum: PENDING, PAID, CANCELLED
                dueDate: null // Optional - backend sẽ set default
            };

            console.log("Tạo invoice với data:", invoiceRequest);
            console.log("Appointment data:", appointment);
            
            const response = await authApis().post(endpoint.invoices, invoiceRequest);
            
            console.log("Invoice creation response:", response);
            console.log("Invoice creation status:", response.status);
            console.log("Invoice creation data:", response.data);
            
            if (response.data && response.data.result) {
                console.log("Invoice đã được tạo thành công:", response.data.result);
                // Load lại invoice sau khi tạo
                setTimeout(() => {
                    loadInvoice();
                }, 1000);
            } else {
                console.error("⚠️ Response không có result field");
                throw new Error("Response không có result field");
            }
        } catch (error) {
            console.error("Lỗi khi tạo invoice:", error);
            throw error;
        }
    };

    const loadPaymentMethod = async (invoiceId) => {
        try {
            const response = await authApis().get(endpoint.paymentsByInvoice(invoiceId));
            if (response.status === 200 && response.data) {
                const payments = response.data.result || response.data;
                // Ưu tiên lấy payment COMPLETED mới nhất
                const completedPayments = payments.filter(p => p.status === "COMPLETED");
                if (completedPayments.length > 0) {
                    return completedPayments[completedPayments.length - 1];
                }
                // Nếu không có, lấy payment cuối cùng (có thể là PENDING/FAILED)
                return payments && payments.length > 0 ? payments[payments.length - 1] : null;
            }
            return null;
        } catch (error) {
            console.error("Lỗi khi lấy phương thức thanh toán:", error);
            return null;
        }
    }

    const formatDate = (timestamp) => {
        if (!timestamp) return "Không xác định";
        const date = new Date(timestamp);
        return date.toLocaleDateString("vi-VN", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
        });
    };

    return (
        loading ? (
            <div className="loading-container">
                <Spinner animation="border" variant="primary" />
                <p className="loading-text">{t('invoice.loading')}</p>
            </div>
        ) : error ? (
            <div className="loading-container">
                <p className="text-danger">{error}</p>
            </div>
        ) : invoice ? (
            <div className="invoice-container">
                <h2 className="invoice-header">{t('invoice.detailTitle')}</h2>
                <Card className="invoice-card">
                    <Card.Body>
                        <h4 className="invoice-title">{t('invoice.paymentInfo') || t('invoice.detailTitle')}</h4>
                        <Table bordered hover className="invoice-table">
                            <tbody>
                                <tr>
                                    <th>{t('invoice.id')}</th>
                                    <td>{invoice.id}</td>
                                </tr>
                                <tr>
                                    <th>{t('invoice.amount')}</th>
                                    <td className="invoice-amount">
                                        {invoice.amount.toLocaleString("vi-VN", {
                                            style: "currency",
                                            currency: "VND",
                                        })}
                                    </td>
                                </tr>
                                <tr>
                                    <th>{t('invoice.issueDate')}</th>
                                    <td>{formatDate(invoice.issueDate)}</td>
                                </tr>
                                <tr>
                                    <th>{invoice.status === "PAID" ? t('invoice.paidDate') : t('invoice.dueDate')}</th>
                                    <td style={invoice.status === "PAID" ? { color: "green", fontWeight: "bold" } : {}}>
                                        {invoice.status === "PAID"
                                            ? formatDate(invoice.updatedAt)
                                            : (invoice.dueDate ? formatDate(invoice.dueDate) : formatDate(invoice.issueDate))}
                                    </td>
                                </tr>
                                <tr>
                                    <th>{t('invoice.paymentMethod')}</th>
                                    <td>
                                        {paymentMethod && paymentMethod.paymentMethod ? (
                                            paymentMethod.paymentMethod === "OTHER" ? t('invoice.cash') :
                                            paymentMethod.paymentMethod === "VNPAY" ? t('invoice.vnPay') :
                                            paymentMethod.paymentMethod === "PAYPAL" ? t('invoice.paypal') :
                                            t('invoice.payWith') + paymentMethod.paymentMethod
                                        ) : (
                                            <span className="text-muted">{t('invoice.noPaymentMethod')}</span>
                                        )}
                                    </td>
                                </tr>

                                <tr>
                                    <th>{t('invoice.paymentStatus')}</th>
                                    <td>
                                        {paymentMethod && paymentMethod.paymentMethod === "OTHER" ? (
                                            <span className="text-warning">{t('invoice.pending')}</span>
                                        ) : paymentMethod && paymentMethod.status ? (
                                            paymentMethod.status === "COMPLETED" ? (
                                                <span className="text-success">{t('invoice.completed')}</span>
                                            ) : paymentMethod.status === "PENDING" ? (
                                                <span className="text-warning">{t('invoice.pending')}</span>
                                            ) : paymentMethod.status === "FAILED" ? (
                                                <span className="text-danger">{t('invoice.failed')}</span>
                                            ) : (
                                                <span className="text-muted">{paymentMethod.status}</span>
                                            )
                                        ) : (
                                            <span className="text-muted">{t('invoice.noPaymentStatus')}</span>
                                        )}
                                    </td>
                                </tr>
                            </tbody>
                        </Table>
                        <div className="invoice-actions">
                            <div className="d-flex flex-wrap gap-2 mt-3 justify-content-center">
                                {invoiceStatus === "PAID" && (
                                    <Button variant="primary" className="invoice-btn" onClick={() => window.print()}>
                                        <i className="fas fa-print me-2"></i>
                                        {t('invoice.print')}
                                    </Button>
                                )}
                                {paymentMethod ? (
                                    paymentMethod.paymentMethod === "OTHER" ? (
                                        <div className="alert alert-info w-100">
                                            <i className="fas fa-info-circle me-2"></i>
                                            {t('invoice.waitForStaff')}
                                        </div>
                                    ) : paymentMethod.status !== "COMPLETED" ? (
                                        <Button
                                            variant="warning"
                                            className="invoice-btn"
                                            onClick={handlePaymentRedirect}
                                        >
                                            <i className="fas fa-redo me-2"></i>
                                            {t('invoice.payAgain')}
                                        </Button>
                                    ) : null
                                ) : (
                                    <Button
                                        variant="success"
                                        className="invoice-btn"
                                        onClick={handlePaymentRedirect}
                                    >
                                        <i className="fas fa-credit-card me-2"></i>
                                        {t('invoice.choosePaymentMethod')}
                                    </Button>
                                )}
                                <Button
                                    variant="secondary"
                                    className="invoice-btn"
                                    onClick={() => navigate("/")}
                                >
                                    <i className="fas fa-home me-2"></i>
                                    {t('invoice.goHome')}
                                </Button>
                            </div>
                        </div>
                    </Card.Body>
                </Card>
            </div>
        ) : (
            <div className="loading-container">
                <p>{t('invoice.notFound')}</p>
            </div>
        )
    );
};

export default Invoice;