package br.com.fiap.softcare.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * AuditLog entity for tracking important system events and user actions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
@CompoundIndex(name = "user_timestamp_idx", def = "{'userId': 1, 'timestamp': -1}")
@CompoundIndex(name = "event_timestamp_idx", def = "{'eventType': 1, 'timestamp': -1}")
public class AuditLog {
    
    @Id
    private String id;
    
    @NotBlank(message = "Tipo de evento é obrigatório")
    @Indexed
    private String eventType;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    // User information
    @Indexed
    private String userId;
    private String userEmail;
    
    // Request information
    private String httpMethod;
    private String requestUrl;
    private String ipAddress;
    
    // Event details
    private String severity; // INFO, WARN, ERROR, CRITICAL
    private String resourceId;
    
    // Results
    @Builder.Default
    private Boolean success = true;
    private String errorMessage;
    
    // Timing
    @NotNull
    @CreatedDate
    @Indexed
    private LocalDateTime timestamp;
    
    /**
     * Common event types as constants
     */
    public static class EventTypes {
        public static final String USER_LOGIN = "USER_LOGIN";
        public static final String USER_LOGOUT = "USER_LOGOUT";
        public static final String USER_CREATED = "USER_CREATED";
        public static final String USER_UPDATED = "USER_UPDATED";
        
        public static final String ASSESSMENT_CREATED = "ASSESSMENT_CREATED";
        public static final String ASSESSMENT_VIEWED = "ASSESSMENT_VIEWED";
        
        public static final String DIARY_ENTRY_CREATED = "DIARY_ENTRY_CREATED";
        public static final String DIARY_ENTRY_VIEWED = "DIARY_ENTRY_VIEWED";
        
        public static final String SUPPORT_CHANNEL_ACCESSED = "SUPPORT_CHANNEL_ACCESSED";
        
        public static final String SECURITY_VIOLATION = "SECURITY_VIOLATION";
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    }
    
    /**
     * Severity levels
     */
    public static class Severity {
        public static final String INFO = "INFO";
        public static final String WARN = "WARN";
        public static final String ERROR = "ERROR";
        public static final String CRITICAL = "CRITICAL";
    }
}