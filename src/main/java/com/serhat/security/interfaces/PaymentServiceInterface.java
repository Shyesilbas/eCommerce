package com.serhat.security.interfaces;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;

import java.util.List;

public interface PaymentServiceInterface {
    PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest);
    List<Transaction> createOrderTransactions(Order order);


}
