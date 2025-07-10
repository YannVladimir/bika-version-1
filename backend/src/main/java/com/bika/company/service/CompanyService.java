package com.bika.company.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.entity.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
    CompanyDTO createCompany(CompanyDTO companyDTO);
    CompanyDTO updateCompany(Long id, CompanyDTO companyDTO);
    void deleteCompany(Long id);
    CompanyDTO getCompanyById(Long id);
    List<CompanyDTO> getAllCompanies();
    CompanyDTO findById(Long id);
    Optional<Company> findEntityById(Long id);
} 