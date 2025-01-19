import React, { useState } from "react";
import usePasswordVisibility from "../hooks/usePasswordVisibility";
import { forgotPasswordRequest } from "../utils/api.js";
import Swal from "sweetalert2";
import PasswordUpdate, { passwordValidationRules, isPasswordValid } from "../utils/PasswordUpdate";
import "../style/PasswordUpdate.css";

const ForgotPasswordModal = ({ onClose }) => {
    const [email, setEmail] = useState("");
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

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (newPassword !== confirmPassword) {
            Swal.fire({
                title: "Error",
                text: "Passwords do not match!",
                icon: "error",
                confirmButtonText: "Try Again",
            });
            return;
        }

        if (!isPasswordValid(newPassword)) {
            Swal.fire({
                title: "Error",
                text: "Password does not meet the required criteria!",
                icon: "error",
                confirmButtonText: "Try Again",
            });
            return;
        }

        try {
            const response = await forgotPasswordRequest({
                email,
                newPassword,
            });

            if (response?.message) {
                Swal.fire({
                    title: "Success!",
                    text: response.message || "Password reset successful!",
                    icon: "success",
                    confirmButtonText: "Okay",
                });
                onClose();
            } else {
                const errorMessage = response?.message || "Password reset failed. Please try again.";
                Swal.fire({
                    title: "Error",
                    text: errorMessage,
                    icon: "error",
                    confirmButtonText: "Try Again",
                });
            }
        } catch (error) {
            console.error("Error resetting password:", error);

            let errorMessage = "An error occurred while resetting the password.";

            if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.message) {
                errorMessage = error.message;
            } else if (typeof error === 'string') {
                errorMessage = error;
            }

            Swal.fire({
                title: "Error",
                text: errorMessage,
                icon: "error",
                confirmButtonText: "Try Again",
            });
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Forgot Password</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="email">Email:</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            autoComplete="off"
                        />
                    </div>
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
                    <div className="button-container">
                        <button type="submit" className="submit-button">Reset Password</button>
                        <button type="button" className="cancel-button" onClick={onClose}>Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ForgotPasswordModal;
