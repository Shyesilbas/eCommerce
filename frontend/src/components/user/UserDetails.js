import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { updateEmailRequest, updatePasswordRequest, logoutRequest, updatePhoneRequest } from "../../utils/api.js";

const UserDetails = ({ userInfo, onLogout }) => {
    const navigate = useNavigate();
    const [showEmailModal, setShowEmailModal] = useState(false);
    const [showPhoneModal, setShowPhoneModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [newEmail, setNewEmail] = useState("");
    const [newPhone, setNewPhone] = useState("");
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");

    const handleEmailUpdate = async () => {
        try {
            const data = { newEmail };
            await updateEmailRequest(data);

            await Swal.fire({
                title: "Success",
                text: "Email updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true,
            });

            localStorage.clear();
            onLogout();
            navigate("/login");
        } catch (err) {
            Swal.fire("Error", "Failed to update email.", "error");
        }
    };

    const handlePhoneUpdate = async () => {
        try {
            const data = { newPhone };
            await updatePhoneRequest(data);

            await Swal.fire({
                title: "Success",
                text: "Phone updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true,
            });

            localStorage.clear();
            onLogout();
            navigate("/login");
        } catch (err) {
            Swal.fire("Error", "Failed to update phone.", "error");
        }
    };

    const handlePasswordUpdate = async () => {
        try {
            const data = { oldPassword: currentPassword, newPassword };
            await updatePasswordRequest(data);

            await Swal.fire({
                title: "Success",
                text: "Password updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true,
            });

            localStorage.clear();
            onLogout();
            navigate("/login");
        } catch (err) {
            Swal.fire("Error", "Failed to update password.", "error");
        }
    };

    return (
        <div className="user-info-container">
            <div className="user-content">
                <p><strong>User ID:</strong> {userInfo?.userId || "N/A"}</p>
                <p><strong>Username:</strong> {userInfo?.username || "N/A"}</p>
                <p><strong>Email:</strong> {userInfo?.email || "N/A"}</p>
                <p><strong>Phone:</strong> {userInfo?.phone || "N/A"}</p>
                <p><strong>Total Orders:</strong> {userInfo?.totalOrders !== undefined ? userInfo.totalOrders : "N/A"}</p>

                <div className="user-nav">
                    <button className="nav-button" onClick={() => setShowEmailModal(true)}>Update Email</button>
                    <button className="nav-button" onClick={() => setShowPasswordModal(true)}>Update Password</button>
                    <button className="nav-button" onClick={() => setShowPhoneModal(true)}>Update Phone</button>
                </div>

                {showEmailModal && (
                    <div className="modal-overlay">
                        <div className="modal">
                            <h2>Update Email</h2>
                            <input
                                type="email"
                                placeholder="New Email"
                                value={newEmail}
                                onChange={(e) => setNewEmail(e.target.value)}
                            />
                            <div className="modal-actions">
                                <button className="action-button" onClick={handleEmailUpdate}>Update</button>
                                <button className="action-button" onClick={() => setShowEmailModal(false)}>Cancel</button>
                            </div>
                        </div>
                    </div>
                )}

                {showPhoneModal && (
                    <div className="modal-overlay">
                        <div className="modal">
                            <h2>Update Phone</h2>
                            <input
                                type="text"
                                placeholder="New Phone"
                                value={newPhone}
                                onChange={(e) => setNewPhone(e.target.value)}
                            />
                            <div className="modal-actions">
                                <button className="action-button" onClick={handlePhoneUpdate}>Update</button>
                                <button className="action-button" onClick={() => setShowPhoneModal(false)}>Cancel</button>
                            </div>
                        </div>
                    </div>
                )}

                {showPasswordModal && (
                    <div className="modal-overlay">
                        <div className="modal">
                            <h2>Update Password</h2>
                            <input
                                type="password"
                                placeholder="Current Password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                            />
                            <input
                                type="password"
                                placeholder="New Password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                            />
                            <div className="modal-actions">
                                <button className="action-button" onClick={handlePasswordUpdate}>Update</button>
                                <button className="action-button" onClick={() => setShowPasswordModal(false)}>Cancel</button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default UserDetails;