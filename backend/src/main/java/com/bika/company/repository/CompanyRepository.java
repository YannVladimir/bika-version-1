package com.bika.company.repository;

import com.bika.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCode(String code);
    Optional<Company> findByEmail(String email);
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
} 