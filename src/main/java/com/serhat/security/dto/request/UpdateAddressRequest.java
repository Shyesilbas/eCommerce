package com.serhat.security.dto.request;

import com.serhat.security.entity.enums.AddressType;

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
