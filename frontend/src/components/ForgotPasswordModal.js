import React, { useState } from "react";
import usePasswordVisibility from "../hooks/usePasswordVisibility";
import { forgotPasswordRequest } from "../utils/api.js";
import Swal from "sweetalert2";
import "../style/ForgotPasswordModal.css";

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

    const validatePassword = (password) => {
        const rules = {
            minLength: password.length >= 6,
            hasUppercase: /[A-Z]/.test(password),
            hasLowercase: /[a-z]/.test(password),
            hasNumber: /\d/.test(password),
            hasSpecialChar: /[!@#$%^&*.]/.test(password),
        };
        setPasswordRules(rules);
        return Object.values(rules).every(rule => rule);
    };

    const handlePasswordChange = (e) => {
        const { value } = e.target;
        setNewPassword(value);
        validatePassword(value);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate passwords
        if (newPassword !== confirmPassword) {
            Swal.fire({
                title: "Error",
                text: "Passwords do not match!",
                icon: "error",
                confirmButtonText: "Try Again",
            });
            return;
        }
        if (!validatePassword(newPassword)) {
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

            // Enhanced error message extraction
            let errorMessage = "An error occurred while resetting the password.";

            if (error.response?.data?.message) {
                // API error with response data
                errorMessage = error.response.data.message;
            } else if (error.message) {
                // Standard Error object message
                errorMessage = error.message;
            } else if (typeof error === 'string') {
                // String error
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
                    <div className="form-group">
                        <label htmlFor="newPassword">New Password:</label>
                        <div className="password-input-container">
                            <input
                                type={showPassword ? "text" : "password"}
                                id="newPassword"
                                value={newPassword}
                                onChange={handlePasswordChange}
                                onFocus={() => setPasswordFocused(true)}
                                onBlur={() => setPasswordFocused(false)}
                                required
                            />
                            <span
                                onClick={togglePasswordVisibility}
                                className="password-toggle"
                            >
                                {showPassword ? "👀" : "🔒"}
                            </span>
                        </div>
                        {passwordFocused && (
                            <div className="password-rules">
                                <ul>
                                    <li className={passwordRules.minLength ? "valid" : ""}>
                                        At least 6 characters
                                    </li>
                                    <li className={passwordRules.hasUppercase ? "valid" : ""}>
                                        At least one uppercase letter (A-Z)
                                    </li>
                                    <li className={passwordRules.hasLowercase ? "valid" : ""}>
                                        At least one lowercase letter (a-z)
                                    </li>
                                    <li className={passwordRules.hasNumber ? "valid" : ""}>
                                        At least one number (0-9)
                                    </li>
                                    <li className={passwordRules.hasSpecialChar ? "valid" : ""}>
                                        At least one special character (!@#$%^&*.)
                                    </li>
                                </ul>
                            </div>
                        )}
                    </div>
                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirm New Password:</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>
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