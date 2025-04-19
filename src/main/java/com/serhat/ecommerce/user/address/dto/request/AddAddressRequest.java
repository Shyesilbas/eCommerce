package com.serhat.ecommerce.user.address.dto.request;

import com.serhat.ecommerce.user.address.enums.AddressType;

public record AddAddressRequest(
        String country,
        String city,
        String street,
        String aptNo,
        String flatNo,
        String description,
        AddressType addressType
) {
}
