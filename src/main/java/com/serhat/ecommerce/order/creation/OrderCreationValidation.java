package com.serhat.ecommerce.order.creation;

import com.serhat.ecommerce.user.address.service.AddressService;
import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userException.addressException.AddressNotBelongToUserException;
import com.serhat.ecommerce.payment.service.CheckPaymentMethodInterface;
import com.serhat.ecommerce.user.userS.service.UserService;
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
