package com.serhat.security.dto.response;

import com.serhat.security.entity.enums.AddressType;

import java.time.LocalDateTime;

public record AddAddressResponse(
  String message,
  Long addressId,
  LocalDateTime time,
  String addressDescription
) {
}
