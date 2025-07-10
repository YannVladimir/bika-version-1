package com.bika.project.service;

import com.bika.project.dto.ProjectDTO;
import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(ProjectDTO projectDTO);
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    void deleteProject(Long id);
    ProjectDTO getProjectById(Long id);
    List<ProjectDTO> getAllProjects();
    List<ProjectDTO> getProjectsByCompany(Long companyId);
    List<ProjectDTO> getProjectsByDepartment(Long departmentId);
} 