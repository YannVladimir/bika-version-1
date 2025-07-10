package com.bika.document.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.user.entity.User;
import com.bika.document.entity.AuditLog;
import com.bika.document.entity.Document;
import com.bika.document.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    public Optional<AuditLog> findById(Long id) {
        return auditLogRepository.findById(id);
    }

    public List<AuditLog> findByCompany(Company company) {
        return auditLogRepository.findByCompany(company);
    }

    public List<AuditLog> findByDepartment(Department department) {
        return auditLogRepository.findByDepartment(department);
    }

    public List<AuditLog> findByUser(User user) {
        return auditLogRepository.findByUser(user);
    }

    public List<AuditLog> findByDocument(Document document) {
        return auditLogRepository.findByDocument(document);
    }

    public List<AuditLog> findByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public void deleteById(Long id) {
        auditLogRepository.deleteById(id);
    }
} 