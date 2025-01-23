import React, { useEffect, useState } from "react";
import { getNotifications } from "../../utils/api";
import "../../style/Notifications.css";

const NotificationsSection = () => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                const allNotifications = await getNotifications();
                setNotifications(allNotifications);

                const unreadNotifications = allNotifications.filter(
                    (notification) => !notification.isRead
                );
                setUnreadCount(unreadNotifications.length);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, []);

    const handleNotificationsClick = () => {
        setUnreadCount(0);
    };

    if (loading) {
        return <div>Loading notifications...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className="notifications-section" onClick={handleNotificationsClick}>
            <h2>
                Notifications
                {unreadCount > 0 && (
                    <span className="unread-count">{unreadCount}</span>
                )}
            </h2>

            {notifications.length > 0 ? (
                <ul className="notifications-list">
                    {notifications.map((notification) => (
                        <li
                            key={notification.notificationId}
                            className="notification-item"
                        >
                            <div className="notification-message">{notification.message}</div>
                            <div className="notification-time">
                                {new Date(notification.at).toLocaleString()}
                            </div>
                            <div className="notification-topic">{notification.notificationTopic}</div>
                        </li>
                    ))}
                </ul>
            ) : (
                <div>No notifications found.</div>
            )}
        </div>
    );
};

export default NotificationsSection;