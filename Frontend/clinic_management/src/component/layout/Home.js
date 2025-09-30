import { Carousel, Col, Container, Form, Image, Row, Button } from "react-bootstrap"
import "./Styles/Home.css"
import { useLocation } from "react-router-dom";
import { Alert } from "react-bootstrap";
import { useState, useEffect } from "react";
import RotatingText from "../reactbits/RotatingText";
import ChatBot from "../chatBot/ChatBot";
import { useTranslation } from "react-i18next";


const Home = () => {
    const { t } = useTranslation();
    const [userAddress, setUserAddress] = useState("");
    const slideItems = [
        { id: 1, title: t('home.slide1.title'), desc: t('home.slide1.desc') },
        { id: 2, title: t('home.slide2.title'), desc: t('home.slide2.desc') },
        { id: 3, title: t('home.slide3.title'), desc: t('home.slide3.desc') },
        { id: 4, title: t('home.slide4.title'), desc: t('home.slide4.desc') },
        { id: 5, title: t('home.slide5.title'), desc: t('home.slide5.desc') },
        { id: 6, title: t('home.slide6.title'), desc: t('home.slide6.desc') },
        { id: 7, title: t('home.slide7.title'), desc: t('home.slide7.desc') },
        { id: 8, title: t('home.slide8.title'), desc: t('home.slide8.desc') },
    ];

    const location = useLocation();
    const [message, setMessage] = useState(location.state?.message || "");

    useEffect(() => {
        if (message) {

            const timer = setTimeout(() => {
                setMessage("");
            }, 3000);
            return () => clearTimeout(timer);
        }
    }, [message]);


    return (
        <>
            <Container fluid className="p-0">
                {message && (
                    <Alert variant="success" className="text-center">
                        {message}
                    </Alert>
                )}

                <Row className="align-items-stretch custom-row mt-5">
                    <Col xs={12} md={6} className="home-text d-flex flex-column justify-content-center">
                        <h2 className="text-white">{t('home.clinicTitle')}</h2>
                        <span>
                            <span><i className="bi bi-calendar-check text-info me-2"></i>{t('home.experience')}</span><br/>
                            <span><i className="bi bi-file-medical text-success me-2"></i>{t('home.healthRecord')}</span><br/>
                            <span><i className="bi bi-credit-card text-warning me-2"></i>{t('home.payment')}</span><br/>
                            <span><i className="bi bi-chat-dots text-primary me-2"></i>{t('home.consulting')}</span>
                        </span>
                        <RotatingText
                            texts={[
                                t('home.rotate1'),
                                t('home.rotate2'),
                                t('home.rotate3'),
                                t('home.rotate4')
                            ]}
                            mainClassName="px-2 sm:px-2 md:px-3 bg-cyan-300 text-black overflow-hidden py-0.5 sm:py-1 md:py-2 justify-center rounded-lg"
                            staggerFrom={"last"}
                            initial={{ y: "100%" }}
                            animate={{ y: 0 }}
                            exit={{ y: "-120%" }}
                            staggerDuration={0.025}
                            splitLevelClassName="overflow-hidden pb-0.5 sm:pb-1 md:pb-1"
                            transition={{ type: "spring", damping: 30, stiffness: 400 }}
                            rotationInterval={2000}
                        />
                    </Col>
                    <Col xs={12} md={6} className="d-flex align-items-center justify-content-center clinic-image-col">
                        <Image src="/assets/images/clinic.png" alt="Clinic" className="clinic-image" fluid />
                    </Col>
                </Row>

                <Row className="align-items-center justify-content-center mt-5">
                    <Col xs={12} md={8} lg={6} className="text-center mt-4 home-text">
                        <h2>{t('home.servicesTitle')}</h2>
                        <hr className="my-4 border border-dark" />
                    </Col>
                </Row>
                <Row className="align-items-center justify-content-center mt-2">
                    <Carousel className="custom-carousel">
                        {/* Slide đầu */}
                        <Carousel.Item>
                            <Row className="justify-content-center ">
                                {slideItems.slice(0, 4).map((item) => (
                                    <Col key={item.id} xs={12} sm={6} md={3} className="mb-4">
                                        <div className="card-wrapper">
                                            <Image
                                                src="/assets/images/service.png"
                                                alt={item.title}
                                                className="carousel-image"
                                                fluid
                                            />
                                            <Carousel.Caption>
                                                <h5>{item.title}</h5>
                                                <p>{item.desc}</p>
                                            </Carousel.Caption>
                                        </div>
                                    </Col>
                                ))}
                            </Row>
                        </Carousel.Item>
                        {/* Slide tiếp theo */}
                        <Carousel.Item>
                            <Row className="justify-content-center">
                                {slideItems.slice(4, 8).map((item) => (
                                    <Col key={item.id} xs={12} sm={6} md={3} className="mb-4">
                                        <div className="card-wrapper">
                                            <Image
                                                src="/assets/images/service.png"
                                                alt={item.title}
                                                className="carousel-image"
                                                fluid
                                            />
                                            <Carousel.Caption>
                                                <h5>{item.title}</h5>
                                                <p>{item.desc}</p>
                                            </Carousel.Caption>
                                        </div>
                                    </Col>
                                ))}
                            </Row>
                        </Carousel.Item>
                    </Carousel>
                </Row>
                {/* Google Maps phòng khám */}
                <Row className="justify-content-center mt-5">
                    <Col xs={12} md={8} lg={6} className="text-center">
                        <h4 className="mb-3">{t('footer.address')}</h4>
                        <iframe
                            src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d1960.359904575282!2d106.68902296194621!3d10.67884295918271!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x317530465869b203%3A0x5b9acb79810d30ee!2zS2h1IGTDom4gY8awIE5oxqFuIMSQ4bupYw!5e0!3m2!1svi!2s!4v1757958388095!5m2!1svi!2s"
                            width="100%"
                            height="450"
                            style={{ border: 0 }}
                            allowFullScreen=""
                            loading="lazy"
                            referrerPolicy="no-referrer-when-downgrade"
                        />
                        <div className="mt-4 mb-2">
                            <Form>
                                <Form.Label htmlFor="user-address">{t('home.inputAddressLabel')}</Form.Label>
                                <Form.Control
                                    id="user-address"
                                    type="text"
                                    placeholder={t('home.inputAddressPlaceholder')}
                                    value={userAddress}
                                    onChange={e => setUserAddress(e.target.value)}
                                    className="mb-2"
                                />
                                <Button
                                    variant="primary"
                                    onClick={() => {
                                        if (userAddress.trim()) {
                                            const clinicAddress = "Khu dân cư Nhơn Đức, xã Hiệp Phước, TP. Hồ Chí Minh";
                                            const url = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(userAddress)}&destination=${encodeURIComponent(clinicAddress)}`;
                                            window.open(url, "_blank");
                                        } else {
                                            alert(t('home.inputAddressLabel'));
                                        }
                                    }}
                                >{t('home.findRouteButton')}</Button>
                            </Form>
                        </div>
                    </Col>
                </Row>
            </Container>
            <ChatBot />
        </>
    );

};


export default Home