package com.bika.document.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.user.entity.User;
import com.bika.document.entity.AuditLog;
import com.bika.document.entity.Document;
import com.bika.document.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        return auditLogService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByCompany(@PathVariable Long companyId) {
        Company company = new Company();
        company.setId(companyId);
        return ResponseEntity.ok(auditLogService.findByCompany(company));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByDepartment(@PathVariable Long departmentId) {
        Department department = new Department();
        department.setId(departmentId);
        return ResponseEntity.ok(auditLogService.findByDepartment(department));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return ResponseEntity.ok(auditLogService.findByUser(user));
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByDocument(@PathVariable Long documentId) {
        Document document = new Document();
        document.setId(documentId);
        return ResponseEntity.ok(auditLogService.findByDocument(document));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.findByAction(action));
    }

    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(@RequestBody AuditLog auditLog) {
        return ResponseEntity.ok(auditLogService.save(auditLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable Long id) {
        auditLogService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 
