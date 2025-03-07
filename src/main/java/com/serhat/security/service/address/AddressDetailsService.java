package com.serhat.security.service.address;

import com.serhat.security.dto.object.PageDTO;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.entity.Address;
import com.serhat.security.entity.User;
import com.serhat.security.exception.AddressNotFoundException;
import com.serhat.security.component.mapper.AddressMapper;
import com.serhat.security.repository.AddressRepository;
import com.serhat.security.service.user.UserService;
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