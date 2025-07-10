package com.bika.project.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.service.CompanyService;
import com.bika.company.service.DepartmentService;
import com.bika.common.exception.ResourceNotFoundException;
import com.bika.project.dto.ProjectDTO;
import com.bika.project.entity.Project;
import com.bika.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CompanyService companyService;
    private final DepartmentService departmentService;

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Company company = companyService.findEntityById(projectDTO.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Project project = Project.builder()
                .name(projectDTO.getName())
                .code(projectDTO.getCode())
                .description(projectDTO.getDescription())
                .company(company)
                .startDate(projectDTO.getStartDate())
                .endDate(projectDTO.getEndDate())
                .status(Project.ProjectStatus.valueOf(projectDTO.getStatus()))
                .isActive(projectDTO.isActive())
                .build();

        if (projectDTO.getDepartmentId() != null) {
            Department department = departmentService.findEntityById(projectDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            project.setDepartment(department);
        }

        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        project.setName(projectDTO.getName());
        project.setCode(projectDTO.getCode());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setStatus(Project.ProjectStatus.valueOf(projectDTO.getStatus()));
        project.setActive(projectDTO.isActive());

        if (projectDTO.getDepartmentId() != null) {
            Department department = departmentService.findEntityById(projectDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            project.setDepartment(department);
        }

        Project updatedProject = projectRepository.save(project);
        return mapToDTO(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return mapToDTO(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByCompany(Long companyId) {
        Company company = companyService.findEntityById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return projectRepository.findByCompany(company).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByDepartment(Long departmentId) {
        Department department = departmentService.findEntityById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return projectRepository.findByDepartment(department).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ProjectDTO mapToDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .code(project.getCode())
                .description(project.getDescription())
                .companyId(project.getCompany().getId())
                .departmentId(project.getDepartment() != null ? project.getDepartment().getId() : null)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus().name())
                .isActive(project.isActive())
                .build();
    }
} 