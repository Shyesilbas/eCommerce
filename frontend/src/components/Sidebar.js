import React from "react";

const Sidebar = ({ isOpen, setIsOpen }) => {
    return (
        <div style={styles.sidebar(isOpen)}>
            <button onClick={() => setIsOpen(!isOpen)} style={styles.toggleButton}>
                {isOpen ? "✕" : "☰"}
            </button>
            {isOpen && (
                <div style={styles.menuContent}>
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
};

export default Sidebar;