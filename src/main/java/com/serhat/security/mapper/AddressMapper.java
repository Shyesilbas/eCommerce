package com.serhat.security.mapper;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.exception.AddressNotFoundException;
import com.serhat.security.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressMapper {
    private final AddressRepository addressRepository;

    public AddressDto toAddressDto(Long addressId) {
        return addressRepository.findById(addressId)
                .map(address -> new AddressDto(
                        address.getAddressId(),
                        address.getCountry(),
                        address.getCity(),
                        address.getStreet(),
                        address.getAptNo(),
                        address.getFlatNo(),
                        address.getDescription(),
                        address.getAddressType()))
                .orElseThrow(() -> new AddressNotFoundException("Address not found for ID: " + addressId));
    }
}
