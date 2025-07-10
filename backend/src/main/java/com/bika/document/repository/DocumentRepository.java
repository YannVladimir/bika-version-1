package com.bika.document.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.document.entity.Document;
import com.bika.document.entity.DocumentType;
import com.bika.document.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCompany(Company company);
    List<Document> findByDepartment(Department department);
    List<Document> findByFolder(Folder folder);
    List<Document> findByDocumentType(DocumentType documentType);
    Optional<Document> findByIdAndCompany(Long id, Company company);
} 