package com.bika.company.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.entity.Company;
import com.bika.company.repository.CompanyRepository;
import com.bika.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = mapToEntity(companyDTO);
        // Set auditing fields - in a real application, this would come from the security context
        company.setCreatedBy("system");
        company.setUpdatedBy("system");
        Company savedCompany = companyRepository.save(company);
        return mapToDTO(savedCompany);
    }

    @Override
    public CompanyDTO updateCompany(Long id, CompanyDTO companyDTO) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        
        existingCompany.setName(companyDTO.getName());
        existingCompany.setCode(companyDTO.getCode());
        existingCompany.setEmail(companyDTO.getEmail());
        existingCompany.setPhone(companyDTO.getPhone());
        existingCompany.setAddress(companyDTO.getAddress());
        existingCompany.setDescription(companyDTO.getDescription());
        existingCompany.setActive(companyDTO.isActive());
        // Set auditing field - in a real application, this would come from the security context
        existingCompany.setUpdatedBy("system");
        
        Company updatedCompany = companyRepository.save(existingCompany);
        return mapToDTO(updatedCompany);
    }

    @Override
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return mapToDTO(company);
    }

    @Override
    public List<CompanyDTO> getAllCompanies() {
        log.info("CompanyServiceImpl: getAllCompanies called - SERVICE ENTRY POINT");
        try {
            log.info("CompanyServiceImpl: About to call companyRepository.findAll()");
            List<Company> companies = companyRepository.findAll();
            log.info("CompanyServiceImpl: Found {} companies in database", companies.size());
            
            if (companies.isEmpty()) {
                log.info("CompanyServiceImpl: No companies found in database");
                return List.of();
            }
            
            log.info("CompanyServiceImpl: Converting {} entities to DTOs", companies.size());
            List<CompanyDTO> result = companies.stream()
                    .map(company -> {
                        try {
                            log.debug("CompanyServiceImpl: Converting company ID: {} to DTO", company.getId());
                            return mapToDTO(company);
                        } catch (Exception e) {
                            log.error("CompanyServiceImpl: Error converting company ID: {} to DTO", company.getId(), e);
                            throw new RuntimeException("Error converting company to DTO", e);
                        }
                    })
                    .collect(Collectors.toList());
            log.info("CompanyServiceImpl: Successfully converted {} companies to DTOs", result.size());
            return result;
        } catch (Exception e) {
            log.error("CompanyServiceImpl: Error in getAllCompanies - Exception: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving companies", e);
        }
    }

    @Override
    public CompanyDTO findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return mapToDTO(company);
    }

    @Override
    public Optional<Company> findEntityById(Long id) {
        return companyRepository.findById(id);
    }

    private CompanyDTO mapToDTO(Company company) {
        log.debug("CompanyServiceImpl: Mapping company with ID {} to DTO", company.getId());
        try {
            return CompanyDTO.builder()
                    .id(company.getId())
                    .name(company.getName())
                    .code(company.getCode())
                    .email(company.getEmail())
                    .phone(company.getPhone())
                    .address(company.getAddress())
                    .description(company.getDescription())
                    .isActive(company.isActive())
                    .build();
        } catch (Exception e) {
            log.error("CompanyServiceImpl: Error mapping company with ID {} to DTO", company.getId(), e);
            throw e;
        }
    }

    private Company mapToEntity(CompanyDTO companyDTO) {
        return Company.builder()
                .id(companyDTO.getId())
                .name(companyDTO.getName())
                .code(companyDTO.getCode())
                .email(companyDTO.getEmail())
                .phone(companyDTO.getPhone())
                .address(companyDTO.getAddress())
                .description(companyDTO.getDescription())
                .active(companyDTO.isActive())
                .build();
    }
} 