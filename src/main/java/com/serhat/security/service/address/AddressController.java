package com.serhat.security.service.address;

import com.serhat.security.dto.object.PageDTO;
import com.serhat.security.dto.request.AddAddressRequest;
import com.serhat.security.dto.request.UpdateAddressRequest;
import com.serhat.security.dto.response.AddAddressResponse;
import com.serhat.security.dto.response.AddressResponse;
import com.serhat.security.dto.response.DeleteAddressResponse;
import com.serhat.security.dto.response.UpdateAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
@Slf4j
public class AddressController {
    private final AddressService addressService;
    @GetMapping("/addressInfo")
    public ResponseEntity<PageDTO<AddressResponse>> getAddressInfo(
            @RequestParam int page,
            @RequestParam int size) {
        PageDTO<AddressResponse> addressResponse = addressService.addressInfo( page, size);
        return ResponseEntity.ok(addressResponse);
    }

    @PutMapping("/update-address")
    public ResponseEntity<UpdateAddressResponse> updateAddress(
            @RequestParam Long addressId,
            @RequestBody UpdateAddressRequest updateAddressRequest
    ) {
        UpdateAddressResponse response = addressService.updateAddress(addressId, updateAddressRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-address")
    public ResponseEntity<AddAddressResponse> addAddress(@RequestBody AddAddressRequest request){
        return ResponseEntity.ok(addressService.addAddress(request));
    }

    @DeleteMapping("/delete-address")
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestParam Long addressId ){
        return ResponseEntity.ok(addressService.deleteAddress(addressId));
    }
}
