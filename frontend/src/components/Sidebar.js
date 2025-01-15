import React from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

const Sidebar = ({ isOpen, setIsOpen, user }) => {
    const navigate = useNavigate();

    const handleProfileClick = () => {
        if (!user) {
            Swal.fire({
                icon: "warning",
                title: "Login Required",
                text: "Please login to access your profile.",
                confirmButtonText: "Go to Login",
            }).then((result) => {
                if (result.isConfirmed) {
                    navigate("/login");
                }
            });
        } else {
            navigate("/user-info");
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
    },
    menuItemHover: {
        backgroundColor: "#34495e",
    },
};

export default Sidebar;