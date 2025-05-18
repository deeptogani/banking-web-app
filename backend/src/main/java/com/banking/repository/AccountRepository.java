package com.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banking.model.Account;
import com.banking.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUser(User user);
    
    List<Account> findByUserUserId(Long userId);
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUserUserIdAndIsActiveTrue(Long userId);
}