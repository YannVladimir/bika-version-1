package com.bika.company.service;

import com.bika.company.dto.DepartmentDTO;
import com.bika.company.entity.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);
    void deleteDepartment(Long id);
    DepartmentDTO getDepartmentById(Long id);
    List<DepartmentDTO> getAllDepartments();
    List<DepartmentDTO> getDepartmentsByCompany(Long companyId);
    Optional<Department> findEntityById(Long id);
} 