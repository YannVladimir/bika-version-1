package com.bika.document.controller;

import com.bika.company.entity.Company;
import com.bika.document.entity.DocumentType;
import com.bika.document.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/document-types")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @Autowired
    public DocumentTypeController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentType>> getAllDocumentTypes() {
        return ResponseEntity.ok(documentTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentType> getDocumentTypeById(@PathVariable Long id) {
        Optional<DocumentType> documentType = documentTypeService.findById(id);
        return documentType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DocumentType>> getDocumentTypesByCompany(@PathVariable Long companyId) {
        Company company = new Company();
        company.setId(companyId);
        return ResponseEntity.ok(documentTypeService.findByCompany(company));
    }

    @GetMapping("/company/{companyId}/code/{code}")
    public ResponseEntity<DocumentType> getDocumentTypeByCompanyAndCode(@PathVariable Long companyId, @PathVariable String code) {
        Company company = new Company();
        company.setId(companyId);
        Optional<DocumentType> documentType = documentTypeService.findByCompanyAndCode(company, code);
        return documentType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DocumentType> createDocumentType(@RequestBody DocumentType documentType) {
        return ResponseEntity.ok(documentTypeService.save(documentType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentType(@PathVariable Long id) {
        documentTypeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 