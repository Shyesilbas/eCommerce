import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../style/LoginPage.css";

const LoginPage = ({ setUser }) => {
    const [formData, setFormData] = useState({ username: "", password: "" });
    const [message, setMessage] = useState({ type: "", text: "" });
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { id, value } = e.target;
        setFormData((prev) => ({ ...prev, [id]: value }));
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

            setUser(userInfoResponse.data);

            navigate("/user-info");
        } catch (err) {
            const errorMessage = err.response?.data?.message || "Invalid credentials. Please try again.";
            setMessage({ type: "error", text: errorMessage });
            console.error("Login error:", err);
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
                        aria-label="Username"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        aria-label="Password"
                    />
                </div>
                <button type="submit" className="submit-button">Login</button>
            </form>
        </div>
    );
};

export default LoginPage;