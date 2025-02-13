package com.serhat.security.service.payment;

import com.serhat.security.entity.Order;


public abstract class PaymentService<T> {
    public abstract T processPayment(Order order);

    protected void logPayment(Order order){
        System.out.println("order id : " +order.getOrderId() + ", total paid :" + order.getTotalPaid() + " order payment done.");
    }

}
