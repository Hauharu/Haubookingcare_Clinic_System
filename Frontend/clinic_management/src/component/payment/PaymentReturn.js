import { useLocation, useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { Alert, Button, Spinner } from "react-bootstrap";
import { authApis, endpoint } from "../../configs/Apis";
import { MyUserContext } from "../../configs/MyContexts";
import { useTranslation } from "react-i18next";

const VNPayReturn = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true); 
    const [paymentStatus, setPaymentStatus] = useState(null); 
    const [paymentData, setPaymentData] = useState({}); 
    const user = useContext(MyUserContext); 
    const { t } = useTranslation();

    useEffect(() => {

        const updateInvoiceStatus = async (status, invoiceId) => {
            try {
                await authApis().patch(endpoint.updateInvoiceStatus(invoiceId), { status });
            } catch (e) {}
        };

        const updatePaymentStatus = async (status, paymentId, transactionId) => {
            try {
                await authApis().patch(endpoint.updatePaymentStatus(paymentId), {
                    status: status,
                    transactionId: transactionId,
                });
            } catch (e) {}
        };

        const sendMailSuccess = async (email, firstName, lastName, amount, transactionId) => {
            try {
                const formData = new FormData();
                formData.append("email", email);
                formData.append("patientName", `${firstName} ${lastName}`);
                formData.append("amount", amount);
                formData.append("transactionId", transactionId);
                await authApis().post(`/payment/send-mail`, formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });
            } catch (error) {}
        };

        const processPayment = async () => {
            const queryParams = new URLSearchParams(location.search);

            const isVNPay = queryParams.has("vnp_Amount");
            const isMoMo = queryParams.has("amount") && queryParams.has("orderId");

            if (isVNPay) {
                // ==== Xử lý VNPay ====
                const vnp_Amount = queryParams.get("vnp_Amount");
                const vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
                const vnp_OrderInfo = queryParams.get("vnp_OrderInfo");
                const vnp_TransactionNo = queryParams.get("vnp_TransactionNo") || queryParams.get("vnp_TxnRef");
                const vnp_TransactionStatus = queryParams.get("vnp_TransactionStatus");
                const amount = parseInt(vnp_Amount) / 100;

                let invoiceId, paymentId;
                if (vnp_OrderInfo && vnp_OrderInfo.includes("-")) {
                    [invoiceId, paymentId] = vnp_OrderInfo.split("-");
                }


                // Xác định trạng thái thanh toán
                if (vnp_ResponseCode === "00" && vnp_TransactionStatus === "00") {
                    setPaymentStatus("success");
                    setPaymentData({
                        amount,
                        transactionId: vnp_TransactionNo,
                        invoiceId,
                        paymentId,
                        gateway: "VNPay",
                    });
                    // Cập nhật trạng thái trên backend
                    await updatePaymentStatus("SUCCESS", paymentId, vnp_TransactionNo);
                    await updateInvoiceStatus("PAID", invoiceId);
                    // Gửi mail thông báo thành công
                    if (user) {
                        await sendMailSuccess(user.email, user.firstName, user.lastName, amount, vnp_TransactionNo);
                    }
                } else {
                    setPaymentStatus("failed");
                    setPaymentData({
                        amount,
                        transactionId: vnp_TransactionNo,
                        invoiceId,
                        paymentId,
                        gateway: "VNPay",
                    });
                    // Cập nhật trạng thái trên backend
                    await updatePaymentStatus("FAILED", paymentId, vnp_TransactionNo);
                    await updateInvoiceStatus("FAILED", invoiceId);
                }
                setLoading(false);
            } else if (isMoMo) {
                // ==== Xử lý MoMo (nếu cần) ====
                // ... (giữ nguyên logic MoMo nếu có)
                setLoading(false);
            } else {
                setPaymentStatus("failed");
                setLoading(false);
            }
        };

        processPayment();
    }, [location.search, user]);

    // Auto redirect về trang chủ sau khi thanh toán thành công
    useEffect(() => {
        if (paymentStatus === "success" && !loading) {
            const timer = setTimeout(() => {
                navigate("/", { replace: true });
            }, 3000);
            return () => clearTimeout(timer);
        }
    }, [paymentStatus, loading, navigate]);

    const handleBackToHome = () => {
        navigate("/"); // Chuyển hướng về trang chủ
    };

    return (
        <div className="container mt-5">
            <h1 className="text-center mb-4">{t('paymentReturn.title')}</h1>

            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                    <p>{t('paymentReturn.processing')}</p>
                </div>
            ) : paymentStatus === "success" ? (
                <Alert variant="success" className="text-center">
                    <h4>{t('paymentReturn.successTitle')}</h4>
                    <p>
                        {t('paymentReturn.amount')}: {Number(paymentData.amount).toLocaleString("vi-VN", {
                            style: "currency",
                            currency: "VND",
                        })}
                    </p>
                    <p>{t('paymentReturn.transactionId')}: {paymentData.transactionId}</p>
                    <p>{t('paymentReturn.invoiceId')}: {paymentData.invoiceId}</p>
                    <p>{t('paymentReturn.gateway')}: {paymentData.gateway}</p>
                    <Button variant="primary" onClick={handleBackToHome}>
                        {t('paymentReturn.backHome')}
                    </Button>
                </Alert>
            ) : (
                <Alert variant="danger" className="text-center">
                    <h4>{t('paymentReturn.failedTitle')}</h4>
                    <p>{t('paymentReturn.failedDesc')}</p>
                    <Button variant="primary" onClick={handleBackToHome}>
                        {t('paymentReturn.backHome')}
                    </Button>
                </Alert>
            )}
        </div>
    );
};

export default VNPayReturn;