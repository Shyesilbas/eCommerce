import React, { useState } from "react";

const ProductSearch = ({ onSearch }) => {
    const [productCode, setProductCode] = useState("");

    const handleSearch = () => {
        onSearch(productCode);
    };

    return (
        <div className="search-bar-container">
            <input
                type="text"
                placeholder="Enter Product Code"
                value={productCode}
                onChange={(e) => setProductCode(e.target.value)}
            />
            <button onClick={handleSearch}>Search</button>
        </div>
    );
};

export default ProductSearch;