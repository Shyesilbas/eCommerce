import React from "react";

const AddressForm = ({ addressForm, handleAddressChange, setStep, handleSubmit }) => {
    return (
        <form onSubmit={(e) => e.preventDefault()}>
            <div className="form-group">
                <label htmlFor="country">Country:</label>
                <input
                    type="text"
                    id="country"
                    value={addressForm.country}
                    onChange={handleAddressChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="city">City:</label>
                <input
                    type="text"
                    id="city"
                    value={addressForm.city}
                    onChange={handleAddressChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="street">Street:</label>
                <input
                    type="text"
                    id="street"
                    value={addressForm.street}
                    onChange={handleAddressChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="aptNo">Apartment No:</label>
                <input
                    type="text"
                    id="aptNo"
                    value={addressForm.aptNo}
                    onChange={handleAddressChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="flatNo">Flat No:</label>
                <input
                    type="text"
                    id="flatNo"
                    value={addressForm.flatNo}
                    onChange={handleAddressChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="description">Description:</label>
                <input
                    type="text"
                    id="description"
                    value={addressForm.description}
                    onChange={handleAddressChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="addressType">Address Type:</label>
                <select
                    id="addressType"
                    value={addressForm.addressType}
                    onChange={handleAddressChange}
                >
                    <option value="HOME">Home</option>
                    <option value="WORK">Work</option>
                    <option value="OTHER">Other</option>
                </select>
            </div>
            <div className="button-container">
                <button type="button" className="back-button" onClick={() => setStep(1)}>
                    Back
                </button>
                <button type="submit" className="submit-button" onClick={handleSubmit}>
                    Register
                </button>
            </div>
        </form>
    );
};

export default AddressForm;