import React from "react";
import "../../style/AddressModal.css";

const AddressModal = ({ addresses, currentAddressIndex, onNext, onPrevious, onClose }) => {
    if (!addresses.length) return null;

    const address = addresses[currentAddressIndex];

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Address Details</h2>
                <div className="address-card">
                    <p><strong>Country:</strong> {address.country}</p>
                    <p><strong>City:</strong> {address.city}</p>
                    <p><strong>Street:</strong> {address.street}</p>
                    <p><strong>Apartment No:</strong> {address.aptNo}</p>
                    <p><strong>Flat No:</strong> {address.flatNo}</p>
                    <p><strong>Description:</strong> {address.description}</p>
                    <p><strong>Type:</strong> {address.addressType}</p>
                </div>
                <div className="modal-navigation">
                    <button onClick={onPrevious} className="nav-button">
                        &lt; Previous
                    </button>
                    <button onClick={onNext} className="nav-button">
                        Next &gt;
                    </button>
                </div>
                <button onClick={onClose} className="close-modal-button">
                    Close
                </button>
            </div>
        </div>
    );
};

export default AddressModal;