import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../style/UserInfo.css";


const UserInfo = ({ user, setUser }) => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useState(user);

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/myInfo", { withCredentials: true });
                setUserInfo(response.data);
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

    return (
        <div>
            {userInfo ? (
                <div>
                    <h1>Welcome, {userInfo.username}</h1>
                    <p>Email: {userInfo.email}</p>
                    <p>Role: {userInfo.role}</p>
                    <button onClick={handleLogout}>Logout</button>
                </div>
            ) : (
                <p>Loading user information...</p>
            )}
        </div>
    );
};

export default UserInfo;
