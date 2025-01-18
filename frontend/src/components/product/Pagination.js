import React from "react";

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
    return (
        <div className="pagination">
            <button
                onClick={() => onPageChange("prev")}
                disabled={currentPage === 0}
            >
                Previous
            </button>
            <span>Page {currentPage + 1} of {totalPages}</span>
            <button
                onClick={() => onPageChange("next")}
                disabled={currentPage === totalPages - 1}
            >
                Next
            </button>
        </div>
    );
};

export default Pagination;