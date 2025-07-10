package com.bika.document.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.document.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByCompany(Company company);
    List<Folder> findByDepartment(Department department);
    List<Folder> findByParent(Folder parent);
    Optional<Folder> findByCompanyAndPath(Company company, String path);
} 