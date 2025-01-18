import React from "react";

const ProductDetails = ({ product }) => {
    if (!product) {
        return <div className="no-product">No product details available.</div>;
    }

    return (
        <div className="product-details">
            <h3>{product.name}</h3>
            <div className="product-info">
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
        </div>
    );
};

export default ProductDetails;