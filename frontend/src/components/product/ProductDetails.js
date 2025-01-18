import React from "react";

const ProductDetails = ({ product }) => {
    return (
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
    );
};

export default ProductDetails;