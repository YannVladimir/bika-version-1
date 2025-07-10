package com.bika.document.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.user.entity.User;
import com.bika.document.entity.AuditLog;
import com.bika.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByCompany(Company company);
    List<AuditLog> findByDepartment(Department department);
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByDocument(Document document);
    List<AuditLog> findByAction(String action);
} 