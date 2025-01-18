import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../style/LoginPage.css";

const LoginPage = ({ setUser, setAddress }) => {
    const [formData, setFormData] = useState({ username: "", password: "" });
    const [message, setMessage] = useState({ type: "", text: "" });
    const [showPassword, setShowPassword] = useState(false);
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

    const handleChange = (e) => {
        const { id, value } = e.target;
        setFormData((prev) => ({ ...prev, [id]: value }));
    };

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    const handleLogin = (userData, addressData) => {
        localStorage.setItem("user", JSON.stringify(userData));
        localStorage.setItem("address", JSON.stringify(addressData));

        setUser(userData);
        setAddress(addressData);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const loginResponse = await axios.post(
                "http://localhost:8080/auth/login",
                formData,
                { withCredentials: true }
            );

            const userInfoResponse = await axios.get("http://localhost:8080/user/myInfo", {
                withCredentials: true,
            });

            const addressInfoResponse = await axios.get("http://localhost:8080/user/addressInfo", {
                withCredentials: true,
            });

            const userData = userInfoResponse.data;
            const addressData = addressInfoResponse.data;

            // Save user data to localStorage
            handleLogin(userData, addressData);

            console.log("User Info:", userData);
            console.log("Address Info:", addressData);

            navigate("/user-info");
        } catch (err) {
            console.error("Login error:", err);
            const errorMessage = err.response?.data?.message || "Invalid credentials. Please try again.";
            setMessage({ type: "error", text: errorMessage });
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
                        style={{ paddingRight: "10px" }}
                    />
                    <span
                        onClick={togglePasswordVisibility}
                        className="password-toggle"
                        style={{ marginTop: '11px', display: 'inline-block' }}
                    >
                        {showPassword ? "👀" : "🔒"}
                    </span>
                </div>
                <button type="submit" className="submit-button">Login</button>
                <p>Don't have an account? <a href="/register">Create one</a></p>
            </form>
        </div>
    );
};

export default LoginPage;
