import React from "react";

const AddressInfo = ({ address }) => {
    return (
        <div className="address-info">
            <h2>Addresses</h2>
            {address.length > 0 ? (
                <ul>
                    {address.map((addr, index) => (
                        <li key={index}>
                            <p><strong>Country:</strong> {addr.country}</p>
                            <p><strong>City:</strong> {addr.city}</p>
                            <p><strong>Street:</strong> {addr.street}</p>
                            <p><strong>Apt No:</strong> {addr.aptNo}</p>
                            <p><strong>Flat No:</strong> {addr.flatNo}</p>
                            <p><strong>Description:</strong> {addr.description}</p>
                            <p><strong>Address Type:</strong> {addr.addressType}</p>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No addresses found.</p>
            )}
        </div>
    );
};

export default AddressInfo;