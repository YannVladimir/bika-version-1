package com.bika.document.repository;

import com.bika.company.entity.Company;
import com.bika.document.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    List<DocumentType> findByCompany(Company company);
    Optional<DocumentType> findByCompanyAndCode(Company company, String code);
}
 