package com.projectmanagement.service;

import com.projectmanagement.dto.ProjectCreationRequest;
import com.projectmanagement.dto.ProjectClientAssociationRequest;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.Client;
import com.projectmanagement.model.User;
import com.projectmanagement.model.User.UserRole;  // Corrected: import as inner enum of User class
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.ClientRepository;
import com.projectmanagement.exception.AccessDeniedException;
import com.projectmanagement.exception.ResourceNotFoundException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;  // Added missing import
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ClientRepository clientRepository;

    
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
    
    @Transactional
    public Project associateProjectWithClient(Long projectId, Long clientId) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        // Validate that user has appropriate role
if (currentUser.getRole() != User.UserRole.ADMIN && currentUser.getRole() != User.UserRole.PROJECT_MANAGER) {
    throw new AccessDeniedException("Only administrators and project managers can associate projects with clients");
}
        
        // Find the project
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Find the client
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        
        // Associate the project with the client
        Hibernate.initialize(client);
        project.setClient(client);
        
        return projectRepository.save(project);
    }
    // Other existing service methods...
}