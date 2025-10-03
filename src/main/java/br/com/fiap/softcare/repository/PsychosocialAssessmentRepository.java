package br.com.fiap.softcare.repository;

import br.com.fiap.softcare.enums.RiskLevel;
import br.com.fiap.softcare.model.PsychosocialAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PsychosocialAssessment entity
 */
@Repository
public interface PsychosocialAssessmentRepository extends MongoRepository<PsychosocialAssessment, String> {
    
    /**
     * Find assessments by user ID
     */
    List<PsychosocialAssessment> findByUserId(String userId);
    
    /**
     * Find latest assessment for a user
     */
    Optional<PsychosocialAssessment> findTopByUserIdOrderByCreatedAtDesc(String userId);
    
    /**
     * Find assessments by risk level
     */
    List<PsychosocialAssessment> findByRiskLevel(RiskLevel riskLevel);
    
    /**
     * Find completed assessments for a user
     */
    List<PsychosocialAssessment> findByUserIdAndIsCompleteTrue(String userId);
    
    /**
     * Find assessments created within a date range
     */
    List<PsychosocialAssessment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count assessments by risk level
     */
    long countByRiskLevel(RiskLevel riskLevel);
    
    /**
     * Find assessments with high or critical risk
     */
    @Query("{'riskLevel': {$in: ['HIGH', 'CRITICAL']}}")
    List<PsychosocialAssessment> findHighRiskAssessments();
    
    /**
     * Find assessments by user with overall score above threshold
     */
    List<PsychosocialAssessment> findByUserIdAndOverallScoreGreaterThan(String userId, Double score);
}