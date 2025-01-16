import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Swal from "sweetalert2";
import "../style/UserInfo.css";

const UserInfo = ({ user, onLogout }) => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useState(user);
    const [address, setAddress] = useState([]);
    const [showAddress, setShowAddress] = useState(false);

    // Kullanıcı oturum açmamışsa login sayfasına yönlendir
    useEffect(() => {
        if (!user) {
            navigate("/login");
        }
    }, [user, navigate]);

    // Kullanıcı bilgilerini ve adres bilgilerini çek
    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/myInfo", {
                    withCredentials: true,
                });
                setUserInfo(response.data);
            } catch (err) {
                console.error("Error fetching user info:", err);
                // Eğer kullanıcı oturum açmamışsa, login sayfasına yönlendir
                navigate("/login");
            }
        };

        const fetchAddressInfo = async () => {
            try {
                const response = await axios.get("http://localhost:8080/user/addressInfo", {
                    withCredentials: true,
                });
                setAddress(response.data);
            } catch (err) {
                console.error("Error fetching address info:", err);
            }
        };

        if (user) {
            fetchUserInfo();
            fetchAddressInfo();
        }
    }, [user, navigate]);

    // Logout işlemi
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
                timer: 500,
                timerProgressBar: false,
                willClose: async () => {
                    try {
                        await axios.post("http://localhost:8080/auth/logout", {}, { withCredentials: true });
                        localStorage.removeItem("user");
                        onLogout(); // App bileşenindeki user state'ini güncelle
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

    // Adres bilgilerini göster/gizle
    const toggleAddress = () => {
        setShowAddress(!showAddress);
    };

    // Kullanıcı bilgileri yüklenene kadar loading göster
    if (!userInfo) {
        return (
            <div className="user-info-container">
                <p>Loading user information...</p>
            </div>
        );
    }

    return (
        <div className="user-info-container">
            <h1>Welcome, {userInfo.username || "N/A"}</h1>
            <div className="user-details">
                <p><strong>User ID:</strong> {userInfo?.userId || "N/A"}</p>
                <p><strong>Username:</strong> {userInfo?.username || "N/A"}</p>
                <p><strong>Email:</strong> {userInfo?.email || "N/A"}</p>
                <p><strong>Total Orders:</strong> {userInfo?.totalOrders !== undefined ? userInfo.totalOrders : "N/A"}</p>
            </div>

            {/* Adres Bilgileri Butonu */}
            <button onClick={toggleAddress} className="address-button">
                {showAddress ? "Hide Address" : "Show Address"}
            </button>

            {/* Adres Bilgileri */}
            {showAddress && (
                <div className="address-info">
                    <h2>Addresses</h2>
                    {address.length > 0 ? (
                        <ul>
                            {address.map((addr, index) => (
                                <li key={index}>
                                    <p><strong>Country:</strong> {addr.country}</p>
                                    <p><strong>City:</strong> {addr.city}</p>
                                    <p><strong>Street:</strong> {addr.street}</p>
                                    <p><strong>Apt No:</strong> {addr.aptNo}</p>
                                    <p><strong>Flat No:</strong> {addr.flatNo}</p>
                                    <p><strong>Description:</strong> {addr.description}</p>
                                    <p><strong>Address Type:</strong> {addr.addressType}</p>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No addresses found.</p>
                    )}
                </div>
            )}

            {/* Logout Butonu */}
            <button onClick={handleLogout} className="logout-button">Logout</button>
        </div>
    );
};

export default UserInfo;