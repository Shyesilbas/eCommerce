import axios from "axios";

const API_URL = "http://localhost:8080";

export const registerRequest = async (formData) => {
    try {
        const response = await axios.post(`${API_URL}/auth/register`, formData);
        return response.data;
    } catch (err) {
        throw err;
    }
};
export const loginRequest = async (formData) => {
    try {
        const loginResponse = await axios.post(`${API_URL}/auth/login`, formData, { withCredentials: true });

        const [userResponse, addressResponse] = await Promise.all([
            axios.get(`${API_URL}/user/myInfo`, { withCredentials: true }),
            axios.get(`${API_URL}/user/addressInfo`, { withCredentials: true })
        ]);

        return {
            userData: userResponse.data,
            addressData: addressResponse.data
        };
    } catch (err) {
        throw err;
    }
};

export const forgotPasswordRequest = async (formData) => {
    try {
        const response = await axios.post(`${API_URL}/user/forgot-password`, formData, { withCredentials: true });
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const updateEmailRequest = async (data) => {
    try {
        const response = await axios.post(`${API_URL}/user/update-Email`, data, { withCredentials: true });
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const updatePasswordRequest = async (data) => {
    try {
        const response = await axios.post(`${API_URL}/user/update-password`, data, { withCredentials: true });
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const getUserInfo = async () => {
    try {
        const response = await axios.get(`${API_URL}/user/myInfo`, { withCredentials: true });
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const getUserAddress = async () => {
    try {
        const response = await axios.get(`${API_URL}/user/addressInfo`, { withCredentials: true });
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const fetchTotalProductCount = async () => {
    try {
        const response = await axios.get(`${API_URL}/api/products/totalCount`);
        return response.data;
    } catch (error) {
        console.error("Error fetching total product count:", error);
        throw error;
    }
};

export const fetchCategories = async () => {
    try {
        const response = await axios.get(`${API_URL}/api/products/categories`);
        return response.data;
    } catch (error) {
        console.error("Error fetching categories:", error);
        throw error;
    }
};

export const fetchProducts = async (selectedCategory, currentPage, pageSize = 10) => {
    try {
        const endpoint = selectedCategory === "All Products" || !selectedCategory
            ? `${API_URL}/api/products/allProducts`
            : `${API_URL}/api/products/byCategory`;

        const params = selectedCategory === "All Products" || !selectedCategory
            ? { page: currentPage, size: pageSize }
            : { category: selectedCategory, page: currentPage, size: pageSize };

        const response = await axios.get(endpoint, { params });
        return response.data;
    } catch (error) {
        console.error("Error fetching products:", error);
        throw error;
    }
};

export const fetchProductById = async (productId) => {
    try {
        const response = await axios.get(`${API_URL}/api/products/info/id/${productId}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const fetchProductInfo = async (productCode) => {
    try {
        const response = await axios.get(`${API_URL}/api/products/info/${productCode}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching product info:", error);
        throw error;
    }
};

export const fetchCategoryProductCount = async (category) => {
    try {
        const response = await axios.get(`${API_URL}/api/products/totalCountByCategory?category=${category}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching category product count:", error);
        throw error;
    }
};

export const addProduct = async (productData) => {
    try {
        const response = await axios.post(`${API_URL}/api/products/addProduct`, productData, { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error("Error adding product:", error);
        throw error;
    }
};

export const logoutRequest = async () => {
    try {
        await axios.post(`${API_URL}/auth/logout`, {}, { withCredentials: true });
    } catch (err) {
        throw err;
    }
};