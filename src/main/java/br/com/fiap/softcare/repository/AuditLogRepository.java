package br.com.fiap.softcare.repository;

import br.com.fiap.softcare.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entity
 */
@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    
    /**
     * Find audit logs by user ID
     */
    List<AuditLog> findByUserId(String userId);
    
    /**
     * Find audit logs by event type
     */
    List<AuditLog> findByEventType(String eventType);
    
    /**
     * Find audit logs by user and event type
     */
    List<AuditLog> findByUserIdAndEventType(String userId, String eventType);
    
    /**
     * Find audit logs within date range
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find audit logs by severity
     */
    List<AuditLog> findBySeverity(String severity);
    
    /**
     * Find failed operations
     */
    List<AuditLog> findBySuccessFalse();
    
    /**
     * Find recent logs for a user (limited and ordered by timestamp desc)
     */
    List<AuditLog> findTop10ByUserIdOrderByTimestampDesc(String userId);
    
    /**
     * Find error and critical logs
     */
    @Query("{'severity': {$in: ['ERROR', 'CRITICAL']}}")
    List<AuditLog> findErrorAndCriticalLogs();
    
    /**
     * Find logs by user within date range
     */
    List<AuditLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count logs by event type
     */
    long countByEventType(String eventType);
    
    /**
     * Count failed operations
     */
    long countBySuccessFalse();
}