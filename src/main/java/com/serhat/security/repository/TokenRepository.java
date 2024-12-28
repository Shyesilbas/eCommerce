package com.serhat.security.repository;

import com.serhat.security.entity.Token;
import com.serhat.security.entity.enums.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {

    Optional<Token> findByUsername(String username);

    Optional<Token> findByToken(String token);

}
