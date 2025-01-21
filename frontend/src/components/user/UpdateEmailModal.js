import React, { useState } from "react";
import Swal from "sweetalert2";
import { updateEmailRequest } from "../../utils/api.js";
import "../../style/UpdateEmailModal.css";

const UpdateEmailModal = ({ onClose, onLogout }) => {
    const [newEmail, setNewEmail] = useState("");

    const handleEmailUpdate = async () => {
        try {
            const data = { newEmail };
            await updateEmailRequest(data);

            await Swal.fire({
                title: "Success",
                text: "Email updated successfully. You will be logged out.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false,
                timerProgressBar: true,
            });

            localStorage.clear();
            onLogout();
        } catch (err) {
            Swal.fire("Error", "Failed to update email.", "error");
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal">
                <h2>Update Email</h2>
                <input
                    type="email"
                    placeholder="New Email"
                    value={newEmail}
                    onChange={(e) => setNewEmail(e.target.value)}
                />
                <div className="modal-actions">
                    <button className="action-button" onClick={handleEmailUpdate}>Update</button>
                    <button className="action-button" onClick={onClose}>Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default UpdateEmailModal;