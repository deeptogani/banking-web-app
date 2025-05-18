package com.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banking.model.CustomerDetails;
import com.banking.model.User;

@Repository
public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {
    
    CustomerDetails findByCustomer(User customer);
    
    // The ID is the same as the User ID due to @MapsId in the entity
    // Additional custom queries can be added here if needed
}