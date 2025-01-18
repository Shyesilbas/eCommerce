import React from "react";

const UserDetails = ({ userInfo }) => {
    return (
        <div className="user-details">
            <p><strong>User ID:</strong> {userInfo?.userId || "N/A"}</p>
            <p><strong>Username:</strong> {userInfo?.username || "N/A"}</p>
            <p><strong>Email:</strong> {userInfo?.email || "N/A"}</p>
            <p><strong>Phone:</strong> {userInfo?.phone || "N/A"}</p>
            <p><strong>Total Orders:</strong> {userInfo?.totalOrders !== undefined ? userInfo.totalOrders : "N/A"}</p>
        </div>
    );
};

export default UserDetails;