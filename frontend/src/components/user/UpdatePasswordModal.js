import React, { useState } from "react";
import Swal from "sweetalert2";
import { updatePasswordRequest } from "../../utils/api.js";
import PasswordUpdate, { isPasswordValid, passwordValidationRules } from "../../utils/PasswordUpdate";
import usePasswordVisibility from "../../hooks/usePasswordVisibility";
import "../../style/UpdatePasswordModal.css";

const UpdatePasswordModal = ({ onClose, onLogout }) => {
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordFocused, setPasswordFocused] = useState(false);
    const [passwordRules, setPasswordRules] = useState({
        minLength: false,
        hasUppercase: false,
        hasLowercase: false,
        hasNumber: false,
        hasSpecialChar: false,
    });
    const { showPassword, togglePasswordVisibility } = usePasswordVisibility();

    const handlePasswordChange = (e) => {
        const { value } = e.target;
        setNewPassword(value);
        setPasswordRules(passwordValidationRules(value));
    };

    const handlePasswordUpdate = async () => {
        if (newPassword !== confirmPassword) {
            Swal.fire("Error", "Passwords do not match!", "error");
            return;
        }

        if (!isPasswordValid(newPassword)) {
            Swal.fire("Error", "Password does not meet the required criteria!", "error");
            return;
        }

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
        } catch (err) {
            Swal.fire("Error", "Failed to update password.", "error");
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal">
                <h2>Update Password</h2>
                <PasswordUpdate
                    newPassword={newPassword}
                    confirmPassword={confirmPassword}
                    passwordFocused={passwordFocused}
                    passwordRules={passwordRules}
                    showPassword={showPassword}
                    handlePasswordChange={handlePasswordChange}
                    setPasswordFocused={setPasswordFocused}
                    togglePasswordVisibility={togglePasswordVisibility}
                    setConfirmPassword={setConfirmPassword}
                />
                <div className="modal-actions">
                    <button className="action-button" onClick={handlePasswordUpdate}>Update</button>
                    <button className="action-button cancel" onClick={onClose}>Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default UpdatePasswordModal;