package com.serhat.security.repository;

import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {


    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);
}
