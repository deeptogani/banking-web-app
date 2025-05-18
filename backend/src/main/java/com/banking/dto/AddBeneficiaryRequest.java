package com.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBeneficiaryRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;
    
    @NotBlank(message = "Account number is required")
    @Size(max = 20, message = "Account number must be less than 20 characters")
    private String accountNumber;
    
    @Size(max = 20, message = "IFSC code must be less than 20 characters")
    private String ifscCode;
    
    private BigDecimal maxTransferLimit;
    
    @Size(max = 50, message = "Relationship must be less than 50 characters")
    private String relationship;
} 