package com.banking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User user;
    
    @Column(name = "account_number", length = 20, nullable = false, unique = true)
    private String accountNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    
    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "opening_date", updatable = false)
    private LocalDateTime openingDate;
    
    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;
    
    @OneToMany(mappedBy = "fromAccount")
    private Set<Transaction> outgoingTransactions = new HashSet<>();
    
    @OneToMany(mappedBy = "toAccount")
    private Set<Transaction> incomingTransactions = new HashSet<>();
    
    public enum AccountType {
        SAVINGS, CURRENT
    }
    
    @PrePersist
    protected void onCreate() {
        this.openingDate = LocalDateTime.now();
        this.lastActivityDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.lastActivityDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId != null && accountId.equals(account.accountId);
    }
    
    @Override
    public int hashCode() {
        return 31;
    }
}