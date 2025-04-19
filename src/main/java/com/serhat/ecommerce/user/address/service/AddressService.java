package com.serhat.ecommerce.user.address.service;

import com.serhat.ecommerce.config.PageDTO;
import com.serhat.ecommerce.user.address.dto.request.AddAddressRequest;
import com.serhat.ecommerce.user.address.dto.request.UpdateAddressRequest;
import com.serhat.ecommerce.user.address.dto.response.AddAddressResponse;
import com.serhat.ecommerce.user.address.dto.response.AddressResponse;
import com.serhat.ecommerce.user.address.dto.response.DeleteAddressResponse;
import com.serhat.ecommerce.user.address.dto.response.UpdateAddressResponse;

public interface AddressService {
    PageDTO<AddressResponse> addressInfo( int page, int size);
    UpdateAddressResponse updateAddress(Long addressId,  UpdateAddressRequest updateAddressRequest);
    AddAddressResponse addAddress( AddAddressRequest addAddressRequest);
    DeleteAddressResponse deleteAddress(Long addressId);

    boolean isAddressBelongsToUser(Long addressId, Long userId);
}
