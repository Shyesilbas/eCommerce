package com.serhat.ecommerce.user.address.dto;

import com.serhat.ecommerce.user.address.enums.AddressType;

public record UpdateAddressRequest(
        String country,
        String city,
        String street,
        String aptNo,
        String flatNo,
        String description,
        AddressType addressType
) {
}
