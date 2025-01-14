import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import UserInfo from "./components/UserInfo";
import AdminPage from "./components/AdminPage";
import CustomerPage from "./components/CustomerPage";

function App() {
    const [user, setUser] = useState(null);

    return (
        <Router>
            <Routes>
                <Route
                    path="/login"
                    element={<LoginPage setUser={setUser} />}
                />
                <Route
                    path="/user-info"
                    element={
                        user ? (
                            <UserInfo user={user} setUser={setUser} />
                        ) : (
                            <Navigate to="/login" />
                        )
                    }
                />
                <Route
                    path="/customerRole"
                    element={
                        user && user.role === "CUSTOMER" ? (
                            <CustomerPage />
                        ) : (
                            <Navigate to="/user-info" />
                        )
                    }
                />
                <Route
                    path="/adminRole"
                    element={
                        user && user.role === "ADMIN" ? (
                            <AdminPage />
                        ) : (
                            <Navigate to="/user-info" />
                        )
                    }
                />
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;
