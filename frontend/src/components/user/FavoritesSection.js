import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getFavoritesByUser, removeFavorite, addToCard } from "../../utils/api";
import Swal from "sweetalert2";
import "../../style/FavoritesSection.css";

const FavoritesSection = () => {
    const navigate = useNavigate();
    const [favorites, setFavorites] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchFavorites = async () => {
        try {
            const favoritesData = await getFavoritesByUser();
            console.log("Favorites Data:", favoritesData); // Log the data

            setFavorites(Array.isArray(favoritesData) ? favoritesData : []);
        } catch (error) {
            Swal.fire("Error", "Failed to fetch favorites.", "error");
        } finally {
            setLoading(false);
        }
    };

    const handleRemoveFavorite = async (productId) => {
        const confirmation = await Swal.fire({
            title: "Are you sure?",
            text: "Do you want to remove this product from favorites?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, remove it!",
        });

        if (confirmation.isConfirmed) {
            try {
                await removeFavorite(productId);
                Swal.fire("Success", "Product removed from favorites!", "success");
                fetchFavorites();
            } catch (error) {
                Swal.fire("Error", "Failed to remove product from favorites.", "error");
            }
        }
    };

    const handleAddToCard = async (productId) => {
        try {
            await addToCard(productId);
            Swal.fire("Success", "Product added to card!", "success");
        } catch (error) {
            Swal.fire("Error", "Failed to add product to card.", "error");
        }
    };

    useEffect(() => {
        fetchFavorites();
    }, []);

    if (loading) {
        return <div>Loading favorites...</div>;
    }

    return (
        <div className="favorites-section">
            <h2>Favorites</h2>
            <button
                className="go-to-products-button"
                onClick={() => navigate("/products")}
            >
                Go to Products
            </button>
            {favorites.length === 0 ? (
                <p>No favorites found. Start adding some!</p>
            ) : (
                <div className="favorites-list">
                    {favorites.map((favorite) => (
                        <div key={favorite.productCode} className="favorite-item">
                            <h3>{favorite.name}</h3>
                            <p>{favorite.description}</p>
                            <p>Price: ${favorite.price}</p>
                            <p>Added on: {new Date(favorite.favorite_since).toLocaleDateString()}</p>
                            <div className="favorite-item-buttons">
                                <button
                                    className="favorite-card-remove-button"
                                    onClick={() => handleRemoveFavorite(favorite.productId)}
                                >
                                    Remove from Favorites
                                </button>
                                <button
                                    className="favorite-card-add-to-card-button"
                                    onClick={() => handleAddToCard(favorite.productId)}
                                >
                                    Add to Card
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default FavoritesSection;