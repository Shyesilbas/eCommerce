import React from "react";
import "../../style/UserNav.css";

const UserNav = ({ activeSection, onSectionChange, unreadCount }) => {
    const sections = [
        { id: "profile", label: "Profile" },
        { id: "address", label: "Address" },
        { id: "notifications", label: "Notifications" },
        { id: "favorites", label: "Favorites" },
        { id: "orders", label: "Orders" },
        { id: "reviews", label: "Reviews" },
    ];

    return (
        <nav className="user-nav">
            {sections.map((section) => (
                <button
                    key={section.id}
                    className={`user-nav__button ${activeSection === section.id ? "user-nav__button--active" : ""}`}
                    onClick={() => onSectionChange(section.id)}
                >
                    {section.label}
                    {section.id === "notifications" && unreadCount > 0 && (
                        <span className="unread-count">{unreadCount}</span>
                    )}
                </button>
            ))}
        </nav>
    );
};

export default UserNav;