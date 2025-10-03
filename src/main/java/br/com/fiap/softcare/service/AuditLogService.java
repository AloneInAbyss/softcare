package br.com.fiap.softcare.service;

import br.com.fiap.softcare.model.AuditLog;
import br.com.fiap.softcare.model.User;
import br.com.fiap.softcare.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Audit Log operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * Log a generic event
     */
    public void logEvent(String eventType, String description, String userId, boolean success) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .description(description)
                .userId(userId)
                .success(success)
                .severity(success ? AuditLog.Severity.INFO : AuditLog.Severity.ERROR)
                .timestamp(LocalDateTime.now())
                .build();
        
        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {}", eventType);
    }
    
    /**
     * Log user creation
     */
    public void logUserCreated(User user) {
        logEvent(AuditLog.EventTypes.USER_CREATED, 
                "User created: " + user.getEmail(), 
                user.getId(), true);
    }
    
    /**
     * Log user update
     */
    public void logUserUpdated(User user) {
        logEvent(AuditLog.EventTypes.USER_UPDATED, 
                "User updated: " + user.getEmail(), 
                user.getId(), true);
    }
    
    /**
     * Log user login
     */
    public void logUserLogin(String userId, String email) {
        logEvent(AuditLog.EventTypes.USER_LOGIN, 
                "User logged in: " + email, 
                userId, true);
    }
    
    /**
     * Log user logout
     */
    public void logUserLogout(String userId, String email) {
        logEvent(AuditLog.EventTypes.USER_LOGOUT, 
                "User logged out: " + email, 
                userId, true);
    }
    
    /**
     * Log assessment creation
     */
    public void logAssessmentCreated(String userId, String assessmentId) {
        logEvent(AuditLog.EventTypes.ASSESSMENT_CREATED, 
                "Psychosocial assessment created", 
                userId, true);
    }
    
    /**
     * Log assessment view
     */
    public void logAssessmentViewed(String userId, String assessmentId) {
        logEvent(AuditLog.EventTypes.ASSESSMENT_VIEWED, 
                "Psychosocial assessment viewed", 
                userId, true);
    }
    
    /**
     * Log diary entry creation
     */
    public void logDiaryEntryCreated(String userId, String entryId) {
        logEvent(AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Emotional diary entry created", 
                userId, true);
    }
    
    /**
     * Log diary entry view
     */
    public void logDiaryEntryViewed(String userId, String entryId) {
        logEvent(AuditLog.EventTypes.DIARY_ENTRY_VIEWED, 
                "Emotional diary entry viewed", 
                userId, true);
    }
    
    /**
     * Log support channel access
     */
    public void logSupportChannelAccessed(String userId, String channelId) {
        logEvent(AuditLog.EventTypes.SUPPORT_CHANNEL_ACCESSED, 
                "Support channel accessed", 
                userId, true);
    }
    
    /**
     * Log security violation
     */
    public void logSecurityViolation(String description, String userId) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(AuditLog.EventTypes.SECURITY_VIOLATION)
                .description(description)
                .userId(userId)
                .success(false)
                .severity(AuditLog.Severity.CRITICAL)
                .timestamp(LocalDateTime.now())
                .build();
        
        auditLogRepository.save(auditLog);
        log.warn("Security violation logged: {}", description);
    }
    
    /**
     * Log system error
     */
    public void logSystemError(String description, String errorMessage) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(AuditLog.EventTypes.SYSTEM_ERROR)
                .description(description)
                .errorMessage(errorMessage)
                .success(false)
                .severity(AuditLog.Severity.ERROR)
                .timestamp(LocalDateTime.now())
                .build();
        
        auditLogRepository.save(auditLog);
        log.error("System error logged: {}", description);
    }
    
    /**
     * Find audit logs by user
     */
    public List<AuditLog> findLogsByUser(String userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    /**
     * Find recent logs for user
     */
    public List<AuditLog> findRecentLogsByUser(String userId) {
        return auditLogRepository.findTop10ByUserIdOrderByTimestampDesc(userId);
    }
    
    /**
     * Find logs by event type
     */
    public List<AuditLog> findLogsByEventType(String eventType) {
        return auditLogRepository.findByEventType(eventType);
    }
    
    /**
     * Find critical and error logs
     */
    public List<AuditLog> findCriticalLogs() {
        return auditLogRepository.findErrorAndCriticalLogs();
    }
    
    /**
     * Find failed operations
     */
    public List<AuditLog> findFailedOperations() {
        return auditLogRepository.findBySuccessFalse();
    }
}