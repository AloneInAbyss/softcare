package br.com.fiap.softcare.service;

import br.com.fiap.softcare.enums.RiskLevel;
import br.com.fiap.softcare.model.PsychosocialAssessment;
import br.com.fiap.softcare.repository.PsychosocialAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Psychosocial Assessment operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PsychosocialAssessmentService {
    
    private final PsychosocialAssessmentRepository assessmentRepository;
    private final AuditLogService auditLogService;
    
    /**
     * Create a new psychosocial assessment
     */
    public PsychosocialAssessment createAssessment(PsychosocialAssessment assessment) {
        log.info("Creating psychosocial assessment for user: {}", assessment.getUserId());
        
        // Calculate scores
        assessment.calculateOverallScore();
        assessment.determineRiskLevel();
        
        PsychosocialAssessment savedAssessment = assessmentRepository.save(assessment);
        
        // Log the creation
        auditLogService.logAssessmentCreated(assessment.getUserId(), savedAssessment.getId());
        
        log.info("Psychosocial assessment created with ID: {}", savedAssessment.getId());
        return savedAssessment;
    }
    
    /**
     * Find assessment by ID
     */
    public Optional<PsychosocialAssessment> findById(String id) {
        return assessmentRepository.findById(id);
    }
    
    /**
     * Find assessments by user ID
     */
    public List<PsychosocialAssessment> findByUserId(String userId) {
        return assessmentRepository.findByUserId(userId);
    }
    
    /**
     * Find latest assessment for user
     */
    public Optional<PsychosocialAssessment> findLatestByUserId(String userId) {
        return assessmentRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Update assessment
     */
    public PsychosocialAssessment updateAssessment(String id, PsychosocialAssessment updatedAssessment) {
        log.info("Updating psychosocial assessment: {}", id);
        
        PsychosocialAssessment existingAssessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada: " + id));
        
        // Update scores
        existingAssessment.setWorkStressLevel(updatedAssessment.getWorkStressLevel());
        existingAssessment.setWorkLifeBalance(updatedAssessment.getWorkLifeBalance());
        existingAssessment.setJobSatisfaction(updatedAssessment.getJobSatisfaction());
        existingAssessment.setRelationshipWithColleagues(updatedAssessment.getRelationshipWithColleagues());
        existingAssessment.setPersonalWellbeing(updatedAssessment.getPersonalWellbeing());
        
        // Recalculate scores
        existingAssessment.calculateOverallScore();
        existingAssessment.determineRiskLevel();
        
        PsychosocialAssessment savedAssessment = assessmentRepository.save(existingAssessment);
        
        log.info("Psychosocial assessment updated: {}", savedAssessment.getId());
        return savedAssessment;
    }
    
    /**
     * Delete assessment
     */
    public void deleteAssessment(String id) {
        log.info("Deleting psychosocial assessment: {}", id);
        
        assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada: " + id));
        
        assessmentRepository.deleteById(id);
        
        log.info("Psychosocial assessment deleted: {}", id);
    }
    
    /**
     * Find assessments by risk level
     */
    public List<PsychosocialAssessment> findByRiskLevel(RiskLevel riskLevel) {
        return assessmentRepository.findByRiskLevel(riskLevel);
    }
    
    /**
     * Find high risk assessments
     */
    public List<PsychosocialAssessment> findHighRiskAssessments() {
        return assessmentRepository.findHighRiskAssessments();
    }
    
    /**
     * Find assessments within date range
     */
    public List<PsychosocialAssessment> findAssessmentsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return assessmentRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Get assessment statistics
     */
    public AssessmentStatistics getAssessmentStatistics() {
        long totalAssessments = assessmentRepository.count();
        long lowRisk = assessmentRepository.countByRiskLevel(RiskLevel.LOW);
        long moderateRisk = assessmentRepository.countByRiskLevel(RiskLevel.MODERATE);
        long highRisk = assessmentRepository.countByRiskLevel(RiskLevel.HIGH);
        long criticalRisk = assessmentRepository.countByRiskLevel(RiskLevel.CRITICAL);
        
        return AssessmentStatistics.builder()
                .totalAssessments(totalAssessments)
                .lowRisk(lowRisk)
                .moderateRisk(moderateRisk)
                .highRisk(highRisk)
                .criticalRisk(criticalRisk)
                .build();
    }
    
    /**
     * Inner class for assessment statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class AssessmentStatistics {
        private long totalAssessments;
        private long lowRisk;
        private long moderateRisk;
        private long highRisk;
        private long criticalRisk;
    }
}