import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/UserInfo.css";
import { logoutRequest, addAddress, deleteAddress, getUserAddress } from "../utils/api.js";
import UserDetails from "../components/user/UserDetails.js";
import AddressInfo from "../components/user/AddressInfo.js";

const UserInfo = ({ user, address, onLogout, activeSection, onSectionChange, updateAddress }) => {
    const navigate = useNavigate();
    const [showAddressModal, setShowAddressModal] = useState(false);
    const [addresses, setAddresses] = useState(address || []);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isAddAddressFormOpen, setIsAddAddressFormOpen] = useState(false);
    const [newAddress, setNewAddress] = useState({
        country: "",
        city: "",
        street: "",
        aptNo: "",
        flatNo: "",
        description: "",
        addressType: "HOME",
    });
    const [currentAddressIndex, setCurrentAddressIndex] = useState(0);

    useEffect(() => {
        setAddresses(address);
    }, [address]);

    if (!user) {
        navigate("/login");
        return null;
    }

    const handleLogout = async () => {
        try {
            await logoutRequest();
            localStorage.clear();
            onLogout();
            await Swal.fire("Logged Out", "You have successfully logged out.", "success");
            navigate("/login");
        } catch (err) {
            console.error("Logout error:", err);
            Swal.fire("Error", "An error occurred while logging out.", "error");
        }
    };

    const toggleAddressModal = () => {
        setShowAddressModal(!showAddressModal);
    };

    const handleAddressUpdate = () => {
        Swal.fire("Info", "Address update feature will be added soon.", "info");
    };

    const handleAddressDelete = async (addressId) => {
        const result = await Swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!",
        });

        if (result.isConfirmed) {
            try {
                const response = await deleteAddress(addressId);
                Swal.fire("Deleted!", response.message, "success");
                const updatedAddresses = await getUserAddress();
                setAddresses(updatedAddresses);
                updateAddress(updatedAddresses);
            } catch (error) {
                Swal.fire("Error", "Failed to delete address.", "error");
            }
        }
    };

    const openDeleteModal = () => {
        setIsDeleteModalOpen(true);
    };

    const closeDeleteModal = () => {
        setIsDeleteModalOpen(false);
    };

    const openAddAddressForm = () => {
        setIsAddAddressFormOpen(true);
    };

    const closeAddAddressForm = () => {
        setIsAddAddressFormOpen(false);
    };

    const handleNewAddressChange = (e) => {
        const { name, value } = e.target;
        setNewAddress({ ...newAddress, [name]: value });
    };

    const handleAddAddressSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await addAddress({ addressDto: newAddress });
            Swal.fire("Success", response.message, "success");
            const updatedAddresses = await getUserAddress();
            setAddresses(updatedAddresses);
            updateAddress(updatedAddresses);
            closeAddAddressForm();
        } catch (error) {
            Swal.fire("Error", "Failed to add address.", "error");
        }
    };

    const handleNextAddress = () => {
        setCurrentAddressIndex((prevIndex) => (prevIndex + 1) % addresses.length);
    };

    const handlePreviousAddress = () => {
        setCurrentAddressIndex((prevIndex) =>
            prevIndex === 0 ? addresses.length - 1 : prevIndex - 1
        );
    };

    return (
        <div className="user-info-container">
            <header className="user-header">
                <h1>Welcome, {user.username || "N/A"}</h1>
                <nav className="user-nav">
                    <button
                        className={`nav-button ${activeSection === "profile" ? "active" : ""}`}
                        onClick={() => onSectionChange("profile")}
                    >
                        Profile
                    </button>
                    <button
                        className={`nav-button ${activeSection === "address" ? "active" : ""}`}
                        onClick={() => onSectionChange("address")}
                    >
                        Address
                    </button>
                    <button
                        className={`nav-button ${activeSection === "notifications" ? "active" : ""}`}
                        onClick={() => onSectionChange("notifications")}
                    >
                        Notifications
                    </button>
                    <button
                        className={`nav-button ${activeSection === "favorites" ? "active" : ""}`}
                        onClick={() => onSectionChange("favorites")}
                    >
                        Favorites
                    </button>
                    <button
                        className={`nav-button ${activeSection === "orders" ? "active" : ""}`}
                        onClick={() => onSectionChange("orders")}
                    >
                        Orders
                    </button>
                    <button
                        className={`nav-button ${activeSection === "reviews" ? "active" : ""}`}
                        onClick={() => onSectionChange("reviews")}
                    >
                        Reviews
                    </button>
                </nav>
            </header>

            <div className="user-content">
                {activeSection === "profile" && (
                    <div className="profile-section">
                        <UserDetails userInfo={user} onLogout={handleLogout} />
                    </div>
                )}

                {activeSection === "address" && (
                    <div className="address-section">
                        <h2>Address Management</h2>
                        <button onClick={toggleAddressModal} className="address-button">
                            {showAddressModal ? "Hide Address" : "Show Address"}
                        </button>
                        <div className="address-actions">
                            <button onClick={handleAddressUpdate} className="action-button">
                                Update Address
                            </button>
                            <button onClick={openDeleteModal} className="action-button">
                                Delete Address
                            </button>
                            <button onClick={openAddAddressForm} className="action-button">
                                Add New Address
                            </button>
                        </div>
                    </div>
                )}

                {activeSection === "notifications" && (
                    <div className="notifications-section">
                        <p>Your Notifications will be listed here.</p>
                    </div>
                )}

                {activeSection === "favorites" && (
                    <div className="favorites-section">
                        <h2>Favorites</h2>
                        <p>Favorites feature will be added soon.</p>
                    </div>
                )}

                {activeSection === "orders" && (
                    <div className="orders-section">
                        <h2>Orders</h2>
                        <p>Orders feature will be added soon.</p>
                    </div>
                )}

                {activeSection === "reviews" && (
                    <div className="reviews-section">
                        <h2>Reviews</h2>
                        <p>Reviews feature will be added soon.</p>
                    </div>
                )}
            </div>

            {/* Address Modal */}
            {showAddressModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Address Details</h2>
                        <div className="address-cards-container">
                            {addresses.length > 0 && (
                                <div className="address-card">
                                    <p><strong>Country:</strong> {addresses[currentAddressIndex].country}</p>
                                    <p><strong>City:</strong> {addresses[currentAddressIndex].city}</p>
                                    <p><strong>Street:</strong> {addresses[currentAddressIndex].street}</p>
                                    <p><strong>Apartment No:</strong> {addresses[currentAddressIndex].aptNo}</p>
                                    <p><strong>Flat No:</strong> {addresses[currentAddressIndex].flatNo}</p>
                                    <p><strong>Description:</strong> {addresses[currentAddressIndex].description}</p>
                                    <p><strong>Type:</strong> {addresses[currentAddressIndex].addressType}</p>
                                </div>
                            )}
                        </div>
                        <div className="modal-navigation">
                            <button onClick={handlePreviousAddress} className="nav-button">
                                &lt; Previous
                            </button>
                            <button onClick={handleNextAddress} className="nav-button">
                                Next &gt;
                            </button>
                        </div>
                        <button onClick={toggleAddressModal} className="close-modal-button">
                            Close
                        </button>
                    </div>
                </div>
            )}

            {/* Add Address Modal */}
            {isAddAddressFormOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Add New Address</h2>
                        <form onSubmit={handleAddAddressSubmit} className="add-address-form">
                            <input
                                type="text"
                                name="country"
                                placeholder="Country"
                                value={newAddress.country}
                                onChange={handleNewAddressChange}
                                required
                            />
                            <input
                                type="text"
                                name="city"
                                placeholder="City"
                                value={newAddress.city}
                                onChange={handleNewAddressChange}
                                required
                            />
                            <input
                                type="text"
                                name="street"
                                placeholder="Street"
                                value={newAddress.street}
                                onChange={handleNewAddressChange}
                                required
                            />
                            <input
                                type="text"
                                name="aptNo"
                                placeholder="Apartment No"
                                value={newAddress.aptNo}
                                onChange={handleNewAddressChange}
                                required
                            />
                            <input
                                type="text"
                                name="flatNo"
                                placeholder="Flat No"
                                value={newAddress.flatNo}
                                onChange={handleNewAddressChange}
                                required
                            />
                            <input
                                type="text"
                                name="description"
                                placeholder="Description"
                                value={newAddress.description}
                                onChange={handleNewAddressChange}
                                required
                            />
                            <select
                                name="addressType"
                                value={newAddress.addressType}
                                onChange={handleNewAddressChange}
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
                                <button type="button" onClick={closeAddAddressForm} className="cancel-button">
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Delete Address Modal */}
            {isDeleteModalOpen && (
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
                                            onClick={() => handleAddressDelete(address.addressId)}
                                            className="delete-button"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>
                        <button onClick={closeDeleteModal} className="close-modal-button">
                            Close
                        </button>
                    </div>
                </div>
            )}

            <button onClick={handleLogout} className="logout-button">Logout</button>
        </div>
    );
};

export default UserInfo;