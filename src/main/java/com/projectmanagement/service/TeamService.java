package com.projectmanagement.service;

import com.projectmanagement.dto.TeamCreationRequest;
import com.projectmanagement.dto.TeamMemberRequest;
import com.projectmanagement.model.Team;
import com.projectmanagement.model.User;
import com.projectmanagement.model.User.UserRole;
import com.projectmanagement.repository.TeamRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.BadRequestException;
import com.projectmanagement.exception.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }
    
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    public Team createTeam(TeamCreationRequest teamRequest) {
        // Validate team name uniqueness
        if (teamRepository.existsByName(teamRequest.getName())) {
            throw new BadRequestException("Team with name '" + teamRequest.getName() + "' already exists");
        }
        
        // Get team lead user
        User teamLead = userRepository.findById(teamRequest.getTeamLeadId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found for team lead with id: " + teamRequest.getTeamLeadId()));
        
        // Create new team
        Team team = new Team();
        team.setName(teamRequest.getName());
        team.setDescription(teamRequest.getDescription());
        team.setTeamLead(teamLead);
        team.getMembers().add(teamLead); // Team lead is also a member
        
        return teamRepository.save(team);
    }
    
    @Transactional
    public Team addTeamMembers(Long teamId, TeamMemberRequest memberRequest) {
        Team team = getTeamById(teamId);
        
        // Check if current user is authorized to modify team
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getRole() != UserRole.ADMIN && !currentUser.getId().equals(team.getTeamLead().getId())) {
            throw new AccessDeniedException("Only team lead or admin can modify team members");
        }
        
        // Fetch users to add as members
        Set<User> membersToAdd = memberRequest.getMemberIds().stream()
            .map(userId -> userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)))
            .collect(Collectors.toSet());
        
        // Add members to team
        for (User member : membersToAdd) {
            team.addMember(member);
        }
        
        return teamRepository.save(team);
    }
    
    @Transactional
    public Team removeTeamMember(Long teamId, Long userId) {
        Team team = getTeamById(teamId);
        
        // Check if current user is authorized to modify team
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getRole() != UserRole.ADMIN && !currentUser.getId().equals(team.getTeamLead().getId())) {
            throw new AccessDeniedException("Only team lead or admin can modify team members");
        }
        
        // Cannot remove team lead from team
        if (team.getTeamLead().getId().equals(userId)) {
            throw new BadRequestException("Cannot remove team lead from team");
        }
        
        // Find user to remove
        User userToRemove = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if user is actually a member
        if (!team.getMembers().contains(userToRemove)) {
            throw new BadRequestException("User is not a member of this team");
        }
        
        // Remove member from team
        team.removeMember(userToRemove);
        
        return teamRepository.save(team);
    }
}