package com.serhat.security.service;

import com.serhat.security.dto.object.PageDTO;
import com.serhat.security.dto.request.AddAddressRequest;
import com.serhat.security.dto.request.UpdateAddressRequest;
import com.serhat.security.dto.response.AddAddressResponse;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.dto.response.DeleteAddressResponse;
import com.serhat.security.dto.response.UpdateAddressResponse;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.NotificationTopic;
import com.serhat.security.exception.AddressNotFoundException;
import com.serhat.security.exception.UnauthorizedAccessException;
import com.serhat.security.interfaces.AddressInterface;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.AddressMapper;
import com.serhat.security.repository.AddressRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService implements AddressInterface {
    private final TokenInterface tokenInterface;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public boolean isAddressBelongsToUser(Long addressId, Long userId) {
        return addressRepository.existsByAddressIdAndUserUserId(addressId, userId);
    }

    // @Cacheable(value = "addressInfoCache", key = "#request.userPrincipal.name + ':page' + #page + ':size' + #size", unless = "#result == null")
    @Override
    public PageDTO<AddressResponse> addressInfo(HttpServletRequest request, int page, int size) {
        User user = tokenInterface.getUserFromToken(request);
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addresses = addressRepository.findByUser_Username(user.getUsername(), pageable);

        if (addresses.isEmpty()) {
            throw new RuntimeException("No addresses found for user: " + user.getUsername());
        }

        List<AddressResponse> addressResponses = addresses.stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());

        return new PageDTO<>(addressResponses, addresses.getNumber(), addresses.getSize(), (int) addresses.getTotalElements());
    }

    @Transactional
    @Override
    // @CacheEvict(value = "addressInfoCache", key = "#request.userPrincipal.name + ':page' + '0' + ':size' + '10'")
    public UpdateAddressResponse updateAddress(Long addressId, HttpServletRequest request, UpdateAddressRequest updateAddressRequest) {
        User user = tokenInterface.getUserFromToken(request);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new UnauthorizedAccessException("Address does not belong to the user: " + user.getUsername());
        }

        addressMapper.updateAddressFromDto(address, updateAddressRequest);
        addressRepository.save(address);
        return new UpdateAddressResponse(
                "Address updated successfully",
                address.getAddressId(),
                LocalDateTime.now(),
                address.getDescription()
        );
    }

    @Transactional
    @Override
    // @CacheEvict(value = "addressInfoCache", key = "#request.userPrincipal.name + ':page' + '0' + ':size' + '10'")
    public AddAddressResponse addAddress(HttpServletRequest request, AddAddressRequest addAddressRequest) {
        User user = tokenInterface.getUserFromToken(request);
        Address newAddress = addressMapper.toAddress(addAddressRequest, user);

        addressRepository.save(newAddress);
        return new AddAddressResponse(
                "Address added successfully",
                newAddress.getAddressId(),
                LocalDateTime.now(),
                newAddress.getDescription()
        );
    }

    @Transactional
    @Override
    @CacheEvict(value = "addressInfoCache", key = "#request.userPrincipal.name + ':page' + '0' + ':size' + '10'")
    public DeleteAddressResponse deleteAddress(Long addressId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new UnauthorizedAccessException("Address does not belong to the user: " + user.getUsername());
        }

        addressRepository.delete(address);

        return new DeleteAddressResponse(
                addressId,
                "Address Deleted Successfully",
                LocalDateTime.now()
        );
    }
}
