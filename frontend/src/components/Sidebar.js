import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import axios from "axios";

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

    const handleLogout = async () => {
        const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
        const username = user?.username || storedUser?.username || "User";

        console.log("Logout initiated for user:", username);

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
                console.log("Attempting to log out...");
                await axios.post("http://localhost:8080/auth/logout", {}, { withCredentials: true });
                console.log("Logout successful on backend.");

                localStorage.removeItem("user");
                setIsAuthenticated(false);
                onLogout();

                console.log("Local storage cleared and state updated.");

                await Swal.fire("Logged Out", "You have successfully logged out.", "success");
                console.log("SweetAlert2 success message shown.");

                navigate("/login");
                console.log("Navigated to login page.");
            } catch (err) {
                console.error("Logout error:", err);
                Swal.fire("Error", "An error occurred while logging out.", "error");
            }
        }
    };

    return (
        <div style={styles.sidebar(isOpen)}>
            <button onClick={() => setIsOpen(!isOpen)} style={styles.toggleButton}>
                {isOpen ? "✕" : "☰"}
            </button>
            {isOpen && (
                <div style={styles.menuContent}>
                    <div style={styles.menuItem} onClick={handleProfileClick}>
                        Profile
                    </div>
                    <div style={styles.menuItem} onClick={handleProductsClick}>
                        Products
                    </div>
                    {isAuthenticated && (
                        <>
                            <div style={styles.menuItem} onClick={handleFavoritesClick}>
                                Favorites
                            </div>
                            <div style={styles.menuItem} onClick={handleLogout}>
                                Logout
                            </div>
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

const styles = {
    sidebar: (isOpen) => ({
        width: isOpen ? "250px" : "50px",
        height: "100vh",
        backgroundColor: "#2c3e50",
        transition: "width 0.3s ease",
        position: "fixed",
        top: 0,
        left: 0,
        zIndex: 1000,
    }),
    toggleButton: {
        backgroundColor: "transparent",
        border: "none",
        color: "white",
        fontSize: "24px",
        cursor: "pointer",
        padding: "10px",
    },
    menuContent: {
        padding: "20px",
        color: "white",
    },
    menuItem: {
        padding: "10px",
        cursor: "pointer",
        transition: "background-color 0.3s ease",
        '&:hover': {
            backgroundColor: "#34495e",
        }
    },
    menuItemHover: {
        backgroundColor: "#34495e",
    },
};

export default Sidebar;