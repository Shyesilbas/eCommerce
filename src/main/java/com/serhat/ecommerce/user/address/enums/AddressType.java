package com.serhat.ecommerce.user.address.enums;

import com.serhat.ecommerce.auth.authException.UnauthorizedAccessException;
import com.serhat.ecommerce.user.address.entity.Address;
import com.serhat.ecommerce.user.address.repository.AddressRepository;
import com.serhat.ecommerce.user.userException.addressException.AddressNotFoundException;
import com.serhat.ecommerce.user.userS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public enum AddressType {
    HOME , WORK , OTHER;

    @Service
    @RequiredArgsConstructor
    public static class AddressValidationService {
        private final AddressRepository addressRepository;

        @Transactional(readOnly = true)
        public boolean isAddressBelongsToUser(Long addressId, Long userId) {
            return addressRepository.existsByAddressIdAndUserUserId(addressId, userId);
        }

        @Transactional(readOnly = true)
        public Address findAndValidateAddress(Long addressId, User user) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));
            if (!address.getUser().getUsername().equals(user.getUsername())) {
                throw new UnauthorizedAccessException("Address does not belong to the user: " + user.getUsername());
            }
            return address;
        }
    }
}
