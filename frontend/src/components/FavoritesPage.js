import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getFavoritesByUser, removeFavorite, addToCard } from "../utils/api";
import Swal from "sweetalert2";
import "../style/FavoritesPage.css";

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

const FavoritesPage = ({ user }) => {
    const navigate = useNavigate();
    const [favorites, setFavorites] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchFavorites = async () => {
        try {
            const favoritesData = await getFavoritesByUser();
            setFavorites(favoritesData);
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

    const handleAddToCart = async (productId) => {
        try {
            await addToCard(productId);
            Swal.fire("Success", "Product added to the cart!", "success");
        } catch (error) {
            Swal.fire("Error", "Failed to add product to the cart.", "error");
        }
    };

    useEffect(() => {
        fetchFavorites();
    }, []);

    if (loading) {
        return <div>Loading favorites...</div>;
    }

    return (
        <div className="favorites-page-container">
            <h1 className="favorites-page-title">My Favorites</h1>
            <button
                className="favorites-page-products-button"
                onClick={() => navigate("/products")}
            >
                Go to Products
            </button>
            {favorites.length === 0 ? (
                <p>No favorites found. Start adding some!</p>
            ) : (
                <div className="favorites-grid-container">
                    {favorites.map((favorite) => (
                        <div key={favorite.productCode} className="favorite-card-container">
                            <h3 className="favorite-card-title">{favorite.name}</h3>
                            <p className="favorite-card-detail">{favorite.description}</p>
                            <p className="favorite-card-detail">Price: ${favorite.price}</p>
                            <p className="favorite-card-detail">Brand: {favorite.brand}</p>
                            <p className="favorite-card-detail">Color: {favorite.color}</p>
                            <p className="favorite-card-detail">Category: {CATEGORY_DISPLAY_NAMES[favorite.category] || favorite.category}</p>
                            <p className="favorite-card-detail">Added on: {new Date(favorite.favorite_since).toLocaleDateString()}</p>

                            <button
                                className="favorite-card-remove-button"
                                onClick={() => handleRemoveFavorite(favorite.productId)}
                            >
                                Remove from Favorites
                            </button>
                            <button
                                className="favorite-card-add-button"
                                onClick={() => handleAddToCart(favorite.productId)}
                            >
                                Add to Cart
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default FavoritesPage;
