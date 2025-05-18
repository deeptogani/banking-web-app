package com.banking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banking.model.Account;
import com.banking.model.Beneficiary;
import com.banking.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    List<Transaction> findByFromAccount(Account account);
    
    List<Transaction> findByToAccount(Account account);
    
    List<Transaction> findByBeneficiary(Beneficiary beneficiary);
    
    Page<Transaction> findByFromAccountAccountId(Long accountId, Pageable pageable);
    
    Page<Transaction> findByToAccountAccountId(Long accountId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.user.userId = :userId OR t.toAccount.user.userId = :userId")
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING'")
    List<Transaction> findAllPendingTransactions();

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.fromAccount LEFT JOIN FETCH t.toAccount")
    Page<Transaction> findAllWithDetails(Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.fromAccount LEFT JOIN FETCH t.toAccount WHERE t.transactionId = :id")
    Optional<Transaction> findByIdWithDetails(@Param("id") Long id);
}