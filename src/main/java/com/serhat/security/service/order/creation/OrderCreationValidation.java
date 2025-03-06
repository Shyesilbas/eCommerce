package com.serhat.security.service.order.creation;

import com.serhat.security.service.address.AddressService;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddressNotBelongToUserException;
import com.serhat.security.service.payment.CheckPaymentMethodInterface;
import com.serhat.security.jwt.TokenInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreationValidation implements CheckPaymentMethodInterface {
    private final AddressService addressService;
    private final TokenInterface tokenInterface;

    public void isAddressBelongsToUser(Long addressId, Long userId) {
        if (!addressService.isAddressBelongsToUser(addressId, userId)) {
            throw new AddressNotBelongToUserException("Shipping address does not belong to the user!");
        }
    }
    public User validateAndGetUser(HttpServletRequest request, OrderRequest orderRequest) {
        User user = tokenInterface.getUserFromToken(request);
        isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId());
        return user;
    }



}
