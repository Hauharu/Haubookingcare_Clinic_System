import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Alert, Button } from "react-bootstrap";
import toast from "react-hot-toast";
import { authApis } from "../configs/Apis";

const NotificationList = () => {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(false);
    const { t } = useTranslation();

    const loadNotifications = async () => {
        setLoading(true);
        try {
            const res = await authApis().get("/notifications");
            const notifications = Array.isArray(res.data.result) ? res.data.result : [];
            setNotifications(notifications);
        } catch (err) {
            toast.error(err.response?.data?.message || t("notifications.loadError"));
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadNotifications();
    }, []);

    return (
        <div>
            <h4>{t("notifications.title")}</h4>
            {loading && <div>{t("notifications.loading")}</div>}
            {notifications.length === 0 && !loading && (
                <Alert variant="info">{t("notifications.empty")}</Alert>
            )}
            {notifications.map(n => (
                <Alert key={n.id} variant={n.isRead ? "secondary" : "info"}>
                    <strong>{n.title}</strong>
                    <div>{n.content}</div>
                    <div>{t("notifications.type")}: {n.type}</div>
                    <div>{t("notifications.time")}: {n.createdAt ? new Date(n.createdAt).toLocaleString("vi-VN") : ""}</div>
                </Alert>
            ))}
            <Button variant="danger" onClick={loadNotifications} disabled={loading}>
                {t("notifications.refresh")}
            </Button>
        </div>
    );
};

export default NotificationList;
