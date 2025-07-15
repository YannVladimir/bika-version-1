package com.bika.document.service;

import com.bika.document.entity.DocumentField;
import com.bika.document.entity.DocumentType;
import com.bika.document.repository.DocumentFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentFieldService {

    private final DocumentFieldRepository documentFieldRepository;

    @Transactional(readOnly = true)
    public List<DocumentField> findAll() {
        return documentFieldRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DocumentField> findById(Long id) {
        return documentFieldRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DocumentField> findByDocumentType(DocumentType documentType) {
        return documentFieldRepository.findByDocumentTypeOrderByDisplayOrderAsc(documentType);
    }

    @Transactional(readOnly = true)
    public List<DocumentField> findActiveByDocumentType(DocumentType documentType) {
        return documentFieldRepository.findByDocumentTypeAndActiveOrderByDisplayOrderAsc(documentType, true);
    }

    @Transactional
    public DocumentField save(DocumentField documentField) {
        return documentFieldRepository.save(documentField);
    }

    @Transactional
    public void deleteById(Long id) {
        documentFieldRepository.deleteById(id);
    }

    @Transactional
    public void delete(DocumentField documentField) {
        documentFieldRepository.delete(documentField);
    }
} 