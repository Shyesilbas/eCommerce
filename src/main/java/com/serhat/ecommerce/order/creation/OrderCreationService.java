package com.serhat.ecommerce.order.creation;

import com.serhat.ecommerce.order.dto.request.OrderRequest;
import com.serhat.ecommerce.order.dto.response.OrderResponse;

public interface OrderCreationService {
    OrderResponse createOrder(OrderRequest orderRequest);
}
