package com.projectmanagement.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamAssociationRequest {
    
    @NotEmpty(message = "At least one team ID is required")
    private Set<Long> teamIds;
}