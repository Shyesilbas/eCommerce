import React from "react";
import { addFavorite, removeFavorite, addToCard } from "../../utils/api";
import Swal from "sweetalert2";
import "../../style/ProductList.css";

const ProductList = ({ products, onProductClick, user, setProducts }) => {
    const handleFavoriteClick = async (productId, isFavorite) => {
        if (!user) {
            Swal.fire("Info", "Please login to add favorites.", "info");
            return;
        }

        try {
            if (isFavorite) {
                await removeFavorite(productId);
                const favorites = JSON.parse(localStorage.getItem("favorites")) || [];
                const updatedFavorites = favorites.filter(id => id !== productId);
                localStorage.setItem("favorites", JSON.stringify(updatedFavorites));
            } else {
                await addFavorite(productId);
                const favorites = JSON.parse(localStorage.getItem("favorites")) || [];
                favorites.push(productId);
                localStorage.setItem("favorites", JSON.stringify(favorites));
            }

            setProducts(prevProducts =>
                prevProducts.map(prod =>
                    prod.productId === productId ? { ...prod, isFavorite: !isFavorite } : prod
                )
            );
            Swal.fire("Success", isFavorite ? "Product removed from favorites!" : "Product added to favorites!", "success");
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
                    <div
                        className={`favorite-icon ${prod.isFavorite ? "favorited" : ""}`}
                        onClick={() => handleFavoriteClick(prod.productId, prod.isFavorite)}
                    >
                        {prod.isFavorite ? "❤️" : "🤍"}
                    </div>

                    <h3>{prod.name}</h3>
                    <p><strong>Price:</strong> ${prod.price.toFixed(2)}</p>
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