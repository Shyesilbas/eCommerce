import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginRequest, getUserInfo, getUserAddress } from "../utils/api.js";
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

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        const storedAddress = localStorage.getItem("address");

        if (storedUser && storedAddress) {
            setUser(JSON.parse(storedUser));
            setAddress(JSON.parse(storedAddress));
            navigate("/user-info");
        }
    }, [navigate, setUser, setAddress]);

    const handleLogin = (userData, addressData) => {
        localStorage.setItem("user", JSON.stringify(userData));
        localStorage.setItem("address", JSON.stringify(addressData));

        setUser(userData);
        setAddress(addressData);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const loginData = await loginRequest(formData);
            const userInfo = await getUserInfo();
            const addressInfo = await getUserAddress();

            handleLogin(userInfo, addressInfo);
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
                        value={formData.password}
                        onChange={handleChange}
                        required
                        style={{paddingRight: "10px"}}
                    />
                    <span
                        onClick={togglePasswordVisibility}
                        className="password-toggle"
                        style={{marginTop: '11px', display: 'inline-block'}}
                    >
                        {showPassword ? "👀" : "🔒"}
                    </span>
                </div>
                <button type="submit" className="submit-button">Login</button>
                <p>Don't have an account? <a href="/register">Create one</a></p>
                <p><a href="#" onClick={(e) => {
                    e.preventDefault();
                    setShowForgotPassword(true);  // Open the modal
                }}>Forgot Password?</a></p>
            </form>

            {/* Conditionally render ForgotPasswordModal */}
            {showForgotPassword && <ForgotPasswordModal onClose={() => setShowForgotPassword(false)} />}
        </div>
    );
};

export default LoginPage;
