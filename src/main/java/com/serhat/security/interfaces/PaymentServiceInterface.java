package com.serhat.security.interfaces;


import com.serhat.security.entity.*;

import java.util.List;

public interface PaymentServiceInterface {
    List<Transaction> createOrderTransactions(Order order);


}
