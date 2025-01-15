import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import UserInfo from "./components/UserInfo";
import RegisterPage from "./components/RegisterPage";

const App = () => {
    const [user, setUser] = useState(null);
    const [address, setAddress] = useState([]);

    return (
        <Router>
            <Routes>
                <Route
                    path="/login"
                    element={<LoginPage setUser={setUser} setAddress={setAddress} />}
                />
                <Route
                    path="/user-info"
                    element={<UserInfo user={user} address={address} />}
                />
                <Route
                    path="/register"
                    element={<RegisterPage />}
                />
            </Routes>
        </Router>
    );
};

export default App;