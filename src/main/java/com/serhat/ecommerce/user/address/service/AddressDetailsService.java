package com.serhat.ecommerce.user.address.service;

import com.serhat.ecommerce.dto.object.PageDTO;
import com.serhat.ecommerce.dto.response.AddressResponse;
import com.serhat.ecommerce.user.address.entity.Address;
import com.serhat.ecommerce.user.address.mapper.AddressMapper;
import com.serhat.ecommerce.user.address.repository.AddressRepository;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userException.addressException.AddressNotFoundException;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressDetailsService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;

    public PageDTO<AddressResponse> addressInfo(int page, int size) {
        User user = userService.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addresses = addressRepository.findByUser_Username(user.getUsername(), pageable);

        if (addresses.isEmpty()) {
            log.warn("No addresses found for user: {}", user.getUsername());
            throw new AddressNotFoundException("No addresses found for user: " + user.getUsername());
        }

        List<AddressResponse> addressResponses = addresses.stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());

        return new PageDTO<>(addressResponses, addresses.getNumber(), addresses.getSize(), (int) addresses.getTotalElements());
    }
}