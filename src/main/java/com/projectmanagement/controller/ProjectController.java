package com.projectmanagement.controller;

import com.projectmanagement.dto.ProjectCreationRequest;
import com.projectmanagement.dto.ProjectClientAssociationRequest;
import com.projectmanagement.dto.ProjectTeamAssociationRequest;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.Team;
import com.projectmanagement.service.ProjectService;
import com.projectmanagement.exception.AccessDeniedException;
import com.projectmanagement.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Set;


@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectCreationRequest projectRequest) {
        try {
            Project createdProject = projectService.createProject(projectRequest);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating project: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{projectId}/client")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public ResponseEntity<?> associateProjectWithClient(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectClientAssociationRequest request) {
        try {
            Project updatedProject = projectService.associateProjectWithClient(projectId, request.getClientId());
            return ResponseEntity.ok(updatedProject);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error associating project with client: " + e.getMessage(),
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

      @PostMapping("/{projectId}/teams")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public ResponseEntity<?> addTeamsToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectTeamAssociationRequest request) {
        try {
            Project updatedProject = projectService.addTeamsToProject(projectId, request);
            return ResponseEntity.ok(updatedProject);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding teams to project: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{projectId}/teams/{teamId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public ResponseEntity<?> removeTeamFromProject(
            @PathVariable Long projectId,
            @PathVariable Long teamId) {
        try {
            Project updatedProject = projectService.removeTeamFromProject(projectId, teamId);
            return ResponseEntity.ok(updatedProject);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error removing team from project: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{projectId}/teams")
    public ResponseEntity<?> getProjectTeams(@PathVariable Long projectId) {
        try {
            Set<Team> teams = projectService.getProjectTeams(projectId);
            return ResponseEntity.ok(teams);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving project teams: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}