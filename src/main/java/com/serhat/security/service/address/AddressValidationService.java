package com.serhat.security.service.address;

import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddressNotFoundException;
import com.serhat.security.exception.UnauthorizedAccessException;
import com.serhat.security.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressValidationService {
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