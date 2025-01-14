import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const AdminPage = () => {
    const navigate = useNavigate();
    const [adminMessage, setAdminMessage] = useState("");

    useEffect(() => {
        const fetchAdminMessage = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/adminRole", { withCredentials: true });
                setAdminMessage(response.data); // Backend'den alınan mesaj
            } catch (err) {
                console.error("Error fetching admin message:", err);
                navigate("/login"); // Hata olursa login sayfasına yönlendir
            }
        };

        fetchAdminMessage();
    }, [navigate]);

    const handleGoBack = () => {
        navigate("/user-info");
    };

    return (
        <div>
            <h1>Admin Page - Only for Admins</h1>
            <p>{adminMessage}</p> {/* Admin mesajını burada gösteriyoruz */}
            <button onClick={handleGoBack}>Go Back to Main</button>
        </div>
    );
};

export default AdminPage;
