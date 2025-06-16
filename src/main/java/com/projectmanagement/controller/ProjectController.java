package com.projectmanagement.controller;

import com.projectmanagement.dto.ProjectClientAssociationRequest;
import com.projectmanagement.dto.ProjectCreationRequest;
import com.projectmanagement.model.Project;
import com.projectmanagement.service.ProjectService;
import com.projectmanagement.exception.AccessDeniedException;
import com.projectmanagement.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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

}