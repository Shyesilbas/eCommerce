import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getFavoritesByUser } from "../../utils/api";
import Swal from "sweetalert2";
import "../../style/FavoritesSection.css";

const FavoritesSection = () => {
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
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default FavoritesSection;