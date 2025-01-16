import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import "../style/ProductPage.css";

const ProductDetailPage = () => {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        axios.get(`http://localhost:8080/api/products/info/id/${productId}`)
            .then(response => {
                setProduct(response.data);
                setLoading(false);
            })
            .catch(() => {
                setError("Product not found or an error occurred.");
                setLoading(false);
            });
    }, [productId]);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div className="product-detail-page">
            <h1>{product.name}</h1>
            <div className="product-details">
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

            <div className="comments-section">
                <h2>Comments</h2>
            </div>
        </div>
    );
};

export default ProductDetailPage;