package com.banking.controller;

import com.banking.dto.AddBeneficiaryRequest;
import com.banking.dto.ApiResponse;
import com.banking.model.Account;
import com.banking.model.Beneficiary;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.BeneficiaryRepository;
import com.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @GetMapping("/balance")
    public ResponseEntity<?> getAccountBalance() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Get user's accounts
            List<Account> accounts = accountRepository.findByUser(user);
            
            if (accounts.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "No accounts found for the user"));
            }

            // Create response with account balances
            Map<String, BigDecimal> balances = accounts.stream()
                    .collect(Collectors.toMap(
                            Account::getAccountNumber,
                            Account::getBalance
                    ));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Account balances retrieved successfully",
                    "balances", balances
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/beneficiaries")
    public ResponseEntity<?> addBeneficiary(@Valid @RequestBody AddBeneficiaryRequest request) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Create new beneficiary
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setCustomer(user);
            beneficiary.setName(request.getName());
            beneficiary.setBankName(request.getBankName());
            beneficiary.setAccountNumber(request.getAccountNumber());
            beneficiary.setIfscCode(request.getIfscCode());
            beneficiary.setMaxTransferLimit(request.getMaxTransferLimit());
            beneficiary.setRelationship(request.getRelationship());
            beneficiary.setIsActive(true);

            // Save beneficiary
            beneficiaryRepository.save(beneficiary);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Beneficiary added successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/beneficiaries")
    public ResponseEntity<?> getBeneficiaries() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Get user's beneficiaries
            List<Beneficiary> beneficiaries = beneficiaryRepository.findByCustomer(user);
            
            if (beneficiaries.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "No beneficiaries found for the user"));
            }

            // Create response with beneficiaries
            List<Map<String, Object>> beneficiaryList = beneficiaries.stream()
                    .map(b -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("beneficiaryId", b.getBeneficiaryId());
                        map.put("name", b.getName());
                        map.put("bankName", b.getBankName());
                        map.put("accountNumber", b.getAccountNumber());
                        map.put("ifscCode", b.getIfscCode());
                        map.put("maxTransferLimit", b.getMaxTransferLimit());
                        map.put("relationship", b.getRelationship());
                        map.put("isActive", b.getIsActive());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Beneficiaries retrieved successfully",
                    "beneficiaries", beneficiaryList
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }
} 