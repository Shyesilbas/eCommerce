package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FinalizeOrderInterface {
    PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest);
    void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request);
}
