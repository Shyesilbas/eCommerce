import { useState } from "react";

const useMessage = () => {
    const [message, setMessage] = useState({ type: "", text: "" });

    const setErrorMessage = (text) => {
        setMessage({ type: "error", text });
    };

    const setSuccessMessage = (text) => {
        setMessage({ type: "success", text });
    };

    return { message, setErrorMessage, setSuccessMessage };
};

export default useMessage;
