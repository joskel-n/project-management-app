package com.projectmanagement.service;

import com.projectmanagement.dto.ProjectCreationRequest;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.User;
import com.projectmanagement.model.UserRole;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.exception.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public Project createProject(ProjectCreationRequest projectRequest) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        // Validate that user has appropriate role
        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.PROJECT_MANAGER) {
            throw new AccessDeniedException("Only administrators and project managers can create projects");
        }
        
        // Create new project
        Project newProject = new Project();
        newProject.setName(projectRequest.getName());
        newProject.setStartDate(projectRequest.getStartDate());
        newProject.setEndDate(projectRequest.getEndDate());
        newProject.setDescription(projectRequest.getDescription());
        newProject.setCreatedBy(currentUser);
        
        return projectRepository.save(newProject);
    }
    
    // Other existing service methods...
}