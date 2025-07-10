package com.bika.document.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.document.entity.Folder;
import com.bika.document.repository.FolderRepository;
import com.bika.document.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private final FolderRepository folderRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

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
    public Optional<Folder> findByCompanyAndPath(Company company, String path) {
        return folderRepository.findByCompanyAndPath(company, path);
    }

    @Transactional
    public Folder save(Folder folder) {
        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteById(Long id) {
        folderRepository.deleteById(id);
    }

    public FolderDTO toDTO(Folder folder) {
        if (folder == null) return null;
        return FolderDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .path(folder.getPath())
                .isActive(folder.isActive())
                .build();
    }

    public List<FolderDTO> findAllDTOs() {
        return folderRepository.findAll().stream().map(this::toDTO).toList();
    }

    public Optional<FolderDTO> findDTOById(Long id) {
        return folderRepository.findById(id).map(this::toDTO);
    }
} 