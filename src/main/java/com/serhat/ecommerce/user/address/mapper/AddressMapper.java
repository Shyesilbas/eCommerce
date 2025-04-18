package com.serhat.ecommerce.user.address.mapper;

import com.serhat.ecommerce.user.address.entity.Address;
import com.serhat.ecommerce.user.address.repository.AddressRepository;
import com.serhat.ecommerce.user.address.dto.AddressDto;
import com.serhat.ecommerce.user.address.dto.AddAddressRequest;
import com.serhat.ecommerce.user.address.dto.UpdateAddressRequest;
import com.serhat.ecommerce.dto.response.AddressResponse;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userException.addressException.AddressNotFoundException;
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

    public Address toAddress(AddAddressRequest addAddressRequest, User user) {
        return Address.builder()
                .country(addAddressRequest.country())
                .city(addAddressRequest.city())
                .street(addAddressRequest.street())
                .aptNo(addAddressRequest.aptNo())
                .flatNo(addAddressRequest.flatNo())
                .description(addAddressRequest.description())
                .addressType(addAddressRequest.addressType())
                .user(user)
                .build();
    }

    public AddAddressRequest toAddAddressRequest(AddressDto addressDto) {
        return new AddAddressRequest(
                addressDto.country(),
                addressDto.city(),
                addressDto.street(),
                addressDto.aptNo(),
                addressDto.flatNo(),
                addressDto.description(),
                addressDto.addressType()
        );
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
