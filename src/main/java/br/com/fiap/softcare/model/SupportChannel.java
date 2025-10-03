package br.com.fiap.softcare.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * SupportChannel entity representing available support resources
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "support_channels")
public class SupportChannel {
    
    @Id
    private String id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição não pode exceder 500 caracteres")
    private String description;
    
    // Contact information
    private String phoneNumber;
    private String email;
    private String website;
    
    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}