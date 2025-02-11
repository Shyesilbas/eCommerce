package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;

import java.math.BigDecimal;

public interface GiftCardInterface {
    BigDecimal applyGiftCard(OrderRequest orderRequest, BigDecimal totalPrice);

}
