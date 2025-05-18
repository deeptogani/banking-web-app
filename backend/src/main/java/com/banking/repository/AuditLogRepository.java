package com.banking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banking.model.AuditLog;
import com.banking.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUser(User user);
    
    List<AuditLog> findByUserUserId(Long userId);
    
    List<AuditLog> findByEntity(String entity);
    
    List<AuditLog> findByEntityAndEntityId(String entity, Long entityId);
    
    List<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
}