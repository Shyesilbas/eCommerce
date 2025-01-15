package com.serhat.security.dto.request;

import com.serhat.security.entity.Address;
import com.serhat.security.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterRequest(
        @NotBlank(message = "Name cannot be empty")
                @Size(min = 2 , max = 20)
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Role cannot be null")
        Role role,

        List<Address> address
) {
}
