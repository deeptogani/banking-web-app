package com.banking.controller;

import com.banking.dto.TransferRequest;
import com.banking.exception.InsufficientBalanceException;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.service.TransferService;
import com.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/beneficiary")
    public ResponseEntity<?> transferToBeneficiary(@Valid @RequestBody TransferRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Transaction transaction = transferService.transferToBeneficiary(user.getUserId(), request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Transfer initiated successfully",
                "transactionId", transaction.getTransactionId(),
                "transactionReference", transaction.getTransactionReference()
            ));
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Transaction> transactions = transferService.getTransactionHistory(user.getUserId(), page, size);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Transaction history retrieved successfully",
                "transactions", transactions
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
} 