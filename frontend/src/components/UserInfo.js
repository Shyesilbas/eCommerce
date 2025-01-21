import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/UserInfo.css";
import { logoutRequest } from "../utils/api.js";
import UserDetails from "../components/user/UserDetails.js";
import AddressInfo from "../components/user/AddressInfo.js";

const UserInfo = ({ user, address, onLogout, activeSection, onSectionChange }) => {
    const navigate = useNavigate();
    const [showAddress, setShowAddress] = useState(false);

    if (!user) {
        navigate("/login");
        return null;
    }

    const handleLogout = async () => {
        try {
            await logoutRequest();
            localStorage.clear();
            onLogout();
            await Swal.fire("Logged Out", "You have successfully logged out.", "success");
            navigate("/login");
        } catch (err) {
            console.error("Logout error:", err);
            Swal.fire("Error", "An error occurred while logging out.", "error");
        }
    };

    const toggleAddress = () => {
        setShowAddress(!showAddress);
    };

    const handleAddressUpdate = () => {
        Swal.fire("Info", "Address update feature will be added soon.", "info");
    };

    const handleAddressDelete = () => {
        Swal.fire("Info", "Address delete feature will be added soon.", "info");
    };

    const handleAddressAdd = () => {
        Swal.fire("Info", "Address add feature will be added soon.", "info");
    };

    return (
        <div className="user-info-container">
            <header className="user-header">
                <h1>Welcome, {user.username || "N/A"}</h1>
                <nav className="user-nav">
                    <button
                        className={`nav-button ${activeSection === "profile" ? "active" : ""}`}
                        onClick={() => onSectionChange("profile")}
                    >
                        Profile
                    </button>
                    <button
                        className={`nav-button ${activeSection === "address" ? "active" : ""}`}
                        onClick={() => onSectionChange("address")}
                    >
                        Address
                    </button>
                    <button
                        className={`nav-button ${activeSection === "notifications" ? "active" : ""}`}
                        onClick={() => onSectionChange("notifications")}
                    >
                        Notifications
                    </button>
                    <button
                        className={`nav-button ${activeSection === "favorites" ? "active" : ""}`}
                        onClick={() => onSectionChange("favorites")}
                    >
                        Favorites
                    </button>
                    <button
                        className={`nav-button ${activeSection === "orders" ? "active" : ""}`}
                        onClick={() => onSectionChange("orders")}
                    >
                        Orders
                    </button>
                    <button
                        className={`nav-button ${activeSection === "reviews" ? "active" : ""}`}
                        onClick={() => onSectionChange("reviews")}
                    >
                        Reviews
                    </button>
                </nav>
            </header>

            <div className="user-content">
                {activeSection === "profile" && (
                    <div className="profile-section">
                        <UserDetails userInfo={user} onLogout={handleLogout} />
                    </div>
                )}

                {activeSection === "address" && (
                    <div className="address-section">
                        <h2>Address Management</h2>
                        <button onClick={toggleAddress} className="address-button">
                            {showAddress ? "Hide Address" : "Show Address"}
                        </button>
                        {showAddress && <AddressInfo address={address} />}
                        <div className="address-actions">
                            <button onClick={handleAddressUpdate} className="action-button">
                                Update Address
                            </button>
                            <button onClick={handleAddressDelete} className="action-button">
                                Delete Address
                            </button>
                            <button onClick={handleAddressAdd} className="action-button">
                                Add New Address
                            </button>
                        </div>
                    </div>
                )}

                {activeSection === "notifications" && (
                    <div className="notifications-section">
                        <p>Your Notifications will be listed here.</p>
                    </div>
                )}

                {activeSection === "favorites" && (
                    <div className="favorites-section">
                        <h2>Favorites</h2>
                        <p>Favorites feature will be added soon.</p>
                    </div>
                )}

                {activeSection === "orders" && (
                    <div className="orders-section">
                        <h2>Orders</h2>
                        <p>Orders feature will be added soon.</p>
                    </div>
                )}

                {activeSection === "reviews" && (
                    <div className="reviews-section">
                        <h2>Reviews</h2>
                        <p>Reviews feature will be added soon.</p>
                    </div>
                )}
            </div>

            <button onClick={handleLogout} className="logout-button">Logout</button>
        </div>
    );
};

export default UserInfo;