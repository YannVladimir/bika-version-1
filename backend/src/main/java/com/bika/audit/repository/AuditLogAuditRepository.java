package com.bika.audit.repository;

import com.bika.document.entity.AuditLog;
import com.bika.company.entity.Company;
import com.bika.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogAuditRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByCompany(Company company);
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByCompanyAndCreatedAtBetween(Company company, LocalDateTime start, LocalDateTime end);
} 