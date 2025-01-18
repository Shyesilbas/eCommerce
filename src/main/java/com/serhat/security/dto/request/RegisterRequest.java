package com.serhat.security.dto.request;

import com.serhat.security.entity.Address;
import com.serhat.security.entity.enums.Role;
import jakarta.validation.constraints.*;

import java.util.List;

public record RegisterRequest(
        @NotBlank(message = "Name cannot be empty")
                @Size(min = 2 , max = 20)
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*.]).{6,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
        )
        String password,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Role cannot be null")
        Role role,

        @Pattern(regexp = "^\\d{4} \\d{3} \\d{2} \\d{2}$", message = "Invalid phone number format. Expected format: xxxx xxx xx xx")
        String phone,

        List<Address> address
) {
}
