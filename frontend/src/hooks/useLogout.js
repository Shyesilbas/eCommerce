import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { logoutRequest } from "../utils/api.js";

const useLogout = (onLogout) => {
    const navigate = useNavigate();

    const handleLogout = async () => {
        const confirmation = await Swal.fire({
            title: "Are you sure?",
            text: "Do you really want to log out?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, log out!",
        });

        if (confirmation.isConfirmed) {
            Swal.fire({
                title: "Logging out...",
                timer: 500,
                timerProgressBar: false,
                willClose: async () => {
                    try {
                        await logoutRequest();
                        localStorage.removeItem("user");
                        onLogout();
                        Swal.fire("Logged Out", "You have successfully logged out.", "success");
                        navigate("/login");
                    } catch (err) {
                        console.error("Logout error:", err);
                        Swal.fire("Error", "An error occurred while logging out.", "error");
                    }
                }
            });
        }
    };

    return handleLogout;
};

export default useLogout;
