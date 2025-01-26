import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import axios from "axios";
import "../style/Sidebar.css";

const Sidebar = ({ isOpen, setIsOpen, user, onLogout }) => {
    const navigate = useNavigate();
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        setIsAuthenticated(!!user || !!storedUser);
    }, [user]);


    const handleProfileClick = () => {
        if (!isAuthenticated) {
            Swal.fire({
                icon: "warning",
                title: "Login Required",
                text: "Please login to access your profile.",
                confirmButtonText: "Login",
            }).then((result) => {
                if (result.isConfirmed) {
                    navigate("/login");
                }
            });
        } else {
            navigate("/user-info");
        }
    };

    const handleProductsClick = () => {
        navigate("/products");
    };

    const handleFavoritesClick = () => {
        if (!isAuthenticated) {
            Swal.fire({
                icon: "warning",
                title: "Login Required",
                text: "Please login to access your favorites.",
                confirmButtonText: "Login",
            }).then((result) => {
                if (result.isConfirmed) {
                    navigate("/login");
                }
            });
        } else {
            navigate("/favorites");
        }
    };

    const handleShoppingCardClick = () => {
        if (!isAuthenticated) {
            Swal.fire({
                icon: "warning",
                title: "Login Required",
                text: "Please login to access your shopping card.",
                confirmButtonText: "Login",
            }).then((result) => {
                if (result.isConfirmed) {
                    navigate("/login");
                }
            });
        } else {
            navigate("/shopping-card");
        }
    };

    const handleLogout = async () => {
        const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
        const username = user?.username || storedUser?.username || "User";

        const confirmation = await Swal.fire({
            title: `Goodbye, ${username}`,
            text: "Do you really want to log out?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, log out!",
        });

        if (confirmation.isConfirmed) {
            try {
                await axios.post("http://localhost:8080/auth/logout", {}, { withCredentials: true });
                localStorage.removeItem("user");
                setIsAuthenticated(false);
                onLogout();
                await Swal.fire("Logged Out", "You have successfully logged out.", "success");
                navigate("/login");
            } catch (err) {
                console.error("Logout error:", err);
                Swal.fire("Error", "An error occurred while logging out.", "error");
            }
        }
    };

    return (
        <div className={`sidebar ${isOpen ? "" : "closed"}`}>
            <button onClick={() => setIsOpen(!isOpen)} className="toggle-button">
                {isOpen ? "✕" : "☰"}
            </button>
            {isOpen && (
                <div className="menu-content">
                    <div className="menu-item" onClick={handleProfileClick}>
                        Profile
                    </div>
                    <div className="menu-item" onClick={handleProductsClick}>
                        Products
                    </div>
                    {isAuthenticated && (
                        <>
                            <div className="menu-item" onClick={handleFavoritesClick}>
                                Favorites
                            </div>
                            <div className="menu-item" onClick={handleShoppingCardClick}>
                                Shopping Card
                            </div>
                            <div className="menu-item" onClick={handleLogout}>
                                Logout
                            </div>

                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default Sidebar;