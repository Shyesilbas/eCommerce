import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../style/UserInfo.css";

const UserInfo = ({ user, setUser }) => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useState(user);
    const [roleMessage, setRoleMessage] = useState("");

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/myInfo", { withCredentials: true });
                setUserInfo(response.data);
                if (response.data.role === "ADMIN") {
                    const adminResponse = await axios.get("http://localhost:8080/user/adminRole", { withCredentials: true });
                    setRoleMessage(adminResponse.data);
                } else if (response.data.role === "CUSTOMER") {
                    const customerResponse = await axios.get("http://localhost:8080/user/customerRole", { withCredentials: true });
                    setRoleMessage(customerResponse.data);
                }
            } catch (err) {
                console.error("Error fetching user info:", err);
                navigate("/login");
            }
        };

        if (!userInfo) {
            fetchUserInfo();
        }
    }, [userInfo, navigate]);

    const handleLogout = async () => {
        try {
            const response = await axios.post("http://localhost:8080/auth/logout", {}, { withCredentials: true });
            console.log("Logout response:", response.data);
            setUser(null);
            navigate("/login");
        } catch (err) {
            console.error("Logout error:", err);
        }
    };

    const handleRoleRedirect = (role) => {
        if (role === "ADMIN") {
            navigate("/adminRole");
        } else if (role === "CUSTOMER") {
            navigate("/customerRole");
        }
    };

    return (
        <div>
            {userInfo ? (
                <div>
                    <h1>Welcome, {userInfo.username}</h1>
                    <p>Email: {userInfo.email}</p>
                    <p>Role: {userInfo.role}</p>
                    <p>{roleMessage}</p>
                    <button onClick={handleLogout}>Logout</button>
                    <button onClick={() => handleRoleRedirect(userInfo.role)}>
                        {userInfo.role}
                    </button>
                </div>
            ) : (
                <p>Loading user information...</p>
            )}
        </div>
    );
};

export default UserInfo;
