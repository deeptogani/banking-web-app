package com.banking.service;

import com.banking.dto.TransferRequest;
import com.banking.exception.InsufficientBalanceException;
import com.banking.model.*;
import com.banking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Transaction transferToBeneficiary(Long userId, TransferRequest request) {
        // Get user's account
        List<Account> userAccounts = accountRepository.findByUserUserId(userId);
        if (userAccounts.isEmpty()) {
            throw new RuntimeException("No accounts found for the user");
        }
        Account fromAccount = userAccounts.get(0); // Using first account for now

        // Get beneficiary
        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));

        // Validate transfer amount
        BigDecimal currentBalance = fromAccount.getBalance();
        BigDecimal transferAmount = request.getAmount();
        
        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalanceException("Account has zero or negative balance");
        }
        
        if (transferAmount.compareTo(currentBalance) > 0) {
            throw new InsufficientBalanceException(
                String.format("Insufficient balance. Current balance: %s, Transfer amount: %s", 
                    currentBalance, transferAmount)
            );
        }

        if (beneficiary.getMaxTransferLimit() != null && 
            transferAmount.compareTo(beneficiary.getMaxTransferLimit()) > 0) {
            throw new RuntimeException("Transfer amount exceeds maximum limit for this beneficiary");
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setFromAccount(fromAccount);
        transaction.setBeneficiary(beneficiary);
        transaction.setAmount(transferAmount);
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(request.getDescription());

        try {
            // Update account balance
            fromAccount.setBalance(currentBalance.subtract(transferAmount));
            accountRepository.save(fromAccount);

            // Save transaction
            transaction = transactionRepository.save(transaction);

            // Create audit log
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(fromAccount.getUser());
            auditLog.setAction("TRANSFER");
            auditLog.setEntity("TRANSACTION");
            auditLog.setEntityId(transaction.getTransactionId());
            auditLog.setNewValue(String.format("Transfer of %s to beneficiary %s", 
                transferAmount, beneficiary.getName()));
            auditLogRepository.save(auditLog);

            // Update transaction status to COMPLETED
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            return transaction;
        } catch (Exception e) {
            // If any error occurs, mark transaction as FAILED
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }

    public List<Transaction> getTransactionHistory(Long userId, int page, int size) {
        return transactionRepository.findByUserId(userId, 
            org.springframework.data.domain.PageRequest.of(page, size)).getContent();
    }
} 