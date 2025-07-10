package com.bika.company.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.dto.DepartmentDTO;
import com.bika.company.entity.Department;
import com.bika.company.repository.DepartmentRepository;
import com.bika.config.TestConfig;
import com.bika.config.TestAuditorAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import({TestConfig.class, TestAuditorAwareConfig.class})
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentRepository departmentRepository;

    private CompanyDTO companyDTO;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        // Create test company
        companyDTO = CompanyDTO.builder()
                .name("Test Company")
                .code("TEST")
                .email("company@test.com")
                .isActive(true)
                .build();
        companyDTO = companyService.createCompany(companyDTO);

        // Create test department
        departmentDTO = DepartmentDTO.builder()
                .name("Test Department")
                .code("TEST-DEPT")
                .companyId(companyDTO.getId())
                .isActive(true)
                .build();
    }

    @Test
    void createDepartment_ShouldCreateNewDepartment_WhenValidRequest() {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        assertNotNull(createdDepartment);
        assertEquals(departmentDTO.getName(), createdDepartment.getName());
        assertEquals(departmentDTO.getCode(), createdDepartment.getCode());
        assertEquals(departmentDTO.getCompanyId(), createdDepartment.getCompanyId());
    }

    @Test
    void getDepartmentById_ShouldReturnDepartment_WhenDepartmentExists() {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        DepartmentDTO foundDepartment = departmentService.getDepartmentById(createdDepartment.getId());
        assertNotNull(foundDepartment);
        assertEquals(createdDepartment.getId(), foundDepartment.getId());
    }

    @Test
    void getAllDepartments_ShouldReturnAllDepartments() {
        departmentService.createDepartment(departmentDTO);
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        assertFalse(departments.isEmpty());
    }

    @Test
    void getDepartmentsByCompany_ShouldReturnDepartments_WhenCompanyExists() {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        List<DepartmentDTO> departments = departmentService.getDepartmentsByCompany(createdDepartment.getCompanyId());
        assertFalse(departments.isEmpty());
        assertEquals(1, departments.size());
        assertEquals(createdDepartment.getId(), departments.get(0).getId());
    }

    @Test
    void deleteDepartment_ShouldDeleteDepartment_WhenDepartmentExists() {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        departmentService.deleteDepartment(createdDepartment.getId());
        assertTrue(departmentRepository.findById(createdDepartment.getId()).isEmpty());
    }
} 