import { useState, useEffect } from "react";
import { getUserAddress } from "../utils/api.js";

const useAddressInfo = () => {
    const [address, setAddress] = useState([]);

    useEffect(() => {
        const fetchAddressInfo = async () => {
            try {
                const response = await getUserAddress();
                setAddress(response.data);
            } catch (err) {
                console.error("Error fetching address info:", err);
            }
        };

        fetchAddressInfo();
    }, []);

    return address;
};

export default useAddressInfo;
