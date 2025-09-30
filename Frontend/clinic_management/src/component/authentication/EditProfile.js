import React, { useState, useEffect, useContext } from "react";
import { Form, Button, Container, Row, Col, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoint } from "../../configs/Apis";
import { MyUserContext } from "../../configs/MyContexts";
import { useTranslation } from "react-i18next";

function EditProfile() {
    const { t } = useTranslation();
    const thisUser = useContext(MyUserContext);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const [userData, setUserData] = useState({
        email: "",
        firstName: "",
        lastName: "",
        phoneNumber: "",
        address: "",
        dateOfBirth: "",
        gender: "",
        avatar: null,
        id: ""
    });

    useEffect(() => {
        authApis().get('/users/profile')
            .then(response => {
                const data = response.data.result || response.data;
                setUserData({
                    email: data.email || "",
                    firstName: data.firstName || "",
                    lastName: data.lastName || "",
                    phoneNumber: data.phoneNumber || "",
                    address: data.address || "",
                    dateOfBirth: data.dateOfBirth || "",
                    gender: data.gender || "",
                    avatar: data.avatar || null,
                    id: data.id || ""
                });
            })
            .catch(() => {
                setError(t('editProfile.error.load'));
            });
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUserData(prev => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        setUserData({ ...userData, avatar: e.target.files[0] });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");
        setLoading(true);
        const formData = new FormData();
        formData.append("firstName", userData.firstName);
        formData.append("lastName", userData.lastName);
        formData.append("phoneNumber", userData.phoneNumber);
        formData.append("address", userData.address);
        formData.append("dateOfBirth", userData.dateOfBirth);
        formData.append("gender", userData.gender);
        if (userData.avatar && typeof userData.avatar !== "string") {
            formData.append("avatar", userData.avatar);
        }
        try {
            const response = await authApis().put(endpoint.update_user(userData.id), formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            if (response.status === 200) {
                navigate("/", { state: { message: t('editProfile.success') } });
            }
        } catch (error) {
            setError("Không thể cập nhật thông tin. Vui lòng thử lại.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5">
            <h2 className="text-center mb-4">{t('editProfile.title')}</h2>
            {message && <Alert variant="success">{message}</Alert>}
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit} encType="multipart/form-data">
                <Row>
                    <Col md={6}>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.lastName')}</Form.Label>
                            <Form.Control
                                type="text"
                                name="lastName"
                                value={userData.lastName}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.firstName')}</Form.Label>
                            <Form.Control
                                type="text"
                                name="firstName"
                                value={userData.firstName}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.dateOfBirth')}</Form.Label>
                            <Form.Control
                                type="date"
                                name="dateOfBirth"
                                value={userData.dateOfBirth}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.gender')}</Form.Label>
                            <Form.Select
                                name="gender"
                                value={userData.gender}
                                onChange={handleInputChange}
                                required
                            >
                                <option value="">{t('editProfile.selectGender')}</option>
                                <option value="MALE">{t('editProfile.male')}</option>
                                <option value="FEMALE">{t('editProfile.female')}</option>
                                <option value="OTHER">{t('editProfile.other')}</option>
                            </Form.Select>
                        </Form.Group>
                    </Col>
                    <Col md={6}>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.email')}</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={userData.email}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.phoneNumber')}</Form.Label>
                            <Form.Control
                                type="text"
                                name="phoneNumber"
                                value={userData.phoneNumber}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.address')}</Form.Label>
                            <Form.Control
                                type="text"
                                name="address"
                                value={userData.address}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('editProfile.avatar')}</Form.Label>
                            <Form.Control
                                type="file"
                                name="avatar"
                                onChange={handleFileChange}
                                accept="image/*"
                            />
                        </Form.Group>
                    </Col>
                </Row>
                <Row>
                    <Col md={6} className="mx-auto">
                        <Button variant="primary" type="submit" disabled={loading} className="w-100 mt-2">
                            {loading ? t('editProfile.loading') : t('editProfile.button')}
                        </Button>
                    </Col>
                </Row>
            </Form>
        </Container>
    );
}

export default EditProfile;
