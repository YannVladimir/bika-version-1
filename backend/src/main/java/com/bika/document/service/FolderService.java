package com.bika.document.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.document.entity.Folder;
import com.bika.document.repository.FolderRepository;
import com.bika.document.dto.FolderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Folder> findAll() {
        return folderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Folder> findById(Long id) {
        return folderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Folder> findByCompany(Company company) {
        return folderRepository.findByCompany(company);
    }

    @Transactional(readOnly = true)
    public List<Folder> findByDepartment(Department department) {
        return folderRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Folder> findByParent(Folder parent) {
        return folderRepository.findByParent(parent);
    }

    @Transactional(readOnly = true)
    public List<Folder> findRootFoldersByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return folderRepository.findByCompany(company).stream()
                .filter(folder -> folder.getParent() == null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Folder> findByCompanyAndPath(Company company, String path) {
        return folderRepository.findByCompanyAndPath(company, path);
    }

    @Transactional
    public Folder save(Folder folder) {
        return folderRepository.save(folder);
    }

    @Transactional
    public FolderDTO createFolder(FolderDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElse(null);
        }

        Folder parent = null;
        if (dto.getParentId() != null) {
            parent = folderRepository.findById(dto.getParentId())
                    .orElse(null);
        }

        // Generate path
        String path = generatePath(parent, dto.getName());

        Folder folder = Folder.builder()
                .name(dto.getName())
                .path(path)
                .description(dto.getDescription())
                .company(company)
                .department(department)
                .parent(parent)
                .isActive(true)
                .createdBy("system") // TODO: Get from security context
                .updatedBy("system")
                .build();

        Folder saved = save(folder);
        return toDTOSimple(saved);
    }

    @Transactional
    public void deleteById(Long id) {
        folderRepository.deleteById(id);
    }

    private String generatePath(Folder parent, String name) {
        if (parent == null) {
            return "/" + name;
        }
        return parent.getPath() + "/" + name;
    }

    public FolderDTO toDTO(Folder folder) {
        if (folder == null) return null;

        List<FolderDTO> childrenDTOs = folder.getChildren().stream()
                .map(this::toDTOSimple)
                .collect(Collectors.toList());

        return FolderDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .path(folder.getPath())
                .description(folder.getDescription())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .companyId(folder.getCompany().getId())
                .departmentId(folder.getDepartment() != null ? folder.getDepartment().getId() : null)
                .children(childrenDTOs)
                .documents(new ArrayList<>()) // Will be populated separately
                .isActive(folder.isActive())
                .build();
    }

    public FolderDTO toDTOSimple(Folder folder) {
        if (folder == null) return null;

        return FolderDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .path(folder.getPath())
                .description(folder.getDescription())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .companyId(folder.getCompany().getId())
                .departmentId(folder.getDepartment() != null ? folder.getDepartment().getId() : null)
                .children(new ArrayList<>())
                .documents(new ArrayList<>())
                .isActive(folder.isActive())
                .build();
    }

    @Transactional(readOnly = true)
    public List<FolderDTO> getRootFoldersByCompany(Long companyId) {
        return findRootFoldersByCompany(companyId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FolderDTO> findAllDTOs() {
        return folderRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FolderDTO> findDTOById(Long id) {
        return folderRepository.findById(id).map(this::toDTO);
    }
} 