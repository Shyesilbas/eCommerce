package com.serhat.ecommerce.user.userS.dto;

import jakarta.validation.constraints.Pattern;

public record UpdatePhoneRequest(
        @Pattern(regexp = "^\\d{4} \\d{3} \\d{2} \\d{2}$", message = "Invalid phone number format. Expected format: xxxx xxx xx xx")

        String phone
) {
}
