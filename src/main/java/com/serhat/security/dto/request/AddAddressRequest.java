package com.serhat.security.dto.request;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.entity.enums.AddressType;

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
