package com.banking.controller;

import com.banking.model.User;
import com.banking.model.Transaction;
import com.banking.repository.UserRepository;
import com.banking.repository.TransactionRepository;
import com.banking.dto.ApiResponse;
import com.banking.dto.TransactionDTO;
import com.banking.dto.UserListDTO;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllCustomerUsers() {
        try {
            logger.info("Fetching all customer users");
            List<User> users = userRepository.findAllCustomers();
            List<UserListDTO> userDTOs = users.stream()
                    .map(UserListDTO::fromEntity)
                    .collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching customer users", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Error fetching users: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        try {
            User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> userDetails = Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "phoneNumber", user.getPhoneNumber(),
                "address", user.getAddress(),
                "isActive", user.getIsActive(),
                "createdAt", user.getCreatedAt(),
                "roles", user.getRoles()
            );

            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getPaginatedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
            Page<Transaction> transactions = transactionRepository.findAllWithDetails(pageable);
            
            List<TransactionDTO> transactionDTOs = transactions.getContent().stream()
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactionDTOs);
            response.put("currentPage", transactions.getNumber());
            response.put("totalItems", transactions.getTotalElements());
            response.put("totalPages", transactions.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionDetails(@PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }
} 