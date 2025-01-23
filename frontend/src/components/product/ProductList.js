import React from "react";
import { addFavorite, removeFavorite } from "../../utils/api";
import Swal from "sweetalert2";
import "../../style/ProductList.css";

const ProductList = ({ products, onProductClick, user }) => {
    const handleFavoriteClick = async (productId, isFavorite) => {
        if (!user) {
            Swal.fire("Info", "Please login to add favorites.", "info");
            return;
        }

        try {
            if (isFavorite) {
                await removeFavorite(productId);
                Swal.fire("Success", "Product removed from favorites!", "success");
            } else {
                await addFavorite(productId);
                Swal.fire("Success", "Product added to favorites!", "success");
            }
        } catch (error) {
            Swal.fire("Error", "Failed to update favorites.", "error");
        }
    };

    return (
        <div className="product-list">
            {products.map((prod) => (
                <div key={prod.productId} className="product-card">
                    <div
                        className={`favorite-icon ${prod.isFavorite ? "favorited" : ""}`}
                        onClick={() => handleFavoriteClick(prod.productId, prod.isFavorite)}
                    >
                        {prod.isFavorite ? "❤️" : "🤍"}
                    </div>

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