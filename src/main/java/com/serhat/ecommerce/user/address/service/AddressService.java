package com.serhat.ecommerce.user.address.service;

import com.serhat.ecommerce.dto.object.PageDTO;
import com.serhat.ecommerce.user.address.dto.AddAddressRequest;
import com.serhat.ecommerce.user.address.dto.UpdateAddressRequest;
import com.serhat.ecommerce.dto.response.AddAddressResponse;
import com.serhat.ecommerce.dto.response.AddressResponse;
import com.serhat.ecommerce.dto.response.DeleteAddressResponse;
import com.serhat.ecommerce.dto.response.UpdateAddressResponse;

public interface AddressService {
    PageDTO<AddressResponse> addressInfo( int page, int size);
    UpdateAddressResponse updateAddress(Long addressId,  UpdateAddressRequest updateAddressRequest);
    AddAddressResponse addAddress( AddAddressRequest addAddressRequest);
    DeleteAddressResponse deleteAddress(Long addressId);

    boolean isAddressBelongsToUser(Long addressId, Long userId);
}
