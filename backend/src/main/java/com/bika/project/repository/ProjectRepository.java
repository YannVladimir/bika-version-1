package com.bika.project.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCompany(Company company);
    List<Project> findByDepartment(Department department);
    List<Project> findByCompanyAndDepartment(Company company, Department department);
    boolean existsByCompanyAndCode(Company company, String code);
} 