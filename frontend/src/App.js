import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import UserInfo from "./components/UserInfo";

function App() {
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem("user");
        return savedUser ? JSON.parse(savedUser) : null;
    });

    useEffect(() => {
        if (user) {
            localStorage.setItem("user", JSON.stringify(user));
        } else {
            localStorage.removeItem("user");
        }
    }, [user]);

    return (
        <Router>
            <Routes>
                <Route
                    path="/login"
                    element={<LoginPage setUser={setUser} />}
                />
                <Route
                    path="/user-info"
                    element={<UserInfo user={user} setUser={setUser} />}
                />
                <Route
                    path="*"
                    element={<UserInfo user={user} setUser={setUser} />}
                />
            </Routes>
        </Router>
    );
}

export default App;
