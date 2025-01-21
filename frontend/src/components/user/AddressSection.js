import React from "react";
import "../../style/AddressSection.css";

const AddressSection = ({ onShowAddress, onUpdateAddress, onDeleteAddress, onAddAddress }) => {
    return (
        <div className="address-section">
            <h2>Address Management</h2>
            <button onClick={onShowAddress} className="address-section__address-button">
                Show Address
            </button>
            <div className="address-section__actions">
                <button onClick={onUpdateAddress} className="address-section__action-button">
                    Update Address
                </button>
                <button onClick={onDeleteAddress} className="address-section__action-button">
                    Delete Address
                </button>
                <button onClick={onAddAddress} className="address-section__action-button">
                    Add New Address
                </button>
            </div>
        </div>
    );
};

export default AddressSection;