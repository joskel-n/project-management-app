package com.projectmanagement.controller;

import com.projectmanagement.security.JwtTokenUtil;
import com.projectmanagement.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // Authenticate against the database
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), 
                    authenticationRequest.getPassword()
                )
            );
            
            // If authentication was successful, the Authentication object will contain the UserDetails
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generate token based on authenticated user details
            final String token = jwtTokenUtil.generateToken(userDetails);
            
            // Get token expiration date
            final Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
            
            return ResponseEntity.ok(new AuthenticationResponse(token, expirationDate));
            
        } catch (DisabledException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}