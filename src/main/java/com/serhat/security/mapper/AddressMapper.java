package com.serhat.security.mapper;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.UpdateAddressRequest;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddressNotFoundException;
import com.serhat.security.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public Address toAddress(AddressDto addressDto, User user) {
        return Address.builder()
                .country(addressDto.country())
                .city(addressDto.city())
                .street(addressDto.street())
                .aptNo(addressDto.aptNo())
                .flatNo(addressDto.flatNo())
                .description(addressDto.description())
                .addressType(addressDto.addressType())
                .user(user)
                .build();
    }

    public AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .aptNo(address.getAptNo())
                .flatNo(address.getFlatNo())
                .description(address.getDescription())
                .addressType(address.getAddressType())
                .build();
    }

    public List<AddressResponse> toAddressResponseList(List<Address> addresses) {
        return addresses.stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    public void updateAddressFromDto(Address address, UpdateAddressRequest dto) {
        if (dto.country() != null) {
            address.setCountry(dto.country());
        }
        if (dto.city() != null) {
            address.setCity(dto.city());
        }
        if (dto.street() != null) {
            address.setStreet(dto.street());
        }
        if (dto.aptNo() != null) {
            address.setAptNo(dto.aptNo());
        }
        if (dto.flatNo() != null) {
            address.setFlatNo(dto.flatNo());
        }
        if (dto.description() != null) {
            address.setDescription(dto.description());
        }
        if (dto.addressType() != null) {
            address.setAddressType(dto.addressType());
        }
    }
}
