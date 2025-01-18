import React, { useState, useEffect } from "react";
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
    const [totalProductCount, setTotalProductCount] = useState(0);
    const [categoryProductCount, setCategoryProductCount] = useState(0);
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
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("All Products");
    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const CATEGORY_DISPLAY_NAMES = {
        ELECTRONICS: "Electronics",
        CLOTHING: "Clothing",
        HOME_AND_KITCHEN: "Home and Kitchen",
        BOOKS_AND_STATIONERY: "Books and Stationery",
        SPORTS_AND_OUTDOORS: "Sports and Outdoors",
        BEAUTY_AND_COSMETICS: "Beauty and Cosmetics",
        TOYS_AND_GAMES: "Toys and Games",
        AUTOMOTIVE: "Automotive",
        HEALTH_AND_WELLNESS: "Health and Wellness",
        GROCERY: "Grocery"
    };

    const getDisplayNameFromEnum = (enumValue) => {
        return CATEGORY_DISPLAY_NAMES[enumValue] || enumValue;
    };

    const getEnumKeyFromDisplayName = (displayName) => {
        return Object.entries(CATEGORY_DISPLAY_NAMES)
            .find(([key, value]) => value === displayName)?.[0] || displayName;
    };

    useEffect(() => {
        axios.get("http://localhost:8080/api/products/totalCount")
            .then(response => {
                setTotalProductCount(response.data);
            })
            .catch(error => {
                console.error("Error fetching total product count:", error);
            });
    }, []);

    useEffect(() => {
        axios.get("http://localhost:8080/api/products/categories")
            .then(response => {
                const enumCategories = response.data.map(category => CATEGORY_DISPLAY_NAMES[category] || category);
                setCategories(["All Products", ...enumCategories]);
            })
            .catch(() => setCategories(["All Products"]));
    }, []);

    useEffect(() => {
        const fetchProducts = () => {
            const endpoint = selectedCategory === "All Products" || !selectedCategory
                ? "http://localhost:8080/api/products/allProducts"
                : "http://localhost:8080/api/products/byCategory";

            const params = selectedCategory === "All Products" || !selectedCategory
                ? { page: currentPage, size: 10 }
                : { category: getEnumKeyFromDisplayName(selectedCategory), page: currentPage, size: 10 };

            axios.get(endpoint, { params })
                .then(response => {
                    if (response.data && Array.isArray(response.data.content)) {
                        const productsWithDisplayNames = response.data.content.map(prod => ({
                            ...prod,
                            category: getDisplayNameFromEnum(prod.category)
                        }));
                        setProducts(productsWithDisplayNames);
                        setTotalPages(response.data.totalPages);
                    }
                })
                .catch(() => setProducts([]));
        };
        fetchProducts();
    }, [currentPage, selectedCategory]);

    useEffect(() => {
        if (selectedCategory !== "All Products") {
            const enumCategory = getEnumKeyFromDisplayName(selectedCategory);
            axios.get(`http://localhost:8080/api/products/totalCountByCategory?category=${enumCategory}`)
                .then(response => {
                    setCategoryProductCount(response.data);
                })
                .catch(error => {
                    console.error("Error fetching category product count:", error);
                    setCategoryProductCount(0);
                });
        } else {
            setCategoryProductCount(totalProductCount);
        }
    }, [selectedCategory, totalProductCount]);

    const fetchProductInfo = () => {
        if (!productCode) {
            setError("Please enter a product code.");
            return;
        }
        axios.get(`http://localhost:8080/api/products/info/${productCode}`)
            .then(response => {
                const productWithDisplayName = {
                    ...response.data,
                    category: getDisplayNameFromEnum(response.data.category)
                };
                setProduct(productWithDisplayName);
                setError("");
            })
            .catch(() => {
                setProduct(null);
                setError("Product not found or an error occurred.");
            });
    };

    const navigateToProductDetail = (productId) => {
        navigate(`/product/${productId}`);
    };

    const handleAddProduct = (e) => {
        e.preventDefault();
        const productData = {
            ...newProduct,
            category: getEnumKeyFromDisplayName(newProduct.category)
        };

        axios.post("http://localhost:8080/api/products/addProduct", productData, { withCredentials: true })
            .then(response => {
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
            })
            .catch(() => Swal.fire("Error", "Failed to add product. Only ADMIN users can add products.", "error"));
    };

    const toggleAddProductForm = () => setShowAddProductForm(!showAddProductForm);

    const handleNewProductChange = (e) => {
        const { name, value } = e.target;
        setNewProduct({ ...newProduct, [name]: value });
    };

    const handlePageChange = (direction) => {
        if (direction === "prev" && currentPage > 0) setCurrentPage(currentPage - 1);
        if (direction === "next" && currentPage < totalPages - 1) setCurrentPage(currentPage + 1);
    };

    return (
        <div className="product-page-container">
            <div className="categories-section">
                <h3>Categories</h3>
                <ul>
                    {categories.map((category, index) => (
                        <li
                            key={index}
                            className={selectedCategory === category ? "active" : ""}
                            onClick={() => setSelectedCategory(category)}
                        >
                            {category}
                        </li>
                    ))}
                </ul>
            </div>

            <div className="main-content">
                <h1>Product Management</h1>
                <div className="total-product-count">
                    <strong>{selectedCategory === "All Products" ? "Total Products:" : `Total Products in ${selectedCategory}:`}</strong> {categoryProductCount}
                </div>

                <div className="search-bar-container">
                    <input
                        type="text"
                        placeholder="Enter Product Code"
                        value={productCode}
                        onChange={(e) => setProductCode(e.target.value)}
                    />
                    <button onClick={fetchProductInfo}>Search</button>
                </div>

                <div className="products-by-category">
                    <h2>{selectedCategory === "All Products" ? "All Products" : `Products in ${selectedCategory}`}</h2>
                    <div className="product-list">
                        {products.map((prod) => (
                            <div key={prod.productId} className="product-card">
                                <h3>{prod.name}</h3>
                                <p><strong>Price:</strong> ${prod.price}</p>
                                <p><strong>Code:</strong> {prod.productCode}</p>
                                <p><strong>Stock Status:</strong> {prod.stockStatus}</p>
                                <p><strong>Category:</strong> {prod.category}</p>
                                <button
                                    className="details-button"
                                    onClick={() => navigateToProductDetail(prod.productId)}
                                >
                                    Details
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="pagination">
                    <button
                        onClick={() => handlePageChange("prev")}
                        disabled={currentPage === 0}
                    >
                        Previous
                    </button>
                    <span>Page {currentPage + 1} of {totalPages}</span>
                    <button
                        onClick={() => handlePageChange("next")}
                        disabled={currentPage === totalPages - 1}
                    >
                        Next
                    </button>
                </div>

                {user?.role === "ADMIN" && (
                    <div className="add-product-section">
                        <button onClick={toggleAddProductForm} className="add-product-button">
                            {showAddProductForm ? "Hide Add Product Form" : "Add New Product"}
                        </button>
                        {showAddProductForm && (
                            <div className="modal-overlay">
                                <div className="modal-content">
                                    <div className="modal-header">
                                        <h2>Add New Product</h2>
                                        <button className="modal-close-button" onClick={toggleAddProductForm}>
                                            &times;
                                        </button>
                                    </div>
                                    <div className="modal-body">
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
                                                {categories
                                                    .filter(category => category !== "All Products")
                                                    .map((category, index) => (
                                                        <option key={index} value={category}>
                                                            {category}
                                                        </option>
                                                    ))
                                                }
                                            </select>
                                            <div className="modal-footer">
                                                <button type="button" onClick={toggleAddProductForm}>
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
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProductPage;