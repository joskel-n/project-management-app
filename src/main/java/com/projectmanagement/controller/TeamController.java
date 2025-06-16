package com.projectmanagement.controller;

import com.projectmanagement.dto.TeamCreationRequest;
import com.projectmanagement.dto.TeamMemberRequest;
import com.projectmanagement.model.Team;
import com.projectmanagement.service.TeamService;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.BadRequestException;
import com.projectmanagement.exception.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;
    
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamCreationRequest teamRequest) {
        try {
            Team createdTeam = teamService.createTeam(teamRequest);
            return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating team: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addTeamMembers(@PathVariable Long id, 
                                            @Valid @RequestBody TeamMemberRequest memberRequest) {
        try {
            Team updatedTeam = teamService.addTeamMembers(id, memberRequest);
            return ResponseEntity.ok(updatedTeam);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding team members: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<?> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        try {
            Team updatedTeam = teamService.removeTeamMember(teamId, userId);
            return ResponseEntity.ok(updatedTeam);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error removing team member: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}