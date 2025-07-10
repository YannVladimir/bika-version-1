package com.bika.company.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.entity.Company;
import com.bika.company.repository.CompanyRepository;
import com.bika.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company company;
    private CompanyDTO companyDTO;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .name("Test Company")
                .code("TEST")
                .email("test@company.com")
                .active(true)
                .build();

        companyDTO = CompanyDTO.builder()
                .id(1L)
                .name("Test Company")
                .code("TEST")
                .email("test@company.com")
                .isActive(true)
                .build();
    }

    @Test
    void createCompany_ShouldReturnSavedCompany() {
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyDTO result = companyService.createCompany(companyDTO);

        assertNotNull(result);
        assertEquals(companyDTO.getName(), result.getName());
        assertEquals(companyDTO.getCode(), result.getCode());
        assertEquals(companyDTO.getEmail(), result.getEmail());
        assertEquals(companyDTO.isActive(), result.isActive());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void updateCompany_ShouldReturnUpdatedCompany() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyDTO result = companyService.updateCompany(1L, companyDTO);

        assertNotNull(result);
        assertEquals(companyDTO.getName(), result.getName());
        assertEquals(companyDTO.getCode(), result.getCode());
        assertEquals(companyDTO.getEmail(), result.getEmail());
        assertEquals(companyDTO.isActive(), result.isActive());
        verify(companyRepository).findById(1L);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void updateCompany_ShouldThrowException_WhenCompanyNotFound() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.updateCompany(1L, companyDTO));
        verify(companyRepository).findById(1L);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void deleteCompany_ShouldDeleteCompany() {
        when(companyRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(companyRepository).deleteById(anyLong());

        companyService.deleteCompany(1L);

        verify(companyRepository).existsById(1L);
        verify(companyRepository).deleteById(1L);
    }

    @Test
    void deleteCompany_ShouldThrowException_WhenCompanyNotFound() {
        when(companyRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> companyService.deleteCompany(1L));
        verify(companyRepository).existsById(1L);
        verify(companyRepository, never()).deleteById(anyLong());
    }

    @Test
    void getCompanyById_ShouldReturnCompany() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));

        CompanyDTO result = companyService.getCompanyById(1L);

        assertNotNull(result);
        assertEquals(company.getName(), result.getName());
        assertEquals(company.getCode(), result.getCode());
        assertEquals(company.getEmail(), result.getEmail());
        assertEquals(company.isActive(), result.isActive());
        verify(companyRepository).findById(1L);
    }

    @Test
    void getCompanyById_ShouldThrowException_WhenCompanyNotFound() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompanyById(1L));
        verify(companyRepository).findById(1L);
    }

    @Test
    void getAllCompanies_ShouldReturnListOfCompanies() {
        List<Company> companies = Arrays.asList(company);
        when(companyRepository.findAll()).thenReturn(companies);

        List<CompanyDTO> result = companyService.getAllCompanies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(company.getName(), result.get(0).getName());
    }
} 