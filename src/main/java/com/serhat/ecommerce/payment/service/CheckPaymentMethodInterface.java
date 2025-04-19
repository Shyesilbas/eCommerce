package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.payment.enums.PaymentMethod;
import com.serhat.ecommerce.payment.paymentException.InvalidPaymentMethodException;
import com.serhat.ecommerce.order.orderException.OrderCancellationException;

import java.util.Optional;
import java.util.Set;

public interface CheckPaymentMethodInterface {
    Set<PaymentMethod> CANCELLABLE_METHODS = Set.of(PaymentMethod.E_WALLET, PaymentMethod.ONLINE);

    default void checkPaymentMethod(Order order) {
        PaymentMethod paymentMethod = Optional.ofNullable(order.getPaymentMethod())
                .orElseThrow(() -> new InvalidPaymentMethodException("Payment method cannot be null"));

        if (!CANCELLABLE_METHODS.contains(paymentMethod)) {
            throw new OrderCancellationException(
                    String.format("Only %s payment methods are allowed.",
                            CANCELLABLE_METHODS)
            );
        }
    }
}
