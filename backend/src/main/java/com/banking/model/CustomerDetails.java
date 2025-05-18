package com.banking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "customer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetails {
    
    @Id
    @Column(name = "customer_id")
    private Long customerId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "customer_id")
    private User customer;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;
    
    @Column(name = "pan_number", length = 20)
    private String panNumber;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "annual_income", precision = 15, scale = 2)
    private BigDecimal annualIncome;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDetails that = (CustomerDetails) o;
        return customerId != null && customerId.equals(that.customerId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}