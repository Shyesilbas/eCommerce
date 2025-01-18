package com.serhat.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*.]).{6,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
        )
        String newPassword,
        String oldPassword,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email
) {
}
