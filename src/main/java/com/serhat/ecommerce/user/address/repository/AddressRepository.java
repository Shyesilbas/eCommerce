package com.serhat.ecommerce.user.address.repository;

import com.serhat.ecommerce.user.address.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    Page<Address> findByUser_Username(String username , Pageable pageable);

    boolean existsByAddressIdAndUserUserId(Long addressId, Long userId);
}
