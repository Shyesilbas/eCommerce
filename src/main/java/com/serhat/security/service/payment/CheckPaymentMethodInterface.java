package com.serhat.security.service.payment;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.exception.InvalidPaymentMethodException;
import com.serhat.security.exception.OrderCancellationException;

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
