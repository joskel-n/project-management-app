package com.projectmanagement.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectClientAssociationRequest {
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
}