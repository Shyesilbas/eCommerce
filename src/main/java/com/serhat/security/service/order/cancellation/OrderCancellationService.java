package com.serhat.security.service.order.cancellation;

import com.serhat.security.dto.response.OrderCancellationResponse;



public interface OrderCancellationService {
    OrderCancellationResponse cancelOrder(Long orderId);

}