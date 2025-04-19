package com.serhat.ecommerce.jwt.entity;

import com.serhat.ecommerce.jwt.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expired_at;

    @PrePersist
    private void initToken(){
        this.createdAt=Date.from(Instant.now());
    }


}
