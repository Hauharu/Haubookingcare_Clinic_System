import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Col, Container, Image, Nav, Row } from "react-bootstrap";
import { Link } from "react-router-dom";
import { faFacebook, faYoutube, faLinkedin, faGoogle } from '@fortawesome/free-brands-svg-icons';
import { useTranslation } from "react-i18next";
import "./Styles/Footer.css";

const Footer = () => {
    const { t } = useTranslation();
    const currentYear = new Date().getFullYear();


    return (
        <footer className="site-footer mt-5">
            <Container className="p-0">
                <Row>
                    {/* Cột 1: Thông tin phòng khám */}
                    <Col xs={12} md={6} lg={3} className="footer-col d-flex flex-column">
                        <h5 className="fw-bold mb-3">{t('footer.clinicName')}</h5>
                        <p className="mb-2">{t('footer.address')}</p>
                        <p className="mb-2">{t('footer.businessLicense')}</p>
                    </Col>
                    {/* Cột 2: Giới thiệu */}
                    <Col xs={12} md={6} lg={2} className="footer-col d-flex flex-column">
                        <h5>{t('footer.introTitle')}</h5>
                        <Nav className="flex-column footer-nav">
                            <Link className="nav-link text-dark" to="/gioi-thieu">{t('footer.aboutUs')}</Link>
                            <Link className="nav-link text-dark" to="/doi-ngu-bac-si">{t('footer.doctorTeam')}</Link>
                            <Link className="nav-link text-dark" to="/tuyen-dung">{t('footer.recruitment')}</Link>
                            <Link className="nav-link text-dark" to="/lien-he">{t('footer.contact')}</Link>
                        </Nav>
                    </Col>
                    {/* Cột 3: Dịch vụ */}
                    <Col xs={12} md={6} lg={2} className="footer-col d-flex flex-column">
                        <h5 className="ms-4">{t('footer.serviceTitle')}</h5>
                        <Nav className="flex-column footer-nav">
                            <Link className="nav-link text-dark" to="/dat-lich-kham">{t('footer.bookAppointment')}</Link>
                            <Link className="nav-link text-dark" to="/tu-van-truc-tuyen">{t('footer.onlineConsult')}</Link>
                            <Link className="nav-link text-dark" to="/quan-ly-ho-so">{t('footer.healthRecord')}</Link>
                            <Link className="nav-link text-dark" to="/thanh-toan">{t('footer.payment')}</Link>
                        </Nav>
                    </Col>
                    {/* Cột 4: Hỗ trợ */}
                    <Col xs={12} md={6} lg={2} className="footer-col">
                        <h5 className="ms-3">{t('footer.supportTitle')}</h5>
                        <Nav className="flex-column footer-nav">
                            <Link className="nav-link text-dark" to="/cau-hoi-thuong-gap">{t('footer.faq')}</Link>
                            <Link className="nav-link text-dark" to="/dieu-khoan-su-dung">{t('footer.terms')}</Link>
                            <Link className="nav-link text-dark" to="/chinh-sach-bao-mat">{t('footer.privacyPolicy')}</Link>
                            <p className="ms-3">{t('footer.customerSupport')}: <Link to="mailto:cskh@OUbookingcare.vn">cskh@OUbookingcare.vn</Link></p>
                        </Nav>
                    </Col>
                    {/* Cột 5: Kết nối & Chứng nhận */}
                    <Col xs={12} lg={3} className="footer-col">
                        <h5>{t('footer.connectTitle')}</h5>
                        <div className="social-icons ">
                            <Link className="ms-2" to="https://facebook.com" target="_blank"><FontAwesomeIcon icon={faFacebook} /></Link>
                            <Link className="ms-2" to="https://youtube.com" target="_blank"><FontAwesomeIcon icon={faYoutube} /></Link>
                            <Link className="ms-2" to="https://linkedin.com" target="_blank"><FontAwesomeIcon icon={faLinkedin} /></Link>
                            <Link className="ms-2" to="https://google.com" target="_blank"><FontAwesomeIcon icon={faGoogle} /></Link>
                        </div>
                    </Col>
                </Row>
                <hr />
                <Row>
                    <Col className="text-center footer-bottom">
                        <p className="small">Copyright ©{currentYear} {t('footer.clinicName')}.</p>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

export default Footer;