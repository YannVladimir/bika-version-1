package com.bika.document.repository;

import com.bika.document.entity.DocumentField;
import com.bika.document.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFieldRepository extends JpaRepository<DocumentField, Long> {
    List<DocumentField> findByDocumentTypeOrderByDisplayOrderAsc(DocumentType documentType);
    List<DocumentField> findByDocumentTypeAndActiveOrderByDisplayOrderAsc(DocumentType documentType, boolean active);
} 