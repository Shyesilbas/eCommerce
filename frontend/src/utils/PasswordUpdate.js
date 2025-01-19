import React from "react";
import "../style/PasswordUpdate.css";

export const passwordValidationRules = (password) => {
    return {
        minLength: password.length >= 6,
        hasUppercase: /[A-Z]/.test(password),
        hasLowercase: /[a-z]/.test(password),
        hasNumber: /\d/.test(password),
        hasSpecialChar: /[!@#$%^&*.]/.test(password),
    };
};

export const isPasswordValid = (password) => {
    const rules = passwordValidationRules(password);
    return Object.values(rules).every(rule => rule);
};

const PasswordUpdate = ({
                            newPassword,
                            confirmPassword,
                            passwordFocused,
                            passwordRules,
                            showPassword,
                            handlePasswordChange,
                            setPasswordFocused,
                            togglePasswordVisibility,
                            setConfirmPassword,
                        }) => {
    return (
        <>
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
        </>
    );
};

export default PasswordUpdate;
