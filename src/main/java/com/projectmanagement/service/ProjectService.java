package com.projectmanagement.service;

import com.projectmanagement.dto.ProjectCreationRequest;
import com.projectmanagement.dto.ProjectClientAssociationRequest;
import com.projectmanagement.dto.ProjectTeamAssociationRequest;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.Client;
import com.projectmanagement.model.Team;
import com.projectmanagement.model.User;
import com.projectmanagement.model.User.UserRole;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.ClientRepository;
import com.projectmanagement.repository.TeamRepository;
import com.projectmanagement.exception.AccessDeniedException;
import com.projectmanagement.exception.ResourceNotFoundException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TeamRepository teamRepository;

    
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

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public Project addTeamsToProject(Long projectId, ProjectTeamAssociationRequest request) {
        // Find the project
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Find the teams
        Set<Team> teamsToAdd = request.getTeamIds().stream()
            .map(teamId -> teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId)))
            .collect(Collectors.toSet());
        
        // Add teams to project
        for (Team team : teamsToAdd) {
            project.addTeam(team);
        }
        
        return projectRepository.save(project);
    }
    
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public Project removeTeamFromProject(Long projectId, Long teamId) {
        // Find the project
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Find the team
        Team teamToRemove = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        // Check if team is associated with the project
        if (!project.getTeams().contains(teamToRemove)) {
            throw new ResourceNotFoundException("Team with id: " + teamId + " is not associated with this project");
        }
        
        // Remove team from project
        project.removeTeam(teamToRemove);
        
        return projectRepository.save(project);
    }
    
    @Transactional(readOnly = true)
    public Set<Team> getProjectTeams(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        return project.getTeams();
    }

     // Other existing service methods...
}