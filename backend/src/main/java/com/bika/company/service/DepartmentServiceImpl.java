package com.bika.company.service;

import com.bika.company.dto.DepartmentDTO;
import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.DepartmentRepository;
import com.bika.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyService companyService;

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Company company = companyService.findEntityById(departmentDTO.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Department department = Department.builder()
                .name(departmentDTO.getName())
                .code(departmentDTO.getCode())
                .company(company)
                .active(departmentDTO.isActive())
                .build();

        if (departmentDTO.getParentId() != null) {
            Department parent = departmentRepository.findById(departmentDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found"));
            department.setParent(parent);
        }

        Department savedDepartment = departmentRepository.save(department);
        return mapToDTO(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        department.setName(departmentDTO.getName());
        department.setCode(departmentDTO.getCode());
        department.setActive(departmentDTO.isActive());

        if (departmentDTO.getParentId() != null) {
            Department parent = departmentRepository.findById(departmentDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found"));
            department.setParent(parent);
        }

        Department updatedDepartment = departmentRepository.save(department);
        return mapToDTO(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found");
        }
        departmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return mapToDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getDepartmentsByCompany(Long companyId) {
        Company company = companyService.findEntityById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return departmentRepository.findByCompany(company).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Department> findEntityById(Long id) {
        return departmentRepository.findById(id);
    }

    private DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .companyId(department.getCompany().getId())
                .parentId(department.getParent() != null ? department.getParent().getId() : null)
                .isActive(department.isActive())
                .build();
    }
} 