import React from "react";

const ProductList = ({ products, onProductClick }) => {
    return (
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
                        onClick={() => onProductClick(prod.productId)}
                    >
                        Details
                    </button>
                </div>
            ))}
        </div>
    );
};

export default ProductList;