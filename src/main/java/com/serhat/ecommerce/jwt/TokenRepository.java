package com.serhat.ecommerce.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {

    Optional<Token> findByUsername(String username);

    Optional<Token> findByToken(String token);

    void deleteByExpiresAtBefore(Date expiresAt);


}
