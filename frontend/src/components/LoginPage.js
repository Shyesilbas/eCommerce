import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../style/LoginPage.css";

const LoginPage = ({ setUser, setAddress }) => {
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

            const addressInfoResponse = await axios.get("http://localhost:8080/user/addressInfo", {
                withCredentials: true,
            });

            setUser(userInfoResponse.data);
            setAddress(addressInfoResponse.data);

            console.log("User Info:", userInfoResponse.data);
            console.log("Address Info:", addressInfoResponse.data);

            navigate("/user-info");
        } catch (err) {
            console.error("Login error:", err);
            console.error("Error response:", err.response);
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
                        autoComplete={"off"}
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
                    />
                </div>
                <button type="submit" className="submit-button">Login</button>
                <p>Don't have an account? <a href="/register">Create</a></p>
            </form>
        </div>
    );
};

export default LoginPage;