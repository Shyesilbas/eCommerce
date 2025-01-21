import React, { useState } from "react";
import Swal from "sweetalert2";
import { updatePhoneRequest } from "../../utils/api.js";
import "../../style/UpdatePhoneModal.css";


const UpdatePhoneModal = ({ onClose, onLogout }) => {
    const [newPhone, setNewPhone] = useState("");

    const handlePhoneUpdate = async () => {
        try {
            const data = { newPhone };
            await updatePhoneRequest(data);

            await Swal.fire({
                title: "Success",
                text: "Phone updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true,
            });

            localStorage.clear();
            onLogout();
        } catch (err) {
            Swal.fire("Error", "Failed to update phone.", "error");
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal">
                <h2>Update Phone</h2>
                <input
                    type="text"
                    placeholder="New Phone"
                    value={newPhone}
                    onChange={(e) => setNewPhone(e.target.value)}
                />
                <div className="modal-actions">
                    <button className="action-button" onClick={handlePhoneUpdate}>Update</button>
                    <button className="action-button" onClick={onClose}>Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default UpdatePhoneModal;