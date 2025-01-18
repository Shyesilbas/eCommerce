import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/RegisterPage.css";
import BasicInfoForm from "../components/user/RegisterUserForm.js";
import AddressForm from "../components/user/RegisterAddressForm.js";
import { registerRequest } from "../utils/api.js";

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        phone: "",
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

            await registerRequest(updatedFormData);

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

    return (
        <div className="register-container">
            <h2>Register</h2>
            {message.text && (
                <p className={`message ${message.type}`}>{message.text}</p>
            )}
            {step === 1 ? (
                <BasicInfoForm
                    formData={formData}
                    handleChange={handleChange}
                    setStep={setStep}
                />
            ) : (
                <AddressForm
                    addressForm={addressForm}
                    handleAddressChange={handleAddressChange}
                    setStep={setStep}
                    handleSubmit={handleSubmit}
                />
            )}
        </div>
    );
};

export default RegisterPage;