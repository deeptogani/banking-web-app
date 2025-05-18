package com.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banking.model.Beneficiary;
import com.banking.model.User;

import java.util.List;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    
    List<Beneficiary> findByCustomer(User customer);
    
    List<Beneficiary> findByCustomerUserId(Long customerId);
    
    List<Beneficiary> findByCustomerUserIdAndIsActiveTrue(Long customerId);
}