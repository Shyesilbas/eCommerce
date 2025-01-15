import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Swal from "sweetalert2";
import "../style/UserInfo.css";

const UserInfo = ({ user, setUser }) => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useState(user);

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/myInfo", {
                    withCredentials: true,
                });
                setUserInfo(response.data);
            } catch (err) {
                console.error("Error fetching user info:", err);
            }
        };

        if (!userInfo) {
            fetchUserInfo();
        }
    }, [userInfo]);

    const handleLogout = async () => {
        const confirmation = await Swal.fire({
            title: "Are you sure?",
            text: "Do you really want to log out?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, log out!",
        });

        if (confirmation.isConfirmed) {
            Swal.fire({
                title: "Logging out...",
                timer: 1500,
                timerProgressBar: true,
                didOpen: () => {
                    Swal.showLoading();
                },
                willClose: async () => {
                    try {
                        await axios.post("http://localhost:8080/auth/logout", {}, { withCredentials: true });
                        setUser(null);
                        localStorage.removeItem("user");
                        Swal.fire("Logged Out", "You have successfully logged out.", "success");
                        navigate("/login");
                    } catch (err) {
                        console.error("Logout error:", err);
                        Swal.fire("Error", "An error occurred while logging out.", "error");
                    }
                }
            });
        }
    };

    if (!userInfo) {
        return <p>Loading user information...</p>;
    }

    return (
        <div className="user-info-container">
            <h1>Welcome, {userInfo.username || "N/A"}</h1>
            <p><strong>User ID:</strong> {userInfo?.userId || "N/A"}</p>
            <p><strong>Email:</strong> {userInfo?.email || "N/A"}</p>
            <p><strong>Role:</strong> {userInfo?.role || "N/A"}</p>
            <p><strong>Password:</strong> {userInfo?.password ? "*****" : "N/A"}</p>
            <button onClick={handleLogout} className="logout-button">Logout</button>
        </div>
    );
};

export default UserInfo;
