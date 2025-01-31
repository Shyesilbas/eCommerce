import React, { useEffect, useState } from "react";
import {
    getShoppingCardByUser,
    removeFromCard,
    increaseQuantity,
    decreaseQuantity,
    getTotalInfo,
} from "../utils/api";
import Swal from "sweetalert2";
import "../style/ShopingCard.css";

const ShoppingCardPage = ({ user }) => {
    const [shoppingCardItems, setShoppingCardItems] = useState([]);
    const [totalItems, setTotalItems] = useState(0);
    const [totalQuantity, setTotalQuantity] = useState(0);
    const [totalPrice, setTotalPrice] = useState(0);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        setIsAuthenticated(!!user || !!storedUser);
    }, [user]);

    useEffect(() => {
        if (isAuthenticated) {
            loadShoppingCard();
            loadTotalInfo();
        }
    }, [isAuthenticated]);

    const loadShoppingCard = async () => {
        try {
            const items = await getShoppingCardByUser();
            setShoppingCardItems(items);
        } catch (error) {
            console.error("Error loading shopping card:", error);
        }
    };

    const loadTotalInfo = async () => {
        try {
            const totalInfo = await getTotalInfo();
            setTotalItems(totalInfo.totalQuantity);
            setTotalQuantity(totalInfo.totalItems);
            setTotalPrice(totalInfo.totalPrice);
        } catch (error) {
            console.error("Error loading total info:", error);
        }
    };

    const handleRemoveFromCard = async (productId) => {
        try {
            await removeFromCard(productId);
            await loadShoppingCard();
            await loadTotalInfo();
            Swal.fire("Success", "Product removed from cart!", "success");
        } catch (error) {
            console.error("Error removing product from cart:", error);
            Swal.fire("Error", "Failed to remove product from cart.", "error");
        }
    };

    const handleIncreaseQuantity = async (productId) => {
        try {
            await increaseQuantity(productId);
            await loadShoppingCard();
            await loadTotalInfo();
        } catch (error) {
            console.error("Error increasing quantity:", error);
        }
    };

    const handleDecreaseQuantity = async (productId) => {
        try {
            await decreaseQuantity(productId);
            await loadShoppingCard();
            await loadTotalInfo();
        } catch (error) {
            console.error("Error decreasing quantity:", error);
        }
    };

    if (!isAuthenticated) {
        return (
            <div className="shopping-card-page">
                <h1>Shopping Cart</h1>
                <p>Please log in to view your shopping cart.</p>
            </div>
        );
    }

    return (
        <div className="shopping-card-page">
            <h1>Your Shopping Cart</h1>
            <div className="shopping-card-section">
                {shoppingCardItems.length > 0 ? (
                    shoppingCardItems.map((item) => (
                        <div key={item.productId} className="card-item">
                            <div className="card-item-header">
                                <h3>{item.name}</h3>
                            </div>
                            <div className="card-item-body">
                                <p><strong>Price:</strong> ${item.price}</p>
                                <p><strong>Product Code:</strong> {item.productCode}</p>
                                <p><strong>Category:</strong> {item.category}</p>
                                <p><strong>Quantity:</strong> {item.quantity}</p>
                                <div className="card-item-actions">
                                    <button onClick={() => handleIncreaseQuantity(item.productId)}>+</button>
                                    <button onClick={() => handleDecreaseQuantity(item.productId)}>-</button>
                                    <button onClick={() => handleRemoveFromCard(item.productId)}>Remove</button>
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <p>Your shopping cart is empty.</p>
                )}
            </div>

            {shoppingCardItems.length > 0 && (
                <div className="shopping-card-footer">
                    <div className="total-items">
                        <span>Total Products:</span>
                        <strong>{totalItems}</strong>
                    </div>
                    <div className="total-quantity">
                        <span>Total Quantity:</span>
                        <strong>{totalQuantity}</strong>
                    </div>
                    <div className="total-price">
                        <span>Total Price:</span>
                        <strong>${totalPrice.toFixed(2)}</strong>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ShoppingCardPage;
