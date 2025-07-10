package com.bika.document.service;

import com.bika.company.entity.Company;
import com.bika.document.entity.DocumentType;
import com.bika.document.repository.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public DocumentTypeService(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DocumentType> findById(Long id) {
        return documentTypeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DocumentType> findByCompany(Company company) {
        return documentTypeRepository.findByCompany(company);
    }

    @Transactional(readOnly = true)
    public Optional<DocumentType> findByCompanyAndCode(Company company, String code) {
        return documentTypeRepository.findByCompanyAndCode(company, code);
    }

    @Transactional
    public DocumentType save(DocumentType documentType) {
        return documentTypeRepository.save(documentType);
    }

    @Transactional
    public void deleteById(Long id) {
        documentTypeRepository.deleteById(id);
    }
} 