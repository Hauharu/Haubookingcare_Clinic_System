import { useState, useEffect } from "react";
import { Button, Form, Alert, Spinner, Card, Row, Col } from "react-bootstrap";
import { useNavigate, useLocation } from "react-router-dom";
import "./Styles/PaymentMethod.css";
import { authApis, endpoint } from "../../configs/Apis";
import VNPayConfig from "../../configs/VNPayConfig";
import { v4 as uuidv4 } from "uuid";
import vnpayLogo from "../../assets/images/vnpay-logo.png";
import paypalLogo from '../../assets/images/paypal-logo.png';
import { useTranslation } from "react-i18next";

const PaymentMethod = () => {
    const { t } = useTranslation();
    const [paymentMethod, setPaymentMethod] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const location = useLocation();
    const appointment = location.state?.appointment;
    const invoiceId = location.state?.invoiceId;
    // Xóa isSuccess, không dùng nữa
    
    const paymentMethods = [
        {
            id: "other",
            name: t('paymentMethod.other'),
            description: t('paymentMethod.otherDesc'),
            icon: "fas fa-money-bill-wave",
            color: "#28a745",
            useIcon: true
        },
        {
            id: "vnpay",
            name: t('paymentMethod.vnpay'),
            description: t('paymentMethod.vnpayDesc'),
            logo: vnpayLogo,
            color: "#005BAA",
            useIcon: false
        },
        {
            id: "paypal",
            name: t('paymentMethod.paypal'),
            description: t('paymentMethod.paypalDesc'),
            logo: paypalLogo,
            color: "#003087",
            useIcon: false
        }
    ];

    useEffect(() => {
        if (!appointment) {
            navigate("/", { replace: true });
        }

        
    }, [appointment, navigate]);

    const generateTransactionId = () => {
        return `TXN-${uuidv4()}`;
    }

    // Không kiểm tra transactionId unique nữa, chỉ tạo mã mới
    const generateUniqueTransactionId = async () => {
        return generateTransactionId();
    };

    const createInvoiceForAppointment = async (appointmentId) => {
        try {
            // Lấy consultation fee từ backend thông qua doctor API
            const doctorResponse = await authApis().get(endpoint.doctorById(appointment.doctorId));
            const doctorData = doctorResponse.data.result || doctorResponse.data;
            if (!doctorData || !doctorData.consultationFee) {
                throw new Error("Không tìm thấy thông tin phí khám của bác sĩ");
            }
            const consultationFee = doctorData.consultationFee;
            const invoiceRequest = {
                appointmentId: appointmentId,
                amount: consultationFee.toString() + ".00",
                status: "PENDING",
                dueDate: null
            };
            const response = await authApis().post(endpoint.invoices, invoiceRequest);
            if (response.data && response.data.result) {
                return response.data.result.id; // Return invoice ID
            }
            throw new Error("Không nhận được response từ server");
        } catch (error) {
            throw error;
        }
    };

    const addPayment = async (method) => {
        setIsLoading(true);
        try {
            // Nếu không có invoiceId, tạo invoice trước
            let currentInvoiceId = invoiceId;
            if (!currentInvoiceId) {
                currentInvoiceId = await createInvoiceForAppointment(appointment.id);
                if (!currentInvoiceId) {
                    throw new Error("Không thể tạo invoice");
                }
            }
            const transactionId = await generateUniqueTransactionId();
            // Lấy amount từ backend (appointment.amount hoặc invoice)
            let amount = 0;
            if (appointment && typeof appointment.amount === 'number') {
                amount = appointment.amount;
            } else if (location.state?.invoice?.amount) {
                amount = Number(location.state.invoice.amount);
            }
            const paymentPayload = {
                invoiceId: currentInvoiceId,
                amount: amount,
                orderInfo: method === "OTHER" ? "Thanh toán trực tiếp tại phòng khám" : "Thanh toán qua VNPay",
                paymentMethod: method, // "OTHER" hoặc "VNPAY" 
                transactionId: transactionId
            };
            const response = await authApis().post(endpoint.payments, paymentPayload);
            if (response.data && response.data.result) {
                // Nếu có paymentUrl (VNPay) → redirect
                if (response.data.result.paymentUrl) {
                    window.location.href = response.data.result.paymentUrl;
                    return null; // Early return vì đã redirect
                }
                // Nếu không có paymentUrl (OTHER) → redirect về trang hóa đơn
                navigate(`/invoice/${currentInvoiceId}`);
                return response.data.result.transactionId || response.data.result.id || "success";
            }
            throw new Error("Không nhận được response từ server");
        } catch (error) {
            return null;
        } finally {
            setIsLoading(false);
        }
    };

    const updatePaymentStatus = async (paymentId, status) => {
        try {
            const updatePayload = {
                status: status,
                transactionId: `OTHER_${Date.now()}` // For OTHER payment method
            };
            await authApis().patch(`payments/${paymentId}`, updatePayload);
            // Also update invoice status
            if (status === "COMPLETED" && invoiceId) {
                await authApis().patch(`invoices/${invoiceId}`, { status: "PAID" });
            }
        } catch (error) {
        }
    };

    const handlePayment = async () => {
        if (!paymentMethod) {
            setMessage("Vui lòng chọn phương thức thanh toán.");
            return;
        }
        if (paymentMethod === "other") {
            await addPayment("OTHER");
        } else if (paymentMethod === "vnpay") {
            setIsLoading(true);
            try {
                const currentInvoiceId = invoiceId;
                let amount = 0;
                if (appointment && typeof appointment.amount === 'number') {
                    amount = appointment.amount;
                } else if (location.state?.invoice?.amount) {
                    amount = Number(location.state.invoice.amount);
                }
                const orderInfo = "Thanh toán qua VNPay";
                const vnpayRequest = {
                    invoiceId: currentInvoiceId,
                    amount: amount,
                    orderInfo: orderInfo
                };
                const response = await authApis().post(endpoint.vnpay_create, vnpayRequest);
                if (response.data && response.data.result && response.data.result.paymentUrl) {
                    window.location.href = response.data.result.paymentUrl;
                } else {
                    setMessage("Không thể tạo link thanh toán VNPay");
                }
            } catch (error) {
                setMessage("Lỗi khi tạo thanh toán VNPay: " + (error.response?.data?.message || error.message));
            } finally {
                setIsLoading(false);
            }
        } else if (paymentMethod === "paypal") {
            setIsLoading(true);
            try {
                const currentInvoiceId = invoiceId;
                let amount = 0;
                if (appointment && typeof appointment.amount === 'number') {
                    amount = appointment.amount;
                } else if (location.state?.invoice?.amount) {
                    amount = Number(location.state.invoice.amount);
                }
                const orderInfo = "Thanh toán qua PayPal";
                const paypalRequest = {
                    invoiceId: currentInvoiceId,
                    amount: amount,
                    orderInfo: orderInfo
                };
                const response = await authApis().post(endpoint.paypal_create, paypalRequest);
                const approvalUrl = response.data?.approvalUrl || response.data?.result?.approvalUrl;
                if (approvalUrl) {
                    window.location.href = approvalUrl;
                } else {
                    setMessage("Không thể tạo link thanh toán PayPal");
                }
            } catch (error) {
                setMessage("Lỗi khi tạo thanh toán PayPal: " + (error.response?.data?.message || error.message));
            } finally {
                setIsLoading(false);
            }
        }
    };

    const createVNPayInvoice = async (invoiceId, paymentId) => {
        try {
            setIsLoading(true);
            // Lấy thông tin từ appointment
            const amount = appointment.doctorId?.consultationFee || 500000; // Default 500k VND
            const doctorName = `${appointment.doctorId?.firstName || ''} ${appointment.doctorId?.lastName || ''}`.trim();
            const appointmentDate = new Date(appointment.appointmentDate).toLocaleDateString('vi-VN');
            // Tạo order info với format phù hợp VNPay
            const rawOrderInfo = `Thanh toan kham benh - Bac si ${doctorName} - Ngay ${appointmentDate}`;
            const orderInfo = VNPayConfig.formatOrderInfo(rawOrderInfo);
            // Validate amount
            if (!VNPayConfig.validateAmount(amount)) {
                throw new Error(`Số tiền không hợp lệ. Phải từ ${VNPayConfig.minAmount.toLocaleString()} đến ${VNPayConfig.maxAmount.toLocaleString()} VND`);
            }
            const vnpayRequest = {
                invoiceId: invoiceId,
                amount: amount, // Backend sẽ tự động x100
                orderInfo: orderInfo
            };
            const response = await authApis().post(endpoint.vnpay_create, vnpayRequest);
            if (response.data && response.data.result) {
                const { paymentUrl } = response.data.result;
                window.location.href = paymentUrl;
            } else {
                throw new Error('Không thể tạo link thanh toán VNPay');
            }
        } catch (error) {
            setMessage("Lỗi khi tạo thanh toán VNPay: " + (error.response?.data?.message || error.message));
            setIsLoading(false);
        }
    };

    return (
        
        <div className="container mt-5">            
            <h2 className="text-center mb-4">{t('paymentMethod.title')}</h2>

            <div className="payment-amount text-center mb-4">
                    <h4>{t('paymentMethod.amountTitle')}</h4>
                    <h3 className="text-success">
                        {new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(Number(location.state?.invoice?.amount) || 0)}
                    </h3>
            </div>

            <Row className="justify-content-center">
                {paymentMethods.map((method) => (
                    <Col md={4} className="mb-4" key={method.id}>
                        <Card
                            className={`payment-method-card ${paymentMethod === method.id ? 'selected' : ''}`}
                            onClick={() => setPaymentMethod(method.id)}
                        >
                            <Card.Body className="card-body-custom">
                                {method.useIcon ? (
                                    <i
                                        className={`${method.icon} cash-icon`}
                                        style={{ color: method.color }}
                                    />
                                ) : (
                                    <img
                                        src={method.logo}
                                        alt={`${method.name} logo`}
                                        className="payment-logo"
                                    />
                                )}
                                <Card.Title className="payment-title">{method.name}</Card.Title>
                                <Card.Text className="payment-description">{method.description}</Card.Text>
                                <Form.Check
                                    type="radio"
                                    name="paymentMethod"
                                    value={method.id}
                                    checked={paymentMethod === method.id}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                    className="mt-3"
                                />
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            <div className="text-center mt-4">
                {isLoading ? (
                    <Spinner animation="border" variant="primary" />
                ) : (
                    <Button
                        variant="primary"
                        size="lg"
                        onClick={handlePayment}
                        className="px-5"
                        disabled={!paymentMethod}
                    >
                        {t('paymentMethod.confirm')}
                    </Button>
                )}
            </div>

            {message && (
                <Alert
                    className="mt-4 text-center"
                    variant={message.includes(t('paymentMethod.success')) ? "success" : "info"}
                >
                    {message}
                </Alert>
            )}
        </div>
    );
};

export default PaymentMethod;