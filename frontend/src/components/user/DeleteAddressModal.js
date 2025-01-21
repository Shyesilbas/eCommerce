import React from "react";
import "../../style/DeleteAddressModal.css";

const DeleteAddressModal = ({ addresses, onDelete, onClose }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Delete Address</h2>
                <div className="address-cards-container">
                    <div className="address-cards-scroll">
                        {addresses.map((address) => (
                            <div key={address.addressId} className="address-card">
                                <p><strong>Country:</strong> {address.country}</p>
                                <p><strong>City:</strong> {address.city}</p>
                                <p><strong>Street:</strong> {address.street}</p>
                                <p><strong>Apartment No:</strong> {address.aptNo}</p>
                                <p><strong>Flat No:</strong> {address.flatNo}</p>
                                <p><strong>Description:</strong> {address.description}</p>
                                <p><strong>Type:</strong> {address.addressType}</p>
                                <button
                                    onClick={() => onDelete(address.addressId)}
                                    className="delete-button"
                                >
                                    Delete
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
                <button onClick={onClose} className="close-modal-button">
                    Close
                </button>
            </div>
        </div>
    );
};

export default DeleteAddressModal;