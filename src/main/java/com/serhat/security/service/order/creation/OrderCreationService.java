package com.serhat.security.service.order.creation;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderResponse;

public interface OrderCreationService {
    OrderResponse createOrder(OrderRequest orderRequest);
}
