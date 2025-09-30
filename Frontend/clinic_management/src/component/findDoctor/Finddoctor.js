
import React, { useEffect, useState, useContext } from "react";
import { Alert, Button, Card, Col, Container, Form, Row, Spinner } from "react-bootstrap";
import Apis, { authApis, endpoint } from "../../configs/Apis";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import MySpinner from "../layout/MySpinner";
import RatingIcon from "../../utils/RattingIcon";
import { toast } from "react-hot-toast";
import MyConfigs from "../../configs/MyConfigs";
import "./Styles/Finddoctor.css";
import axios from "axios";
import { BASE_URL } from "../../configs/Apis";
import { useTranslation } from "react-i18next";


const Finddoctor = () => {
    const { t } = useTranslation();
    // Nhớ làm xem thêm
    const [loading, setLoading] = useState(false);
    const [doctors, setDoctors] = useState([]);
    const [page, setPage] = useState(1);
    const [q, setQ] = useSearchParams();
    const [hasMore, setHasMore] = useState(true);

    const [keyword, setKeyword] = useState("");

    // Chuyên khoa lấy từ DB
    const [specialties, setSpecialties] = useState([]);

    useEffect(() => {
        // Gọi API lấy chuyên khoa
        const fetchSpecialties = async () => {
            try {
                const res = await Apis.get(endpoint.specialties || '/specialties');
                // Trả về mảng chuyên khoa dạng [{id, name}]
                if (Array.isArray(res.data.result)) {
                    setSpecialties(res.data.result);
                } else if (Array.isArray(res.data)) {
                    setSpecialties(res.data);
                } else if (Array.isArray(res.data.content)) {
                    setSpecialties(res.data.content);
                } else {
                    setSpecialties([]);
                }
            } catch (err) {
                setSpecialties([]);
            }
        };
        fetchSpecialties();
    }, []);

    const nav = useNavigate();


    const handleKeywordChange = (e) => {
        let value = e.target.value;
        setKeyword(value);
        setQ({ keyword: value })

    }

    const handlerFinDoctor = async (doctorId) => {
        try {
            setLoading(true);
            let res = await Apis.get(endpoint.findDoctorById(doctorId));
            let slots = [];
            if (res.data && res.data.result && Array.isArray(res.data.result.content)) {
                slots = res.data.result.content;
            } else if (res.data && Array.isArray(res.data.content)) {
                slots = res.data.content;
            }
            nav("/calendar", { state: { slots, doctorId } });
            toast.success(res.data.message || "Lấy lịch trống thành công!");
        } catch (ex) {
            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
        } finally {
            setLoading(false);
        }
    }


    const loadDoctors = async () => {
        try {
            let keyword = q.get('keyword');
            setLoading(true);
            let url = '';
            if (keyword && keyword.trim() !== "") {
                url = `${BASE_URL}doctors/search?keyword=${encodeURIComponent(keyword)}&page=${page - 1}`;
            } else {
                url = `${BASE_URL}doctors?page=${page - 1}`;
            }
            let res = await axios.get(url);
            let doctorList = Array.isArray(res.data.result)
                ? res.data.result
                : (res.data.result && Array.isArray(res.data.result.content)
                    ? res.data.result.content
                    : []);
            if (!Array.isArray(doctorList)) {
                doctorList = doctorList ? [doctorList] : [];
            }
            // Group doctors by doctorId to avoid duplicates
            const doctorMap = new Map();
            doctorList.forEach(doctor => {
                const doctorId = doctor.userResponse?.id || doctor.id;
                if (doctorId) {
                    if (!doctorMap.has(doctorId)) {
                        doctorMap.set(doctorId, doctor);
                    } else {
                        const existing = doctorMap.get(doctorId);
                        if (doctor.averageRating > existing.averageRating) {
                            doctorMap.set(doctorId, doctor);
                        }
                    }
                }
            });
            const uniqueDoctors = Array.from(doctorMap.values());
            if (page === 1) {
                setDoctors(uniqueDoctors);
            } else {
                setDoctors(prev => {
                    const combinedMap = new Map();
                    prev.forEach(doctor => {
                        const doctorId = doctor.userResponse?.id || doctor.id;
                        if (doctorId) combinedMap.set(doctorId, doctor);
                    });
                    uniqueDoctors.forEach(doctor => {
                        const doctorId = doctor.userResponse?.id || doctor.id;
                        if (doctorId) {
                            const existing = combinedMap.get(doctorId);
                            if (!existing || doctor.averageRating > existing.averageRating) {
                                combinedMap.set(doctorId, doctor);
                            }
                        }
                    });
                    return Array.from(combinedMap.values());
                });
            }
            if (uniqueDoctors.length < MyConfigs.PAGE_SIZE) {
                setHasMore(false);
            }
        } catch (ex) {
            // Có thể log lỗi ra toast hoặc gửi về server nếu cần
        } finally {
            setLoading(false);
        }
    }



    // Bỏ reset page, doctors khi query param thay đổi để tránh giật trang


    useEffect(() => {
        loadDoctors();
    }, [page, q])

    return (
        <>
            <Container fluid className="p-0">
                <Row className="justify-content-center g-4 custom-row mt-5 search-bar">
                    <Col xs={12} className="text-center">
                        <h2 className="search-title" style={{color: '#94ac29ff'}}>
                            <i className="fa fa-user-md me-2 search-icon"></i> {t('findDoctor.title')}
                        </h2>
                        <p className="search-subtitle" style={{color: '#94ac29ff'}}>{t('findDoctor.subtitle')}</p>
                    </Col>
                    <Col md={6} lg={6} xs={12} className="d-flex align-items-center gap-2">
                        <Form.Control
                            value={keyword}
                            onChange={handleKeywordChange}
                            placeholder={t('findDoctor.searchPlaceholder')}
                        />
                        <Form.Select
                            style={{ minWidth: 180 }}
                            value={keyword}
                            onChange={e => {
                                setKeyword(e.target.value);
                                setQ({ keyword: e.target.value });
                            }}
                        >
                            <option value="">{t('findDoctor.selectSpecialty')}</option>
                            {specialties.map(s => (
                                <option key={s.id} value={s.name}>{s.name}</option>
                            ))}
                        </Form.Select>
                    </Col>
                </Row>

                <Row className="justify-content-center g-4  mt-4">
                    {loading && !q.get('keyword') && (
                        <Col xs={12} className="text-center">
                            <MySpinner />
                        </Col>
                    )}
                    {Array.isArray(doctors) && doctors.length === 0 && <Alert variant="info" className="m-2 text-center">{t('findDoctor.notFound')}</Alert>}
                    {Array.isArray(doctors) && doctors.map(d => (
                        <Col key={d.user?.id || Math.random()} xs={12} sm={6} md={4} lg={3} >
                            <Card className="card-doctor shadow-sm">
                                <Card.Img 
                                    variant="top" 
                                    src={d.user?.avatar || "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDMwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRTVFNUU1Ii8+CjxjaXJjbGUgY3g9IjE1MCIgY3k9IjgwIiByPSIyNSIgZmlsbD0iIzk5OSIvPgo8cGF0aCBkPSJNMTAwIDEzMEMxMDAgMTE2IDExMSAxMDUgMTI1IDEwNUgxNzVDMTg5IDEwNSAyMDAgMTE2IDIwMCAxMzBWMTcwSDEwMFYxMzBaIiBmaWxsPSIjOTk5Ii8+Cjx0ZXh0IHg9IjE1MCIgeT0iMTg1IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTJweCIgZmlsbD0iIzk5OSI+RG9jdG9yPC90ZXh0Pgo8L3N2Zz4K"} 
                                    onError={(e) => {
                                        e.target.src = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDMwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRTVFNUU1Ii8+CjxjaXJjbGUgY3g9IjE1MCIgY3k9IjgwIiByPSIyNSIgZmlsbD0iIzk5OSIvPgo8cGF0aCBkPSJNMTAwIDEzMEMxMDAgMTE2IDExMSAxMDUgMTI1IDEwNUgxNzVDMTg5IDEwNSAyMDAgMTE2IDIwMCAxMzBWMTcwSDEwMFYxMzBaIiBmaWxsPSIjOTk5Ii8+Cjx0ZXh0IHg9IjE1MCIgeT0iMTg1IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTJweCIgZmlsbD0iIzk5OSI+RG9jdG9yPC90ZXh0Pgo8L3N2Zz4K";
                                    }}
                                />
                                <Card.Body className="card-body-custom">
                                    <div>
                                        <Card.Title className="card-title">{t('findDoctor.doctorLabel')} {`${d.user?.firstName || ''} ${d.user?.lastName || ''}`.split(' ').slice(0, 4).join(' ')}
                                            {`${d.user?.firstName || ''} ${d.user?.lastName || ''}`.split(' ').length > 4 && '...'}
                                        </Card.Title>
                                        <Card.Text className="card-text" style={{ fontSize: '0.85rem' }}>
                                            <strong>{t('findDoctor.specialty')}:</strong> {d.specialty?.name || '---'}
                                        </Card.Text>
                                        <Card.Text className="card-text" style={{ fontSize: '0.85rem' }}>
                                            <strong>{t('findDoctor.experience')}:</strong> {d.yearsExperience || 0} {t('findDoctor.year')}
                                        </Card.Text>
                                        <Card.Text className="card-text" style={{ fontSize: '0.85rem' }}>
                                            <strong>{t('findDoctor.rating')}:</strong> {d.averageRating || 0} <RatingIcon rating={d.averageRating || 0} />
                                        </Card.Text>
                                        <Card.Text className="card-text" style={{ fontSize: '0.85rem' }}>
                                            <strong>{t('findDoctor.fee')}:</strong>  {d.consultationFee ? d.consultationFee.toLocaleString('vi-VN') : '---'} VNĐ
                                        </Card.Text>
                                    </div>
                                    <div className="d-grid gap-1 mt-2">
                                        <Button variant="primary" onClick={ () => handlerFinDoctor(d.user?.id)} size="sm">{t('findDoctor.viewCalendar')}</Button>
                                        <Button variant="danger" as={Link} to={`/review/?doctorId=${d.user?.id}`} size="sm">{t('findDoctor.viewReview')}</Button>
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>

                {/* Xem thêm */}
                <Row className="justify-content-center align-items-center g-4 mb-4 mt-4">
                    {hasMore && doctors.length > 0 && !loading && (
                        <Col md={6} lg={1} xs={10}>
                            <Button variant="info" onClick={() => setPage(prev => prev + 1)} >{t('findDoctor.loadMore')}</Button>
                        </Col>
                    )}
                </Row>
                <Row className="g-4 mb-4 mt-4"></Row>
            </Container>
        </>
    );
};

export default Finddoctor