import React from "react";
import "../../style/AddAddressForm.css";

const AddAddressForm = ({ newAddress, onChange, onSubmit, onClose }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Add New Address</h2>
                <form onSubmit={onSubmit} className="add-address-form">
                    <input
                        type="text"
                        name="country"
                        placeholder="Country"
                        value={newAddress.country}
                        onChange={onChange}
                        required
                    />
                    <input
                        type="text"
                        name="city"
                        placeholder="City"
                        value={newAddress.city}
                        onChange={onChange}
                        required
                    />
                    <input
                        type="text"
                        name="street"
                        placeholder="Street"
                        value={newAddress.street}
                        onChange={onChange}
                        required
                    />
                    <input
                        type="text"
                        name="aptNo"
                        placeholder="Apartment No"
                        value={newAddress.aptNo}
                        onChange={onChange}
                        required
                    />
                    <input
                        type="text"
                        name="flatNo"
                        placeholder="Flat No"
                        value={newAddress.flatNo}
                        onChange={onChange}
                        required
                    />
                    <input
                        type="text"
                        name="description"
                        placeholder="Description"
                        value={newAddress.description}
                        onChange={onChange}
                        required
                    />
                    <select
                        name="addressType"
                        value={newAddress.addressType}
                        onChange={onChange}
                        required
                    >
                        <option value="HOME">Home</option>
                        <option value="WORK">Work</option>
                        <option value="OTHER">Other</option>
                    </select>
                    <div className="form-actions">
                        <button type="submit" className="add-button">
                            Add
                        </button>
                        <button type="button" onClick={onClose} className="cancel-button">
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddAddressForm;