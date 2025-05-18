package com.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCustomerDetailsRequest {

    @NotBlank(message = "Date of Birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Aadhar Number is required")
    @Size(max = 12, message = "Aadhar Number must be of 12 numbers")
    private String aadharNumber;

    @NotBlank(message = "Pan Number is required")
    @Size(max = 10, message = "Pan Number must be of 10 numbers")
    private String panNumber;

    @NotBlank(message = "Occupation is required")
    @Size(max = 50, message = "Occupation must be less than 100 characters")
    private String occupation;

    @NotBlank(message = "Annual Income is required")
    private BigDecimal annualIncome;
}
