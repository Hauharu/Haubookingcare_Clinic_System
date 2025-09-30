import { use, useContext, useEffect, useState } from "react"
import { useTranslation } from "react-i18next"
import { Link, useSearchParams } from "react-router-dom"
import Apis, { endpoint } from "../../configs/Apis"
import { Container, Row, Col, Card, Image, Spinner, Button, Form } from "react-bootstrap";
import RatingIcon from "../../utils/RattingIcon";
import "./Styles/DoctorReview.css"
import { authApis } from "../../configs/Apis";
import { MyUserContext } from "../../configs/MyContexts";
import MyConfigs from "../../configs/MyConfigs";
const Review = () => {
    const { t, i18n } = useTranslation();

    const [q] = useSearchParams();
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [doctor, setDoctor] = useState({});
    const [loadingDoctor, setLoadingDoctor] = useState(false);

    const [editedResponses, setEditedResponses] = useState({});
    const [isEditing, setIsEditing] = useState(null);
    const user = useContext(MyUserContext);
    // Always extract user info from user.result for permission checks
    const currentUser = user && user.result ? user.result : null;
    const [updateLoading, setUpdateLoading] = useState(false)
    const [doctors, setDoctors] = useState([]) 

    function formatDate(timestamp) {
        return new Date(timestamp).toLocaleString(i18n.language === 'vi' ? 'vi-VN' : 'en-US');
    }

    useEffect(() => {

        const fetchDoctor = async () => {//Lay bac si theo id
            setLoadingDoctor(true);
            try {
                let doctorId = q.get('doctorId');
                if (doctorId) {
                    const res = await Apis.get(`${endpoint['doctor']}/${doctorId}`);
                    setDoctor(res.data && res.data.result ? res.data.result : {});
                }
            } catch (error) {
                // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            } finally {
                setLoadingDoctor(false);
            }
        };

        fetchDoctor();
    }, [q])//Fetch bac si khi id thay doi

    useEffect(() => {
        const fetchReviewsOrDoctors = async () => {
            setLoading(true);
            try {
                let doctorId = q.get('doctorId');
                if (doctorId) {
                    await fetchReviews(doctorId);
                } else {
                    const res = await Apis.get(`${endpoint['reviews']}?page=${page}&size=${MyConfigs.PAGE_SIZE}`);
                    let newReviews = [];
                    if (res.data && res.data.result) {
                        if (Array.isArray(res.data.result)) {
                            newReviews = res.data.result;
                        } else if (res.data.result.content && Array.isArray(res.data.result.content)) {
                            newReviews = res.data.result.content;
                        }
                    } else if (Array.isArray(res.data)) {
                        newReviews = res.data;
                    }
                    setReviews(prev => {
                        if (page === 0) {
                            return newReviews;
                        }
                        // For pagination, remove duplicates
                        const existingIds = new Set(prev.map(r => r.id));
                        const uniqueNewReviews = newReviews.filter(r => !existingIds.has(r.id));
                        return [...prev, ...uniqueNewReviews];
                    });
                    if (newReviews.length < MyConfigs.PAGE_SIZE) {
                        setPage(0);
                    }
                }
            } catch (error) {
                // Có thể log lỗi ra toast hoặc gửi về server nếu cần
            } finally {
                setLoading(false);
            }
        };

        // Luôn gọi API khi page >= 0
        if (page >= 0) {
            fetchReviewsOrDoctors();
        }
    }, [q, page]);

    const fetchReviews = async (doctorId) => {
        try {
            const res = await Apis.get(`${endpoint.reviewsOfDoctor(doctorId)}?page=${page}&size=${MyConfigs.PAGE_SIZE}`);
            let reviewArr = [];
            if (res.data && res.data.result) {
                if (Array.isArray(res.data.result)) {
                    reviewArr = res.data.result;
                } else if (res.data.result.content && Array.isArray(res.data.result.content)) {
                    reviewArr = res.data.result.content;
                }
            } else if (Array.isArray(res.data)) {
                reviewArr = res.data;
            }
            const isLastPage = reviewArr.length < MyConfigs.PAGE_SIZE;
            setReviews(prev => {
                if (page === 0) {
                    return reviewArr;
                }
                return [...prev, ...reviewArr];
            });
            if (isLastPage) {
                setPage(0);
            }
        } catch (error) {
            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
        }
    };

    // Tương tự cho fetchDoctors
    const fetchDoctors = async () => {
        try {
            const res = await Apis.get(`${endpoint['doctor']}?page=${page}&size=${MyConfigs.PAGE_SIZE}`);
            let doctorArr = [];
            if (res.data && res.data.result) {
                if (Array.isArray(res.data.result)) {
                    doctorArr = res.data.result;
                } else if (res.data.result.content && Array.isArray(res.data.result.content)) {
                    doctorArr = res.data.result.content;
                }
            } else if (Array.isArray(res.data)) {
                doctorArr = res.data;
            }
            const isLastPage = doctorArr.length < MyConfigs.PAGE_SIZE;
            setDoctors(prev => {
                if (page === 1) return doctorArr;
                return [...prev, ...doctorArr];
            });
            if (isLastPage) {
                setPage(0);
            }
        } catch (error) {
            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
        }
    };

    const handleReplyChange = (reviewId, text) => {
        setEditedResponses(prev => ({
            ...prev,
            [reviewId]: text
        }));
    };

    const handleSaveResponse = async (reviewId) => {
        const text = editedResponses[reviewId];
        setUpdateLoading(true)
        try {
            let url = endpoint.updateDoctorResponse(reviewId);
            await authApis().patch(url, text, {
                headers: {
                    'Content-Type': 'text/plain'
                }
            });
            setReviews(prevReviews => {
                const updatedReviews = prevReviews.map(r => {
                    return r.id === reviewId
                        ? {
                            ...r,
                            doctorResponse: text,
                            responseDate: Date.now()
                        }
                        : r;
                });
                return updatedReviews;
            });
        } catch (error) {
            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
        }
        finally {
            setIsEditing(null);
            setUpdateLoading(false)
        }

    };

    const loadMore = () => {
        if (!loading && page > 0) {
            setPage(page + 1);
        }
    }

    useEffect(() => {
    setPage(0)
    setReviews([])
    setDoctors([])
    }, [q])

    useEffect(() => {
    // Có thể log doctor ra toast hoặc gửi về server nếu cần

    }, [doctor])

    return (
        <>
            {q.get('doctorId') && (
                <Container className="mt-3">
                    <Row className="mb-2">
                        <Col xs={12} className="text-start">
                            <Button variant="secondary" onClick={() => window.history.back()}>
                                &larr; Quay lại
                            </Button>
                        </Col>
                    </Row>
                </Container>
            )}
            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                    <p>Đang tải...</p>
                </div>
            ) : q.get("doctorId") === null ? (
                <Container className="mt-4">
                    <Row>
                        {Array.isArray(reviews) && reviews.filter(r => r && r.id && r.doctor && r.doctor.user && r.doctor.user.id).length > 0 ? (() => {
                            // Group reviews by doctor to avoid duplicate cards
                            const doctorMap = new Map();
                            reviews.forEach(r => {
                                if (r && r.doctor && r.doctor.user && r.doctor.user.id) {
                                    const doctorId = r.doctor.user.id;
                                    if (!doctorMap.has(doctorId)) {
                                        doctorMap.set(doctorId, r.doctor);
                                    }
                                }
                            });
                            return Array.from(doctorMap.values()).map(doctor => (
                                doctor && doctor.user && doctor.user.id ? (
                                    <Col key={doctor.user.id} md={4} lg={3} className="mb-3">
                                        <Card className="card-doctor shadow-sm">
                                            <Card.Img variant="top" src={doctor.user.avatar} />
                                            <Card.Body className="card-body-custom">
                                                <Card.Title className="card-title">{doctor.user.firstName} {doctor.user.lastName}</Card.Title>
                                                <Card.Text className="card-text">
                                                    <strong>{t("reviewDoctor.specialty")}:</strong> {doctor.specialty?.name}
                                                </Card.Text>
                                                <Card.Text className="card-text">
                                                    <strong>{t("reviewDoctor.rating")}:</strong> {doctor.averageRating} <RatingIcon rating={doctor.averageRating} />
                                                </Card.Text>
                                                <Button variant="primary" as={Link} to={`/review/?doctorId=${doctor.user.id}`}>{t("reviewDoctor.detail")}</Button>
                                            </Card.Body>
                                        </Card>
                                    </Col>
                                ) : null
                            ));
                        })() : <div className="text-center text-muted">{t("reviewDoctor.noReviews")}</div>}
                    </Row>
                </Container>
            ) : (
                <Container className="mt-3">
                    <Row className="justify-content-center">
                        <Col md={3} sm={10}>
                            {loadingDoctor ? (
                                <div className="text-center">
                                    <Spinner animation="border" />
                                    <p>Đang tải thông tin bác sĩ...</p>
                                </div>
                            ) : doctor && doctor.user ? (
                                <Card className="card-doctor shadow-sm" style={{ background: "#f5f9ff", fontSize: "0.9rem" }}>
                                    <Card.Img variant="top" src={doctor.user.avatar} />
                                    <Card.Body className="p-2">
                                        <Card.Title className="text-primary fs-6 fw-bold mb-2">
                                            {doctor.user.firstName} {doctor.user.lastName}
                                        </Card.Title>
                                        <Card.Text className="mb-1">
                                            <i className="bi bi-star-fill text-warning me-1"></i>
                                            <strong>{t("reviewDoctor.rating")}:</strong> {doctor.averageRating} <RatingIcon rating={doctor.averageRating} />
                                        </Card.Text>
                                        <Card.Text className="mb-0">
                                            <i className="bi bi-award text-danger me-1"></i>
                                            <strong>{t("reviewDoctor.biography")}:</strong> {doctor.biography}
                                        </Card.Text>
                                    </Card.Body>
                                </Card>
                            ) : (
                                <div className="text-center text-danger">
                                    <p>{t("reviewDoctor.notFound")}</p>
                                </div>
                            )}
                        </Col>
                    </Row>
                    {/* Danh sách đánh giá */}
                    {loading ? (
                        <div className="text-center">
                            <Spinner animation="border" />
                            <p>{t("reviewDoctor.loading")}</p>
                        </div>
                    ) : (
                        <section className="py-4">
                            <Container>
                                <Row className="justify-content-center">
                                    <Col md={8}>
                                        <h4 className="text-center mb-4 fw-bold text-primary">Hỏi đáp bác sĩ</h4>
                                        {reviews.length ? (
                                            reviews
                                                .filter(r => r && r.id && r.patient && r.doctor && r.doctor.user)
                                                .slice()
                                                .sort((a, b) => {
                                                    if (currentUser && currentUser.id === a.patient.id) return -1;
                                                    if (currentUser && currentUser.id === b.patient.id) return 1;
                                                    return 0;
                                                }).map((r) => (
                                                    <Card key={r.id} className="shadow-sm rounded-4 border-0 mb-4">
                                                        <Card.Body className="p-4">
                                                            <div className="d-flex">
                                                                <Image
                                                                    src={r.patient.avatar || "/default-avatar.png"}
                                                                    roundedCircle
                                                                    width={60}
                                                                    height={60}
                                                                    className="me-3"
                                                                    style={{ objectFit: "cover" }}
                                                                />
                                                                <div className="w-100">
                                                                    <div className="d-flex justify-content-between align-items-center mb-2">
                                                                        <h6 className="fw-bold text-primary mb-0">
                                                                            {currentUser && currentUser.id === r.patient.id
                                                                                ? t("reviewDoctor.you")
                                                                                : `${r.patient.firstName} ${r.patient.lastName}`
                                                                            }
                                                                            <span className="text-muted fw-normal ms-2 small">
                                                                                {formatDate(r.reviewDate)}
                                                                            </span>
                                                                        </h6>
                                                                    </div>
                                                                    <div className="d-flex align-items-center mb-2">
                                                                        {[...Array(5)].map((_, index) => (
                                                                            <i
                                                                                key={index}
                                                                                className={`bi bi-star${index < r.rating ? "-fill" : ""} text-warning`}
                                                                            />
                                                                        ))}
                                                                    </div>
                                                                    <p className="text-secondary mb-3">{r.comment}</p>
                                                                    {/* Phần phản hồi */}
                                                                    <div className="mt-3">
                                                                        {isEditing === r.id ? (
                                                                            <div className="reply-form bg-light rounded-3 p-3 border">
                                                                                <Form.Group>
                                                                                        <Form.Label className="fw-bold text-primary mb-2">
                                                                                            {r.doctorResponse ? t("reviewDoctor.editResponse") : t("reviewDoctor.addResponse")}
                                                                                        </Form.Label>
                                                                                    <Form.Control
                                                                                        as="textarea"
                                                                                        rows={3}
                                                                                        value={editedResponses[r.id] || ""}
                                                                                        onChange={(e) => handleReplyChange(r.id, e.target.value)}
                                                                                        className="mb-3"
                                                                                        placeholder={t("reviewDoctor.responsePlaceholder")}
                                                                                    />
                                                                                    <div className="d-flex gap-2 justify-content-end">
                                                                                        <Button
                                                                                            size="sm"
                                                                                            variant="light"
                                                                                            onClick={() => setIsEditing(null)}
                                                                                            disabled={updateLoading}
                                                                                        >
                                                                                            <i className="bi bi-x-lg me-1"></i>
                                                                                            {t("reviewDoctor.cancel")}
                                                                                        </Button>
                                                                                        <Button
                                                                                            size="sm"
                                                                                            variant="primary"
                                                                                            onClick={() => handleSaveResponse(r.id)}
                                                                                            disabled={updateLoading}
                                                                                        >
                                                                                            {updateLoading ? (
                                                                                                <>
                                                                                                    <Spinner
                                                                                                        as="span"
                                                                                                        animation="border"
                                                                                                        size="sm"
                                                                                                        role="status"
                                                                                                        aria-hidden="true"
                                                                                                        className="me-1"
                                                                                                    />
                                                                                                    {t("reviewDoctor.saving")}
                                                                                                </>
                                                                                            ) : (
                                                                                                <>
                                                                                                    <i className="bi bi-check-lg me-1"></i>
                                                                                                    {t("reviewDoctor.save")}
                                                                                                </>
                                                                                            )}
                                                                                        </Button>
                                                                                    </div>
                                                                                </Form.Group>
                                                                            </div>
                                                                        ) : (
                                                                            r.doctorResponse ? (
                                                                                <div className="doctor-response bg-light rounded-3 p-3">
                                                                                    <div className="d-flex">
                                                                                        <Image
                                                                                            src={r.doctor.user.avatar}
                                                                                            roundedCircle
                                                                                            width={45}
                                                                                            height={45}
                                                                                            className="me-2"
                                                                                            style={{
                                                                                                objectFit: "cover",
                                                                                                border: "2px solid #e9ecef"
                                                                                            }}
                                                                                        />
                                                                                        <div className="flex-grow-1">
                                                                                            <div className="d-flex justify-content-between align-items-center mb-1">
                                                                                                <h6 className="text-primary mb-0">
                                                                                                    {t("reviewDoctor.doctor")} {r.doctor.user.firstName} {r.doctor.user.lastName}
                                                                                                    <span className="text-muted ms-2 small">
                                                                                                        {formatDate(r.responseDate)}
                                                                                                    </span>
                                                                                                </h6>
                                                                                                {/* Only show edit button if logged-in user is the doctor of this review */}
                                                                                                {currentUser && currentUser.id === r.doctor.user.id && (
                                                                                                    <Button
                                                                                                        variant="link"
                                                                                                        className="p-0 text-primary"
                                                                                                        onClick={() => {
                                                                                                            setIsEditing(r.id);
                                                                                                            setEditedResponses(prev => ({
                                                                                                                ...prev,
                                                                                                                [r.id]: r.doctorResponse
                                                                                                            }));
                                                                                                        }}
                                                                                                    >
                                                                                                        <i className="bi bi-pencil-square me-1"></i>
                                                                                                        {t("reviewDoctor.edit")}
                                                                                                    </Button>
                                                                                                )}
                                                                                            </div>
                                                                                            <p className="text-secondary mb-0">{r.doctorResponse}</p>
                                                                                        </div>
                                                                                    </div>
                                                                                </div>
                                                                            ) : (
                                                                                <>
                                                                                    {/* Only show reply button if logged-in user is the doctor of this review and there is no doctorResponse yet */}
                                                                                    {currentUser && currentUser.id === r.doctor.user.id && !r.doctorResponse && (
                                                                                        <Button
                                                                                            variant="outline-primary"
                                                                                            size="sm"
                                                                                            className="mt-2"
                                                                                            onClick={() => {
                                                                                                setIsEditing(r.id);
                                                                                                setEditedResponses(prev => ({
                                                                                                    ...prev,
                                                                                                    [r.id]: ""
                                                                                                }));
                                                                                            }}
                                                                                        >
                                                                                            <i className="bi bi-reply me-1"></i>
                                                                                            {t("reviewDoctor.reply")}
                                                                                        </Button>
                                                                                    )}
                                                                                </>
                                                                            )
                                                                        )}
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </Card.Body>
                                                    </Card>
                                                ))
                                        ) : (
                                            <div className="text-center text-muted">{t("reviewDoctor.noReviews")}</div>
                                        )}
                                    </Col>
                                </Row>
                            </Container>
                        </section>
                    )}
                </Container>
            )}
            {/* Nút "Xem thêm" */}
            <Row className="justify-content-center align-items-center g-4 mb-4 mt-4">
                {page > 0 && (
                    <Col md={1} lg={1} xs={1}>
                        <Button variant="info" onClick={loadMore}>{t("reviewDoctor.loadMore")}</Button>
                    </Col>
                )}
            </Row>
            <Row className="g-4 mb-4 mt-4"></Row>
        </>
    );

}

export default Review

