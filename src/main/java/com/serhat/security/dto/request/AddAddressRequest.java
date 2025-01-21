package com.serhat.security.dto.request;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.entity.enums.AddressType;

public record AddAddressRequest(
    AddressDto addressDto
) {
}
