import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/RegisterPage.css";

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        role: "CUSTOMER",
        address: [],
    });

    const [addressForm, setAddressForm] = useState({
        country: "",
        city: "",
        street: "",
        aptNo: "",
        flatNo: "",
        description: "",
        addressType: "HOME",
    });

    const [step, setStep] = useState(1);
    const [message, setMessage] = useState({ type: "", text: "" });
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { id, value } = e.target;
        setFormData((prev) => ({ ...prev, [id]: value }));
    };

    const handleAddressChange = (e) => {
        const { id, value } = e.target;
        setAddressForm((prev) => ({ ...prev, [id]: value }));
    };

    const handleSubmit = async () => {
        try {
            Swal.fire({
                title: "Registering...",
                text: "Please wait while we register your account.",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                },
            });

            const updatedFormData = {
                ...formData,
                address: [addressForm],
            };

            const registerResponse = await axios.post(
                "http://localhost:8080/auth/register",
                updatedFormData
            );

            Swal.fire({
                icon: "success",
                title: "Registration Successful!",
                text: "You have been registered successfully.",
                timer: 2000,
                showConfirmButton: false,
            }).then(() => {
                navigate("/login");
            });
        } catch (err) {
            const errorMessage = err.response?.data?.message || "Registration failed. Please try again.";
            Swal.fire({
                icon: "error",
                title: "Registration Failed",
                text: errorMessage,
            });
            console.error("Register error:", err);
        }
    };

    const renderBasicInfoForm = () => (
        <form onSubmit={(e) => { e.preventDefault(); setStep(2); }}>
            <div className="form-group">
                <label htmlFor="username">Username:</label>
                <input
                    type="text"
                    id="username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                    autoComplete={"off"}
                />
            </div>
            <div className="form-group">
                <label htmlFor="email">Email:</label>
                <input
                    type="email"
                    id="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    autoComplete={"off"}
                />
            </div>
            <div className="form-group">
                <label htmlFor="password">Password:</label>
                <input
                    type="password"
                    id="password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="role">Role:</label>
                <select
                    id="role"
                    value={formData.role}
                    onChange={handleChange}
                >
                    <option value="CUSTOMER">Customer</option>
                    <option value="ADMIN">Admin</option>
                    <option value="SUPER_ADMIN">Super Admin</option>
                    <option value="MANAGER">Manager</option>
                    <option value="DEVELOPER">Developer</option>
                    <option value="TESTER">Tester</option>
                </select>
            </div>
            <button type="submit" className="submit-button">Continue</button>
            <p>Already have an account? <a href="/login">Login</a></p>
        </form>
    );

    const renderAddressForm = () => (
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

    return (
        <div className="register-container">
            <h2>Register</h2>
            {message.text && (
                <p className={`message ${message.type}`}>{message.text}</p>
            )}
            {step === 1 ? renderBasicInfoForm() : renderAddressForm()}
        </div>
    );
};

export default RegisterPage;