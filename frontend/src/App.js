import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import UserInfo from "./components/UserInfo";
import RegisterPage from "./components/RegisterPage";
import FavoritesPage from "./components/FavoritesPage";
import ProductPage from "./components/ProductPage";
import Sidebar from "./components/Sidebar";
import ProductDetailPage from "./components/ProductDetailPage";
import ShoppingCardPage from "./components/ShoppingCardPage";

import "./style/UserNav.css";
import "./style/Notifications.css";

const App = () => {
    const [user, setUser] = useState(null);
    const [address, setAddress] = useState([]);
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [activeSection, setActiveSection] = useState("profile");
    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        const storedAddress = localStorage.getItem("address");
        const savedSection = localStorage.getItem("activeSection");

        if (storedUser) setUser(JSON.parse(storedUser));
        if (storedAddress) setAddress(JSON.parse(storedAddress));
        if (savedSection) setActiveSection(savedSection);
    }, []);

    const handleLogout = () => {
        setUser(null);
        setAddress([]);
        localStorage.removeItem("user");
        localStorage.removeItem("address");
        localStorage.removeItem("activeSection");
    };

    const handleSectionChange = (section) => {
        setActiveSection(section);
        localStorage.setItem("activeSection", section);
    };

    const updateAddress = (newAddress) => {
        setAddress(newAddress);
        localStorage.setItem("address", JSON.stringify(newAddress));
    };

    return (
        <Router>
            <Sidebar
                isOpen={isSidebarOpen}
                setIsOpen={setIsSidebarOpen}
                user={user}
                onLogout={handleLogout}
            />
            <div style={{ marginLeft: isSidebarOpen ? "250px" : "50px", transition: "margin-left 0.3s ease" }}>
                <Routes>
                    <Route
                        path="/login"
                        element={<LoginPage setUser={setUser} setAddress={setAddress} />}
                    />
                    <Route path="/favorites" element={<FavoritesPage />} />
                    <Route
                        path="/shopping-card"
                        element={<ShoppingCardPage user={user} />}
                    />
                    <Route
                        path="/user-info"
                        element={
                            <UserInfo
                                user={user}
                                address={address}
                                onLogout={handleLogout}
                                activeSection={activeSection}
                                onSectionChange={handleSectionChange}
                                updateAddress={updateAddress}
                            />
                        }
                    />
                    <Route
                        path="/register"
                        element={<RegisterPage />}
                    />
                    <Route
                        path="/products"
                        element={<ProductPage user={user} />}
                    />
                    <Route
                        path="/product/:productId"
                        element={<ProductDetailPage />}
                    />
                    <Route
                        path="/"
                        element={<ProductPage user={user} />}
                    />
                </Routes>
            </div>
        </Router>
    );
};

export default App;