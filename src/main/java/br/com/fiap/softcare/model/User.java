package br.com.fiap.softcare.model;

import br.com.fiap.softcare.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * User entity representing system users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String password;
    
    @NotNull(message = "Função é obrigatória")
    private UserRole role;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Method to check if user is an admin
    public boolean isAdmin() {
        return role == UserRole.SYSTEM_ADMIN;
    }
    
    // Method to check if user can access psychosocial data
    public boolean canAccessPsychosocialData() {
        return role == UserRole.MANAGER || role == UserRole.SYSTEM_ADMIN;
    }
}