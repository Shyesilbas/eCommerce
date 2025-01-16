import React, { useState } from "react";
import axios from "axios";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";
import "../style/ProductPage.css";

const ProductPage = ({ user }) => {
    const navigate = useNavigate();
    const [productCode, setProductCode] = useState("");
    const [product, setProduct] = useState(null);
    const [error, setError] = useState("");
    const [showAddProductForm, setShowAddProductForm] = useState(false);
    const [newProduct, setNewProduct] = useState({
        name: "",
        originOfCountry: "",
        productCode: "",
        description: "",
        price: "",
        brand: "",
        averageRating: "",
        stockStatus: "",
        color: "",
        quantity: "",
        category: "",
    });

    const fetchProductInfo = async () => {
        if (!productCode) {
            setError("Please enter a product code.");
            return;
        }

        try {
            const response = await axios.get(`http://localhost:8080/api/products/info/${productCode}`);
            setProduct(response.data);
            setError("");
        } catch (err) {
            setProduct(null);
            setError("Product not found or an error occurred.");
        }
    };

    const handleAddProduct = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(
                "http://localhost:8080/api/products/addProduct",
                newProduct,
                { withCredentials: true }
            );
            Swal.fire("Success", response.data.message, "success");
            setShowAddProductForm(false);
            setNewProduct({
                name: "",
                originOfCountry: "",
                productCode: "",
                description: "",
                price: "",
                brand: "",
                averageRating: "",
                stockStatus: "",
                color: "",
                quantity: "",
                category: "",
            });
        } catch (err) {
            Swal.fire("Error", "Failed to add product. Only ADMIN users can add products.", "error");
        }
    };

    const toggleAddProductForm = () => {
        setShowAddProductForm(!showAddProductForm);
    };

    const handleNewProductChange = (e) => {
        const { name, value } = e.target;
        setNewProduct({ ...newProduct, [name]: value });
    };

    return (
        <div className="product-page-container">
            <h1>Product Management</h1>

            <div className="product-info-section">
                <h2>Product Information</h2>
                <div className="search-bar">
                    <input
                        type="text"
                        placeholder="Enter Product Code"
                        value={productCode}
                        onChange={(e) => setProductCode(e.target.value)}
                    />
                    <button onClick={fetchProductInfo}>Search</button>
                </div>

                {error && <p className="error-message">{error}</p>}

                {product && (
                    <div className="product-details">
                        <h3>{product.name}</h3>
                        <p><strong>Product ID:</strong> {product.productId}</p>
                        <p><strong>Origin:</strong> {product.originOfCountry}</p>
                        <p><strong>Description:</strong> {product.description}</p>
                        <p><strong>Price:</strong> ${product.price}</p>
                        <p><strong>Brand:</strong> {product.brand}</p>
                        <p><strong>Rating:</strong> {product.averageRating}</p>
                        <p><strong>Stock Status:</strong> {product.stockStatus}</p>
                        <p><strong>Color:</strong> {product.color}</p>
                        <p><strong>Quantity:</strong> {product.quantity}</p>
                        <p><strong>Category:</strong> {product.category}</p>
                    </div>
                )}
            </div>

            {user?.role === "ADMIN" && (
                <div className="add-product-section">
                    <button onClick={toggleAddProductForm} className="add-product-button">
                        {showAddProductForm ? "Hide Add Product Form" : "Add New Product"}
                    </button>

                    {showAddProductForm && (
                        <form onSubmit={handleAddProduct} className="add-product-form">
                            <input
                                type="text"
                                name="name"
                                placeholder="Name"
                                value={newProduct.name}
                                onChange={handleNewProductChange}
                                required
                            />
                            <input
                                type="text"
                                name="originOfCountry"
                                placeholder="Origin of Country"
                                value={newProduct.originOfCountry}
                                onChange={handleNewProductChange}
                                required
                            />
                            <input
                                type="text"
                                name="productCode"
                                placeholder="Product Code"
                                value={newProduct.productCode}
                                onChange={handleNewProductChange}
                                required
                            />
                            <textarea
                                name="description"
                                placeholder="Description"
                                value={newProduct.description}
                                onChange={handleNewProductChange}
                                required
                            />
                            <input
                                type="number"
                                name="price"
                                placeholder="Price"
                                value={newProduct.price}
                                onChange={handleNewProductChange}
                                required
                            />
                            <input
                                type="text"
                                name="brand"
                                placeholder="Brand"
                                value={newProduct.brand}
                                onChange={handleNewProductChange}
                                required
                            />
                            <input
                                type="number"
                                name="averageRating"
                                placeholder="Average Rating"
                                value={newProduct.averageRating}
                                onChange={handleNewProductChange}
                                required
                            />
                            <select
                                name="stockStatus"
                                value={newProduct.stockStatus}
                                onChange={handleNewProductChange}
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
                                onChange={handleNewProductChange}
                                required
                            />
                            <input
                                type="number"
                                name="quantity"
                                placeholder="Quantity"
                                value={newProduct.quantity}
                                onChange={handleNewProductChange}
                                required
                            />
                            <select
                                name="category"
                                value={newProduct.category}
                                onChange={handleNewProductChange}
                                required
                            >
                                <option value="">Select Category</option>
                                <option value="ELECTRONICS">Electronics</option>
                                <option value="CLOTHING">Clothing</option>
                                <option value="HOME_AND_KITCHEN">Home & Kitchen</option>
                                <option value="BOOKS_AND_STATIONERY">Books & Stationery</option>
                                <option value="SPORTS_AND_OUTDOORS">Sports & Outdoors</option>
                                <option value="BEAUTY_AND_COSMETICS">Beauty & Cosmetics</option>
                                <option value="TOYS_AND_GAMES">Toys & Games</option>
                                <option value="AUTOMOTIVE">Automotive</option>
                                <option value="HEALTH_AND_WELLNESS">Health & Wellness</option>
                                <option value="GROCERY">Grocery</option>
                            </select>
                            <button type="submit" className="submit-button">Add Product</button>
                        </form>
                    )}
                </div>
            )}
        </div>
    );
};

export default ProductPage;