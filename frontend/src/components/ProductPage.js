import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../style/ProductPage.css";
import {
    fetchTotalProductCount,
    fetchCategories,
    fetchProducts,
    fetchProductInfo,
    fetchCategoryProductCount,
    addProduct,
} from "../utils/api.js";
import CategoriesSection from "../components/product/CategoriesSection.js";
import ProductSearch from "../components/product/ProductSearch.js";
import ProductList from "../components/product/ProductList.js";
import Pagination from "../components/product/Pagination.js";
import AddProductForm from "../components/product/AddProductForm.js";
import ProductDetails from "../components/product/ProductDetails.js";

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
    const [selectedCategory, setSelectedCategory] = useState(() => {
        // Read selectedCategory from localStorage on initial render
        const savedCategory = localStorage.getItem("selectedCategory");
        return savedCategory || "All Products";
    });
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
        GROCERY: "Grocery",
    };

    const getDisplayNameFromEnum = (enumValue) => {
        return CATEGORY_DISPLAY_NAMES[enumValue] || enumValue;
    };

    const getEnumKeyFromDisplayName = (displayName) => {
        return Object.entries(CATEGORY_DISPLAY_NAMES)
            .find(([key, value]) => value === displayName)?.[0] || displayName;
    };

    // Save selectedCategory to localStorage whenever it changes
    useEffect(() => {
        localStorage.setItem("selectedCategory", selectedCategory);
    }, [selectedCategory]);

    useEffect(() => {
        const loadTotalProductCount = async () => {
            try {
                const count = await fetchTotalProductCount();
                setTotalProductCount(count);
            } catch (error) {
                console.error("Error loading total product count:", error);
            }
        };

        loadTotalProductCount();
    }, []);

    useEffect(() => {
        const loadCategories = async () => {
            try {
                const categories = await fetchCategories();
                const enumCategories = categories.map(category => CATEGORY_DISPLAY_NAMES[category] || category);
                setCategories(["All Products", ...enumCategories]);
            } catch (error) {
                console.error("Error loading categories:", error);
                setCategories(["All Products"]);
            }
        };

        loadCategories();
    }, []);

    useEffect(() => {
        const loadProducts = async () => {
            try {
                const response = await fetchProducts(
                    selectedCategory === "All Products" ? null : getEnumKeyFromDisplayName(selectedCategory),
                    currentPage
                );

                if (response && Array.isArray(response.content)) {
                    const productsWithDisplayNames = response.content.map(prod => ({
                        ...prod,
                        category: getDisplayNameFromEnum(prod.category),
                    }));
                    setProducts(productsWithDisplayNames);
                    setTotalPages(response.totalPages);
                }
            } catch (error) {
                console.error("Error loading products:", error);
                setProducts([]);
            }
        };

        loadProducts();
    }, [currentPage, selectedCategory]);

    useEffect(() => {
        const loadCategoryProductCount = async () => {
            if (selectedCategory !== "All Products") {
                try {
                    const count = await fetchCategoryProductCount(getEnumKeyFromDisplayName(selectedCategory));
                    setCategoryProductCount(count);
                } catch (error) {
                    console.error("Error loading category product count:", error);
                    setCategoryProductCount(0);
                }
            } else {
                setCategoryProductCount(totalProductCount);
            }
        };

        loadCategoryProductCount();
    }, [selectedCategory, totalProductCount]);

    const handleSearchProduct = async (productCode) => {
        if (!productCode) {
            setError("Please enter a product code.");
            return;
        }

        try {
            const product = await fetchProductInfo(productCode);
            const productWithDisplayName = {
                ...product,
                category: getDisplayNameFromEnum(product.category),
            };
            setProduct(productWithDisplayName);
            setError("");
        } catch (error) {
            console.error("Error fetching product info:", error);
            setProduct(null);
            setError("Product not found or an error occurred.");
        }
    };

    const handleAddProduct = async (e) => {
        e.preventDefault();
        const productData = {
            ...newProduct,
            category: getEnumKeyFromDisplayName(newProduct.category),
        };

        try {
            const response = await addProduct(productData);
            Swal.fire("Success", response.message, "success");
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
        } catch (error) {
            Swal.fire("Error", "Failed to add product. Only ADMIN users can add products.", "error");
        }
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
            <CategoriesSection
                categories={categories}
                selectedCategory={selectedCategory}
                onCategorySelect={setSelectedCategory}
            />

            <div className="main-content">
                <h1>Product Management</h1>
                <div className="total-product-count">
                    <strong>{selectedCategory === "All Products" ? "Total Products:" : `Total Products in ${selectedCategory}:`}</strong> {categoryProductCount}
                </div>

                <ProductSearch onSearch={handleSearchProduct} />

                <div className="product-info-section">
                    {error && <p className="error-message">{error}</p>}
                    {product && <ProductDetails product={product} />}
                </div>

                <div className="products-by-category">
                    <h2>{selectedCategory === "All Products" ? "All Products" : `Products in ${selectedCategory}`}</h2>
                    <ProductList products={products} onProductClick={(id) => navigate(`/product/${id}`)} />
                </div>

                <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                />

                {user?.role === "ADMIN" && (
                    <div className="add-product-section">
                        <button onClick={toggleAddProductForm} className="add-product-button">
                            {showAddProductForm ? "Hide Add Product Form" : "Add New Product"}
                        </button>
                        {showAddProductForm && (
                            <AddProductForm
                                newProduct={newProduct}
                                onFormChange={handleNewProductChange}
                                onSubmit={handleAddProduct}
                                onCancel={toggleAddProductForm}
                            />
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProductPage;