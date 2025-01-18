import React from "react";

const BasicInfoForm = ({ formData, handleChange, setStep }) => {
    return (
        <form onSubmit={(e) => { e.preventDefault(); setStep(2); }}>
            <div className="form-group">
                <label htmlFor="username">Username:</label>
                <input
                    type="text"
                    id="username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                    autoComplete="off"
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
                    autoComplete="off"
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
};

export default BasicInfoForm;