import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { loginRequest } from "../utils/api.js";
import usePasswordVisibility from "../hooks/usePasswordVisibility.js";
import useFormData from "../hooks/useFormData.js";
import useMessage from "../hooks/useMessage.js";
import ForgotPasswordModal from "./ForgotPasswordModal.js";
import "../style/LoginPage.css";

const LoginPage = ({ setUser, setAddress }) => {
    const { formData, handleChange } = useFormData({ username: "", password: "" });
    const { showPassword, togglePasswordVisibility } = usePasswordVisibility();
    const { message, setErrorMessage, setSuccessMessage } = useMessage();
    const [showForgotPassword, setShowForgotPassword] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const { userData, addressData } = await loginRequest(formData);

            localStorage.setItem("user", JSON.stringify(userData));
            localStorage.setItem("address", JSON.stringify(addressData));

            setUser(userData);
            setAddress(addressData);

            setSuccessMessage("Login successful!");
            navigate("/user-info");
        } catch (err) {
            console.error("Login error:", err);
            const errorMessage = err.response?.data?.message || "Invalid credentials. Please try again.";
            setErrorMessage(errorMessage);
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            {message.text && (
                <p className={`message ${message.type}`}>{message.text}</p>
            )}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                        autoComplete="off"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type={showPassword ? "text" : "password"}
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                    <span
                        onClick={togglePasswordVisibility}
                        className="password-toggle"
                    >
                        {showPassword ? "👀" : "🔒"}
                    </span>
                </div>
                <button type="submit" className="submit-button">Login</button>
                <p>Don't have an account? <a href="/register">Create one</a></p>
                <p>
                    <a
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            setShowForgotPassword(true);
                        }}
                    >
                        Forgot Password?
                    </a>
                </p>
            </form>

            {showForgotPassword && (
                <ForgotPasswordModal onClose={() => setShowForgotPassword(false)} />
            )}
        </div>
    );
};

export default LoginPage;