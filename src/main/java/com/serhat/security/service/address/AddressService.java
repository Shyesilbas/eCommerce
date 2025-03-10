package com.serhat.security.service.address;

import com.serhat.security.dto.object.PageDTO;
import com.serhat.security.dto.request.AddAddressRequest;
import com.serhat.security.dto.request.UpdateAddressRequest;
import com.serhat.security.dto.response.AddAddressResponse;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.dto.response.DeleteAddressResponse;
import com.serhat.security.dto.response.UpdateAddressResponse;

public interface AddressService {
    PageDTO<AddressResponse> addressInfo( int page, int size);
    UpdateAddressResponse updateAddress(Long addressId,  UpdateAddressRequest updateAddressRequest);
    AddAddressResponse addAddress( AddAddressRequest addAddressRequest);
    DeleteAddressResponse deleteAddress(Long addressId);

    boolean isAddressBelongsToUser(Long addressId, Long userId);
}
