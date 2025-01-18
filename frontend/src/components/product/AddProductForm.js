import React from "react";

const AddProductForm = ({ newProduct, onFormChange, onSubmit, onCancel }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Add New Product</h2>
                    <button className="modal-close-button" onClick={onCancel}>
                        &times;
                    </button>
                </div>
                <div className="modal-body">
                    <form onSubmit={onSubmit} className="add-product-form">
                        <input
                            type="text"
                            name="name"
                            placeholder="Name"
                            value={newProduct.name}
                            onChange={onFormChange}
                            required
                        />
                        <input
                            type="text"
                            name="originOfCountry"
                            placeholder="Origin of Country"
                            value={newProduct.originOfCountry}
                            onChange={onFormChange}
                            required
                        />
                        <input
                            type="text"
                            name="productCode"
                            placeholder="Product Code"
                            value={newProduct.productCode}
                            onChange={onFormChange}
                            required
                        />
                        <textarea
                            name="description"
                            placeholder="Description"
                            value={newProduct.description}
                            onChange={onFormChange}
                            required
                        />
                        <input
                            type="number"
                            name="price"
                            placeholder="Price"
                            value={newProduct.price}
                            onChange={onFormChange}
                            required
                        />
                        <input
                            type="text"
                            name="brand"
                            placeholder="Brand"
                            value={newProduct.brand}
                            onChange={onFormChange}
                            required
                        />
                        <input
                            type="number"
                            name="averageRating"
                            placeholder="Average Rating"
                            value={newProduct.averageRating}
                            onChange={onFormChange}
                            required
                        />
                        <select
                            name="stockStatus"
                            value={newProduct.stockStatus}
                            onChange={onFormChange}
                            required
                        >
                            <option value="">Select Stock Status</option>
                            <option value="AVAILABLE">Available</option>
                            <option value="OUT_OF_STOCKS">Out of Stocks</option>
                        </select>
                        <input
                            type="text"
                            name="color"
                            placeholder="Color"
                            value={newProduct.color}
                            onChange={onFormChange}
                            required
                        />
                        <input
                            type="number"
                            name="quantity"
                            placeholder="Quantity"
                            value={newProduct.quantity}
                            onChange={onFormChange}
                            required
                        />
                        <select
                            name="category"
                            value={newProduct.category}
                            onChange={onFormChange}
                            required
                        >
                            <option value="">Select Category</option>
                            {newProduct.categories
                                .filter(category => category !== "All Products")
                                .map((category, index) => (
                                    <option key={index} value={category}>
                                        {category}
                                    </option>
                                ))
                            }
                        </select>
                        <div className="modal-footer">
                            <button type="button" onClick={onCancel}>
                                Cancel
                            </button>
                            <button type="submit" className="submit-button">
                                Add Product
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddProductForm;