import React from "react";
import { Modal, Button } from "react-bootstrap";
import { useTranslation } from "react-i18next";

const MyConfirm = ({ show, onHide, onConfirm, loading, title, body, nav }) => {
    const { t } = useTranslation();
    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>{title || t('commonButton.confirm')}</Modal.Title>
            </Modal.Header>
            <Modal.Body>{body || t('booking.confirmBody')}</Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>
                    {t('commonButton.cancel')}
                </Button>
                <Button variant="success" onClick={onConfirm} disabled={loading}>
                    {loading ? t('commonButton.processing') : t('commonButton.confirm')}
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default MyConfirm;