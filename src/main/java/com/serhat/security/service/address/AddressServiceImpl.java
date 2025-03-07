package com.serhat.security.service.address;

import com.serhat.security.dto.object.PageDTO;
import com.serhat.security.dto.request.AddAddressRequest;
import com.serhat.security.dto.request.UpdateAddressRequest;
import com.serhat.security.dto.response.AddAddressResponse;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.dto.response.DeleteAddressResponse;
import com.serhat.security.dto.response.UpdateAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressOperationsService addressOperationsService;
    private final AddressValidationService addressValidationService;
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