package com.banking.service;

import com.banking.model.CustomerDetails;
import com.banking.model.Role;
import com.banking.model.User;
import com.banking.repository.CustomerDetailsRepository;
import com.banking.repository.RoleRepository;
import com.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerDetails addCustomerDetails(Long userId, CustomerDetails details) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Parse date string to LocalDate
        if (details.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBirth = LocalDate.parse(details.getDateOfBirth().toString(), formatter);
            details.setDateOfBirth(dateOfBirth);
        }

        // Set the user
        details.setCustomer(user);

        // Save the details
        return customerDetailsRepository.save(details);
    }

    public CustomerDetails getCustomerDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return customerDetailsRepository.findByCustomer(user);
    }

    @Transactional
    public CustomerDetails updateCustomerDetails(Long userId, CustomerDetails updatedDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerDetails existingDetails = customerDetailsRepository.findByCustomer(user);
        if (existingDetails == null) {
            throw new RuntimeException("Customer details not found");
        }

        // Update fields
        existingDetails.setAadharNumber(updatedDetails.getAadharNumber());
        existingDetails.setPanNumber(updatedDetails.getPanNumber());
        existingDetails.setOccupation(updatedDetails.getOccupation());
        existingDetails.setAnnualIncome(updatedDetails.getAnnualIncome());

        // Parse and update date of birth if provided
        if (updatedDetails.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBirth = LocalDate.parse(updatedDetails.getDateOfBirth().toString(), formatter);
            existingDetails.setDateOfBirth(dateOfBirth);
        }

        return customerDetailsRepository.save(existingDetails);
    }

    @Transactional
    public User addCustomerDetailsForUser(User user, CustomerDetails details) {
        // Set the CUSTOMER role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role CUSTOMER not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        
        // Save the user
        User savedUser = userRepository.save(user);
        
        // Set customer details
        details.setCustomer(savedUser);
        
        // Parse date of birth if provided
        if (details.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBirth = LocalDate.parse(details.getDateOfBirth().toString(), formatter);
            details.setDateOfBirth(dateOfBirth);
        }
        
        // Save customer details
        customerDetailsRepository.save(details);
        
        return savedUser;
    }

    @Transactional
    public User registerAdmin(User user) {
        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set the ADMIN role
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);
        
        // Save the user
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateUserDetails(Long userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                    user.setAddress(updatedUser.getAddress());
                    // Don't update sensitive fields like username, email, or password here
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }

    @Transactional
    public User changePassword(Long userId, String newPassword) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }
}