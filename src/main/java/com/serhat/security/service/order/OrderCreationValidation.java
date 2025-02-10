package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddressNotBelongToUserException;
import com.serhat.security.interfaces.AddressInterface;
import com.serhat.security.interfaces.TokenInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreationValidation {
    private final AddressInterface addressInterface;
    private final TokenInterface tokenInterface;

    public void isAddressBelongsToUser(Long addressId, Long userId) {
        if (!addressInterface.isAddressBelongsToUser(addressId, userId)) {
            throw new AddressNotBelongToUserException("Shipping address does not belong to the user!");
        }
    }
    public User validateAndGetUser(HttpServletRequest request, OrderRequest orderRequest) {
        User user = tokenInterface.getUserFromToken(request);
        isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId());
        return user;
    }



}
