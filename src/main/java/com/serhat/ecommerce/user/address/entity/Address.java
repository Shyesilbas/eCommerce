package com.serhat.ecommerce.user.address.entity;

import com.serhat.ecommerce.user.address.enums.AddressType;
import com.serhat.ecommerce.user.userS.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String country;
    private String city;
    private String street;
    private String aptNo;
    private String flatNo;
    private String description;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;


}
