import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import "../style/ProductPage.css";
import { fetchProductById } from "../utils/api.js";
import ProductDetails from "../components/product/ProductDetails";
import CommentsSection from "../components/product/CommentsSection";


const ProductDetailPage = () => {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadProduct = async () => {
            try {
                const product = await fetchProductById(productId);
                setProduct(product);
            } catch (error) {
                setError("Product not found or an error occurred.");
            } finally {
                setLoading(false);
            }
        };

        loadProduct();
    }, [productId]);


    return (
        <div className="product-detail-page">
            <ProductDetails product={product} />
            <CommentsSection />
        </div>
    );
};

export default ProductDetailPage;