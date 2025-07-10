package com.bika.document.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.document.entity.Folder;
import com.bika.document.service.FolderService;
import com.bika.document.dto.FolderDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public ResponseEntity<List<FolderDTO>> getAllFolders() {
        return ResponseEntity.ok(folderService.findAllDTOs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderDTO> getFolderById(@PathVariable Long id) {
        return folderService.findDTOById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Folder>> getFoldersByCompany(@PathVariable Long companyId) {
        Company company = new Company();
        company.setId(companyId);
        return ResponseEntity.ok(folderService.findByCompany(company));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Folder>> getFoldersByDepartment(@PathVariable Long departmentId) {
        Department department = new Department();
        department.setId(departmentId);
        return ResponseEntity.ok(folderService.findByDepartment(department));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Folder>> getFoldersByParent(@PathVariable Long parentId) {
        Folder parent = new Folder();
        parent.setId(parentId);
        return ResponseEntity.ok(folderService.findByParent(parent));
    }

    @GetMapping("/company/{companyId}/path/{path}")
    public ResponseEntity<Folder> getFolderByCompanyAndPath(
            @PathVariable Long companyId,
            @PathVariable String path) {
        Company company = new Company();
        company.setId(companyId);
        Optional<Folder> folder = folderService.findByCompanyAndPath(company, path);
        return folder.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Folder> createFolder(@Valid @RequestBody Folder folder) {
        // Set required auditing fields
        folder.setCreatedBy("system");
        folder.setUpdatedBy("system");
        Folder savedFolder = folderService.save(folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFolder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(
            @PathVariable Long id,
            @Valid @RequestBody Folder folder) {
        Optional<Folder> existingFolder = folderService.findById(id);
        if (existingFolder.isPresent()) {
            folder.setId(id);
            folder.setUpdatedBy("system");
            Folder updatedFolder = folderService.save(folder);
            return ResponseEntity.ok(updatedFolder);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        Optional<Folder> folder = folderService.findById(id);
        if (folder.isPresent()) {
            folderService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Folder> updateFolderStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        Optional<Folder> folderOpt = folderService.findById(id);
        if (folderOpt.isPresent()) {
            Folder folder = folderOpt.get();
            folder.setActive(active);
            folder.setUpdatedBy("system");
            Folder updatedFolder = folderService.save(folder);
            return ResponseEntity.ok(updatedFolder);
        }
        return ResponseEntity.notFound().build();
    }
} 