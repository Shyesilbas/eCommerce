package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderCreationInterface {
    OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest);
    PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards , User user , OrderRequest orderRequest);
}
