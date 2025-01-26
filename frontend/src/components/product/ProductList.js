import React from "react";
import { addFavorite, removeFavorite, addToCard } from "../../utils/api";
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

    const handleAddToCard = async (productId) => {
        if (!user) {
            Swal.fire("Info", "Please login to add products to your shopping card.", "info");
            return;
        }

        try {
            await addToCard(productId);
            Swal.fire("Success", "Product added to your shopping card!", "success");
        } catch (error) {
            console.error("Error adding product to card:", error);
            Swal.fire("Error", "Failed to add product to card.", "error");
        }
    };

    return (
        <div className="product-list">
            {products.map((prod) => (
                <div key={prod.productId} className="product-card">
                    {/* Favorite Icon */}
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

                    <button
                        className="add-to-card-button"
                        onClick={() => handleAddToCard(prod.productId)}
                    >
                        Add to Card
                    </button>
                </div>
            ))}
        </div>
    );
};

export default ProductList;