package com.serhat.ecommerce.dto.response;

import java.time.LocalDateTime;

public record AddAddressResponse(
  String message,
  Long addressId,
  LocalDateTime time,
  String addressDescription
) {
}
