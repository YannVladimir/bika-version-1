package com.bika.company.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByCompany(Company company);
    List<Department> findByCompanyAndParentIsNull(Company company);
    List<Department> findByParent(Department parent);
    Optional<Department> findByCompanyAndCode(Company company, String code);
    boolean existsByCompanyAndCode(Company company, String code);
} 