import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { updateEmailRequest, updatePasswordRequest, logoutRequest } from "../../utils/api.js";

const UserDetails = ({ userInfo, onLogout }) => {
    const navigate = useNavigate();
    const [showEmailModal, setShowEmailModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [newEmail, setNewEmail] = useState("");
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");

    const handleEmailUpdate = async () => {
        try {
            const data = {
                newEmail: newEmail
            };

            await updateEmailRequest(data);

            await Swal.fire({
                title: "Success",
                text: "Email updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true
            });

            localStorage.clear();

            onLogout();

            navigate("/login");

        } catch (err) {
            Swal.fire("Error", "Failed to update email.", "error");
        }
    };

    const handlePasswordUpdate = async () => {
        try {
            const data = {
                oldPassword: currentPassword,
                newPassword: newPassword
            };
            await updatePasswordRequest(data);
            await Swal.fire({
                title: "Success",
                text: "Password updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true
            });


            localStorage.clear();
            onLogout();
            navigate("/login");
        } catch (err) {
            await Swal.fire("Error", "Failed to update password.", "error");
        }
    };

    return (
        <div className="user-details">
            <p><strong>User ID:</strong> {userInfo?.userId || "N/A"}</p>
            <p><strong>Username:</strong> {userInfo?.username || "N/A"}</p>
            <p><strong>Email:</strong> {userInfo?.email || "N/A"}</p>
            <p><strong>Phone:</strong> {userInfo?.phone || "N/A"}</p>
            <p><strong>Total Orders:</strong> {userInfo?.totalOrders !== undefined ? userInfo.totalOrders : "N/A"}</p>

            <button onClick={() => setShowEmailModal(true)}>Update Email</button>
            <button onClick={() => setShowPasswordModal(true)}>Update Password</button>

            {showEmailModal && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>Update Email</h2>
                        <input
                            type="email"
                            placeholder="New Email"
                            value={newEmail}
                            onChange={(e) => setNewEmail(e.target.value)}
                        />
                        <button onClick={handleEmailUpdate}>Update</button>
                        <button onClick={() => setShowEmailModal(false)}>Cancel</button>
                    </div>
                </div>
            )}

            {showPasswordModal && (
                <div className="modal">
                    <div className="modal-content">
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
                        <button onClick={handlePasswordUpdate}>Update</button>
                        <button onClick={() => setShowPasswordModal(false)}>Cancel</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserDetails;