package com.serhat.ecommerce.order.creation;

import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.dto.response.OrderResponse;

public interface OrderCreationService {
    OrderResponse createOrder(OrderRequest orderRequest);
}
