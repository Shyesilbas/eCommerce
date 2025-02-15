package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.AddressType;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record AddressResponse(
        Long addressId,
        String country,
        String city,
        String street,
        String aptNo,
        String flatNo,
        String description,
        AddressType addressType
) implements Serializable  {
}
