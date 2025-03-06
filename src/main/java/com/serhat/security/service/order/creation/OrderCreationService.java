package com.serhat.security.service.order.creation;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderCreationService {
    OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest);
}
