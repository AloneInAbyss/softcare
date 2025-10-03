package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.model.AuditLog;
import br.com.fiap.softcare.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Audit Log operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    /**
     * Get audit logs by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String userId) {
        log.info("Getting audit logs for user: {}", userId);
        
        List<AuditLog> logs = auditLogService.findLogsByUser(userId);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get recent audit logs by user ID
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogsByUser(@PathVariable String userId) {
        log.info("Getting recent audit logs for user: {}", userId);
        
        List<AuditLog> logs = auditLogService.findRecentLogsByUser(userId);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get audit logs by event type
     */
    @GetMapping("/event-type/{eventType}")
    public ResponseEntity<List<AuditLog>> getLogsByEventType(@PathVariable String eventType) {
        log.info("Getting audit logs for event type: {}", eventType);
        
        List<AuditLog> logs = auditLogService.findLogsByEventType(eventType);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get critical and error logs
     */
    @GetMapping("/critical")
    public ResponseEntity<List<AuditLog>> getCriticalLogs() {
        log.info("Getting critical audit logs");
        
        List<AuditLog> logs = auditLogService.findCriticalLogs();
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get failed operations
     */
    @GetMapping("/failed")
    public ResponseEntity<List<AuditLog>> getFailedOperations() {
        log.info("Getting failed operations from audit logs");
        
        List<AuditLog> logs = auditLogService.findFailedOperations();
        
        return ResponseEntity.ok(logs);
    }
}