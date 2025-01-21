import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import UpdateEmailModal from "./UpdateEmailModal";
import UpdatePhoneModal from "./UpdatePhoneModal";
import UpdatePasswordModal from "./UpdatePasswordModal";
import "../../style/UserDetails.css";

const UserDetails = ({ userInfo, onLogout }) => {
    const navigate = useNavigate();
    const [showEmailModal, setShowEmailModal] = useState(false);
    const [showPhoneModal, setShowPhoneModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);

    return (
        <div className="user-details">
            <p><strong>User ID:</strong> {userInfo?.userId || "N/A"}</p>
            <p><strong>Username:</strong> {userInfo?.username || "N/A"}</p>
            <p><strong>Email:</strong> {userInfo?.email || "N/A"}</p>
            <p><strong>Phone:</strong> {userInfo?.phone || "N/A"}</p>
            <p><strong>Total Orders:</strong> {userInfo?.totalOrders !== undefined ? userInfo.totalOrders : "N/A"}</p>

            <div className="user-details__actions">
                <button className="user-details__action-button" onClick={() => setShowEmailModal(true)}>Update Email</button>
                <button className="user-details__action-button" onClick={() => setShowPasswordModal(true)}>Update Password</button>
                <button className="user-details__action-button" onClick={() => setShowPhoneModal(true)}>Update Phone</button>
            </div>

            {showEmailModal && (
                <UpdateEmailModal
                    onClose={() => setShowEmailModal(false)}
                    onLogout={onLogout}
                />
            )}

            {showPhoneModal && (
                <UpdatePhoneModal
                    onClose={() => setShowPhoneModal(false)}
                    onLogout={onLogout}
                />
            )}

            {showPasswordModal && (
                <UpdatePasswordModal
                    onClose={() => setShowPasswordModal(false)}
                    onLogout={onLogout}
                />
            )}
        </div>
    );
};

export default UserDetails;