package com.serhat.ecommerce.user.address.service;

import com.serhat.ecommerce.user.address.enums.AddressType;
import com.serhat.ecommerce.user.address.dto.AddAddressRequest;
import com.serhat.ecommerce.user.address.dto.UpdateAddressRequest;
import com.serhat.ecommerce.dto.response.AddAddressResponse;
import com.serhat.ecommerce.dto.response.DeleteAddressResponse;
import com.serhat.ecommerce.dto.response.UpdateAddressResponse;
import com.serhat.ecommerce.user.address.entity.Address;
import com.serhat.ecommerce.user.address.mapper.AddressMapper;
import com.serhat.ecommerce.user.address.repository.AddressRepository;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressOperationsService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;
    private final AddressType.AddressValidationService addressValidationService;

    @Transactional
    public AddAddressResponse addAddress(AddAddressRequest addAddressRequest) {
        User user = userService.getAuthenticatedUser();
        Address newAddress = addressMapper.toAddress(addAddressRequest, user);

        addressRepository.save(newAddress);
        log.info("Address added for user: {}, address ID: {}", user.getUsername(), newAddress.getAddressId());

        return new AddAddressResponse(
                "Address added successfully",
                newAddress.getAddressId(),
                LocalDateTime.now(),
                newAddress.getDescription()
        );
    }

    @Transactional
    public UpdateAddressResponse updateAddress(Long addressId, UpdateAddressRequest updateAddressRequest) {
        User user = userService.getAuthenticatedUser();
        Address address = addressValidationService.findAndValidateAddress(addressId, user);

        addressMapper.updateAddressFromDto(address, updateAddressRequest);
        addressRepository.save(address);
        log.info("Address updated for user: {}, address ID: {}", user.getUsername(), addressId);

        return new UpdateAddressResponse(
                "Address updated successfully",
                address.getAddressId(),
                LocalDateTime.now(),
                address.getDescription()
        );
    }

    @Transactional
    @CacheEvict(value = "addressInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public DeleteAddressResponse deleteAddress(Long addressId) {
        User user = userService.getAuthenticatedUser();
        Address address = addressValidationService.findAndValidateAddress(addressId, user);

        addressRepository.delete(address);
        log.info("Address deleted for user: {}, address ID: {}", user.getUsername(), addressId);

        return new DeleteAddressResponse(
                addressId,
                "Address deleted successfully",
                LocalDateTime.now()
        );
    }
}