import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import UserInfo from "./components/UserInfo";
import RegisterPage from "./components/RegisterPage";
import ProductPage from "./components/ProductPage";
import Sidebar from "./components/Sidebar";

const App = () => {
    const [user, setUser] = useState(null);
    const [address, setAddress] = useState([]);
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const handleLogout = () => {
        setUser(null);
        setAddress([]);
    };

    return (
        <Router>
            <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} user={user} />
            <div style={{ marginLeft: isSidebarOpen ? "250px" : "50px", transition: "margin-left 0.3s ease" }}>
                <Routes>
                    <Route
                        path="/login"
                        element={<LoginPage setUser={setUser} setAddress={setAddress} />}
                    />
                    <Route
                        path="/user-info"
                        element={<UserInfo user={user} address={address} onLogout={handleLogout} />}
                    />
                    <Route
                        path="/register"
                        element={<RegisterPage />}
                    />
                    <Route
                        path="/products"
                        element={<ProductPage user={user} />}
                    />
                </Routes>
            </div>
        </Router>
    );
};

export default App;