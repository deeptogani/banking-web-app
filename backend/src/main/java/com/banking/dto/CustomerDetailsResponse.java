package com.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CustomerDetailsResponse {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private String aadharNumber;
    private String panNumber;
    private String occupation;
    private BigDecimal annualIncome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 