package com.serhat.ecommerce.user.address.service;

import com.serhat.ecommerce.config.PageDTO;
import com.serhat.ecommerce.user.address.enums.AddressType;
import com.serhat.ecommerce.user.address.dto.request.AddAddressRequest;
import com.serhat.ecommerce.user.address.dto.request.UpdateAddressRequest;
import com.serhat.ecommerce.user.address.dto.response.AddAddressResponse;
import com.serhat.ecommerce.user.address.dto.response.AddressResponse;
import com.serhat.ecommerce.user.address.dto.response.DeleteAddressResponse;
import com.serhat.ecommerce.user.address.dto.response.UpdateAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressOperationsService addressOperationsService;
    private final AddressType.AddressValidationService addressValidationService;
    private final AddressDetailsService addressDetailsService;

    @Override
    public boolean isAddressBelongsToUser(Long addressId, Long userId) {
        return addressValidationService.isAddressBelongsToUser(addressId, userId);
    }

    @Override
    public PageDTO<AddressResponse> addressInfo(int page, int size) {
        return addressDetailsService.addressInfo(page, size);
    }

    @Override
    public UpdateAddressResponse updateAddress(Long addressId, UpdateAddressRequest updateAddressRequest) {
        return addressOperationsService.updateAddress(addressId, updateAddressRequest);
    }

    @Override
    public AddAddressResponse addAddress(AddAddressRequest addAddressRequest) {
        return addressOperationsService.addAddress(addAddressRequest);
    }

    @Override
    public DeleteAddressResponse deleteAddress(Long addressId) {
        return addressOperationsService.deleteAddress(addressId);
    }
}