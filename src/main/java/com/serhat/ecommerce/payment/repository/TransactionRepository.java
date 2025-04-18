package com.serhat.ecommerce.payment.repository;

import com.serhat.ecommerce.payment.entity.Transaction;
import com.serhat.ecommerce.wallet.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {


    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);
}
