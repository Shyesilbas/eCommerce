import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const CustomerPage = () => {
    const navigate = useNavigate();
    const [customerMessage, setCustomerMessage] = useState("");

    useEffect(() => {
        const fetchCustomerMessage = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/customerRole", { withCredentials: true });
                setCustomerMessage(response.data); // Backend'den alınan mesaj
            } catch (err) {
                console.error("Error fetching customer message:", err);
                navigate("/login"); // Hata olursa login sayfasına yönlendir
            }
        };

        fetchCustomerMessage();
    }, [navigate]);

    const handleGoBack = () => {
        navigate("/user-info");
    };

    return (
        <div>
            <h1>Customer Page - Only for Customers</h1>
            <p>{customerMessage}</p> {/* Customer mesajını burada gösteriyoruz */}
            <button onClick={handleGoBack}>Go Back to Main</button>
        </div>
    );
};

export default CustomerPage;
