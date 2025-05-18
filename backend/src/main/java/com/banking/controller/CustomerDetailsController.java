package com.banking.controller;

import com.banking.repository.RoleRepository;
import com.banking.repository.UserRepository;
import com.banking.service.JwtService;
import com.banking.service.UserService;
import com.banking.dto.UserRegistrationRequest;
import com.banking.model.Role;
import com.banking.model.User;
import com.banking.model.CustomerDetails;
import com.banking.dto.AddCustomerDetailsRequest;
import com.banking.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import com.banking.dto.UserLoginRequest;
import com.banking.repository.CustomerDetailsRepository;
import com.banking.service.CustomerDetailsService;
import com.banking.dto.CustomerDetailsResponse;

@RestController
@RequestMapping("/api/customer-details")
public class CustomerDetailsController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private RoleRepository roleRepository;

    //Add Customer Details Starts
    @PostMapping("/add")
    public ResponseEntity<?> addCustomerDetails(@Valid @RequestBody AddCustomerDetailsRequest request) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if customer details already exist
            CustomerDetails existingDetails = customerDetailsRepository.findByCustomer(user);
            
            CustomerDetails customerDetails;
            if (existingDetails != null) {
                // Update existing details
                customerDetails = existingDetails;
            } else {
                // Create new details
                customerDetails = new CustomerDetails();
                customerDetails.setCustomer(user);
            }

            // Set/update the details
            customerDetails.setDateOfBirth(request.getDateOfBirth());
            customerDetails.setAadharNumber(request.getAadharNumber());
            customerDetails.setPanNumber(request.getPanNumber());
            customerDetails.setOccupation(request.getOccupation());
            customerDetails.setAnnualIncome(request.getAnnualIncome());

            // Save the details
            customerDetailsRepository.save(customerDetails);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Customer details " + (existingDetails != null ? "updated" : "added") + " successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    private CustomerDetails addUserCustomerDetailsFromRequest(AddCustomerDetailsRequest request) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setDateOfBirth(request.getDateOfBirth());
        customerDetails.setAadharNumber(request.getAadharNumber());
        customerDetails.setPanNumber(request.getPanNumber());
        // customerDetails.setCustomerId(request.());
        customerDetails.setOccupation(request.getOccupation());
        customerDetails.setAnnualIncome(request.getAnnualIncome());
        return customerDetails;
    }
    // //Add Customer Details Ends

    @GetMapping
    public ResponseEntity<?> getCustomerDetails() {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Get customer details
            CustomerDetails customerDetails = customerDetailsRepository.findByCustomer(user);
            
            if (customerDetails == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Customer details not found"));
            }

            // Convert to DTO
            CustomerDetailsResponse response = new CustomerDetailsResponse();
            response.setCustomerId(customerDetails.getCustomerId());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setAddress(user.getAddress());
            response.setDateOfBirth(customerDetails.getDateOfBirth());
            response.setAadharNumber(customerDetails.getAadharNumber());
            response.setPanNumber(customerDetails.getPanNumber());
            response.setOccupation(customerDetails.getOccupation());
            response.setAnnualIncome(customerDetails.getAnnualIncome());
            response.setCreatedAt(customerDetails.getCreatedAt());
            response.setUpdatedAt(customerDetails.getUpdatedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }
}
