package com.bika.company.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.entity.Company;
import com.bika.company.repository.CompanyRepository;
import com.bika.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = mapToEntity(companyDTO);
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
        existingCompany.setActive(companyDTO.isActive());
        
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
        return companyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .code(company.getCode())
                .email(company.getEmail())
                .isActive(company.isActive())
                .build();
    }

    private Company mapToEntity(CompanyDTO companyDTO) {
        return Company.builder()
                .id(companyDTO.getId())
                .name(companyDTO.getName())
                .code(companyDTO.getCode())
                .email(companyDTO.getEmail())
                .active(companyDTO.isActive())
                .build();
    }
} 