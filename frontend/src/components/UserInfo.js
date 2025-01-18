import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/UserInfo.css";
import { getUserInfo, getUserAddress, logoutRequest } from "../utils/api.js";
import UserDetails from "../components/user/UserDetails.js"
import AddressInfo from "../components/user/AddressInfo.js";

const UserInfo = ({ user, onLogout }) => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useState(user);
    const [address, setAddress] = useState([]);
    const [showAddress, setShowAddress] = useState(false);

    useEffect(() => {
        if (!user) {
            navigate("/login");
            return;
        }

        const loadData = async () => {
            try {
                const userData = await getUserInfo();
                setUserInfo(userData);

                const addressData = await getUserAddress();
                setAddress(addressData);
            } catch (err) {
                console.error("Error loading data:", err);
                navigate("/login");
            }
        };

        loadData();
    }, [user, navigate]);

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
            try {
                await logoutRequest();
                localStorage.removeItem("user");
                onLogout();
                await Swal.fire("Logged Out", "You have successfully logged out.", "success");
                navigate("/login");
            } catch (err) {
                console.error("Logout error:", err);
                Swal.fire("Error", "An error occurred while logging out.", "error");
            }
        }
    };

    const toggleAddress = () => {
        setShowAddress(!showAddress);
    };

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
            <UserDetails userInfo={userInfo} />
            <button onClick={toggleAddress} className="address-button">
                {showAddress ? "Hide Address" : "Show Address"}
            </button>
            {showAddress && <AddressInfo address={address} />}
            <button onClick={handleLogout} className="logout-button">Logout</button>
        </div>
    );
};

export default UserInfo;