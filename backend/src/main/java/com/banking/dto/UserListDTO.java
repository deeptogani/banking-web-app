package com.banking.dto;

import com.banking.model.User;
import com.banking.model.Account;
import com.banking.model.CustomerDetails;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserListDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private boolean isActive;
    private List<AccountDTO> accounts;
    private CustomerDetailsDTO customerDetails;

    @Data
    public static class AccountDTO {
        private Long id;
        private String accountNumber;
        private String accountType;
        private Double balance;
        private boolean isActive;
    }

    @Data
    public static class CustomerDetailsDTO {
        private String dateOfBirth;
        private String aadharNumber;
        private String panNumber;
        private String occupation;
        private Double annualIncome;
    }

    public static UserListDTO fromEntity(User user) {
        if (user == null) return null;

        UserListDTO dto = new UserListDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setActive(user.getIsActive() != null ? user.getIsActive() : false);

        // Set customer details if available
        if (user.getCustomerDetails() != null) {
            CustomerDetails details = user.getCustomerDetails();
            CustomerDetailsDTO detailsDTO = new CustomerDetailsDTO();
            detailsDTO.setDateOfBirth(details.getDateOfBirth() != null ? details.getDateOfBirth().toString() : null);
            detailsDTO.setAadharNumber(details.getAadharNumber());
            detailsDTO.setPanNumber(details.getPanNumber());
            detailsDTO.setOccupation(details.getOccupation());
            detailsDTO.setAnnualIncome(details.getAnnualIncome() != null ? details.getAnnualIncome().doubleValue() : null);
            dto.setCustomerDetails(detailsDTO);
        }

        // Set accounts if available
        if (user.getAccounts() != null) {
            dto.setAccounts(user.getAccounts().stream()
                .filter(account -> account != null)
                .map(account -> {
                    AccountDTO accountDTO = new AccountDTO();
                    accountDTO.setId(account.getAccountId());
                    accountDTO.setAccountNumber(account.getAccountNumber());
                    accountDTO.setAccountType(account.getAccountType() != null ? account.getAccountType().name() : null);
                    accountDTO.setBalance(account.getBalance() != null ? account.getBalance().doubleValue() : 0.0);
                    accountDTO.setActive(account.getIsActive() != null ? account.getIsActive() : false);
                    return accountDTO;
                })
                .collect(Collectors.toList()));
        }

        return dto;
    }
} 