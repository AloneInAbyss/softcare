package br.com.fiap.softcare.dto;

import br.com.fiap.softcare.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for User response
 */
@Data
public class UserResponse {
    
    private String id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}