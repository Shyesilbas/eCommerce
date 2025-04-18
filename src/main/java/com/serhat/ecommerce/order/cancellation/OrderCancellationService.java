package com.serhat.ecommerce.order.cancellation;

import com.serhat.ecommerce.dto.response.OrderCancellationResponse;



public interface OrderCancellationService {
    OrderCancellationResponse cancelOrder(Long orderId);

}