import React, { useState, useContext } from "react";
import { Form, Button, Container, Row, Col, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoint } from "../../configs/Apis";
import { MyUserContext, MyDipatcherContext } from "../../configs/MyContexts";
import { useTranslation } from "react-i18next";
const ChangePassword = () => {
    const { t } = useTranslation();
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const thisUser = useContext(MyUserContext);
    const dispatch = useContext(MyDipatcherContext);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        if (newPassword !== confirmPassword) {
            setError("Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return;
        }

        try {
            setLoading(true);
            // Đảm bảo endpoint không truyền userId, chỉ dùng endpoint.change_password
            const response = await authApis().post(endpoint.change_password, {
                oldPassword: currentPassword,
                newPassword: newPassword
            });
            if (response.data.success === true || response.status === 200) {
                setMessage(response.data.message);
                dispatch({ type: "logout" });
                navigate("/login", { state: { message: "Thay đổi mật khẩu thành công. Vui lòng đăng nhập lại!" } });
            } else {
                setError(response.data.message);
            }
        } catch (error) {
            console.error("Lỗi khi thay đổi mật khẩu:", error);
            setError(error.response?.data?.message || "Không thể thay đổi mật khẩu. Vui lòng thử lại.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5">
            <h2 className="text-center mb-4">{t('changePassword.title')}</h2>
            {message && <Alert variant="success">{message}</Alert>}
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Row>
                    <Col md={6} className="offset-md-3">
                        <Form.Group className="mb-3">
                            <Form.Label>{t('changePassword.current')}</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder={t('changePassword.currentPlaceholder')}
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('changePassword.new')}</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder={t('changePassword.newPlaceholder')}
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('changePassword.confirm')}</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder={t('changePassword.confirmPlaceholder')}
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </Form.Group>
                        <div className="text-center">
                            {loading ? (
                                <div className="text-center mb-3"><span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span></div>
                            ) : (
                                <Button variant="primary" type="submit">
                                    {t('changePassword.button')}
                                </Button>
                            )}
                        </div>
                    </Col>
                </Row>
            </Form>
        </Container>
    );
};

export default ChangePassword;