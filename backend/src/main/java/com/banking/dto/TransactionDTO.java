package com.banking.dto;

import com.banking.model.Transaction;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long transactionId;
    private String transactionReference;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String toBankName;
    private String transactionType;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
    private LocalDateTime transactionDate;

    public static TransactionDTO fromEntity(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionReference(transaction.getTransactionReference());
        
        // Set from account details
        if (transaction.getFromAccount() != null) {
            dto.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        }
        
        // Set to account details
        if (transaction.getToAccount() != null) {
            dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        } else if (transaction.getBeneficiary() != null) {
            dto.setToAccountNumber(transaction.getBeneficiary().getAccountNumber());
            dto.setToBankName(transaction.getBeneficiary().getBankName());
        }
        
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setStatus(transaction.getStatus().name());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
} 