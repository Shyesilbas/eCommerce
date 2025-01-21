package com.serhat.security.dto.object;

import com.serhat.security.entity.enums.AddressType;

public record AddressDto(
        Long addressId,
        String country,
        String city,
        String street,
        String aptNo,
        String flatNo,
        String description,
        AddressType addressType
) {}
