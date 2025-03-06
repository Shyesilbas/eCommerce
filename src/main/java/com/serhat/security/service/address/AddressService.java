package com.serhat.security.service.address;

import com.serhat.security.dto.object.PageDTO;
import com.serhat.security.dto.request.AddAddressRequest;
import com.serhat.security.dto.request.UpdateAddressRequest;
import com.serhat.security.dto.response.AddAddressResponse;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.dto.response.DeleteAddressResponse;
import com.serhat.security.dto.response.UpdateAddressResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AddressService {
    PageDTO<AddressResponse> addressInfo(HttpServletRequest request, int page, int size);
    UpdateAddressResponse updateAddress(Long addressId, HttpServletRequest request, UpdateAddressRequest updateAddressRequest);
    AddAddressResponse addAddress(HttpServletRequest request, AddAddressRequest addAddressRequest);
    DeleteAddressResponse deleteAddress(Long addressId, HttpServletRequest request);

    boolean isAddressBelongsToUser(Long addressId, Long userId);
}
