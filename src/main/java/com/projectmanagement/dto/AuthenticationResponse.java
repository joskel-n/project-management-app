package com.projectmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Date expirationDate;
}