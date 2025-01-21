import React from "react";
import UserDetails from "./UserDetails";
import "../../style/ProfileSection.css";

const ProfileSection = ({ user, onLogout }) => {
    return (
        <div className="profile-section">
            <UserDetails userInfo={user} onLogout={onLogout} />
        </div>
    );
};

export default ProfileSection;