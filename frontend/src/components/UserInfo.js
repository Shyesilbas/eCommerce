import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/UserInfo.css";
import { logoutRequest, addAddress, deleteAddress, getUserAddress } from "../utils/api.js";
import UserNav from "../components/user/UserNav.js";
import ProfileSection from "../components/user/ProfileSection.js";
import AddressSection from "../components/user/AddressSection.js";
import AddressModal from "../components/user/AddressModal.js";
import AddAddressForm from "../components/user/AddAddressForm.js";
import DeleteAddressModal from "../components/user/DeleteAddressModal.js";
import NotificationsSection from "../components/user/NotificationsSection.js";
import FavoritesSection from "../components/user/FavoritesSection.js";
import OrdersSection from "../components/user/OrdersSection.js";
import ReviewsSection from "../components/user/ReviewsSection.js";

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
            setIsAddAddressFormOpen(false);
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
        <div className="user-info">
            <header className="user-info__header">
                <h1>Welcome, {user.username || "N/A"}</h1>
                <UserNav activeSection={activeSection} onSectionChange={onSectionChange} />
            </header>

            <div className="user-info__content">
                {activeSection === "profile" && (
                    <ProfileSection user={user} onLogout={handleLogout} />
                )}

                {activeSection === "address" && (
                    <AddressSection
                        onShowAddress={() => setShowAddressModal(true)}
                        onUpdateAddress={handleAddressUpdate}
                        onDeleteAddress={() => setIsDeleteModalOpen(true)}
                        onAddAddress={() => setIsAddAddressFormOpen(true)}
                    />
                )}

                {activeSection === "notifications" && <NotificationsSection />}

                {activeSection === "favorites" && <FavoritesSection />}

                {activeSection === "orders" && <OrdersSection />}

                {activeSection === "reviews" && <ReviewsSection />}
            </div>

            {showAddressModal && (
                <AddressModal
                    addresses={addresses}
                    currentAddressIndex={currentAddressIndex}
                    onNext={handleNextAddress}
                    onPrevious={handlePreviousAddress}
                    onClose={() => setShowAddressModal(false)}
                />
            )}

            {isAddAddressFormOpen && (
                <AddAddressForm
                    newAddress={newAddress}
                    onChange={handleNewAddressChange}
                    onSubmit={handleAddAddressSubmit}
                    onClose={() => setIsAddAddressFormOpen(false)}
                />
            )}

            {isDeleteModalOpen && (
                <DeleteAddressModal
                    addresses={addresses}
                    onDelete={handleAddressDelete}
                    onClose={() => setIsDeleteModalOpen(false)}
                />
            )}
        </div>
    );
};

export default UserInfo;