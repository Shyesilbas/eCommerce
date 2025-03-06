package com.serhat.security.service.order.creation;

import com.serhat.security.service.address.AddressService;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddressNotBelongToUserException;
import com.serhat.security.service.payment.CheckPaymentMethodInterface;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreationValidation implements CheckPaymentMethodInterface {
    private final AddressService addressService;
    private final UserService userService;

    public void isAddressBelongsToUser(Long addressId, Long userId) {
        if (!addressService.isAddressBelongsToUser(addressId, userId)) {
            throw new AddressNotBelongToUserException("Shipping address does not belong to the user!");
        }
    }
    public User validateAndGetUser(OrderRequest orderRequest) {
        User user = userService.getAuthenticatedUser();
        isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId());
        return user;
    }



}
