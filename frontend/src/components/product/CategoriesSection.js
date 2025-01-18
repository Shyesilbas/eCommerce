import React from "react";

const CategoriesSection = ({ categories, selectedCategory, onCategorySelect }) => {
    return (
        <div className="categories-section">
            <h3>Categories</h3>
            <ul>
                {categories.map((category, index) => (
                    <li
                        key={index}
                        className={selectedCategory === category ? "active" : ""}
                        onClick={() => onCategorySelect(category)}
                    >
                        {category}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default CategoriesSection;