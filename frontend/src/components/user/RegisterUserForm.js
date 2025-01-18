import React, { useState } from "react";
import usePasswordVisibility from "../../hooks/usePasswordVisibility";

const BasicInfoForm = ({ formData, handleChange, setStep }) => {
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
        handleChange(e);
        validatePassword(value);
    };

    const handlePasswordFocus = () => {
        setPasswordFocused(true);
    };

    const handlePasswordBlur = () => {
        setPasswordFocused(false);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validatePassword(formData.password)) {
            setStep(2);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div className="form-group">
                <label htmlFor="username">Username:</label>
                <input
                    type="text"
                    id="username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                    autoComplete="off"
                />
            </div>
            <div className="form-group">
                <label htmlFor="email">Email:</label>
                <input
                    type="email"
                    id="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    autoComplete="off"
                />
            </div>
            <div className="form-group">
                <label htmlFor="phone">Phone:</label>
                <input
                    type="text"
                    id="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    required
                    autoComplete="off"
                    pattern="^\d{4} \d{3} \d{2} \d{2}$"
                    title="Pattern should match xxxx xxx xx xx"
                />
            </div>
            <div className="form-group">
                <label htmlFor="password">Password:</label>
                <input
                    type="password"
                    id="password"
                    value={formData.password}
                    onChange={handlePasswordChange}
                    onFocus={handlePasswordFocus}
                    onBlur={handlePasswordBlur}
                    required
                    minLength={6}
                />
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
                                At least one special character (!@#$%^&*)
                            </li>
                        </ul>
                    </div>
                )}
            </div>
            <div className="form-group">
                <label htmlFor="role">Role:</label>
                <select
                    id="role"
                    value={formData.role}
                    onChange={handleChange}
                >
                    <option value="CUSTOMER">Customer</option>
                    <option value="ADMIN">Admin</option>
                    <option value="SUPER_ADMIN">Super Admin</option>
                    <option value="MANAGER">Manager</option>
                    <option value="DEVELOPER">Developer</option>
                    <option value="TESTER">Tester</option>
                </select>
            </div>
            <button type="submit" className="submit-button">Continue</button>
            <p>Already have an account? <a href="/login">Login</a></p>
        </form>
    );
};

export default BasicInfoForm;