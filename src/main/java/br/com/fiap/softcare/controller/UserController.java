package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.dto.CreateUserRequest;
import br.com.fiap.softcare.dto.LoginRequest;
import br.com.fiap.softcare.dto.UserResponse;
import br.com.fiap.softcare.model.User;
import br.com.fiap.softcare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for User operations
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Create a new user
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();
        
        User createdUser = userService.createUser(user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToResponse(createdUser));
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("Getting user by ID: {}", id);
        
        Optional<User> user = userService.findById(id);
        
        return user.map(u -> ResponseEntity.ok(convertToResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Getting all users");
        
        List<User> users = userService.findAllUsers();
        List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, 
                                                   @Valid @RequestBody CreateUserRequest request) {
        log.info("Updating user: {}", id);
        
        try {
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .role(request.getRole())
                    .build();
            
            User updatedUser = userService.updateUser(id, user);
            
            return ResponseEntity.ok(convertToResponse(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Deleting user: {}", id);
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        boolean isValid = userService.validateCredentials(request.getEmail(), request.getPassword());
        
        if (isValid) {
            // In a real application, you would generate and return a JWT token here
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }
    
    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        
        Optional<User> user = userService.findByEmail(email);
        
        return user.map(u -> ResponseEntity.ok(convertToResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Change password
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable String id,
                                                  @RequestBody ChangePasswordRequest request) {
        log.info("Changing password for user: {}", id);
        
        try {
            userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
    
    /**
     * Inner class for change password request
     */
    @lombok.Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}